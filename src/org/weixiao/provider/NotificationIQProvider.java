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
package org.weixiao.provider;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.weixiao.iq.NotificationIQ;
import org.xmlpull.v1.XmlPullParser;

import android.annotation.SuppressLint;

/** 
 * This class parses incoming IQ packets to NotificationIQ objects.
 *
 * @author Sehwan Noh (devnoh@gmail.com)
 */
public class NotificationIQProvider implements IQProvider {

    public NotificationIQProvider() {
    }

    @SuppressLint("SimpleDateFormat")
	@Override
    public IQ parseIQ(XmlPullParser parser) throws Exception {

        NotificationIQ notification = new NotificationIQ();
        for (boolean done = false; !done;) {
            int eventType = parser.next();
            if (eventType == 2) {
                if ("notificationId".equals(parser.getName())) {
                    notification.setNotificationId(parser.nextText());
                }
                if ("title".equals(parser.getName())) {
                    notification.setTitle(parser.nextText());
                }
                if ("message".equals(parser.getName())) {
                    notification.setMessage(parser.nextText());
                }
                if ("groupId".equals(parser.getName())) {
                	notification.setGroupId(parser.nextText());
                }
                
                if ("receiver".equals(parser.getName())) {
                    notification.setReceiver(parser.nextText());
                }
                if ("sender".equals(parser.getName())) {
                    notification.setSender(parser.nextText());
                }
                if ("createdDate".equals(parser.getName())) {
                	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                	Date createdDate = sdf.parse(parser.nextText());
                    notification.setCreatedDate(createdDate);
                }
            } else if (eventType == 3
                    && "notification".equals(parser.getName())) {
                done = true;
            }
        }

        return notification;
    }

}
