package com.ehi.ptfm.tool.gov.email;

import com.ehi.ptfm.tool.gov.Application;
import com.ehi.ptfm.tool.gov.common.ApplicationProperties;
import com.ehi.ptfm.tool.gov.email.excel.FileDownloadStatus;
import com.ehi.ptfm.tool.gov.email.excel.FileMessage;
import com.ehi.ptfm.tool.gov.email.exception.SendMailException;
import com.sargeraswang.util.ExcelUtil.ExcelUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class SendEmailTest {

	@Before
	public void init(){
		FastEhealthEmail.config(FastEhealthEmail.eHealth(false),
				ApplicationProperties.getProperties("mail.user.name"),
				ApplicationProperties.getProperties("mail.user.password"));
	}
	@Test
	public void test()throws Exception {
		Map<String,String> map = new LinkedHashMap<>();
		map.put("fileName","File name");
		map.put("size","Size");
		map.put("pathFrom","Url getting");
		map.put("localPath","Local path");
		map.put("status","Status");
		ArrayList<FileMessage> messages = new ArrayList<>();
		messages.add(new FileMessage("ada", "adda", "ada", "asdads", FileDownloadStatus.SUCCESS));
		messages.add(new FileMessage("adaw", "adda", "ada", "asdads", FileDownloadStatus.SUCCESS));

		File file = new File("PUF download tool info.xls");
		OutputStream outputStream =  new FileOutputStream(file);
		ExcelUtil.exportExcel(map, messages, outputStream);
		outputStream.close();
		FastEhealthEmail.subject("This is a testing email")
				.from("PUF Files Download Info")
				.to(ApplicationProperties.getProperties("eng.mail.to"))
				.attach(file)
				.text("信件内容")
				.send();
	}

	@Test
	public void html(){
		String html = "<html xmlns:v=\"urn:schemas-microsoft-com:vml\" \n" +
				"xmlns:o=\"urn:schemas-microsoft-com:office:office\" \n" +
				"xmlns:w=\"urn:schemas-microsoft-com:office:word\" xmlns:m=\"http://schemas.microsoft.com/office/2004/12/omml\"\n" +
				" xmlns=\"http://www.w3.org/TR/REC-html40\">\n" +
				" <head>\n" +
				" <meta http-equiv=Content-Type content=\"text/html; charset=utf-8\">\n" +
				" <meta name=Generator content=\"Microsoft Word 15 (filtered medium)\">\n" +
				" <style>\n" +
				" </head>\n" +
				" <body lang=ZH-CN link=\"#0563C1\" vlink=\"#954F72\">\n" +
				" <div class=WordSection1>\n" +
				" <p class=MsoNormal><span lang=EN-US style='font-size:10.5pt;font-family:DengXian;color:#1F497D'><o:p>&nbsp;</o:p></span></p><p class=MsoNormal><span lang=EN-US style='font-size:10.5pt;font-family:DengXian;color:#1F497D'><o:p>&nbsp;</o:p></span></p><p class=MsoNormal><b><span lang=EN-US style='font-size:11.0pt;font-family:\"Calibri\",sans-serif'>From:</span></b><span lang=EN-US style='font-size:11.0pt;font-family:\"Calibri\",sans-serif'> Auto-Download-PCPF-Tool-Notification &lt;service@ehealthinsurance.com&gt; <br><b>Sent:</b> 2020</span><span style='font-size:11.0pt'>年</span><span lang=EN-US style='font-size:11.0pt;font-family:\"Calibri\",sans-serif'>1</span><span style='font-size:11.0pt'>月</span><span lang=EN-US style='font-size:11.0pt;font-family:\"Calibri\",sans-serif'>2</span><span style='font-size:11.0pt'>日</span><span lang=EN-US style='font-size:11.0pt;font-family:\"Calibri\",sans-serif'> 15:20<br><b>To:</b> Lotus Chen &lt;Lotus.Chen@ehealth.com&gt;<br><b>Cc:</b> Michonne Liao &lt;Michonne.Liao@ehealth.com&gt;; Fion Lin &lt;fion.lin@ehealth.com&gt;; Bess Xiao &lt;Bess.Xiao@ehealth.com&gt;<br><b>Subject:</b> SFTP FILE UPDATE IMFORMATION<o:p></o:p></span></p><p class=MsoNormal><span lang=EN-US><o:p>&nbsp;</o:p></span></p><p class=MsoNormal style='margin-bottom:12.0pt'><span lang=EN-US>Carrier: Harvard Pilgrim &amp; updated files: <br>H1660.zip, H6750.zip<o:p></o:p></span></p></div></body></html>";

		Document doc = Jsoup.parse(html);
		System.out.println(doc);
	}
}
