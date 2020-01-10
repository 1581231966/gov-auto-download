package com.ehi.ptfm.tool.gov.email;


import com.ehi.ptfm.tool.gov.common.ApplicationProperties;

import javax.mail.Session;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class FastEhealthEmail {

	private static String user;
	private static Session session;

	private MimeMessage message;
	private String text;
	private String html;
	private List<MimeBodyPart> attachments = new ArrayList<>();

	private FastEhealthEmail(){

	}

	public static Properties defaultConfig(Boolean debug){
		Properties properties = new Properties();
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.ssl.enable", "true");
		properties.put("mail.transport.protocol", "smtp");
		properties.put("mail.debug", null != debug ? debug.toString() : "false");
		properties.put("mail.smtp.timeout", "10000");
		properties.put("mail.smtp.port", ApplicationProperties.getProperties("mail.smtp.port"));
		return properties;
	}


}
