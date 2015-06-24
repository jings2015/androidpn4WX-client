package org.weixiao.iq;

import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.smack.packet.IQ;
import org.weixiao.client.SearchGroupModel;
import org.weixiao.db.GroupMemberItem;
import org.weixiao.db.GroupRequestItem;
import org.weixiao.db.NotificationItem;
import org.weixiao.db.UserGroupItem;

public class ConfigResultIQ extends IQ {
	private String method;
	private String count;
	private List<UserGroupItem> userGroupItems = new ArrayList<UserGroupItem>();
	private List<NotificationItem> notificationItems = new ArrayList<NotificationItem>();
	private List<GroupRequestItem> groupRequests = new ArrayList<GroupRequestItem>();
	private List<GroupMemberItem> groupMemberItem = new ArrayList<GroupMemberItem>();
	private SearchGroupModel searchGroupModel;

	@Override
	public String getChildElementXML() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public List<UserGroupItem> getUserGroupItems() {
		return userGroupItems;
	}

	public void setUserGroupItems(List<UserGroupItem> userGroupItems) {
		this.userGroupItems = userGroupItems;
	}

	public List<NotificationItem> getNotificationItems() {
		return notificationItems;
	}

	public void setNotificationItems(List<NotificationItem> notificationItems) {
		this.notificationItems = notificationItems;
	}

	public SearchGroupModel getSearchGroupModel() {
		return searchGroupModel;
	}

	public void setSearchGroupModel(SearchGroupModel searchGroupModel) {
		this.searchGroupModel = searchGroupModel;
	}

	public List<GroupRequestItem> getGroupRequests() {
		return groupRequests;
	}

	public void setGroupRequests(List<GroupRequestItem> groupRequests) {
		this.groupRequests = groupRequests;
	}

	public List<GroupMemberItem> getGroupMemberItem() {
		return groupMemberItem;
	}

	public void setGroupMemberItem(List<GroupMemberItem> groupMemberItem) {
		this.groupMemberItem = groupMemberItem;
	}
	

}


