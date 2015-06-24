package org.weixiao.iq;

import org.jivesoftware.smack.packet.IQ;

import android.util.Log;

public class DeliverConfirmIQ extends IQ {
	private String uuid;
	
	@Override
	public String getChildElementXML() {
		 StringBuilder buf = new StringBuilder();
	        buf.append("<").append("deliverconfirm").append(" xmlns=\"").append(
	                "androidpn:iq:deliverconfirm").append("\">");
	        if (uuid != null) {
	            buf.append("<uuid>").append(uuid).append("</uuid>");
	        }else{
	        	Log.d("TAG", "uuid = " + uuid);
	        }
	        buf.append("</").append("deliverconfirm").append("> ");
	        return buf.toString();
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

}
