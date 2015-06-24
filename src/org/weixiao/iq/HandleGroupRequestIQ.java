package org.weixiao.iq;

import org.jivesoftware.smack.packet.IQ;

import android.util.Log;

public class HandleGroupRequestIQ extends IQ {
	private String requester;
	private String groupId;
	private String result;

	@Override
	public String getChildElementXML() {
		StringBuilder buf = new StringBuilder();
		buf.append("<").append("handlegrouprequest").append(" xmlns=\"")
				.append("androidpn:iq:handlegrouprequest").append("\">");
		
		if (requester != null) {
			buf.append("<requester>").append(requester).append("</requester>");
		} else {
			Log.d("TAG", "requester = " + requester);
		}
		if (groupId != null) {
			buf.append("<groupId>").append(groupId).append("</groupId>");
		} else {
			Log.d("TAG", "groupId = " + groupId);
		}
		if (result != null) {
			buf.append("<result>").append(result).append("</result>");
		} else {
			Log.d("TAG", "result = " + result);
		}

		buf.append("</").append("handlegrouprequest").append("> ");
		Log.d("TAG",buf.toString());
		return buf.toString();
	}

	public String getRequester() {
		return requester;
	}

	public void setRequester(String requester) {
		this.requester = requester;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}
	

}
