package org.weixiao.db;

import org.litepal.crud.DataSupport;

public class GroupMemberItem extends DataSupport {
	private String account;
	private String groupId;
	private boolean pushable;
	private String owner;
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
	public boolean isPushable() {
		return pushable;
	}
	public void setPushable(boolean pushable) {
		this.pushable = pushable;
	}
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	
}
