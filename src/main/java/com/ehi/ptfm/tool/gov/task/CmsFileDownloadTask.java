package com.ehi.ptfm.tool.gov.task;

import com.ehi.ptfm.tool.gov.email.excel.FileMessage;
import com.ehi.ptfm.tool.gov.html.HtmlHelper;
import com.ehi.ptfm.tool.gov.html.Selector;
import com.ehi.ptfm.tool.gov.http.HttpConnector;
import okhttp3.HttpUrl;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CmsFileDownloadTask implements Callable<ArrayList<FileMessage>>{

	private Map<HttpUrl, Selector> selectors;
	private static final Logger LOGGER = Logger.getLogger(CmsFileDownloadTask.class.getName());
	public CmsFileDownloadTask(Map<HttpUrl, Selector> selectors){
		this.selectors = selectors;
	}

	private Elements filter(Elements elements){
		int temp = 0;
		String regex = "\\d{4}";
		Elements els = new Elements();
		for (Element element : elements){
			String text;
			Pattern pattern = Pattern.compile(regex);
			Matcher m = pattern.matcher(element.text());
			if(m.find()){
				text = m.group().trim();
				if (temp <= Integer.valueOf(text)){
					els.add(element);
					temp = Integer.valueOf(text);
				}
			}else {
				els.add(element);
				break;
			}
		}
		return els;
	}

	@Override
	public ArrayList<FileMessage> call() throws Exception {
		Set<HttpUrl> set = selectors.keySet();
		ArrayList<FileMessage> messages = new ArrayList<>();
		for (HttpUrl s : set){
			HttpConnector connector = new HttpConnector(s);
			Document doc = Jsoup.parse(connector.getSiteBody());
			Elements elements = HtmlHelper.getElementsBySelector(doc, selectors.get(s));
			if (elements.size() > 1){
				LOGGER.info("Start to filter elements.");
				elements = filter(elements);
			}else if (elements.size() == 0){
				LOGGER.info("Can not fine elements by selector.");
			}
			for (Element element : elements){
				HttpUrl url  =connector.getUrl().resolve(element.attributes().get("href").trim());
				List<String> strs = url.pathSegments();
				if (strs.get(strs.size() -1).matches(".*zip|.*pdf|.*txt")){
					String urlString = url.toString();
					FileMessage message = connector.download(urlString);
					messages.add(message);
				}
			}
		}
		return messages;
	}
}
