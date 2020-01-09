package com.ehi.ptfm.tool.gov;

import com.ehi.ptfm.tool.gov.html.Selector;
import com.ehi.ptfm.tool.gov.task.CmsFileDownloadTask;
import okhttp3.HttpUrl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AppRunner {

	public static void main(String[] args){
		CmsFileDownloadTask cmsFileDownloadTask = new CmsFileDownloadTask(initSelectors());
		ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
		executorService.schedule(cmsFileDownloadTask, 3, TimeUnit.SECONDS);
	}
	private static Map<HttpUrl, Selector> initSelectors(){
		Map<HttpUrl, Selector> selectorMap = new HashMap<HttpUrl, Selector>();
		selectorMap.put(HttpUrl.parse("http://www.nber.org/data/cbsa-msa-fips-ssa-county-crosswalk.html"),
				new Selector("center", "(Source txt).*"));
		selectorMap.put(HttpUrl.parse("https://www.cms.gov/Research-Statistics-Data-and-Systems/Statistics-Trends-and-Reports/MCRAdvPartDEnrolData/MA-Plan-Directory-Items/MA-Plan-Directory"),
				new Selector("div", "(MA Plan Directory).*(ZIP)"));
		selectorMap.put(HttpUrl.parse("https://www.cms.gov/Research-Statistics-Data-and-Systems/Statistics-Trends-and-Reports/MCRAdvPartDEnrolData/PDP-Plan-Directory-Items/PDP-Plan-Directory"),
				new Selector("div", "PDP Plan Directory.*(ZIP)"));
		selectorMap.put(HttpUrl.parse("https://www.cms.gov/Medicare/Prescription-Drug-Coverage/PrescriptionDrugCovGenIn/PerformanceData"),
				new Selector("div", "Star Ratings Data Table"));
		selectorMap.put(HttpUrl.parse("https://www.cms.gov/Medicare/Prescription-Drug-Coverage/PrescriptionDrugCovContra/RxContracting_FormularyGuidance"),
				new Selector("li", "(CY \\d\\d\\d\\d).*Formulary Reference File"));
		selectorMap.put(HttpUrl.parse("http://download.cms.gov/nppes/NPI_Files.html"),
				new Selector("li", ".*(NPPES Data Dissemination - Monthly Deactivation Update)"));
		selectorMap.put(HttpUrl.parse("https://www.cms.gov/Medicare/Health-Plans/MedicareAdvtgSpecRateStats/Ratebooks-and-Supporting-Data-Items/2020Rates"),
				new Selector("li", "(Regional rates and benchmarks).*"));
		selectorMap.put(HttpUrl.parse("https://www.cms.gov/Medicare/Prescription-Drug-Coverage/PrescriptionDrugCovGenIn/index"),
				new Selector("div", ".*Landscape Source Files|.*Plan and Premium Information for Medicare Plans Offering Part D Coverage"));
		return selectorMap;
	}
}
