package com.ehi.ptfm.tool.gov.html;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HtmlHelper {


	public static Map<String, String> getElementsByText(Document doc, String selector, String textRegex) {
		Elements elements = doc.select(selector);
		elements = elements.select(String.format("a:contains(%s)", textRegex));
		Map<String, String> nameList = new HashMap<>();
		for (Element element : elements){
			nameList.put(element.textNodes().get(0).toString().trim(), element.attributes().get("href").trim());
		}
		return nameList;
	}
	public static Elements getElementsBySelector(Document doc, String selector, String textRegex) {
		Elements elements = doc.select(selector);
		return elements.select(String.format("a:matchesOwn(%s)", textRegex));
	}
	public static Elements getElementsBySelector(Document doc, Selector selector) {
		Elements elements = doc.select(selector.getSelector());
		return elements.select(String.format("a:matchesOwn(%s)", selector.getTextRangx()));
	}
	public static Elements getElementsBySelector(Document doc, String selector) {
		return doc.select(selector);
	}


}
