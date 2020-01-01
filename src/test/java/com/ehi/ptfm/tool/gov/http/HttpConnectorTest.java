package com.ehi.ptfm.tool.gov.http;

import org.junit.Before;
import org.junit.Test;

import java.util.Date;

public class HttpConnectorTest {
	private HttpConnetor connetor;

	@Before
	public void init(){
		String path = "https://www.cms.gov/Research-Statistics-Data-and-Systems/Statistics-Trends-and-Reports/MCRAdvPartDEnrolData/MA-Plan-Directory-Items/MA-Plan-Directory";
		connetor = new HttpConnetor(path);
	}

	@Test
	public void test(){
		String filePath = "https://www.cms.gov/Research-Statistics-Data-and-Systems/Statistics-Trends-and-Reports/MCRAdvPartDEnrolData/Downloads/MA-Plan-Directory.zip";
		connetor.download(filePath);
	}


}
