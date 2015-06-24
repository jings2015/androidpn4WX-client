package org.weixiao.activity;

import java.util.ArrayList;
import java.util.List;

import org.litepal.crud.DataSupport;
import org.weixiao.client.Constants;
import org.weixiao.db.UserGroupItem;
import org.weixiao.service.ServiceManager;

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
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	public static String CLASS_NAME = "org.weixiao.activity.MainActivity";
	private ListView listView;
	private UserGroupListAdapter mAdapter;
	private List<UserGroupItem> list = new ArrayList<UserGroupItem>();
	private MainActivityBroadcastReceiver mainActivityBroadcastReceiver;
	private Button setting;
	private Button createGroup;
	private Button refresh;
	private Builder joinGroupBuilder;
	private AlertDialog joinDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);

		SharedPreferences sharedPrefs = getSharedPreferences(
				Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
		String account = sharedPrefs.getString(Constants.USER_ACCOUNT, "");
		TextView showAccount = (TextView) findViewById(R.id.show_account);
		showAccount.setText(account);

		mainActivityBroadcastReceiver = new MainActivityBroadcastReceiver(this);
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Constants
				.getActionConfig(Constants.ACTION_USER_GROUP_LIST));
		intentFilter.addAction(Constants
				.getActionConfig(Constants.ACTION_SEARCH_GROUP));
		intentFilter.addAction(Constants
				.getActionReply(Constants.ACTION_JOIN_GROUP));
		registerReceiver(mainActivityBroadcastReceiver, intentFilter);

		joinGroupBuilder = new AlertDialog.Builder(MainActivity.this);

		// 设置按钮点击事件
		setting = (Button) findViewById(R.id.setting);
		setting.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ServiceManager.viewNotificationSettings(MainActivity.this);
			}
		});

		// 新建群按钮点击事件
		createGroup = (Button) findViewById(R.id.create_group);
		createGroup.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClassName(MainActivity.this,
						CreateGroupActivity.CLASS_NAME);
				startActivity(intent);

				// Toast.makeText(MainActivity.this, "createGroup",
				// Toast.LENGTH_LONG).show();
			}
		});

		// 刷新按钮点击事件
		refresh = (Button) findViewById(R.id.refresh);
		refresh.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SharedPreferences sharedPrefs = getSharedPreferences(
						Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
				String account = sharedPrefs.getString(Constants.USER_ACCOUNT,
						"");
				if (account == null) {
					Toast.makeText(MainActivity.this, "请重新登录",
							Toast.LENGTH_LONG).show();
				} else {
					Intent intent = new Intent(Constants.ACTION_USER_GROUP_LIST);
					intent.putExtra("account", account);
					sendBroadcast(intent);
				}
			}
		});
		
		//
		Button groupRequestBtn = (Button) findViewById(R.id.group_request_button);
		groupRequestBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClassName(MainActivity.this, GroupRequestListActivity.CLASS_NAME);
				startActivity(intent);
			}
		});

		// 查找按钮 点击事件
		Button searchGroup = (Button) findViewById(R.id.search_group);
		searchGroup.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 新建弹出框
				LayoutInflater inflater = LayoutInflater
						.from(MainActivity.this);
				final View textEntryView = inflater.inflate(
						R.layout.search_group_dialog, null);
				AlertDialog.Builder builder = new AlertDialog.Builder(
						MainActivity.this);
				builder.setTitle("查找群");
				builder.setView(textEntryView);
				// 弹出框查找按钮点击事件
				builder.setPositiveButton("查找",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								EditText searchInput = (EditText) textEntryView
										.findViewById(R.id.search_input);
								String groupId = searchInput.getText()
										.toString();
								if ("".equals(groupId.trim())) {
									Toast.makeText(MainActivity.this, "请输入群号",
											Toast.LENGTH_LONG).show();
									return;
								}
								Intent intent = new Intent(
										Constants.ACTION_SEARCH_GROUP);
								intent.putExtra("groupId", groupId);
								sendBroadcast(intent);

							}
						});
				joinDialog = builder.show();

			}

		});

		showListView();
		refresh.performClick();

	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		startActivity(intent);
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		refresh.performClick();
	}

	public void showListView() {
		list = DataSupport.findAll(UserGroupItem.class);
		Log.d("TAG", "group list size" + list.size());
		listView = (ListView) findViewById(R.id.list_view);
		mAdapter = new UserGroupListAdapter(this, 0, list);
		listView.setAdapter(mAdapter);
		// setListViewHeightBasedOnChildren(listView);

		// 点击群后进入群消息列表
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				UserGroupItem userGroupItem = list.get(arg2);
				String groupId = userGroupItem.getGroupId();
				String groupName = userGroupItem.getGroupName();
				String info = userGroupItem.getInfo();
				boolean pushable = userGroupItem.isPushable();

				// TODO
				Intent intent = new Intent();
				intent.setClassName(MainActivity.this,
						NotificationListActivity.CLASS_NAME);
				intent.putExtra("groupId", groupId);
				intent.putExtra("groupName", groupName);
				intent.putExtra("pushable", pushable);
				startActivity(intent);

			}
		});

	}

	/**
	 * 显示添加群的对话框
	 */
	public void showJoinGroupDialog(String groupName, final String groupId,
			String owner, String info) {
		LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
		final View textEntryView = inflater.inflate(R.layout.join_group_dialog,
				null);
		// Builder builder = new AlertDialog.Builder(MainActivity.this);
		joinGroupBuilder.setTitle("加入群");
		joinGroupBuilder.setView(textEntryView);
		TextView groupNameText = (TextView) textEntryView
				.findViewById(R.id.join_dialog_group_name_text);
		TextView groupIdText = (TextView) textEntryView
				.findViewById(R.id.join_dialog_group_id_text);
		TextView groupOwnerText = (TextView) textEntryView
				.findViewById(R.id.join_dialog_group_owner_text);
		TextView groupInfoText = (TextView) textEntryView
				.findViewById(R.id.join_dialog_group_info_text);
		final EditText messageText = (EditText) textEntryView
				.findViewById(R.id.join_dialog_message_text);
		groupNameText.setText("群名：" + groupName);
		groupIdText.setText("群号：" + groupId);
		groupOwnerText.setText("群主：" + owner);
		groupInfoText.setText("群信息：" + info);
		joinGroupBuilder.setCancelable(false);
		joinGroupBuilder.setPositiveButton("加入",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						SharedPreferences sharedPrefs = getSharedPreferences(
								Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
						String account = sharedPrefs.getString(Constants.USER_ACCOUNT, "");
						if("".equals(account)){
							Toast.makeText(MainActivity.this, "未知错误，请重新登录", Toast.LENGTH_LONG).show();
						}
						
						String message = messageText.getText().toString();
						Intent intent = new Intent(Constants.ACTION_JOIN_GROUP);
						intent.putExtra("account", account);
						intent.putExtra("groupId", groupId);
						intent.putExtra("message", message);
						sendBroadcast(intent);
					}
				});

		joinGroupBuilder.setNegativeButton("取消",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						MainActivity.this.closeJionGroupDialog();
					}
				});
		joinGroupBuilder.show();

	}

	/**
	 * 关闭添加群对话框
	 */
	public void closeJionGroupDialog() {
		joinDialog.dismiss();
	}

	public void setListViewHeightBasedOnChildren(ListView listView) {
		// 获取ListView对应的Adapter
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}

		int totalHeight = 0;
		for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
			// listAdapter.getCount()返回数据项的数目
			View listItem = listAdapter.getView(i, null, listView);
			// 计算子项View 的宽高
			listItem.measure(0, 0);
			// 统计所有子项的总高度
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		// listView.getDividerHeight()获取子项间分隔符占用的高度
		// params.height最后得到整个ListView完整显示需要的高度
		listView.setLayoutParams(params);
	}

	@Override
	public void finish() {
		Log.d("TAG", "MainActivity finish");
		unregisterReceiver(mainActivityBroadcastReceiver);
		super.finish();
	}

	@Override
	protected void onDestroy() {
		Log.d("TAG", "MainActivity onDestroy");
		// unregisterReceiver(refreshBroadcastReceiver);
		super.onDestroy();
	}
}

