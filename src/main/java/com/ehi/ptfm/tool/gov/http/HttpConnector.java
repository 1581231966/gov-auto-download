package com.ehi.ptfm.tool.gov.http;

import com.ehi.ptfm.tool.gov.common.ApplicationProperties;
import com.ehi.ptfm.tool.gov.email.excel.FileDownloadStatus;
import com.ehi.ptfm.tool.gov.email.excel.FileMessage;
import com.ehi.ptfm.tool.gov.http.listener.ProgressListener;
import okhttp3.*;
import org.apache.log4j.Logger;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class HttpConnector {

	private static OkHttpClient okHttpClient;
	private HttpUrl url;

	private static HashMap<String, List<Cookie>> cookieStore = new HashMap<String, List<Cookie>>();
	private static final Logger LOGGER = Logger.getLogger(HttpConnector.class.getName());

	static {
		okHttpClient = new OkHttpClient.Builder()
				.readTimeout(30, TimeUnit.SECONDS)
				.connectTimeout(30, TimeUnit.SECONDS)
				.cookieJar(new CookieJar() {
					@Override
					public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
						cookieStore.put(url.host(), cookies);
					}

					@Override
					public List<Cookie> loadForRequest(HttpUrl url) {
						List<Cookie> cookies = cookieStore.get(url.host());
						return cookies != null ? cookies : new ArrayList<>();
					}
				})
				.addNetworkInterceptor(chain -> {
					Response response = chain.proceed(chain.request());
					List<String> strings = chain.request().url().pathSegments();
					if (strings.get(strings.size() -1).matches(".*zip|.*pdf|.*txt")){
						return response.newBuilder()
								.body(new ProgressResponseBody(response.body(), new ProgressListener()))
								.build();
					}
					return response;
				})
				.build();
	}
	public HttpConnector(HttpUrl url) {
		this.url = url;
	}

	public void changeUrlTo(HttpUrl target){
		setUrl(target);
	}
	/**
	 * @return Return the target site html text.
	 */
	public String getSiteBody() {
		LOGGER.info("Connect to : " + url.toString());
		Request request = new Request.Builder()
				.url(url.toString())
				.build();
		return getSiteBody(request);
	}

	/**
	 * @param formBody A form used to login site.
	 * @return Return the target site html text.
	 */
	public String getSiteBody(FormBody formBody){
		LOGGER.info("Connect to by form : " + url.toString());
		Request request = new Request.Builder()
				.url(url.toString())
				.post(formBody)
				.build();
		return getSiteBody(request);
	}

	public String getSiteBody(Request request){
		if (request == null){
			LOGGER.info("Request can not be empty.");
			return  "";
		}
		Call call = okHttpClient.newCall(request);
		ResponseBody body = null;
		try {
			Response response = call.execute();
			body = response.body();
			return body.string();
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}finally {
			if (body != null) {
				body.close();
			}
		}
	}

	public String getHost(){
		return url.host();
	}
	/**
	 * @param fileUrl path of PUF file.
	 * download file from file-url and save to local dir.
	 */
	public FileMessage download(String fileUrl) {
		Request request = new Request.Builder()
				.url(fileUrl)
				.get()
				.build();
		Call call = okHttpClient.newCall(request);
		try {
			Response response = call.execute();
			return writeFile(response);
		} catch (IOException e) {
			LOGGER.error("Error in creating file.\n", e);
			return null;
		}
	}

	private String getFileName(Response response) {
		String dispositionHeader = response.header("Content-Disposition");
		if (dispositionHeader != null &&!dispositionHeader.isEmpty()) {
			String[] strings = dispositionHeader.split(";");
			if (strings.length > 1) {
				dispositionHeader = strings[1].replace("filename=", "");
				return dispositionHeader.replace("\"", "").trim();
			}
		}else {
			return getFileName(response.request().url());
		}
		return null;
	}

	private String getFileName(HttpUrl fileUrl){
		List<String> segments = fileUrl.pathSegments();
		return segments.get(segments.size() - 1).replace("%20", " ");
	}

	private String getFolderName(Date date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
		return File.separator + dateFormat.format(date).trim();
	}

	public String formatByte(long bytes){
		long kb = 1024;
		long mb = kb * 1024;
		long gb = mb * 1024;

		if (bytes >= gb){
			return String.format("%.1f GB", (float)bytes / gb);
		}else if(bytes >= mb){
			return String.format("%.0f MB", (float)bytes / mb);
		}else if (bytes >= kb){
			return String.format("%.0f KB", (float)bytes / kb);
		}else {
			return String.format("%d B", bytes);
		}
	}

	/**
	 * @param response Get inputStream from response and write to local file.
	 * @return Return message about the file which should be downloaded.
	 * @throws IOException Throws Exception.
	 */
	private FileMessage writeFile(Response response) throws IOException{
		FileMessage message = new FileMessage();
		File file = new File(ApplicationProperties.getProperties("file.dir.root") + getFolderName(new Date()));
		if (!file.exists()) {
			if (file.mkdirs()) {
				LOGGER.info(String.format("Create a new dir:%s.", file.getPath()));
			} else {
				LOGGER.error("Error in creating new dir " + file.getPath());
			}
		}
		String fileName = getFileName(response);
		message.setPathFrom(response.request().url().toString());
		message.setFileName(fileName);
		if (fileName != null && !fileName.isEmpty()) {
			file = new File(file, fileName);
			if (file.createNewFile()) {
				LOGGER.info("Start to download " + fileName);
				InputStream inputStream = response.body().byteStream();
				FileOutputStream fileOutputStream = new FileOutputStream(file);
				int length;
				byte[] buff = new byte[2048];
				while ((length = inputStream.read(buff)) != -1) {
					fileOutputStream.write(buff, 0, length);
				}
				fileOutputStream.flush();
				message.setLocalPath(file.getPath());
				message.setStatus(FileDownloadStatus.SUCCESS);
				message.setSize(formatByte(file.length()));
				inputStream.close();
				fileOutputStream.close();
				LOGGER.info(String.format("Download Success:%s.\n", file.getPath()));
			} else {
				if (file.exists()) {
					message.setStatus(FileDownloadStatus.EXIST);
					message.setSize(formatByte(file.length()));
					message.setLocalPath(file.getPath());
					LOGGER.info(String.format("The file %s is exists.\n", fileName));
				}else {
					message.setStatus(FileDownloadStatus.FAILED);
					LOGGER.error("Error in create new file\n");
				}
			}
		} else {
			LOGGER.error("Error in getting file name.\n");
		}
		return message;
	}

	public HttpUrl getUrl() {
		return url;
	}

	public void setUrl(HttpUrl url) {
		this.url = url;
	}
}
