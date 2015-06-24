package org.weixiao.iq;

import org.jivesoftware.smack.packet.IQ;

import android.util.Log;

public class UserGroupListIQ extends IQ {
	private String account;
	@Override
	public String getChildElementXML() {
		StringBuilder buf = new StringBuilder();
        buf.append("<").append("usergrouplist").append(" xmlns=\"").append(
                "androidpn:iq:usergrouplist").append("\">");
        if (account != null) {
            buf.append("<account>").append(account).append("</account>");
        }else{
        	Log.d("TAG", "account = " + account);
        }
        
        buf.append("</").append("usergrouplist").append("> ");
        Log.d("TAG",buf.toString());
        return buf.toString();
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	

}
