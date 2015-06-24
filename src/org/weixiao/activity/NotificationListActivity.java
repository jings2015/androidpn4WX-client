package org.weixiao.activity;

import java.util.ArrayList;
import java.util.List;

import org.litepal.crud.DataSupport;
import org.weixiao.client.Constants;
import org.weixiao.db.NotificationItem;
import org.weixiao.db.UserGroupItem;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class NotificationListActivity extends Activity {
	public static final String CLASS_NAME = "org.weixiao.activity.NotificationListActivity";
	private String groupId;
	private ListView listView;
	private NotificationListAdapter mAdapter;
	private List<NotificationItem> list = new ArrayList<NotificationItem>();
	private RefreshNotificationListBroadcastReceiver refreshNotificationListBroadcastReceiver;
	private SendMessage4GroupBroadcastReceiver sendMessage4GroupBroadcastReceiver;
	private ReceiveGroupMessageBroadcastReceiver receiveGroupMessageBroadcastReceiver;
	private EditText input;
	private Button sendBtn;
	private Button refreshBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notification_list_activity);

		Intent intentFrom = getIntent();
		String groupName = intentFrom.getStringExtra("groupName");
		groupId = intentFrom.getStringExtra("groupId");
		boolean pushable = intentFrom.getBooleanExtra("pushable", false);

		TextView groupNameText = (TextView) findViewById(R.id.group_name);
		groupNameText.setText(groupName);

		// 刷新结果监听
		IntentFilter refreshFilter = new IntentFilter();
		refreshFilter.addAction(Constants
				.getActionConfig(Constants.ACTION_NOTIFICATION_LIST_4_GROUP));
		refreshNotificationListBroadcastReceiver = new RefreshNotificationListBroadcastReceiver(
				this);
		registerReceiver(refreshNotificationListBroadcastReceiver,
				refreshFilter);

		// 发送结果监听
		IntentFilter sendFilter = new IntentFilter();
		sendFilter.addAction(Constants
				.getActionReply(Constants.ACTION_SEND_MESSAGE_4_GROUP));
		sendMessage4GroupBroadcastReceiver = new SendMessage4GroupBroadcastReceiver(
				this);
		registerReceiver(sendMessage4GroupBroadcastReceiver, sendFilter);

		// 收到群消息监听
		IntentFilter receiveFilter = new IntentFilter();
		receiveFilter.addAction(Constants.ACTION_RECEIVE_GROUP_MESSAGE);
		receiveGroupMessageBroadcastReceiver = new ReceiveGroupMessageBroadcastReceiver(
				this);
		registerReceiver(receiveGroupMessageBroadcastReceiver, receiveFilter);

		Button back = (Button) findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				NotificationListActivity.this.finish();
			}
		});

		refreshBtn = (Button) findViewById(R.id.refresh);
		refreshBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SharedPreferences sharedPrefs = getSharedPreferences(
						Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
				String rfsAccount = sharedPrefs.getString(
						Constants.USER_ACCOUNT, "");
				String rfsGroupId = NotificationListActivity.this.getGroupId();
				if ("".equals(rfsAccount) || "".equals(rfsGroupId)) {
					Toast.makeText(NotificationListActivity.this, "未知错误，请重新登录",
							Toast.LENGTH_LONG).show();
					return;
				}
				Intent intent = new Intent(
						Constants.ACTION_NOTIFICATION_LIST_4_GROUP);
				intent.putExtra("receiver", rfsAccount);
				intent.putExtra("groupId", rfsGroupId);
				sendBroadcast(intent);

			}
		});

		Button details = (Button) findViewById(R.id.details);
		details.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 显示详细信息
				List<UserGroupItem> list = new ArrayList<UserGroupItem>();
				list = DataSupport.where("groupId =?",
						groupId).find(UserGroupItem.class);
				UserGroupItem userGroupItem;
				if (list == null || list.isEmpty()) {
					return;
				} else {
					userGroupItem = list.get(0);
				}
				String detailsStr = "群名：" + userGroupItem.getGroupName()
						+ "\n群号：" + userGroupItem.getGroupId() + "\n群介绍："
						+ userGroupItem.getInfo();
				Builder builder = new AlertDialog.Builder(
						NotificationListActivity.this);
				builder.setMessage(detailsStr);
				builder.setNegativeButton("取消", null);
				builder.setPositiveButton("成员列表",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								Intent intent = new Intent();
								intent.setClassName(
										NotificationListActivity.this,
										GroupMemberListActivity.CLASS_NAME);
								intent.putExtra("groupId", groupId);
								startActivity(intent);
							}
						});

				builder.show();

			}
		});

		// 没有发送权限的不显示 输入框
		LinearLayout bottomBar = (LinearLayout) findViewById(R.id.bottom_bar);
		if (!pushable) {
			bottomBar.setVisibility(View.INVISIBLE);
		}

		input = (EditText) findViewById(R.id.input_text);
		sendBtn = (Button) findViewById(R.id.send_button);
		sendBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String message = input.getText().toString();
				if ("".equals(message.trim())) {
					Toast.makeText(NotificationListActivity.this, "消息内容不能为空。",
							Toast.LENGTH_LONG);
					return;
				}
				String groupId = getGroupId();
				SharedPreferences sharedPrefs = getSharedPreferences(
						Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
				String sender = sharedPrefs.getString(Constants.USER_ACCOUNT,
						"");
				if ("".equals(sender)) {
					Toast.makeText(NotificationListActivity.this, "未知错误，请重新登录",
							Toast.LENGTH_LONG).show();
					return;
				}
				Intent intent = new Intent(
						Constants.ACTION_SEND_MESSAGE_4_GROUP);
				intent.putExtra("sender", sender);
				intent.putExtra("message", message);
				intent.putExtra("groupId", groupId);
				sendBroadcast(intent);
			}
		});
		showListView();
		refreshBtn.performClick();

	}

	public void showListView() {
		SharedPreferences sharedPrefs = getSharedPreferences(
				Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
		String receiver = sharedPrefs.getString(Constants.USER_ACCOUNT, "");
		if ("".equals(receiver)) {
			Toast.makeText(this, "发生未知错误，请重新登录", Toast.LENGTH_LONG).show();
			return;
		}
		list = DataSupport.where("groupId= ?", groupId).order("createdDate")
				.find(NotificationItem.class);
		Log.d("TAG", "notification list size" + list.size());
		listView = (ListView) findViewById(R.id.notification_list_view);
		mAdapter = new NotificationListAdapter(this, 0, list);
		listView.setAdapter(mAdapter);
		listView.setSelection(ListView.FOCUS_DOWN);
		// setListViewHeightBasedOnChildren(listView);

		// 点击
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				NotificationItem notificationItem = list.get(arg2);
				String groupId = notificationItem.getGroupId();

			}
		});
	}

	@Override
	public void onBackPressed() {
		this.finish();
	}

	@Override
	public void finish() {
		unregisterReceiver(refreshNotificationListBroadcastReceiver);
		unregisterReceiver(sendMessage4GroupBroadcastReceiver);
		unregisterReceiver(receiveGroupMessageBroadcastReceiver);
		super.finish();
	}

	@Override
	protected void onDestroy() {
		// unregisterReceiver(refreshNotificationListBroadcastReceiver);
		// unregisterReceiver(sendMessage4GroupBroadcastReceiver);
		super.onDestroy();
	}

	public String getGroupId() {
		return groupId;
	}

	public void cleanInputText() {
		input.setText("");
	}

	public void refresh() {
		refreshBtn.performClick();
	}

}

