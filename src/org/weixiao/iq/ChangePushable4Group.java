package org.weixiao.iq;

import org.jivesoftware.smack.packet.IQ;

import android.util.Log;

public class ChangePushable4Group extends IQ {
	private String  groupId;
	private String account;
	private String result;
	private String owner;
	
	@Override
	public String getChildElementXML() {
		StringBuilder buf = new StringBuilder();
		buf.append("<").append("changepushable4group").append(" xmlns=\"")
				.append("androidpn:iq:changepushable4group").append("\">");
		
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
		if (result != null) {
			buf.append("<result>").append(result).append("</result>");
		} else {
			Log.d("TAG", "result = " + result);
		}
		if (owner != null) {
			buf.append("<owner>").append(owner).append("</owner>");
		} else {
			Log.d("TAG", "owner = " + owner);
		}
		buf.append("</").append("changepushable4group").append("> ");
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

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

}
