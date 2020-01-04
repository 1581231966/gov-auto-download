package com.ehi.ptfm.tool.gov.http.listener;

public interface ProgressListen {
	void update(long bytesRead, long contentLength, boolean done);
}
