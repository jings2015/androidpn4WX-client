package org.weixiao.provider;

import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.weixiao.client.LogUtil;
import org.weixiao.client.SearchGroupModel;
import org.weixiao.db.GroupMemberItem;
import org.weixiao.db.GroupRequestItem;
import org.weixiao.db.NotificationItem;
import org.weixiao.db.UserGroupItem;
import org.weixiao.iq.ConfigResultIQ;
import org.xmlpull.v1.XmlPullParser;

import android.util.Log;

public class ConfigIQProvider implements IQProvider {
	private static final String LOGTAG = LogUtil
			.makeLogTag(ConfigIQProvider.class);

	@Override
	public IQ parseIQ(XmlPullParser parser) throws Exception {
		Log.d(LOGTAG, "ConfigIQProvider.parseIQ()...");
		ConfigResultIQ reply = new ConfigResultIQ();
		String method = "";
		int count = 0;
		for (boolean done = false; !done;) {
			int eventType = parser.next();
			if (eventType == 2) {
				if ("method".equals(parser.getName())) {
					method = parser.nextText();
					reply.setMethod(method);
				}
				if ("count".equals(parser.getName())) {
					count = Integer.parseInt(parser.nextText());
					reply.setCount(Integer.toString(count));

					if ("usergrouplist".equals(method)) {
						// 用户获取群列表
						List<UserGroupItem> userGroupItems = new ArrayList<UserGroupItem>();
						for (int i = 0; i < count; i++) {
							UserGroupItem userGroupItem = new UserGroupItem();
							for (done = false; !done;) {
								eventType = parser.next();
								if (eventType == 3
										&& reply.getMethod().equals(
												parser.getName())) {
									done = true;
								}
								if (("groupId" + i).equals(parser.getName())) {
									userGroupItem.setGroupId(parser.nextText());
								}
								if (("groupName" + i).equals(parser.getName())) {
									userGroupItem.setGroupName(parser
											.nextText());
								}
								if (("info" + i).equals(parser.getName())) {
									userGroupItem.setInfo(parser.nextText());
								}
								if (("owner" + i).equals(parser.getName())) {
									userGroupItem.setOwner(parser.nextText());
								}
								if (("pushable" + i).equals(parser.getName())) {
									boolean pushable = "1".equals(parser
											.nextText()) ? true : false;
									userGroupItem.setPushable(pushable);
								}
								if (("message" + i).equals(parser.getName())) {
									userGroupItem.setMessage(parser.nextText());
								}
								if (("createdDate" + i)
										.equals(parser.getName())) {
									userGroupItem.setCreatedDate(parser
											.nextText());
									break;
								}
							}
							userGroupItems.add(userGroupItem);
							if (done) {
								break;
							}
						}
						reply.setUserGroupItems(userGroupItems);
					} else if ("notificationlist4group".equals(method)) {
						// 获取（单个）群消息列表
						List<NotificationItem> notificationItems = new ArrayList<NotificationItem>();
						for (int i = 0; i < count; i++) {
							NotificationItem notificationItem = new NotificationItem();
							for (done = false; !done;) {
								eventType = parser.next();
								if (eventType == 3
										&& reply.getMethod().equals(
												parser.getName())) {
									done = true;
								}
								if(eventType ==2){
									
								}
								if (("sender" + i).equals(parser.getName())) {
									notificationItem.setSender(parser
											.nextText());
								}
								if (("receiver" + i).equals(parser.getName())) {
									notificationItem.setReceiver(parser
											.nextText());
								}
								if (("notificationId" + i).equals(parser
										.getName())) {
									notificationItem.setNotificationId(parser
											.nextText());
								}
								if (("message" + i).equals(parser.getName())) {
									notificationItem.setMessage(parser
											.nextText());
								}
								if (("groupId" + i).equals(parser.getName())) {
									notificationItem.setGroupId(parser
											.nextText());
								}
								if (("createdDate" + i)
										.equals(parser.getName())) {
									notificationItem.setCreatedDate(parser
											.nextText());
									break;
								}
							}
							notificationItems.add(notificationItem);
							if (done) {
								break;
							}
						}
						reply.setNotificationItems(notificationItems);

					} else if ("searchgroup".equals(method)) {
						Log.d(LOGTAG,
								"ConfigIQProvider.parseIQ()...searchgroup");
						SearchGroupModel searchGroupModel = new SearchGroupModel();
						for (done = false; !done;) {
							eventType = parser.next();
							if (eventType == 2) {

								if ("groupName".equals(parser.getName())) {
									searchGroupModel.setGroupName(parser
											.nextText());
								}
								if ("groupId".equals(parser.getName())) {
									searchGroupModel.setGroupId(parser
											.nextText());
								}
								if ("owner".equals(parser.getName())) {
									searchGroupModel
											.setOwner(parser.nextText());
								}
								if ("info".equals(parser.getName())) {
									searchGroupModel.setInfo(parser.nextText());
								}

							} else if (eventType == 3
									&& reply.getMethod().equals(
											parser.getName())) {
								done = true;
							}
						}
						reply.setSearchGroupModel(searchGroupModel);
					} else if ("grouprequestlist".equals(method)) {
						// 获取
						List<GroupRequestItem> groupRequestItems = new ArrayList<GroupRequestItem>();
						
						for (int i = 0; i < count; i++) {
							GroupRequestItem groupRequestItem = new GroupRequestItem();
							for (done = false; !done;) {
								eventType = parser.next();
								if (eventType == 3
										&& reply.getMethod().equals(
												parser.getName())) {
									done = true;
								}
								if (("requester" + i).equals(parser.getName())) {
									groupRequestItem.setRequester(parser
											.nextText());
								}
								if (("groupName" + i).equals(parser.getName())) {
									groupRequestItem.setGroupName(parser
											.nextText());
								}
								if (("groupId" + i).equals(parser.getName())) {
									groupRequestItem.setGroupId(parser
											.nextText());
								}
								if (("message" + i).equals(parser.getName())) {
									groupRequestItem.setMessage(parser
											.nextText());
								}

							}
							groupRequestItems.add(groupRequestItem);
							if (done) {
								break;
							}
						}
						reply.setGroupRequests(groupRequestItems);

					} else if ("groupmemberlist".equals(method)) {
						Log.d(LOGTAG,
								"ConfigIQProvider.parseIQ()...groupmemberlist");
						Log.d("TAG","count = " + count);
						List<GroupMemberItem> groupMemberItems = new ArrayList<GroupMemberItem>();
						for (int i = 0; i < count; i++) {
							Log.d("TAG","i = " +i);
							GroupMemberItem groupMemberItem = new GroupMemberItem();
							for (done = false; !done;) {
								eventType = parser.next();
								if(eventType ==2){
									
									if (("account" + i).equals(parser.getName())) {
										groupMemberItem.setAccount(parser
												.nextText());
									}
									if (("groupId" + i).equals(parser.getName())) {
										groupMemberItem.setGroupId(parser
												.nextText());
									}
									if (("pushable" + i).equals(parser.getName())) {
										String pushable = parser.nextText();
										boolean pushablebool = "1".equals(pushable) ? true
												: false;
										groupMemberItem.setPushable(pushablebool);
									}
									if (("owner" + i).equals(parser.getName())) {
										groupMemberItem.setOwner(parser.nextText());
										break;
									}
								}else if (eventType == 3
										&& reply.getMethod().equals(
												parser.getName())) {
									Log.d("TAG","done = true \n + eventType = " + eventType );
									done = true;
								}

							}
							groupMemberItems.add(groupMemberItem);
							if (done) {
								break;
							}
						}
						Log.d("TAG", "groupMemberItems.size()=" + groupMemberItems.size());
						reply.setGroupMemberItem(groupMemberItems);

					} else {

						// ..其他Config

					}
				}
			} else if (eventType == 3
					&& reply.getMethod().equals(parser.getName())) {
				done = true;
			}
		}
		return reply;
	}

}
