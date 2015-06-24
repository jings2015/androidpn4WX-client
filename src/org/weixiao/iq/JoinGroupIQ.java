package org.weixiao.iq;

import org.jivesoftware.smack.packet.IQ;

import android.util.Log;

public class JoinGroupIQ extends IQ {
	private String groupId;
	private String account;
	private String message;
	@Override
	public String getChildElementXML() {
		StringBuilder buf = new StringBuilder();
		buf.append("<").append("joingroup").append(" xmlns=\"")
				.append("androidpn:iq:joingroup").append("\">");
		
		if (groupId != null) {
			buf.append("<groupId>").append(groupId).append("</groupId>");
		} else {
			Log.d("TAG", "groupId = " + groupId);
		}
		if (account != null) {
			buf.append("<account>").append(account).append("</account>");
		} else {
			Log.d("TAG", "account = " + account);
		}
		if (message != null) {
			buf.append("<message>").append(message).append("</message>");
		} else {
			Log.d("TAG", "message = " + message);
		}

		buf.append("</").append("joingroup").append("> ");
		Log.d("TAG",buf.toString());
		return buf.toString();
	}
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}


}
