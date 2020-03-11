package com.ehi.ptfm.tool.gov;

import com.ehi.ptfm.tool.gov.common.ApplicationProperties;
import com.ehi.ptfm.tool.gov.email.FastEhealthEmail;
import com.ehi.ptfm.tool.gov.email.excel.FileMessage;
import com.ehi.ptfm.tool.gov.html.Selector;
import com.ehi.ptfm.tool.gov.task.CmsFileDownloadTask;
import com.ehi.ptfm.tool.gov.task.ZipCodeDownloadTask;
import com.sargeraswang.util.ExcelUtil.ExcelUtil;
import okhttp3.HttpUrl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.concurrent.*;

public class AppRunner {

	public static void main(String[] args)throws Exception{
		CmsFileDownloadTask cmsFileDownloadTask = new CmsFileDownloadTask(initSelectors());
		ExecutorService executorService = Executors.newScheduledThreadPool(1);
		ArrayList<FileMessage> result = executorService.submit(cmsFileDownloadTask).get();
		result.addAll(executorService.submit(new ZipCodeDownloadTask()).get());
		executorService.shutdown();
		sendMessageAsEmail(result);
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
	private static void sendMessageAsEmail(ArrayList<FileMessage> messages)throws Exception{
		FastEhealthEmail.config(FastEhealthEmail.eHealth(false),
				ApplicationProperties.getProperties("mail.user.name"),
				ApplicationProperties.getProperties("mail.user.password"));
		messages.sort(Comparator.comparing(FileMessage::getFileName));
		Map<String,String> map = new LinkedHashMap<>();
		map.put("fileName","File Name");
		map.put("size","Size");
		map.put("pathFrom","Url Getting");
		map.put("localPath","Local Path");
		map.put("status","Status");
		File file = new File("PUF download tool info.xls");
		OutputStream outputStream =  new FileOutputStream(file);
		ExcelUtil.exportExcel(map, messages, outputStream);
		outputStream.close();
		FastEhealthEmail.subject("This is a testing email")
				.from("PUF Files Download Info")
				.to(ApplicationProperties.getProperties("eng.mail.to"))
				.attach(file)
				.text("Show the file download status.")
				.send();
	}
}
