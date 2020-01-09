package com.ehi.ptfm.tool.gov.html;

public class Selector {
	private String selector;
	private String textRangx;

	public Selector(){

	}
	public Selector(String selector, String textRangx){
		this.selector = selector;
		this.textRangx = textRangx;
	}
	public String getSelector() {
		return selector;
	}

	public void setSelector(String selector) {
		this.selector = selector;
	}

	public String getTextRangx() {
		return textRangx;
	}

	public void setTextRangx(String textRangx) {
		this.textRangx = textRangx;
	}
}