class NotificationListAdapter extends ArrayAdapter<NotificationItem> {

	public NotificationListAdapter(Context context, int textViewResourceId,
			List<NotificationItem> objects) {
		super(context, textViewResourceId, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		NotificationItem item = getItem(position);
		View view;
		if (convertView == null) {
			view = LayoutInflater.from(getContext()).inflate(
					R.layout.notification_list_item, null);
		} else {
			view = convertView;
		}
		TextView sender = (TextView) view.findViewById(R.id.sender);
		TextView time = (TextView) view.findViewById(R.id.time);
		TextView message = (TextView) view.findViewById(R.id.message);
		sender.setBackgroundColor(1);
		sender.setText(item.getSender());
		time.setText(item.getCreatedDate());
		message.setText(item.getMessage());
		return view;
	}

}

class RefreshNotificationListBroadcastReceiver extends BroadcastReceiver {

	NotificationListActivity activity;

	public RefreshNotificationListBroadcastReceiver(
			NotificationListActivity activity) {
		this.activity = activity;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		activity.showListView();
	}

}

class SendMessage4GroupBroadcastReceiver extends BroadcastReceiver {

	NotificationListActivity activity;

	public SendMessage4GroupBroadcastReceiver(NotificationListActivity activity) {
		this.activity = activity;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String ecode = intent.getStringExtra("ecode");
		String emsg = intent.getStringExtra("emsg");

		if ("500".equals(ecode)) {
			activity.cleanInputText();
			activity.refresh();
			Toast.makeText(activity, emsg, Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(activity, emsg, Toast.LENGTH_LONG).show();
		}

	}

}

class ReceiveGroupMessageBroadcastReceiver extends BroadcastReceiver {

	NotificationListActivity activity;

	public ReceiveGroupMessageBroadcastReceiver(
			NotificationListActivity activity) {
		this.activity = activity;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		activity.refresh();

	}

}