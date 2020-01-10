package com.ehi.ptfm.tool.gov.task;

import com.ehi.ptfm.tool.gov.common.ApplicationProperties;
import com.ehi.ptfm.tool.gov.email.excel.FileMessage;
import com.ehi.ptfm.tool.gov.html.HtmlHelper;
import com.ehi.ptfm.tool.gov.html.Selector;
import com.ehi.ptfm.tool.gov.http.HttpConnector;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.concurrent.Callable;

public class ZipCodeDownloadTask implements Callable<ArrayList<FileMessage>> {

	@Override
	public ArrayList<FileMessage> call() throws Exception {
		ArrayList<FileMessage> messages = new ArrayList<>();
		HttpConnector connector = new HttpConnector(HttpUrl.parse("https://www.zip-codes.com/account_login.asp"));
		FormBody formBody = new FormBody.Builder()
				.add("loginUsername", ApplicationProperties.getProperties("zip.code.username"))
				.add("loginPassword", ApplicationProperties.getProperties("zip.code.password"))
				.add("Action", "Login")
				.add("Submit", "Login")
				.add("redir", "account_home.asp")
				.build();
		Document doc = Jsoup.parse(connector.getSiteBody(formBody));

		Selector selector = new Selector("body>table>tbody>tr>td:nth-child(2)>div>table>tbody>tr:nth-child(2)", "(Download)");
		Elements elements = HtmlHelper.getElementsBySelector(doc, selector);
		for(Element element : elements){
			connector.changeUrlTo(connector.getUrl().resolve(element.attributes().get("href").trim()));
			selector.setSelector("body>table>tbody>tr>td:nth-child(2)>div>table:nth-child(6)>tbody");
			selector.setTextRangx("(Download)");

			Elements els =HtmlHelper.getElementsBySelector(Jsoup.parse(connector.getSiteBody()), selector);
			for (Element el : els){
				HttpUrl url  =connector.getUrl().resolve(el.attributes().get("href").trim());
				String str = url.queryParameter("type");
				if (str!=null && str.matches("(csv)|(mdb)")){
					String urlString = url.toString();
					FileMessage message = connector.download(urlString);
					messages.add(message);
				}
			}
		}
		return messages;
	}
}
