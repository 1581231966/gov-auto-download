package com.ehi.ptfm.tool.gov.email;


import com.ehi.ptfm.tool.gov.common.ApplicationProperties;
import com.ehi.ptfm.tool.gov.email.exception.SendMailException;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.util.*;

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
		properties.put("mail.smtp.timeout", ApplicationProperties.getProperties("mail.smtp.timeout"));
		properties.put("mail.smtp.port", ApplicationProperties.getProperties("mail.smtp.port"));
		return properties;
	}

	public static Properties eHealth(boolean debug){
		Properties props = defaultConfig(debug);
		props.put("mail.smtp.host", ApplicationProperties.getProperties("mail.smtp.host"));
		return props;
	}

	public static void config(Properties props, String username, String password){
		props.put("username", username);
		props.put("password", password);
		config(props);
	}

	public static void config(Properties props){
		final String username = props.getProperty("username");
		final String password = props.getProperty("password");
		user = username;
		session =Session.getDefaultInstance(props, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});
	}

	public static FastEhealthEmail subject(String subject) throws SendMailException {
		FastEhealthEmail email = new FastEhealthEmail();
		email.message = new MimeMessage(session);
		try {
			email.message.setSubject(subject);
		} catch (MessagingException e) {
			throw new SendMailException(e);
		}
		return email;
	}

	public FastEhealthEmail from(String nickName) throws SendMailException{
		return from(nickName, user);
	}

	public FastEhealthEmail from(String nickName, String from) throws SendMailException{
		try {
			String encodeNickName = MimeUtility.encodeText(nickName);
			message.setFrom(new InternetAddress(encodeNickName + "<" + from + ">"));
		} catch (Exception e) {
			throw new SendMailException(e);
		}
		return this;
	}

	public FastEhealthEmail to(String... to)throws SendMailException{
		try {
			return addRecipients(to, Message.RecipientType.TO);
		} catch (MessagingException e) {
			throw new SendMailException(e);
		}
	}

	public FastEhealthEmail to(String to) throws SendMailException{
		try {
			return addRecipient(to, Message.RecipientType.TO);
		} catch (MessagingException e) {
			throw new SendMailException(e);
		}
	}

	public FastEhealthEmail cc(String... cc) throws SendMailException{
		try {
			return addRecipients(cc, Message.RecipientType.CC);
		} catch (MessagingException e) {
			throw new SendMailException(e);
		}
	}

	public FastEhealthEmail cc(String cc) throws SendMailException{
		try {
			return addRecipient(cc, Message.RecipientType.CC);
		} catch (MessagingException e) {
			throw new SendMailException(e);
		}
	}

	public FastEhealthEmail bcc(String... bcc) throws SendMailException{
		try {
			return addRecipients(bcc, Message.RecipientType.BCC);
		} catch (MessagingException e) {
			throw new SendMailException(e);
		}
	}

	public FastEhealthEmail bcc(String bcc) throws SendMailException{
		try {
			return addRecipient(bcc, Message.RecipientType.BCC);
		} catch (MessagingException e) {
			throw new SendMailException(e);
		}
	}

	public FastEhealthEmail text(String text){
		this.text = text;
		return this;
	}

	public FastEhealthEmail html(String html){
		this.html = html;
		return this;
	}

	public FastEhealthEmail attach(File file) throws SendMailException{
		attachments.add(createAttachment(file, null));
		return this;
	}

	public FastEhealthEmail attach(File file, String fileName) throws SendMailException{
		attachments.add(createAttachment(file, fileName));
		return this;
	}

	public void send() throws SendMailException{
		if (text == null && html == null){
			throw new IllegalArgumentException("At least one context has to be provided: Text or html");
		}

		MimeMultipart cover;
		boolean usingAlternative =false;
		boolean hasAttachments =attachments.size() > 0;

		try{
			if (text != null && html == null){
				cover = new MimeMultipart("mixed");
				cover.addBodyPart(textPart());
			} else if (text == null) {
				// HTML ONLY
				cover = new MimeMultipart("mixed");
				cover.addBodyPart(htmlPart());
			} else {
				// HTML + TEXT
				cover = new MimeMultipart("alternative");
				cover.addBodyPart(textPart());
				cover.addBodyPart(htmlPart());
				usingAlternative = true;
			}
			MimeMultipart content = cover;
			if (usingAlternative && hasAttachments) {
				content = new MimeMultipart("mixed");
				content.addBodyPart(toBodyPart(cover));
			}

			for (MimeBodyPart attachment : attachments) {
				content.addBodyPart(attachment);
			}

			message.setContent(content);
			message.setSentDate(new Date());
			Transport.send(message);
		}catch (Exception e){
			throw new SendMailException(e);
		}
	}

	private MimeBodyPart createAttachment(File file, String fileName) throws SendMailException{
		MimeBodyPart attachment = new MimeBodyPart();
		FileDataSource dataSource = new FileDataSource(file);

		try {
			attachment.setDataHandler(new DataHandler(dataSource));
			attachment.setFileName(null == fileName?MimeUtility.encodeText(dataSource.getName()):MimeUtility.encodeText(fileName));
		} catch (Exception e) {
			throw new SendMailException(e);
		}
		return attachment;
	}

	private MimeBodyPart toBodyPart(MimeMultipart cover) throws MessagingException {
		MimeBodyPart wrap = new MimeBodyPart();
		wrap.setContent(cover);
		return wrap;
	}

	private MimeBodyPart textPart() throws MessagingException {
		MimeBodyPart bodyPart = new MimeBodyPart();
		bodyPart.setText(text);
		return bodyPart;
	}

	private MimeBodyPart htmlPart() throws MessagingException {
		MimeBodyPart bodyPart = new MimeBodyPart();
		bodyPart.setContent(html, "text/html; charset=utf-8");
		return bodyPart;
	}

	private FastEhealthEmail addRecipients(String[] recipients, Message.RecipientType type) throws MessagingException{
		String result = Arrays.asList(recipients).toString().replace("(^\\[|\\]$)", "").replace(", ", ",");
		message.setRecipients(type, InternetAddress.parse(result));
		return this;
	}

	private FastEhealthEmail addRecipient(String recipient, Message.RecipientType type) throws MessagingException{
		message.setRecipients(type, InternetAddress.parse(recipient.replace(";", ",")));
		return this;
	}
}
