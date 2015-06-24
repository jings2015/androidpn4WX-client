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

/**
 * Static constants for this package.
 * 
 * ack:客户端内应答
 * reply:服务器返回的应答结果（请求成功失败状态信息）
 * config:服务器返回的数据结果（包含请求数据）
 */
public class Constants {

    public static final String SHARED_PREFERENCE_NAME = "client_preferences";

    // PREFERENCE KEYS

    public static final String CALLBACK_ACTIVITY_PACKAGE_NAME = "CALLBACK_ACTIVITY_PACKAGE_NAME";

    public static final String CALLBACK_ACTIVITY_CLASS_NAME = "CALLBACK_ACTIVITY_CLASS_NAME";

//    public static final String API_KEY = "API_KEY";

    public static final String VERSION = "VERSION";

    public static final String XMPP_HOST = "XMPP_HOST";

    public static final String XMPP_PORT = "XMPP_PORT";

//    public static final String XMPP_USERNAME = "XMPP_USERNAME";
    
    public static final String XMPP_CLIENTID = "XMPP_CLIENTID";

    public static final String XMPP_PASSWORD = "XMPP_PASSWORD";

    // public static final String USER_KEY = "USER_KEY";

    public static final String DEVICE_ID = "DEVICE_ID";
    
    public static final String USER_ACCOUNT = "USER_ACCOUNT";
    
    public static final String USER_PASSWORD = "USER_PASSWORD";
    		
    public static final String EMULATOR_DEVICE_ID = "EMULATOR_DEVICE_ID";

    public static final String NOTIFICATION_ICON = "NOTIFICATION_ICON";

    public static final String SETTINGS_NOTIFICATION_ENABLED = "SETTINGS_NOTIFICATION_ENABLED";

    public static final String SETTINGS_SOUND_ENABLED = "SETTINGS_SOUND_ENABLED";

    public static final String SETTINGS_VIBRATE_ENABLED = "SETTINGS_VIBRATE_ENABLED";

    public static final String SETTINGS_TOAST_ENABLED = "SETTINGS_TOAST_ENABLED";

    // NOTIFICATION FIELDS
    //..消息参数
    public static final String NOTIFICATION_ID = "NOTIFICATION_ID";

//    public static final String NOTIFICATION_API_KEY = "NOTIFICATION_API_KEY";

    public static final String NOTIFICATION_TITLE = "NOTIFICATION_TITLE";

    public static final String NOTIFICATION_MESSAGE = "NOTIFICATION_MESSAGE";
    
    public static final String NOTIFICATION_RECEIVER = "NOTIFICATION_RECEIVER";
    
    public static final String NOTIFICATION_SENDER = "NOTIFICATION_SENDER";

    public static final String NOTIFICATION_GROUP_ID = "NOTIFICATION_GROUP_ID";
    
//    public static final String NOTIFICATION_URI = "NOTIFICATION_URI";

    //.. INTENT ACTIONS
    public static final String ACTION_SHOW_NOTIFICATION = "org.weixiao.client.SHOW_NOTIFICATION";

    public static final String ACTION_NOTIFICATION_CLICKED = "org.weixiao.client.NOTIFICATION_CLICKED";

    public static final String ACTION_NOTIFICATION_CLEARED = "org.weixiao.client.NOTIFICATION_CLEARED";

    public static final String ACTION_SEND_NOTIFICATION = "org.weixiao.client.SEND_NOTIFICATION";
    
    public static final String ACTION_USER_LOGIN = "userlogin";
//    public static final String ACTION_USER_LOGIN_REPLY = "LOGIN_REPLY";
    
    public static final String ACTION_USER_REGISTER = "userregister";
//    public static final String ACTION_USER_REGISTER_REPLY ="REGISTER_REPLY";
    public static final String ACTION_CREATE_GROUP = "creategroup";
    
    public static final String ACTION_USER_GROUP_LIST = "usergrouplist";
    
    public static final String ACTION_NOTIFICATION_LIST_4_GROUP = "notificationlist4group";
    
    public static final String ACTION_RECEIVE_GROUP_MESSAGE = "receivegroupmessage";

    public static final String ACTION_SEND_MESSAGE_4_GROUP = "sendmessage4group";
    
    public static final String ACTION_CONNECT_LOGIN = "connectlogin";
    
    public static final String ACTION_SEARCH_GROUP = "searchgroup";
    
    public static final String ACTION_JOIN_GROUP ="joingroup";
    
    public static final String ACTION_HANDLE_GROUP_REQUEST = "handlegrouprequest";
    
    public static final String ACTION_GROUP_REQUEST_LIST = "grouprequestlist";
    
    public static final String ACTION_GROUP_MEMBER_LIST ="groupmemberlist";
    
    public static final String ACTION_CHANGE_PUSHABLE_4_GROUP = "changepushable4gorup";
    
    public static final String ACTION_OUT_MEMBER_4_GROUP = "outmember4group";
     
    public static final String ACTION_CONNECT_LOGIN_ACK= "connectlogin_ack";
    
    //..reply 参数
    public static final String REPLY_ECODE = "ecode";
    public static final String REPLY_EMSG = "emsg";
    
    //..config 参数
    public static final String CONFIG_COUNT = "count";
    
    public static String getActionReply(String action){
    	return action+"_REPLY";
    }
    public static String getActionConfig(String action){
    	return action+"_CONFIG";
    }
    
}
