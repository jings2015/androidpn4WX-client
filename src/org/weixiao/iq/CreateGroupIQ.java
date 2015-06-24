package org.weixiao.iq;

import org.jivesoftware.smack.packet.IQ;

import android.util.Log;

public class CreateGroupIQ extends IQ {
	private String account;
	private String groupName;
	private String info;
	
	@Override
	public String getChildElementXML() {
		StringBuilder buf = new StringBuilder();
        buf.append("<").append("creategroup").append(" xmlns=\"").append(
                "androidpn:iq:creategroup").append("\">");
        if (account != null) {
            buf.append("<account>").append(account).append("</account>");
        }else{
        	Log.d("TAG", "account = " + account);
        }
        
        if (groupName != null) {
        	buf.append("<groupName>").append(groupName).append("</groupName>");
        }else{
        	Log.d("TAG", "groupName = " + groupName);
        }
        
        if (info != null) {
        	buf.append("<info>").append(info).append("</info>");
        }else{
        	Log.d("TAG", "info = " + info);
        }
        
        
        buf.append("</").append("creategroup").append("> ");
        return buf.toString();
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

}
