/*
 * Copyright 2010 the original author or authors.
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
package org.weixiao.activity;

import org.litepal.crud.DataSupport;
import org.weixiao.client.Constants;
import org.weixiao.client.LogUtil;
import org.weixiao.db.GroupMemberItem;
import org.weixiao.db.GroupRequestItem;
import org.weixiao.db.NotificationItem;
import org.weixiao.db.UserGroupItem;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * Activity for displaying the notification setting view.
 *
 * @author Sehwan Noh (devnoh@gmail.com)
 */
public class NotificationSettingsActivity extends PreferenceActivity {

	private static final String LOGTAG = LogUtil
			.makeLogTag(NotificationSettingsActivity.class);

	public NotificationSettingsActivity() {
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		
		Button logoutBtn = (Button) findViewById(R.id.logout);
		logoutBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SharedPreferences sharedPrefs = getSharedPreferences(
						Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
				String xmppClient = sharedPrefs.getString(Constants.XMPP_CLIENTID, "");
				String xmppPassword = sharedPrefs.getString(Constants.XMPP_PASSWORD, "");
				//清理SharedPreferences数据
				Editor edit= sharedPrefs.edit();
				edit.clear().commit();
				edit.putString(Constants.XMPP_CLIENTID, xmppClient);
				edit.putString(Constants.XMPP_PASSWORD, xmppPassword);
				edit.commit();
				
				//清理litepal 数据
				DataSupport.deleteAll(NotificationItem.class);
				DataSupport.deleteAll(UserGroupItem.class);
				DataSupport.deleteAll(GroupRequestItem.class);
				DataSupport.deleteAll(GroupMemberItem.class);
				
				Intent intent = new Intent();
				intent.setClassName(NotificationSettingsActivity.this, LoginActivity.CLASS_NAME);
				startActivity(intent);
				NotificationSettingsActivity.this.finish();

			}
		});
		
		
		setPreferenceScreen(createPreferenceHierarchy());
		setPreferenceDependencies();

		CheckBoxPreference notifyPref = (CheckBoxPreference) getPreferenceManager()
				.findPreference(Constants.SETTINGS_NOTIFICATION_ENABLED);
		if (notifyPref.isChecked()) {
			notifyPref.setTitle("Notifications Enabled");
		} else {
			notifyPref.setTitle("Notifications Disabled");
		}
		
	}

	private PreferenceScreen createPreferenceHierarchy() {
		Log.d(LOGTAG, "createSettingsPreferenceScreen()...");

		PreferenceManager preferenceManager = getPreferenceManager();
		preferenceManager
				.setSharedPreferencesName(Constants.SHARED_PREFERENCE_NAME);
		preferenceManager.setSharedPreferencesMode(Context.MODE_PRIVATE);

		PreferenceScreen root = preferenceManager.createPreferenceScreen(this);

		// PreferenceCategory prefCat = new PreferenceCategory(this);
		// // inlinePrefCat.setTitle("");
		// root.addPreference(prefCat);

		CheckBoxPreference notifyPref = new CheckBoxPreference(this);
		notifyPref.setKey(Constants.SETTINGS_NOTIFICATION_ENABLED);
		notifyPref.setTitle("推送通知");
		notifyPref.setSummaryOn("接收推送");
		notifyPref.setSummaryOff("不接受推送");
		notifyPref.setDefaultValue(Boolean.TRUE);
		notifyPref
				.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						boolean checked = Boolean.valueOf(newValue.toString());
						if (checked) {
							preference.setTitle("Notifications Enabled");
						} else {
							preference.setTitle("Notifications Disabled");
						}
						return true;
					}
				});

		CheckBoxPreference soundPref = new CheckBoxPreference(this);
		soundPref.setKey(Constants.SETTINGS_SOUND_ENABLED);
		soundPref.setTitle("声音");
		soundPref.setSummary("推送时声音提示");
		soundPref.setDefaultValue(Boolean.TRUE);
		// soundPref.setDependency(Constants.SETTINGS_NOTIFICATION_ENABLED);

		CheckBoxPreference vibratePref = new CheckBoxPreference(this);
		vibratePref.setKey(Constants.SETTINGS_VIBRATE_ENABLED);
		vibratePref.setTitle("震动");
		vibratePref.setSummary("推送时震动");
		vibratePref.setDefaultValue(Boolean.TRUE);
		// vibratePref.setDependency(Constants.SETTINGS_NOTIFICATION_ENABLED);

		

		root.addPreference(notifyPref);
		root.addPreference(soundPref);
		root.addPreference(vibratePref);

		// prefCat.addPreference(notifyPref);
		// prefCat.addPreference(soundPref);
		// prefCat.addPreference(vibratePref);
		// root.addPreference(prefCat);

		return root;
	}

	private void setPreferenceDependencies() {
		Preference soundPref = getPreferenceManager().findPreference(
				Constants.SETTINGS_SOUND_ENABLED);
		if (soundPref != null) {
			soundPref.setDependency(Constants.SETTINGS_NOTIFICATION_ENABLED);
		}
		Preference vibratePref = getPreferenceManager().findPreference(
				Constants.SETTINGS_VIBRATE_ENABLED);
		if (vibratePref != null) {
			vibratePref.setDependency(Constants.SETTINGS_NOTIFICATION_ENABLED);
		}
	}

}
