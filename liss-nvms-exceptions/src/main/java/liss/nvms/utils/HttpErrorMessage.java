package liss.nvms.utils;

import java.util.Date;

public class HttpErrorMessage {
	private Date timestamp;
	private String message;
	
	public HttpErrorMessage() {}
	
	public HttpErrorMessage(Date timestamp, String message)
	{
		this.timestamp = timestamp;
		this.message = message;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}