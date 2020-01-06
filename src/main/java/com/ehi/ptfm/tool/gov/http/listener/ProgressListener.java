package com.ehi.ptfm.tool.gov.http.listener;


public class ProgressListener implements ProgressListen{

	@Override
	public void update(long bytesRead, long contentLength, boolean done) {
		if (!done){
			System.out.print("\r");
			int percent = (int) (100 * bytesRead / contentLength);
			System.out.print(String.format("\033[31;4m" + "Progress: %s", percent) + "%" + "\033[0m");
		}else {
			System.out.print("\n");
		}
	}
}
