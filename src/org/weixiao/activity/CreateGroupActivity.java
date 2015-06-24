package org.weixiao.activity;

import org.weixiao.client.Constants;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class CreateGroupActivity extends Activity {
	public static String CLASS_NAME = "org.weixiao.activity.CreateGroupActivity";
	private CreateGroupBroadcastReceiver createGroupBroadcastReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_group);

		// 注册结果监听
		IntentFilter createGroupFilter = new IntentFilter();
		createGroupFilter.addAction(Constants
				.getActionReply(Constants.ACTION_CREATE_GROUP));
		createGroupBroadcastReceiver = new CreateGroupBroadcastReceiver(this);
		registerReceiver(createGroupBroadcastReceiver, createGroupFilter);

		Button submit = (Button) findViewById(R.id.submit);
		submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				TextView groupNameText = (TextView) findViewById(R.id.group_name);
				TextView groupInfoText = (TextView) findViewById(R.id.group_info);
				String groupName = groupNameText.getText().toString().trim();
				String info = groupInfoText.getText().toString();

				SharedPreferences sharedPrefs = CreateGroupActivity.this
						.getSharedPreferences(Constants.SHARED_PREFERENCE_NAME,
								Context.MODE_PRIVATE);
				String account = sharedPrefs.getString(Constants.USER_ACCOUNT,
						"");
				if (account == null || "".equals(account)) {
					Toast.makeText(CreateGroupActivity.this, "请重新登录！",
							Toast.LENGTH_LONG).show();
					return;
				}
				if (groupName == null || "".equals(groupName)) {
					Toast.makeText(CreateGroupActivity.this, "群名不能为空",
							Toast.LENGTH_LONG).show();
					return;
				}
				Intent intent = new Intent(Constants.ACTION_CREATE_GROUP);
				intent.putExtra("account", account);
				intent.putExtra("groupName", groupName);
				intent.putExtra("info", info);
				sendBroadcast(intent);
			}
		});

		Button cancel = (Button) findViewById(R.id.cancel);
		cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CreateGroupActivity.this.finish();
			}
		});
	}
	@Override
	public void finish() {
		unregisterReceiver(createGroupBroadcastReceiver);
		super.finish();
	}
}

class CreateGroupBroadcastReceiver extends BroadcastReceiver {
	Activity activity;

	public CreateGroupBroadcastReceiver(Activity activity) {
		this.activity = activity;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String ecode = intent.getStringExtra("ecode");
		String emsg = intent.getStringExtra("emsg");

		if ("400".equals(ecode)) {
			Toast.makeText(context, emsg, Toast.LENGTH_LONG).show();
			activity.finish();
		} else {
			Toast.makeText(context, emsg, Toast.LENGTH_LONG).show();
		}
	}

}