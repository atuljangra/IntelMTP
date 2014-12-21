package com.example.p2pinternetsharing.connectivity;


public class Message {
	// Codes for different messages.
	public static final String SHADOWDETAILS = "0";
	public static final String APDETAILS = "1";
	public static final String LEADERDETAILS = "2";
	
	private String msg;
	private String code;
	public Message(String i, String s) {
		code = i;
		msg = s;
	}
	
	public String getMessageToSend() {
		return this.code + " " + msg;
	}
	
	public static Message createMessage(String s) {
		String code = s.substring(0, s.indexOf(" "));
		String msg = s.substring(s.indexOf(" ") + 1, s.length() - 1);
		Message m = new Message(code, msg);
		return m;
		
	}
	public String getMsg() {
		return msg;
	}

	public void setMsg(String message) {
		this.msg = message;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
}
