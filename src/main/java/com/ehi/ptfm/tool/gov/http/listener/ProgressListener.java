package com.ehi.ptfm.tool.gov.http.listener;


public class ProgressListener implements ProgressListen{

	@Override
	public void update(long bytesRead, long contentLength, boolean done) {
		if (!done){
			System.out.print("\r");
			int percent = (int) (100 * bytesRead / contentLength);
			System.out.print(String.format("Progress: %s", percent) + "%");
		}else {
			System.out.println("Transfer completed.");
		}
	}
}
