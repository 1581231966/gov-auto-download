package com.ehi.ptfm.tool.gov.http;

import com.ehi.ptfm.tool.gov.common.ApplicationProperties;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class HttpConnetor {

	private static OkHttpClient okHttpClient;
	private String path;
	private static final Logger LOGGER = Logger.getLogger(HttpConnetor.class.getName());

	static {
		okHttpClient = new OkHttpClient.Builder()
				.readTimeout(30, TimeUnit.SECONDS)
				.connectTimeout(30, TimeUnit.SECONDS)
				.build();
	}

	public HttpConnetor(String path) {
		this.path = path;
	}

	/**
	 * @return Return the target site html text.
	 */
	public String getSiteBody() {
		Request request = new Request.Builder()
				.url(path)
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
		if (!dispositionHeader.isEmpty()) {
			String[] strings = dispositionHeader.split(";");
			if (strings.length > 1) {
				dispositionHeader = strings[1].replace("filename=", "");
				return dispositionHeader.replace("\"", "").trim();
			}
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
				LOGGER.info(String.format("Create a new file:%s.", file.getPath()));
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
}
