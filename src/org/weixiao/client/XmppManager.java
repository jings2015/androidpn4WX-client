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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Future;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Registration;
import org.jivesoftware.smack.provider.ProviderManager;
import org.weixiao.iq.ConfigResultIQ;
import org.weixiao.iq.NotificationIQ;
import org.weixiao.iq.ReplyResultIQ;
import org.weixiao.iq.UserRegisterIQ;
import org.weixiao.provider.ConfigIQProvider;
import org.weixiao.provider.NotificationIQProvider;
import org.weixiao.provider.ReplyIQProvider;
import org.weixiao.service.NotificationService;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.util.Log;

/**
 * This class is to manage the XMPP connection between client and server.
 * 
 * @author Sehwan Noh (devnoh@gmail.com)
 */
public class XmppManager {

	private static final String LOGTAG = LogUtil.makeLogTag(XmppManager.class);

	private static final String XMPP_RESOURCE_NAME = "AndroidpnClient";

	private Context context;

	private NotificationService.TaskSubmitter taskSubmitter;

	private NotificationService.TaskTracker taskTracker;

	private SharedPreferences sharedPrefs;

	private String xmppHost;

	private int xmppPort;

	private XMPPConnection connection;

	private String clientId;

	private String password;

	private ConnectionListener connectionListener;

	private PacketListener notificationPacketListener;

	private PacketListener replyPacketListener;

	private PacketListener configPacketListener;

	private Handler handler;

	private List<Runnable> taskList;

	private boolean running = false;

	private Future<?> futureTask;

	private Thread reconnection;

	public XmppManager(NotificationService notificationService) {
		context = notificationService;
		taskSubmitter = notificationService.getTaskSubmitter();
		taskTracker = notificationService.getTaskTracker();
		sharedPrefs = notificationService.getSharedPreferences();

		xmppHost = sharedPrefs.getString(Constants.XMPP_HOST, "localhost");
		xmppPort = sharedPrefs.getInt(Constants.XMPP_PORT, 5222);
		clientId = sharedPrefs.getString(Constants.XMPP_CLIENTID, "");
		password = sharedPrefs.getString(Constants.XMPP_PASSWORD, "");

		connectionListener = new PersistentConnectionListener(this);
		notificationPacketListener = new NotificationPacketListener(this);
		replyPacketListener = new ReplyPacketListener(this);
		configPacketListener = new ConfigPacketListener(this);

		handler = new Handler();
		taskList = new ArrayList<Runnable>();
		reconnection = new ReconnectionThread(this);
	}

	public Context getContext() {
		return context;
	}

	// ..
	public void connect() {
		Log.d(LOGTAG, "connect()...");
		submitLoginTask();
	}

	public void disconnect() {
		Log.d(LOGTAG, "disconnect()...");
		terminatePersistentConnection();
	}

	public void terminatePersistentConnection() {
		Log.d(LOGTAG, "terminatePersistentConnection()...");
		Runnable runnable = new Runnable() {

			final XmppManager xmppManager = XmppManager.this;

			public void run() {
				if (xmppManager.isConnected()) {
					Log.d(LOGTAG, "terminatePersistentConnection()... run()");
					xmppManager.getConnection().removePacketListener(
							xmppManager.getNotificationPacketListener());
					xmppManager.getConnection().removePacketListener(
							xmppManager.getReplyPacketListener());
					xmppManager.getConnection().removePacketListener(
							xmppManager.getConfigPacketListener());
					xmppManager.getConnection().disconnect();
				}
				xmppManager.runTask();
			}

		};
		addTask(runnable);
	}

	public XMPPConnection getConnection() {
		return connection;
	}

