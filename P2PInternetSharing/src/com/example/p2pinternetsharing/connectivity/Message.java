package com.example.p2pinternetsharing.connectivity;


public class Message {
	// Codes for different messages.
	final static int APPASSPHRASE = 0;
	
	private String msg;
	private int code;
	public Message(int i, String s) {
		code = i;
		msg = s;
	}
	
	public String getMessageToSend() {
		return this.code + " " + msg;
	}
	
	public static Message createMessage(String s) {
		int code = Integer.parseInt(s.substring(0, s.indexOf(" ")));
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

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}
	
}
