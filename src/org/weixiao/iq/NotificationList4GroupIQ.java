package org.weixiao.iq;

import java.util.List;

import org.jivesoftware.smack.packet.IQ;

import android.util.Log;

public class NotificationList4GroupIQ extends IQ {
	private String account;
	private String groupId;
	private String count;
	private List<String> notifications;

	@Override
	public String getChildElementXML() {
		StringBuilder buf = new StringBuilder();
		buf.append("<").append("notificationlist4group").append(" xmlns=\"")
				.append("androidpn:iq:notificationlist4group").append("\">");
		if (account != null) {
			buf.append("<account>").append(account).append("</account>");
		} else {
			Log.d("TAG", "account = " + account);
		}
		if (groupId != null) {
			buf.append("<groupId>").append(groupId).append("</groupId>");
		} else {
			Log.d("TAG", "groupId = " + groupId);
		}
		if (count != null) {
			buf.append("<count>").append(count).append("</count>");
			if (notifications != null && notifications.size() > 0) {
				for (int i = 0; i < Integer.parseInt(count); i++) {
					buf.append("<notificationId" + i + ">")
							.append(notifications.get(i))
							.append("</notificationId" + i + ">");
				}
			}
		} else {
			Log.d("TAG", "count = " + count);
		}

		buf.append("</").append("notificationlist4group").append("> ");
		Log.d("TAG",buf.toString());
		return buf.toString();
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public List<String> getNotifications() {
		return notifications;
	}

	public void setNotifications(List<String> notifications) {
		this.notifications = notifications;
	}
	
	

}
