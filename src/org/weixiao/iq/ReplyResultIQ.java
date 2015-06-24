package org.weixiao.iq;

import org.jivesoftware.smack.packet.IQ;


public class ReplyResultIQ extends IQ{
	private String method;
	private String ecode;
	private String emsg;
	
	@Override
	public String getChildElementXML() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public String getEcode() {
		return ecode;
	}
	public void setEcode(String ecode) {
		this.ecode = ecode;
	}
	public String getEmsg() {
		return emsg;
	}
	public void setEmsg(String emsg) {
		this.emsg = emsg;
	}

	
	
	
}
