package org.weixiao.db;

import org.litepal.crud.DataSupport;

public class UserGroupItem extends DataSupport{
	private String groupId;
	private String groupName;
	private String info;
	private String owner;
	private boolean pushable;
	private String message;
	private String createdDate;
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
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
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
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
