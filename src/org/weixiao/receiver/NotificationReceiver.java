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
package org.weixiao.receiver;

import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.smack.packet.IQ;
import org.weixiao.client.Constants;
import org.weixiao.client.LogUtil;
import org.weixiao.client.Notifier;
import org.weixiao.db.NotificationItem;
import org.weixiao.iq.ChangePushable4Group;
import org.weixiao.iq.CreateGroupIQ;
import org.weixiao.iq.GroupMemberListIQ;
import org.weixiao.iq.GroupRequestListIQ;
import org.weixiao.iq.HandleGroupRequestIQ;
import org.weixiao.iq.JoinGroupIQ;
import org.weixiao.iq.NotificationList4GroupIQ;
import org.weixiao.iq.OutMember4GroupIQ;
import org.weixiao.iq.SearchGroupIQ;
import org.weixiao.iq.SendMessage4GroupIQ;
import org.weixiao.iq.UserGroupListIQ;
import org.weixiao.iq.UserLoginIQ;
import org.weixiao.iq.UserRegisterIQ;
import org.weixiao.service.NotificationService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Broadcast receiver that handles push notification messages from the server.
 * This should be registered as receiver in AndroidManifest.xml.
 * 监听消息广播，在NotificationService里被注册
 * 
 * @author Sehwan Noh (devnoh@gmail.com)
 */
public final class NotificationReceiver extends BroadcastReceiver {

	private static final String LOGTAG = LogUtil
			.makeLogTag(NotificationReceiver.class);

	private NotificationService notificationService;

	// public NotificationReceiver() {
	//
	// }

