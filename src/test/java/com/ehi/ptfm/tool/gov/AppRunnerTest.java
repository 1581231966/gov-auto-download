package com.ehi.ptfm.tool.gov;

import com.ehi.ptfm.tool.gov.common.ApplicationProperties;
import com.ehi.ptfm.tool.gov.html.HtmlHelper;
import com.ehi.ptfm.tool.gov.http.HttpConnector;
import okhttp3.*;
import org.jsoup.Jsoup;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class AppRunnerTest {

	@Test
	public void testDownloadZipCodeDatabase(){

		HttpConnector connector = new HttpConnector("https://www.zip-codes.com/account_login.asp");
		FormBody formBody = new FormBody.Builder()
				.add("loginUsername", ApplicationProperties.getProperties("zip.code.username"))
				.add("loginPassword", ApplicationProperties.getProperties("zip.code.password"))
				.add("Action", "Login")
				.add("Submit", "Login")
				.add("redir", "account_home.asp")
				.build();

		HtmlHelper htmlHelper = new HtmlHelper(Jsoup.parse(connector.getSiteBody(formBody)));
		ArrayList<Map<String, String>> maps = htmlHelper.getElementsByText("body>table>tbody>tr>td:nth-child(2)>div>table>tbody>tr:nth-child(2)", "Download");
		if(maps.size() == 1){
			connector.changeUrlTo("https://" + connector.getHost() + "/" + maps.get(0).get("Download"));
			htmlHelper = new HtmlHelper(Jsoup.parse(connector.getSiteBody()));
			maps = htmlHelper.getElementsByText("body > table > tbody > tr > td:nth-child(2) > div > table:nth-child(6) > tbody", "[ Download ]");
			for (Map<String, String> map : maps){
				System.out.println("Download "+ map.get("[ Download ]"));
				connector.download("https://" + connector.getHost() + "/" + map.get("[ Download ]"));
			}
		}
	}

	@Test
	public void testDownloadPlanDirectory(){
		HttpConnector connector = new HttpConnector("http://www.cms.gov/Research-Statistics-Data-and-Systems/Statistics-Trends-and-Reports/MCRAdvPartDEnrolData/index.html");
		HtmlHelper htmlHelper = new HtmlHelper(Jsoup.parse(connector.getSiteBody()));
		ArrayList<Map<String, String>> maps = htmlHelper.getElementsByText("li.menu-item", "Plan Directory");
		for (Map<String, String> map : maps){
			for (String key: map.keySet()) {
				connector.changeUrlTo("https://" + connector.getHost()+ map.get(key));
				htmlHelper = new HtmlHelper(Jsoup.parse(connector.getSiteBody()));
			}
		}
	}
}
