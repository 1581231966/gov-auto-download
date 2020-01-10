package com.ehi.ptfm.tool.gov;

import com.ehi.ptfm.tool.gov.common.ApplicationProperties;
import com.ehi.ptfm.tool.gov.html.HtmlHelper;
import com.ehi.ptfm.tool.gov.html.Selector;
import com.ehi.ptfm.tool.gov.http.HttpConnector;
import okhttp3.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AppRunnerTest {

	@Test
	public void testDownloadZipCodeDatabase(){

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
				HttpUrl url  =parseUrl(connector.getUrl(), el.attributes().get("href").trim());
				String str = url.queryParameter("type");
				if (str!=null && str.matches("(csv)|(mdb)")){
					String urlString = url.toString();
					connector.download(urlString);
				}
			}
		}
	}

	@Test
	public void testDownloadSourceText(){
		HttpConnector connector = new HttpConnector(HttpUrl.parse("http://www.nber.org/data/cbsa-msa-fips-ssa-county-crosswalk.html"));
		Document doc = Jsoup.parse(connector.getSiteBody());
		Elements elements = HtmlHelper.getElementsBySelector(doc, "center", "(Source txt).*");

		for (Element element : elements){
			HttpUrl url  =parseUrl(connector.getUrl(), element.attributes().get("href").trim());
			List<String> strs = url.pathSegments();
			if (strs.get(strs.size() -1).matches(".*zip|.*pdf|.*txt")){
				String urlString = url.toString();
				connector.download(urlString);
				break;
			}
		}
	}

	@Test
	public void testDownloadMAPlanDirectory(){
		HttpConnector connector = new HttpConnector(HttpUrl.parse("https://www.cms.gov/Research-Statistics-Data-and-Systems/Statistics-Trends-and-Reports/MCRAdvPartDEnrolData/MA-Plan-Directory-Items/MA-Plan-Directory"));
		Document doc = Jsoup.parse(connector.getSiteBody());
		Elements elements = HtmlHelper.getElementsBySelector(doc, "div", "MA Plan Directory as of Dececmber (\\d\\d\\d\\d).*");
		for (Element element : elements){
			HttpUrl url  =parseUrl(connector.getUrl(), element.attributes().get("href").trim());
			List<String> strs = url.pathSegments();
			if (strs.get(strs.size() -1).matches(".*zip")){
				String urlString = url.toString();
				connector.download(urlString);
			}
		}
	}

	@Test
	public void testDownloadPDPPlanDirectory(){
		HttpConnector connector = new HttpConnector(HttpUrl.parse("https://www.cms.gov/Research-Statistics-Data-and-Systems/Statistics-Trends-and-Reports/MCRAdvPartDEnrolData/PDP-Plan-Directory-Items/PDP-Plan-Directory"));
		Document doc = Jsoup.parse(connector.getSiteBody());
		Elements elements = HtmlHelper.getElementsBySelector(doc, "div", "PDP Plan Directory as of December (\\d\\d\\d\\d).*");
		for (Element element : elements){
			HttpUrl url  =parseUrl(connector.getHost(), element.attributes().get("href").trim());
			List<String> strs = url.pathSegments();
			if (strs.get(strs.size() -1).matches(".*zip")){
				String urlString = url.toString();
				connector.download(urlString);
			}
		}
	}

	@Test
	public void testDownloadRatingsData(){
		HttpConnector connector = new HttpConnector(HttpUrl.parse("https://www.cms.gov/Medicare/Prescription-Drug-Coverage/PrescriptionDrugCovGenIn/PerformanceData"));
		Document doc = Jsoup.parse(connector.getSiteBody());
		Elements elements = HtmlHelper.getElementsBySelector(doc, "div", "Star Ratings Data Table");
		for (Element element : elements){
			HttpUrl url  =parseUrl(connector.getHost(), element.attributes().get("href").trim());
			List<String> strs = url.pathSegments();
			if (strs.get(strs.size() -1).matches(".*zip")){
				String urlString = url.toString();
				connector.download(urlString);
			}
		}
	}

	@Test
	public void testDownloadSourceLandscape(){
		HttpConnector connector = new HttpConnector(HttpUrl.parse("https://www.cms.gov/Medicare/Prescription-Drug-Coverage/PrescriptionDrugCovGenIn/index"));
		Document doc = Jsoup.parse(connector.getSiteBody());
		Elements elements = HtmlHelper.getElementsBySelector(doc, "div", ".*Landscape Source Files|.*Plan and Premium Information for Medicare Plans Offering Part D Coverage");
		elements = filter(elements);
		for (Element element : elements){
			HttpUrl url  =parseUrl(connector.getHost(), element.attributes().get("href").trim());
			List<String> strs = url.pathSegments();
			if (strs.get(strs.size() -1).matches(".*zip")){
				String urlString = url.toString();
				connector.download(urlString);
			}
		}
	}

	@Test
	public void testDownloadFormularyReference(){
		HttpConnector connector = new HttpConnector(HttpUrl.parse("https://www.cms.gov/Medicare/Prescription-Drug-Coverage/PrescriptionDrugCovContra/RxContracting_FormularyGuidance"));
		Document doc = Jsoup.parse(connector.getSiteBody());
		Elements elements = HtmlHelper.getElementsBySelector(doc, "li", "(CY \\d\\d\\d\\d).*Formulary Reference File");
		elements = filter(elements);
		for (Element element : elements){
			HttpUrl url  =parseUrl(connector.getHost(), element.attributes().get("href").trim());
			List<String> strs = url.pathSegments();
			if (strs.get(strs.size() -1).matches(".*zip")){
				String urlString = url.toString();
				connector.download(urlString);
			}
		}
	}

	@Test
	public void testDownloadFullNpi(){
		HttpConnector connector = new HttpConnector(HttpUrl.parse("http://download.cms.gov/nppes/NPI_Files.html"));
		Document doc = Jsoup.parse(connector.getSiteBody());
		Elements elements = HtmlHelper.getElementsBySelector(doc, "li", ".*(NPPES Data Dissemination - Monthly Deactivation Update)");

		for (Element element : elements){
			HttpUrl url  =parseUrl(connector.getUrl(), element.attributes().get("href").trim());
			List<String> strs = url.pathSegments();
			if (strs.get(strs.size() -1).matches(".*zip")){
				String urlString = url.toString();
				connector.download(urlString);
			}
		}
	}

	@Test
	public void testDownloadRegional(){
		HttpConnector connector = new HttpConnector(HttpUrl.parse("https://www.cms.gov/Medicare/Health-Plans/MedicareAdvtgSpecRateStats/Ratebooks-and-Supporting-Data-Items/2020Rates"));
		Document doc = Jsoup.parse(connector.getSiteBody());
		Elements elements = HtmlHelper.getElementsBySelector(doc, "li", "(Regional rates and benchmarks).*");

		for (Element element : elements){
			HttpUrl url  =parseUrl(connector.getUrl(), element.attributes().get("href").trim());
			List<String> strs = url.pathSegments();
			if (strs.get(strs.size() -1).matches(".*zip|.*pdf")){
				String urlString = url.toString();
				connector.download(urlString);
			}
		}
	}


	private HttpUrl parseUrl(String host, String index){
		if (index.startsWith("/")){
			return HttpUrl.parse("https://" +host + index);
		}else{
			return HttpUrl.parse("https://" +host + "/" + index);
		}
	}

	private HttpUrl parseUrl(HttpUrl url, String index){
			return url.resolve(index);
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
					System.out.println(element.text());
					temp = Integer.valueOf(text);
				}
			}
		}
		return els;
	}
}
