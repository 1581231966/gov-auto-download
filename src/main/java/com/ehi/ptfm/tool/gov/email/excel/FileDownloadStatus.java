package com.ehi.ptfm.tool.gov.email.excel;

public enum FileDownloadStatus{
	SUCCESS("Download Success"),
	FAILED("Download Failed"),
	EXIST("File Is Exists");
	private String status;
	FileDownloadStatus(String status){
		this.status = status;
	}
	public String getStatus(){
		return this.status;
	}
}
