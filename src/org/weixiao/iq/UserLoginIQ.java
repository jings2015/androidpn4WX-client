package org.weixiao.iq;

import org.jivesoftware.smack.packet.IQ;

import android.util.Log;

public class UserLoginIQ extends IQ {

	private String account;
	private String password;
	private String timestamp;
	private String clientId;

	@Override
	public String getChildElementXML() {
		StringBuilder buf = new StringBuilder();
		buf.append("<").append("userlogin").append(" xmlns=\"")
				.append("androidpn:iq:userlogin").append("\">");
		if (account != null) {
			buf.append("<account>").append(account).append("</account>");
		} else {
			Log.d("TAG", "account = " + account);
		}
		if (password != null) {
			buf.append("<password>").append(password).append("</password>");
		} else {
			Log.d("TAG", "password = " + password);
		}
		if (timestamp != null) {
			buf.append("<timestamp>").append(timestamp).append("</timestamp>");
		} else {
			Log.d("TAG", "timestamp = " + timestamp);
		}
		if (clientId != null) {
			buf.append("<clientId>").append(clientId).append("</clientId>");
		} else {
			Log.d("TAG", "clientId = " + clientId);
		}

		buf.append("</").append("userlogin").append("> ");
		Log.d("TAG",buf.toString());
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

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

}
