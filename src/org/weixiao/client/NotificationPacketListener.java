/*
 * Copyright (C) 2010 Moduad Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.weixiao.client;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Packet;
import org.weixiao.db.NotificationItem;
import org.weixiao.iq.DeliverConfirmIQ;
import org.weixiao.iq.NotificationIQ;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * This class notifies the receiver of incoming notifcation packets
 * asynchronously.
 *
 * @author Sehwan Noh (devnoh@gmail.com)
 */
public class NotificationPacketListener implements PacketListener {

	private static final String LOGTAG = LogUtil
			.makeLogTag(NotificationPacketListener.class);

	private final XmppManager xmppManager;

	public NotificationPacketListener(XmppManager xmppManager) {
		this.xmppManager = xmppManager;
	}

	@Override
	public void processPacket(Packet packet) {
		Log.d(LOGTAG, "NotificationPacketListener.processPacket()...");
		Log.d(LOGTAG, "packet.toXML()=" + packet.toXML());

		if (packet instanceof NotificationIQ) {
			NotificationIQ notification = (NotificationIQ) packet;

			if (notification.getChildElementXML().contains(
					"androidpn:iq:notification")) {
				String notificationId = notification.getNotificationId();
				String title = notification.getTitle();
				String message = notification.getMessage();
				String receiver = notification.getReceiver();
				String sender = notification.getSender();
				String groupId = notification.getGroupId();
				Date createdDate = notification.getCreatedDate();
				
				SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String createdDateStr = sdf.format(createdDate);
				

				Intent intent = new Intent(Constants.ACTION_SHOW_NOTIFICATION);
				intent.putExtra(Constants.NOTIFICATION_ID, notificationId);
				intent.putExtra(Constants.NOTIFICATION_TITLE, title);
				intent.putExtra(Constants.NOTIFICATION_MESSAGE, message);
				intent.putExtra(Constants.NOTIFICATION_RECEIVER, receiver);
				intent.putExtra(Constants.NOTIFICATION_SENDER, sender);
				intent.putExtra(Constants.NOTIFICATION_GROUP_ID, groupId);

				SharedPreferences sharedPrefs = xmppManager.getContext()
						.getSharedPreferences(Constants.SHARED_PREFERENCE_NAME,
								Context.MODE_PRIVATE);
				String account = sharedPrefs.getString(Constants.USER_ACCOUNT,
						"");
				if (!"".equals(account)) {
					if (!account.equals(sender)) {
						xmppManager.getContext().sendBroadcast(intent);
						
						Intent sendIntent = new Intent(Constants.ACTION_RECEIVE_GROUP_MESSAGE);
						xmppManager.getContext().sendBroadcast(sendIntent);
						
						
					}
					NotificationItem notificationItem = new NotificationItem();
					notificationItem.setSender(sender);
					notificationItem.setMessage(message);
					notificationItem.setGroupId(groupId);
					notificationItem.setCreatedDate(createdDateStr);
					notificationItem.setNotificationId(notificationId);
					notificationItem.setReceiver("");
					notificationItem.save();
				}

//				// 发送回执
//				DeliverConfirmIQ deliverConfirmIQ = new DeliverConfirmIQ();
//				deliverConfirmIQ.setUuid(notificationId);
//				deliverConfirmIQ.setType(IQ.Type.SET);
//				xmppManager.getConnection().sendPacket(deliverConfirmIQ);
//				// Log.d("TAG", "has confirmed.");
			}
		}

	}

}