	public void setConnection(XMPPConnection connection) {
		this.connection = connection;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public ConnectionListener getConnectionListener() {
		return connectionListener;
	}

	public PacketListener getNotificationPacketListener() {
		return notificationPacketListener;
	}

	public PacketListener getReplyPacketListener() {
		return replyPacketListener;
	}

	public PacketListener getConfigPacketListener() {
		return configPacketListener;
	}

	public void startReconnectionThread() {
		synchronized (reconnection) {
			if (reconnection == null || !reconnection.isAlive()) {
				reconnection = new ReconnectionThread(this);
				reconnection.setName("Xmpp Reconnection Thread");
				reconnection.start();
			}
		}
	}

	public Handler getHandler() {
		return handler;
	}

	public void reregisterAccount() {
		removeAccount();
		submitLoginTask();
		runTask();
	}

	public List<Runnable> getTaskList() {
		return taskList;
	}

	public Future<?> getFutureTask() {
		return futureTask;
	}

	public void runTask() {
		Log.d(LOGTAG, "runTask()...");
		synchronized (taskList) {// å é
			running = false;
			futureTask = null;
			if (!taskList.isEmpty()) {
				// ååºä»»å¡æ§è¡
				Runnable runnable = (Runnable) taskList.get(0);
				taskList.remove(0);
				running = true;
				futureTask = taskSubmitter.submit(runnable);
				// ä»»å¡æ°-1
				if (futureTask == null) {
					taskTracker.decrease();
				}
			}
		}
		taskTracker.decrease();
		Log.d(LOGTAG, "runTask()...done");
	}

	private String newRandomUUID() {
		String uuidRaw = UUID.randomUUID().toString();
		return uuidRaw.replaceAll("-", "");
	}

	private boolean isConnected() {
		return connection != null && connection.isConnected();
	}

	public boolean isAuthenticated() {
		return connection != null && connection.isConnected()
				&& connection.isAuthenticated();
	}

	private boolean isRegistered() {
		return sharedPrefs.contains(Constants.XMPP_CLIENTID)
				&& sharedPrefs.contains(Constants.XMPP_PASSWORD);
	}

	private void submitConnectTask() {
		Log.d(LOGTAG, "submitConnectTask()...");
		addTask(new ConnectTask());
	}

	private void submitRegisterTask() {
		Log.d(LOGTAG, "submitRegisterTask()...");
		submitConnectTask();
		addTask(new RegisterTask());
	}

	private void submitLoginTask() {
		Log.d(LOGTAG, "submitLoginTask()...");
		submitRegisterTask();
		addTask(new LoginTask());
	}

	private void addTask(Runnable runnable) {
		Log.d(LOGTAG, "addTask(runnable)...");
		taskTracker.increase();// ä»»å¡æ°+1
		synchronized (taskList) {// å é
			if (taskList.isEmpty() && !running) {
				// å½ä»»å¡åè¡¨ä¸ºç©ºä¸å½åæ²¡æä»»å¡å¨è¿è¡ï¼åç´æ¥æ§è¡è¯¥ä»»å¡
				running = true;
				futureTask = taskSubmitter.submit(runnable);
				// ä»»å¡æ°-1
				if (futureTask == null) {
					taskTracker.decrease();
				}
			} else {
				// ä»»å¡å å°listä¸­ç­å¾æ§è¡
				taskList.add(runnable);
			}
		}
		Log.d(LOGTAG, "addTask(runnable)... done");
	}

	private void removeAccount() {
		Editor editor = sharedPrefs.edit();
		editor.remove(Constants.XMPP_CLIENTID);
		editor.remove(Constants.XMPP_PASSWORD);
		editor.commit();
	}

	/**
	 * ä»»å¡å¼å¸¸æ¶ç§»é¤ç¸åºæ°éçtask
	 */
	private void dropTask(int dropCount) {
		synchronized (taskList) {
			if (taskList.size() >= dropCount) {
				for (int i = 0; i < dropCount; i++) {
					taskList.remove(0);
					taskTracker.decrease();
				}
			}
		}
	}

	/**
	 * A runnable task to connect the server.
	 */
	private class ConnectTask implements Runnable {

		final XmppManager xmppManager;

		private ConnectTask() {
			this.xmppManager = XmppManager.this;
		}

		public void run() {
			Log.i(LOGTAG, "ConnectTask.run()...");

			if (!xmppManager.isConnected()) {
				// Create the configuration for this new connection
				ConnectionConfiguration connConfig = new ConnectionConfiguration(
						xmppHost, xmppPort);
				// connConfig.setSecurityMode(SecurityMode.disabled);
				connConfig.setSecurityMode(SecurityMode.required);
				connConfig.setSASLAuthenticationEnabled(false);
				connConfig.setCompressionEnabled(false);

				XMPPConnection connection = new XMPPConnection(connConfig);
				xmppManager.setConnection(connection);

				try {
					// Connect to the server
					connection.connect();
					Log.i(LOGTAG, "XMPP connected successfully");

					// packet provider
					ProviderManager.getInstance().addIQProvider("notification",
							"androidpn:iq:notification",
							new NotificationIQProvider());
					ProviderManager.getInstance().addIQProvider("userregister",
							"androidpn:iq:userregister", new ReplyIQProvider());
					ProviderManager.getInstance().addIQProvider("userlogin",
							"androidpn:iq:userlogin", new ReplyIQProvider());
					ProviderManager.getInstance().addIQProvider("creategroup",
							"androidpn:iq:creategroup", new ReplyIQProvider());
					ProviderManager.getInstance().addIQProvider(
							"usergrouplist", "androidpn:iq:usergrouplist",
							new ConfigIQProvider());
					ProviderManager.getInstance().addIQProvider(
							"notificationlist4group",
							"androidpn:iq:notificationlist4group",
							new ConfigIQProvider());
					ProviderManager.getInstance().addIQProvider(
							"sendmessage4group",
							"androidpn:iq:sendmessage4group",
							new ReplyIQProvider());
					ProviderManager.getInstance().addIQProvider("searchgroup",
							"androidpn:iq:searchgroup", new ConfigIQProvider());

					ProviderManager.getInstance().addIQProvider("joingroup",
							"androidpn:iq:joingroup", new ReplyIQProvider());
					ProviderManager.getInstance().addIQProvider(
							"grouprequestlist",
							"androidpn:iq:grouprequestlist",
							new ConfigIQProvider());
					ProviderManager.getInstance().addIQProvider(
							"handlegrouprequest",
							"androidpn:iq:handlegrouprequest",
							new ReplyIQProvider());
					
					
					ProviderManager.getInstance().addIQProvider(
							"groupmemberlist",
							"androidpn:iq:groupmemberlist",
							new ConfigIQProvider());
					ProviderManager.getInstance().addIQProvider(
							"changepushable4group",
							"androidpn:iq:changepushable4group",
							new ReplyIQProvider());
					ProviderManager.getInstance().addIQProvider(
							"outmember4group",
							"androidpn:iq:outmember4group",
							new ReplyIQProvider());

					// .. addIQProvider


					xmppManager.runTask();
				} catch (XMPPException e) {
					Log.e(LOGTAG, "XMPP connection failed", e);
					Log.d(LOGTAG, "dropTask(2) in connectTask");
					xmppManager.dropTask(2);
					xmppManager.startReconnectionThread();
					xmppManager.runTask();
				}

			} else {
				Log.i(LOGTAG, "XMPP connected already");
				xmppManager.runTask();
			}
		}
	}

	/**
	 * A runnable task to register a new user onto the server.
	 */
	private class RegisterTask implements Runnable {

		final XmppManager xmppManager;

		boolean isRegisterSucceed;
		boolean hasDropTask;

		private RegisterTask() {
			xmppManager = XmppManager.this;
		}

		public void run() {
			Log.i(LOGTAG, "RegisterTask.run()...");

			if (!xmppManager.isRegistered()) {
				isRegisterSucceed = false;
				hasDropTask = false;
				final String newClientId = newRandomUUID();
				final String newPassword = newRandomUUID();

				Registration registration = new Registration();

				PacketFilter packetFilter = new AndFilter(new PacketIDFilter(
						registration.getPacketID()), new PacketTypeFilter(
						IQ.class));

				PacketListener packetListener = new PacketListener() {

					/*
					 * (non-Javadoc)
					 * 
					 * @see
					 * org.jivesoftware.smack.PacketListener#processPacket(org
					 * .jivesoftware.smack.packet.Packet)
					 */
					public void processPacket(Packet packet) {
						synchronized (xmppManager) {
							Log.d("RegisterTask.PacketListener",
									"processPacket().....");
							Log.d("RegisterTask.PacketListener", "packet="
									+ packet.toXML());

							if (packet instanceof IQ) {
								IQ response = (IQ) packet;
								if (response.getType() == IQ.Type.ERROR) {
									if (!response.getError().toString()
											.contains("409")) {
										Log.e(LOGTAG,
												"Unknown error while registering XMPP account! "
														+ response.getError()
																.getCondition());
									}
								} else if (response.getType() == IQ.Type.RESULT) {
									// è¿æ¥æå
									xmppManager.setClientId(newClientId);
									xmppManager.setPassword(newPassword);
									Log.d(LOGTAG, "clientId=" + newClientId);
									Log.d(LOGTAG, "password=" + newPassword);

									Editor editor = sharedPrefs.edit();
									editor.putString(Constants.XMPP_CLIENTID,
											newClientId);
									editor.putString(Constants.XMPP_PASSWORD,
											newPassword);
									editor.commit();
									isRegisterSucceed = true;
									Log.i(LOGTAG,
											"Account registered successfully");
									if (!hasDropTask) {
										xmppManager.runTask();
									}
								}
							}
						}
					}
				};

				connection.addPacketListener(packetListener, packetFilter);

				registration.setType(IQ.Type.SET);
				// registration.setTo(xmppHost);
				// Map<String, String> attributes = new HashMap<String,
				// String>();
				// attributes.put("username", rUsername);
				// attributes.put("password", rPassword);
				// registration.setAttributes(attributes);
				registration.addAttribute("clientId", newClientId);
				registration.addAttribute("password", newPassword);
				connection.sendPacket(registration);

				try {
					// ç­å¾10ç§
					Thread.sleep(10 * 1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				synchronized (xmppManager) {
					if (!isRegisterSucceed) {
						Log.d(LOGTAG, "dropTask(1) in register");
						xmppManager.dropTask(1);
						xmppManager.runTask();
						xmppManager.startReconnectionThread();
						hasDropTask = true;
					}
				}

			} else {
				Log.i(LOGTAG, "Account registered already");
				xmppManager.runTask();
			}
		}
	}

	/**
	 * A runnable task to log into the server.
	 */
	private class LoginTask implements Runnable {

		final XmppManager xmppManager;

		private LoginTask() {
			this.xmppManager = XmppManager.this;
		}

		public void run() {
			Log.i(LOGTAG, "LoginTask.run()...");

			if (!xmppManager.isAuthenticated()) {
				Log.d(LOGTAG, "clientId=" + clientId);
				Log.d(LOGTAG, "password=" + password);

				try {
					xmppManager.getConnection().login(
							xmppManager.getClientId(),
							xmppManager.getPassword(), XMPP_RESOURCE_NAME);
					Log.d(LOGTAG, "Loggedn in successfully");

					// connection listener
					if (xmppManager.getConnectionListener() != null) {
						xmppManager.getConnection().addConnectionListener(
								xmppManager.getConnectionListener());
					}

					// .. 监听拦截器
					// packet filter
					PacketFilter packetFilter = new PacketTypeFilter(
							NotificationIQ.class);
					// packet listener
					PacketListener packetListener = xmppManager
							.getNotificationPacketListener();
					connection.addPacketListener(packetListener, packetFilter);
					// reply 监听
					PacketFilter replyPacketFilter = new PacketTypeFilter(
							ReplyResultIQ.class);
					PacketListener replyPacketListener = xmppManager
							.getReplyPacketListener();
					connection.addPacketListener(replyPacketListener,
							replyPacketFilter);
					// config 监听
					PacketFilter configPacketFilter = new PacketTypeFilter(
							ConfigResultIQ.class);
					PacketListener configPacketListener = xmppManager
							.getConfigPacketListener();
					connection.addPacketListener(configPacketListener,
							configPacketFilter);

					connection.startHeartBeat();
					sendBroadcastConnectLogin();

				} catch (XMPPException e) {
					Log.e(LOGTAG, "LoginTask.run()... xmpp error");
					Log.e(LOGTAG, "Failed to login to xmpp server. Caused by: "
							+ e.getMessage());
					String INVALID_CREDENTIALS_ERROR_CODE = "401";
					String errorMessage = e.getMessage();
					if (errorMessage != null
							&& errorMessage
									.contains(INVALID_CREDENTIALS_ERROR_CODE)) {
						xmppManager.reregisterAccount();
						return;
					}
					xmppManager.startReconnectionThread();

				} catch (Exception e) {
					Log.e(LOGTAG, "LoginTask.run()... other error");
					Log.e(LOGTAG, "Failed to login to xmpp server. Caused by: "
							+ e.getMessage());
					xmppManager.startReconnectionThread();
				} finally {
					xmppManager.runTask();
				}

			} else {
				Log.i(LOGTAG, "Logged in already");
				sendBroadcastConnectLogin();
				xmppManager.runTask();
			}

		}
	}

	public void sendBroadcastConnectLogin() {
		// 连接为登录成功状态，发送广播
		Intent intent = new Intent(Constants.ACTION_CONNECT_LOGIN_ACK);
		intent.putExtra("ecode", "100");
		context.sendBroadcast(intent);
	}
}
