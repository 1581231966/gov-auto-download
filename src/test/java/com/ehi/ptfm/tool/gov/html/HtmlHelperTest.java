package com.ehi.ptfm.tool.gov.html;

import okhttp3.HttpUrl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlHelperTest {

	@Test
	public void test(){
		String str="CY 2019 September Formulary Reference File";
		String regex = "\\d{4} | (\\d{4} (January|February|March|April|May|June|July|August|September|October|November|December))"; //正则表达式
		Pattern pattern = Pattern.compile(regex);
		Matcher m = pattern.matcher(str);
		List<String> matchRegexList = new ArrayList<String>();
		while(m.find()){
			matchRegexList.add(m.group().trim());
		}
		System.out.println(matchRegexList);
	}

	@Test
	public void test2(){
		HttpUrl url = HttpUrl.parse("http://download.cms.gov/nppes/NPI_Files.html");
		System.out.println(url.toString());
	}

}
