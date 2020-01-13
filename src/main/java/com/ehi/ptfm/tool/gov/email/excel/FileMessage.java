package com.ehi.ptfm.tool.gov.email.excel;


import com.sargeraswang.util.ExcelUtil.ExcelCell;

public class FileMessage {

	@ExcelCell(index = 0)
	private String fileName;
	@ExcelCell(index = 1)
	private String size;
	@ExcelCell(index = 2)
	private String pathFrom;
	@ExcelCell(index = 3)
	private String localPath;
	@ExcelCell(index = 4)
	private FileDownloadStatus status;

	public FileMessage(){

	}
	public FileMessage(String fileName, String size, String pathFrom, String localPath, FileDownloadStatus status){
		this.fileName = fileName;
		this.size = size;
		this.pathFrom = pathFrom;
		this.localPath = localPath;
		this.status = status;
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
