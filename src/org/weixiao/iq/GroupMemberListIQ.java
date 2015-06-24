package org.weixiao.iq;

import org.jivesoftware.smack.packet.IQ;

import android.util.Log;

public class GroupMemberListIQ extends IQ {
	private String groupId;
	private String account ;
	

	@Override
	public String getChildElementXML() {
		StringBuilder buf = new StringBuilder();
		buf.append("<").append("groupmemberlist").append(" xmlns=\"")
				.append("androidpn:iq:groupmemberlist").append("\">");
		
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
		buf.append("</").append("groupmemberlist").append("> ");
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

}
