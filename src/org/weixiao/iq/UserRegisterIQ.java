package org.weixiao.iq;

import org.jivesoftware.smack.packet.IQ;

import android.util.Log;

public class UserRegisterIQ extends IQ {

	public static final String ELEMENT_NAME = "userregister";
	public static final String NAMESPACE ="androidpn:iq:userregister";
	
	private String account;
	
	private String password;
	
//	private String clientId;
	
	@Override
	public String getChildElementXML() {
		StringBuilder buf = new StringBuilder();
        buf.append("<").append(ELEMENT_NAME).append(" xmlns=\"").append(
                NAMESPACE).append("\">");
        if (account != null) {
            buf.append("<account>").append(account).append("</account>");
        }else{
        	Log.d("TAG", "account = " + account);
        }
        if (password != null) {
        	buf.append("<password>").append(password).append("</password>");
        }else{
        	Log.d("TAG", "password = " + password);
        }
        
        buf.append("</").append(ELEMENT_NAME).append("> ");
        return buf.toString();
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

//	public String getClientId() {
//		return clientId;
//	}
//
//	public void setClientId(String clientId) {
//		this.clientId = clientId;
//	}
	

}
