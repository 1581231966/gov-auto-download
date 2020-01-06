package com.ehi.ptfm.tool.gov.http;

import okhttp3.HttpUrl;
import org.junit.Before;
import org.junit.Test;

public class HttpConnectorTest {
	private HttpConnector connector;

	@Before
	public void init(){
		String path = "https://www.cms.gov/Research-Statistics-Data-and-Systems/Statistics-Trends-and-Reports/MCRAdvPartDEnrolData/MA-Plan-Directory-Items/MA-Plan-Directory";
		connector = new HttpConnector(HttpUrl.parse(path));
	}

	@Test
	public void test(){
		String filePath = "https://www.cms.gov/Research-Statistics-Data-and-Systems/Statistics-Trends-and-Reports/MCRAdvPartDEnrolData/Downloads/MA-Plan-Directory.zip";
		connector.download(filePath);
	}


}
