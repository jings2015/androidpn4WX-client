package org.weixiao.iq;

import org.jivesoftware.smack.packet.IQ;

public class LoginIQ extends IQ {
	private String account;
	
	private String password;
	
	private String timestamp;

	@Override
	public String getChildElementXML() {
		// TODO Auto-generated method stub
		return null;
	}

}
