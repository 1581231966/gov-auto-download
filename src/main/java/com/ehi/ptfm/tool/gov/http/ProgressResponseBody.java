package com.ehi.ptfm.tool.gov.http;

import com.ehi.ptfm.tool.gov.http.listener.ProgressListen;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.*;

import java.io.IOException;

public class ProgressResponseBody extends ResponseBody {

	private  ResponseBody responseBody;
	private ProgressListen progressListener;
	private BufferedSource bufferedSource;
	public ProgressResponseBody(ResponseBody responseBody, ProgressListen progressListener ){
		this.progressListener = progressListener;
		this.responseBody = responseBody;
	}
	@Override
	public MediaType contentType() {
		return responseBody.contentType();
	}

	@Override
	public long contentLength() {
		return responseBody.contentLength();
	}

	@Override
	public BufferedSource source() {
		if(bufferedSource == null){
			bufferedSource = Okio.buffer(source(responseBody.source()));
		}
		return bufferedSource;
	}
	private Source source(Source source){
		return new ForwardingSource(source) {
			long totalBytesRead = 0L;
			@Override
			public long read(Buffer sink, long byteCount) throws IOException {
				long bytesRead = super.read(sink, byteCount);
				totalBytesRead += bytesRead != -1 ? bytesRead : 0;
				progressListener.update(totalBytesRead, responseBody.contentLength(),bytesRead == -1);
				return bytesRead;
			}
		};
	}
}
