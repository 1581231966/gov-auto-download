package com.ehi.ptfm.tool.gov.email.exception;

public class SendMailException extends Exception {

	public SendMailException(){

	}

	public SendMailException(String message){
		super(message);
	}

	public SendMailException(Throwable cause){
		super(cause);
	}
}
