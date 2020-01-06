package com.ehi.ptfm.tool.gov.http;

import com.ehi.ptfm.tool.gov.common.ApplicationProperties;
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
						return cookies != null ? cookies : new ArrayList<Cookie>();
					}
				})
				.addNetworkInterceptor(new Interceptor() {
					@Override
					public Response intercept(Chain chain) throws IOException {
						Response response = chain.proceed(chain.request());
						List<String> strings = chain.request().url().pathSegments();
						if (strings.get(strings.size() -1).matches(".*zip|.*pdf")){
							return response.newBuilder()
									.body(new ProgressResponseBody(response.body(), new ProgressListener()))
									.build();
						}
						return response;
					}
				})
				.build();
	}
	public HttpConnector(HttpUrl url) {
		this.url = url;
	}

	public void changeUrlTo(String target){
		this.url = HttpUrl.parse(target);
	}
	/**
	 * @return Return the target site html text.
	 */
	public String getSiteBody() {
		Request request = new Request.Builder()
				.url(url.toString())
				.build();
		Call call = okHttpClient.newCall(request);
		try {
			Response response = call.execute();
			return response.body().string();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * @param formBody A form used to login site.
	 * @return Return the target site html text.
	 */
	public String getSiteBody(FormBody formBody){
		Request request = new Request.Builder()
				.url(url.toString())
				.post(formBody)
				.build();
		Call call = okHttpClient.newCall(request);
		try {
			Response response = call.execute();
			return response.body().string();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	public String getHost(){
		return url.host();
	}
	/**
	 * @param fileUrl path of PUF file.
	 * download file from file-url and save to local dir.
	 */
	public void download(String fileUrl) {
		Request request = new Request.Builder()
				.url(fileUrl)
				.get()
				.build();
		Call call = okHttpClient.newCall(request);
		try {
			Response response = call.execute();
			writeFile(response);
		} catch (IOException e) {
			LOGGER.error("Error in creating file.", e);
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
			List<String> segments = response.request().url().pathSegments();
			return segments.get(segments.size() - 1);
		}
		return null;
	}


	private String getFolderName(Date date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
		return File.separator + dateFormat.format(date).trim();
	}

	private void writeFile(Response response) throws IOException{

		File file = new File(ApplicationProperties.getProperties("file.dir.root") + getFolderName(new Date()));
		if (!file.exists()) {
			if (file.mkdirs()) {
				LOGGER.info(String.format("Create a new dir:%s.", file.getPath()));
			} else {
				LOGGER.error("Error in creating new dir " + file.getPath());
			}
		}
		String fileName = getFileName(response);
		if (fileName != null && !fileName.isEmpty()) {
			file = new File(file, fileName);
			if (file.createNewFile()) {
				LOGGER.info("Start to download " + file.getName());
				InputStream inputStream = response.body().byteStream();
				FileOutputStream fileOutputStream = new FileOutputStream(file);
				int length;
				byte[] buff = new byte[2048];
				while ((length = inputStream.read(buff)) != -1) {
					fileOutputStream.write(buff, 0, length);
				}
				fileOutputStream.flush();
				inputStream.close();
				fileOutputStream.close();
				LOGGER.info(String.format("Download Success:%s.", file.getPath()));
			} else {
				if (file.exists()) {
					LOGGER.info(String.format("The file %s is exists.", file.getName()));
				}else {
					LOGGER.error("Error in create new file");
				}
			}
		} else {
			LOGGER.error("Error in getting file name.");
		}
	}

	public HttpUrl getUrl() {
		return url;
	}

	public void setUrl(HttpUrl url) {
		this.url = url;
	}
}