class UserGroupListAdapter extends ArrayAdapter<UserGroupItem> {

	public UserGroupListAdapter(Context context, int textViewResourceId,
			List<UserGroupItem> objects) {
		super(context, textViewResourceId, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		UserGroupItem item = getItem(position);
		View view;
		if (convertView == null) {
			view = LayoutInflater.from(getContext()).inflate(
					R.layout.group_list_item, null);
		} else {
			view = convertView;
		}
		TextView groupName = (TextView) view.findViewById(R.id.group_name);
		TextView message = (TextView) view.findViewById(R.id.message);
		TextView time = (TextView) view.findViewById(R.id.time_text);
		groupName.setText(item.getGroupName());
		message.setText(item.getMessage());
		time.setText(item.getCreatedDate());
		return view;
	}

}

class MainActivityBroadcastReceiver extends BroadcastReceiver {
	MainActivity activity;

	public MainActivityBroadcastReceiver(MainActivity activity) {
		this.activity = activity;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String method = intent.getStringExtra("method");
		if ("usergrouplist".equals(method)) {
			activity.showListView();
		} else if ("searchgroup".equals(method)) {
			// 弹出加群框
//			Toast.makeText(activity, "searchgroup received", Toast.LENGTH_LONG).show();
			String groupName = intent.getStringExtra("groupName");
			String groupId = intent.getStringExtra("groupId");
			String owner = intent.getStringExtra("owner");
			String info = intent.getStringExtra("info");

			activity.showJoinGroupDialog(groupName, groupId, owner, info);
		} else if ("joingroup".equals(method)) {
			String ecode = intent.getStringExtra("ecode");
			if ("600".equals(ecode)) {
				activity.closeJionGroupDialog();
			}
			String emsg = intent.getStringExtra("emsg");
			Toast.makeText(activity, emsg, Toast.LENGTH_LONG).show();
		}
	}

}

// class RefreshUserGroupListBroadcastReceiver extends BroadcastReceiver {
// MainActivity activity;
//
// public RefreshUserGroupListBroadcastReceiver(MainActivity activity) {
// this.activity = activity;
// }
//
// @Override
// public void onReceive(Context context, Intent intent) {
// activity.showListView();
// }

// }