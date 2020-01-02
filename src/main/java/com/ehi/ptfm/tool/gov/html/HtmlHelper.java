package com.ehi.ptfm.tool.gov.html;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HtmlHelper {

	private Document doc;

	public HtmlHelper(Document doc){
		this.doc = doc;
	}

	public ArrayList<Map<String, String>> getElementsByText(String selector, String textRegex) {
		Elements elements = doc.select(selector);
		elements = elements.select(String.format("a:contains(%s)", textRegex));
		ArrayList<Map<String, String>> nameList = new ArrayList<Map<String, String>>();
		for (Element element : elements){
			Map<String, String> namePathMap = new HashMap<String, String>();
			namePathMap.put(element.textNodes().get(0).toString().trim(), element.attributes().get("href").trim());
			nameList.add(namePathMap);
		}
		return nameList;
	}
}
