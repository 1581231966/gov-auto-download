package com.ehi.ptfm.tool.gov;

import com.ehi.ptfm.tool.gov.common.ApplicationProperties;
import com.ehi.ptfm.tool.gov.html.HtmlHelper;
import com.ehi.ptfm.tool.gov.http.HttpConnector;
import okhttp3.FormBody;
import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.Map;

public class AppRunner {

	public static void main(String[] args){/*
		String path = "https://www.cms.gov/Research-Statistics-Data-and-Systems/Statistics-Trends-and-Reports/MCRAdvPartDEnrolData/MA-Plan-Directory-Items/MA-Plan-Directory";
		HttpConnector connector = new HttpConnector(path);
		String filePath = "https://www.cms.gov/Research-Statistics-Data-and-Systems/Statistics-Trends-and-Reports/MCRAdvPartDEnrolData/Downloads/MA-Plan-Directory.zip";
		connector.download(filePath);*/

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
}
