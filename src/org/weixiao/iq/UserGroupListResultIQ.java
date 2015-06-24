package org.weixiao.iq;

import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.smack.packet.IQ;
import org.weixiao.db.UserGroupItem;

public class UserGroupListResultIQ extends IQ {
	private String method;
	private String count;
	private List<UserGroupItem> items = new ArrayList<UserGroupItem>();
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
	public List<UserGroupItem> getItems() {
		return items;
	}
	public void setItems(List<UserGroupItem> items) {
		this.items = items;
	}
	

}
