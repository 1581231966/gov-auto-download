package com.ehi.ptfm.tool.gov.email.excel;


public class FileMessage {

	private String fileName;
	private String size;
	private String pathFrom;
	private String localPath;
	private FileDownloadStatus status;

	public FileMessage(){

	}
	public FileMessage(String fileName, String size, String pathFrom, String localPath){
		this.fileName = fileName;
		this.size = size;
		this.pathFrom = pathFrom;
		this.localPath = localPath;
	}
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getPathFrom() {
		return pathFrom;
	}

	public void setPathFrom(String pathFrom) {
		this.pathFrom = pathFrom;
	}

	public String getLocalPath() {
		return localPath;
	}

	public void setLocalPath(String localPath) {
		this.localPath = localPath;
	}
	public FileDownloadStatus getStatus() {
		return status;
	}

	public void setStatus(FileDownloadStatus status) {
		this.status = status;
	}
}
