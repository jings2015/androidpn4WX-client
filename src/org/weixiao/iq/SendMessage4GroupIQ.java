package org.weixiao.iq;

import org.jivesoftware.smack.packet.IQ;

import android.util.Log;

public class SendMessage4GroupIQ extends IQ {
	private String sender;
	private String message;
	private String groupId;
	
	@Override
	public String getChildElementXML() {
		StringBuilder buf = new StringBuilder();
		buf.append("<").append("sendmessage4group").append(" xmlns=\"")
				.append("androidpn:iq:sendmessage4group").append("\">");
		if (sender != null) {
			buf.append("<sender>").append(sender).append("</sender>");
		} else {
			Log.d("TAG", "sender = " + sender);
		}
		if (message != null) {
			buf.append("<message>").append(message).append("</message>");
		} else {
			Log.d("TAG", "message = " + message);
		}
		if (groupId != null) {
			buf.append("<groupId>").append(groupId).append("</groupId>");
		} else {
			Log.d("TAG", "groupId = " + groupId);
		}

		buf.append("</").append("sendmessage4group").append("> ");
		Log.d("TAG",buf.toString());
		return buf.toString();
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	

}
