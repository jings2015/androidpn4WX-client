package org.weixiao.iq;

import org.jivesoftware.smack.packet.IQ;

import android.util.Log;

public class SearchGroupIQ extends IQ {
	private String groupId;
	@Override
	public String getChildElementXML() {
		StringBuilder buf = new StringBuilder();
		buf.append("<").append("searchgroup").append(" xmlns=\"")
				.append("androidpn:iq:searchgroup").append("\">");
		
		if (groupId != null) {
			buf.append("<groupId>").append(groupId).append("</groupId>");
		} else {
			Log.d("TAG", "groupId = " + groupId);
		}

		buf.append("</").append("searchgroup").append("> ");
		Log.d("TAG",buf.toString());
		return buf.toString();
	}
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	
}