	public NotificationReceiver(NotificationService notificationService) {
		this.notificationService = notificationService;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(LOGTAG, "NotificationReceiver.onReceive()...");
		String action = intent.getAction();
		Log.d(LOGTAG, "action=" + action);

		if (Constants.ACTION_SHOW_NOTIFICATION.equals(action)) {
			String notificationId = intent
					.getStringExtra(Constants.NOTIFICATION_ID);
			String notificationTitle = intent
					.getStringExtra(Constants.NOTIFICATION_TITLE);
			String notificationMessage = intent
					.getStringExtra(Constants.NOTIFICATION_MESSAGE);

			Log.d(LOGTAG, "notificationId=" + notificationId);
			Log.d(LOGTAG, "notificationTitle=" + notificationTitle);
			Log.d(LOGTAG, "notificationMessage=" + notificationMessage);

			Notifier notifier = new Notifier(context);
			notifier.notify(notificationId, notificationTitle,
					notificationMessage);
		} else if (Constants.ACTION_SEND_NOTIFICATION.equals(action)) {
			// .. 把消息提交到服务器

		} else if (Constants.ACTION_CONNECT_LOGIN.equals(action)) {
			//
			if (notificationService.getXmppManager().isAuthenticated()) {
				notificationService.getXmppManager()
						.sendBroadcastConnectLogin();
			}

		} else if (Constants.ACTION_USER_LOGIN.equals(action)) {
			// ..用户登录事件
			String account = intent.getStringExtra("account");
			String password = intent.getStringExtra("password");
			String timestamp = intent.getStringExtra("timestamp");
			String clientId = notificationService.getXmppManager()
					.getClientId();

			UserLoginIQ userLoginIQ = new UserLoginIQ();
			userLoginIQ.setAccount(account);
			userLoginIQ.setPassword(password);
			userLoginIQ.setTimestamp(timestamp);
			userLoginIQ.setClientId(clientId);
			Log.d("TAG", "clientId = " + clientId);

			userLoginIQ.setType(IQ.Type.SET);
			notificationService.getXmppManager().getConnection()
					.sendPacket(userLoginIQ);
		} else if (Constants.ACTION_USER_REGISTER.equals(action)) {
			// .. 用户注册事件

			String account = intent.getStringExtra("account");
			String password = intent.getStringExtra("password");

			UserRegisterIQ userRegisterIQ = new UserRegisterIQ();
			userRegisterIQ.setAccount(account);
			userRegisterIQ.setPassword(password);

			userRegisterIQ.setType(IQ.Type.SET);
			notificationService.getXmppManager().getConnection()
					.sendPacket(userRegisterIQ);

		} else if (Constants.ACTION_CREATE_GROUP.equals(action)) {
			// 创建新群事件
			String account = intent.getStringExtra("account");
			String groupName = intent.getStringExtra("groupName");
			String info = intent.getStringExtra("info");
			CreateGroupIQ createGroupIQ = new CreateGroupIQ();
			createGroupIQ.setAccount(account);
			createGroupIQ.setGroupName(groupName);
			createGroupIQ.setInfo(info);
			createGroupIQ.setType(IQ.Type.SET);
			notificationService.getXmppManager().getConnection()
					.sendPacket(createGroupIQ);
		} else if (Constants.ACTION_USER_GROUP_LIST.equals(action)) {
			// 请求群列表事件
			Log.d(LOGTAG, Constants.ACTION_USER_GROUP_LIST);
			String account = intent.getStringExtra("account");
			UserGroupListIQ userGroupListIQ = new UserGroupListIQ();
			userGroupListIQ.setAccount(account);
			userGroupListIQ.setType(IQ.Type.SET);
			notificationService.getXmppManager().getConnection()
					.sendPacket(userGroupListIQ);
		} else if (Constants.ACTION_NOTIFICATION_LIST_4_GROUP.equals(action)) {
			// 请求（单个）消息列表
			Log.d(LOGTAG, Constants.ACTION_NOTIFICATION_LIST_4_GROUP);
			String receiver = intent.getStringExtra("receiver");
			String groupId = intent.getStringExtra("groupId");
			String count;
			List<String> list = new ArrayList<String>();
			List<NotificationItem> notificationItems = NotificationItem.where(
					"groupId = ?", groupId).find(NotificationItem.class);
			if (notificationItems == null || notificationItems.isEmpty()) {
				count = "0";
			} else {
				count = Integer.toString(notificationItems.size());
				for (int i = 0; i < notificationItems.size(); i++) {
					NotificationItem notificationItem = notificationItems
							.get(i);
					String notificationId = notificationItem
							.getNotificationId();
					list.add(notificationId);
				}
			}

			NotificationList4GroupIQ notificationList4Group = new NotificationList4GroupIQ();
			notificationList4Group.setAccount(receiver);
			notificationList4Group.setGroupId(groupId);
			notificationList4Group.setCount(count);
			notificationList4Group.setNotifications(list);
			notificationList4Group.setType(IQ.Type.SET);
			notificationService.getXmppManager().getConnection()
					.sendPacket(notificationList4Group);

		} else if (Constants.ACTION_SEND_MESSAGE_4_GROUP.equals(action)) {
			// 发送群消息
			Log.d(LOGTAG, Constants.ACTION_SEND_MESSAGE_4_GROUP);
			String sender = intent.getStringExtra("sender");
			String message = intent.getStringExtra("message");
			String groupId = intent.getStringExtra("groupId");
			SendMessage4GroupIQ sendMessage4GroupIQ = new SendMessage4GroupIQ();
			sendMessage4GroupIQ.setSender(sender);
			sendMessage4GroupIQ.setMessage(message);
			sendMessage4GroupIQ.setGroupId(groupId);
			sendMessage4GroupIQ.setType(IQ.Type.SET);
			notificationService.getXmppManager().getConnection()
					.sendPacket(sendMessage4GroupIQ);
		} else if (Constants.ACTION_SEARCH_GROUP.equals(action)) {
			// 查找群
			Log.d(LOGTAG, Constants.ACTION_SEARCH_GROUP);
			String groupId = intent.getStringExtra("groupId");
			Log.d("TAG", groupId);
			SearchGroupIQ searchGroupIQ = new SearchGroupIQ();
			searchGroupIQ.setGroupId(groupId);
			searchGroupIQ.setType(IQ.Type.SET);
			notificationService.getXmppManager().getConnection()
					.sendPacket(searchGroupIQ);

		} else if (Constants.ACTION_JOIN_GROUP.equals(action)) {
			// 申请加入群
			Log.d(LOGTAG, Constants.ACTION_JOIN_GROUP);
			String groupId = intent.getStringExtra("groupId");
			String account = intent.getStringExtra("account");
			String message = intent.getStringExtra("message");
			JoinGroupIQ joinGroupIQ = new JoinGroupIQ();
			joinGroupIQ.setGroupId(groupId);
			joinGroupIQ.setAccount(account);
			joinGroupIQ.setMessage(message);
			joinGroupIQ.setType(IQ.Type.SET);
			notificationService.getXmppManager().getConnection()
					.sendPacket(joinGroupIQ);
		} else if (Constants.ACTION_GROUP_REQUEST_LIST.equals(action)) {
			// 请求验证列表
			Log.d(LOGTAG, Constants.ACTION_GROUP_REQUEST_LIST);
			String account = intent.getStringExtra("account");

			GroupRequestListIQ groupRequestListIQ = new GroupRequestListIQ();
			groupRequestListIQ.setAccount(account);
			groupRequestListIQ.setType(IQ.Type.SET);
			notificationService.getXmppManager().getConnection()
					.sendPacket(groupRequestListIQ);

		} else if (Constants.ACTION_HANDLE_GROUP_REQUEST.equals(action)) {
			// 处理验证请求
			Log.d(LOGTAG, Constants.ACTION_HANDLE_GROUP_REQUEST);
			String requester = intent.getStringExtra("requester");
			String groupId = intent.getStringExtra("groupId");
			String result = intent.getStringExtra("result");

			HandleGroupRequestIQ handleGroupRequestIQ = new HandleGroupRequestIQ();
			handleGroupRequestIQ.setRequester(requester);
			handleGroupRequestIQ.setGroupId(groupId);
			handleGroupRequestIQ.setResult(result);
			handleGroupRequestIQ.setType(IQ.Type.SET);
			notificationService.getXmppManager().getConnection()
					.sendPacket(handleGroupRequestIQ);

		} else if (Constants.ACTION_GROUP_MEMBER_LIST.equals(action)) {
			// 请求群成员列表
			Log.d(LOGTAG, Constants.ACTION_GROUP_MEMBER_LIST);
			String groupId = intent.getStringExtra("groupId");
			String account = intent.getStringExtra("account");

			GroupMemberListIQ groupMemberListIQ = new GroupMemberListIQ();
			groupMemberListIQ.setGroupId(groupId);
			groupMemberListIQ.setAccount(account);
			groupMemberListIQ.setType(IQ.Type.SET);
			notificationService.getXmppManager().getConnection()
					.sendPacket(groupMemberListIQ);

		} else if (Constants.ACTION_CHANGE_PUSHABLE_4_GROUP.equals(action)) {
			// 修改 成员发送消息权限
			Log.d(LOGTAG, Constants.ACTION_CHANGE_PUSHABLE_4_GROUP);
			String groupId = intent.getStringExtra("groupId");
			String account = intent.getStringExtra("account");
			String result = intent.getStringExtra("result");
			String owner = intent.getStringExtra("owner");

			ChangePushable4Group changePushable4Group = new ChangePushable4Group();
			changePushable4Group.setGroupId(groupId);
			changePushable4Group.setAccount(account);
			changePushable4Group.setResult(result);
			changePushable4Group.setOwner(owner);
			changePushable4Group.setType(IQ.Type.SET);
			notificationService.getXmppManager().getConnection()
					.sendPacket(changePushable4Group);

		} else if (Constants.ACTION_OUT_MEMBER_4_GROUP.equals(action)) {
			// 踢出成员
			Log.d(LOGTAG, Constants.ACTION_OUT_MEMBER_4_GROUP);
			String groupId = intent.getStringExtra("groupId");
			String account = intent.getStringExtra("account");
			String owner = intent.getStringExtra("owner");

			OutMember4GroupIQ outMember4GroupIQ = new OutMember4GroupIQ();
			outMember4GroupIQ.setGroupId(groupId);
			outMember4GroupIQ.setAccount(account);
			outMember4GroupIQ.setOwner(owner);
			outMember4GroupIQ.setType(IQ.Type.SET);
			notificationService.getXmppManager().getConnection()
					.sendPacket(outMember4GroupIQ);

		}

		// } else if (Constants.ACTION_NOTIFICATION_CLICKED.equals(action)) {
		// String notificationId = intent
		// .getStringExtra(Constants.NOTIFICATION_ID);
		// String notificationApiKey = intent
		// .getStringExtra(Constants.NOTIFICATION_API_KEY);
		// String notificationTitle = intent
		// .getStringExtra(Constants.NOTIFICATION_TITLE);
		// String notificationMessage = intent
		// .getStringExtra(Constants.NOTIFICATION_MESSAGE);
		// String notificationUri = intent
		// .getStringExtra(Constants.NOTIFICATION_URI);
		//
		// Log.e(LOGTAG, "notificationId=" + notificationId);
		// Log.e(LOGTAG, "notificationApiKey=" + notificationApiKey);
		// Log.e(LOGTAG, "notificationTitle=" + notificationTitle);
		// Log.e(LOGTAG, "notificationMessage=" + notificationMessage);
		// Log.e(LOGTAG, "notificationUri=" + notificationUri);
		//
		// Intent detailsIntent = new Intent();
		// detailsIntent.setClass(context, NotificationDetailsActivity.class);
		// detailsIntent.putExtras(intent.getExtras());
		// // detailsIntent.putExtra(Constants.NOTIFICATION_ID, notificationId);
		// // detailsIntent.putExtra(Constants.NOTIFICATION_API_KEY,
		// notificationApiKey);
		// // detailsIntent.putExtra(Constants.NOTIFICATION_TITLE,
		// notificationTitle);
		// // detailsIntent.putExtra(Constants.NOTIFICATION_MESSAGE,
		// notificationMessage);
		// // detailsIntent.putExtra(Constants.NOTIFICATION_URI,
		// notificationUri);
		// detailsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// detailsIntent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		//
		// try {
		// context.startActivity(detailsIntent);
		// } catch (ActivityNotFoundException e) {
		// Toast toast = Toast.makeText(context,
		// "No app found to handle this request",
		// Toast.LENGTH_LONG);
		// toast.show();
		// }
		//
		// } else if (Constants.ACTION_NOTIFICATION_CLEARED.equals(action)) {
		// //
		// }

	}

}
