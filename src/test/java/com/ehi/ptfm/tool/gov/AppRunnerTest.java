package com.ehi.ptfm.tool.gov;

import okhttp3.*;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AppRunnerTest {

	@Test
	public void test(){
		String path = "https://www.zip-codes.com/";
		String cookieStr;
		final HashMap<String, List<Cookie>> cookieStore = new HashMap<String, List<Cookie>>();
		FormBody formBody = new FormBody.Builder()
				.add("loginUsername", "michael@bass-family.net")
				.add("loginPassword", "ehiZIP0!")
				.add("Action", "Login")
				.add("Submit", "Login")
				.add("redir", "account_home.asp")
				.build();

		Request request =  new Request.Builder()
				.url(path)
				.build();
		OkHttpClient okHttpClient = new OkHttpClient.Builder()
				.readTimeout(30, TimeUnit.SECONDS)
				.connectTimeout(30, TimeUnit.SECONDS)
				.cookieJar(new CookieJar() {
					@Override
					public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
						cookieStore.put("https://www.zip-codes.com/", cookies);
						cookieStore.put(url.host(), cookies);
						for (Cookie cookie : cookies){
							System.out.println("cookie Name:"+cookie.name());
							System.out.println("cookie Path:"+cookie.path());
						}
					}

					@Override
					public List<Cookie> loadForRequest(HttpUrl url) {
						List<Cookie> cookies = cookieStore.get("https://www.zip-codes.com/");
						return cookies != null ? cookies : new ArrayList<Cookie>();
					}
				})
				.build();
		try {
			Request request1 = new Request.Builder()
					.post(formBody)
					.url("https://www.zip-codes.com/account_login.asp")
					.build();
			Response response = okHttpClient.newCall(request1).execute();

			cookieStr = response.header("Set-Cookie");
			System.out.println(response.body().string());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
