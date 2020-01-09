package com.ehi.ptfm.tool.gov;

import com.ehi.ptfm.tool.gov.html.Selector;
import okhttp3.HttpUrl;
import org.junit.Before;

import java.util.List;

public class Application {
	HttpUrl url = HttpUrl.parse("https://www.cms.gov/Research-Statistics-Data-and-Systems/Statistics-Trends-and-Reports/MCRAdvPartDEnrolData/MA-Plan-Directory-Items/MA-Plan-Directory");
	List<Selector> selectors;
	@Before
	public void init(){

	}
}
