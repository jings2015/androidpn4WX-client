package org.weixiao.client;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Packet;
import org.weixiao.db.GroupMemberItem;
import org.weixiao.db.GroupRequestItem;
import org.weixiao.db.NotificationItem;
import org.weixiao.db.UserGroupItem;
import org.weixiao.iq.ConfigResultIQ;

import android.content.Intent;
import android.util.Log;

public class ConfigPacketListener implements PacketListener {
	private static final String LOGTAG = LogUtil
			.makeLogTag(ConfigPacketListener.class);
	private final XmppManager xmppManager;

	public ConfigPacketListener(XmppManager xmppManager) {
		this.xmppManager = xmppManager;
	}

	@Override
	public void processPacket(Packet packet) {
		Log.d(LOGTAG, "ConfigPacketListener.processPacket()...");
		Log.d(LOGTAG, "packet.toXML()=" + packet.toXML());
		if (packet instanceof ConfigResultIQ) {
			ConfigResultIQ reply = (ConfigResultIQ) packet;
			String method = reply.getMethod();
			String count = reply.getCount();
			
			Log.d(LOGTAG, "method=" + method);
			Intent intent = new Intent(Constants.getActionConfig(method));
			intent.putExtra("method", method);
			intent.putExtra(Constants.CONFIG_COUNT, count);
			
			if ("usergrouplist".equals(method)) {
				// 把数据存到litepal
				UserGroupItem.deleteAll(UserGroupItem.class);
				for (UserGroupItem userGroupItem : reply.getUserGroupItems()) {
					userGroupItem.save();
				}
			}else if("notificationlist4group".equals(method)){
				// 把数据存到litepal
				for(NotificationItem notificationItem:reply.getNotificationItems()){
					notificationItem.save();
				}
			}else if("searchgroup".equals(method)){
				Log.d(LOGTAG, "ConfigPacketListener.processPacket()...searchgroup");
				SearchGroupModel searchGroupModel = reply.getSearchGroupModel();
				intent.putExtra("groupName", searchGroupModel.getGroupName());
				intent.putExtra("groupId", searchGroupModel.getGroupId());
				intent.putExtra("owner", searchGroupModel.getOwner());
				intent.putExtra("info", searchGroupModel.getInfo());
			}else if("grouprequestlist".equals(method)){
				GroupRequestItem.deleteAll(GroupRequestItem.class);
				for (GroupRequestItem groupRequestItem : reply.getGroupRequests()) {
					groupRequestItem.save();
				}
			}else if("groupmemberlist".equals(method)){
				GroupMemberItem.deleteAll(GroupMemberItem.class);
				for (GroupMemberItem groupMemberItem : reply.getGroupMemberItem()) {
					groupMemberItem.save();
				}
			}
			
			xmppManager.getContext().sendBroadcast(intent);
			
		}
	}

}
