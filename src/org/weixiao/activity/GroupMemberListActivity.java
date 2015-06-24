package org.weixiao.activity;

import java.util.ArrayList;
import java.util.List;

import org.litepal.crud.DataSupport;
import org.weixiao.client.Constants;
import org.weixiao.db.GroupMemberItem;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class GroupMemberListActivity extends Activity {
	public static final String CLASS_NAME = "org.weixiao.activity.GroupMemberListActivity"; 
	private String groupId;
	private ListView listView;
	private GroupMemberListAdapter mAdapter;
	private List<GroupMemberItem> list = new ArrayList<GroupMemberItem>();
	private GroupMemberListBroadcastReceiver groupMemberListBroadcastReceiver;
	Button refreshBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.group_member_list_activity);

		Intent intentFrom = getIntent();
		groupId = intentFrom.getStringExtra("groupId");

		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Constants
				.getActionReply(Constants.ACTION_CHANGE_PUSHABLE_4_GROUP));
		intentFilter.addAction(Constants
				.getActionReply(Constants.ACTION_OUT_MEMBER_4_GROUP));
		intentFilter.addAction(Constants
				.getActionConfig(Constants.ACTION_GROUP_MEMBER_LIST));
		groupMemberListBroadcastReceiver = new GroupMemberListBroadcastReceiver(
				this);
		registerReceiver(groupMemberListBroadcastReceiver, intentFilter);

		Button backBtn = (Button) findViewById(R.id.group_member_list_back_button);
		backBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				GroupMemberListActivity.this.finish();
			}
		});

		refreshBtn = (Button) findViewById(R.id.group_member_list_refresh_button);
		refreshBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SharedPreferences sharedPrefs = getSharedPreferences(
						Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
				String account = sharedPrefs.getString(Constants.USER_ACCOUNT, "");
				Intent intent = new Intent(Constants.ACTION_GROUP_MEMBER_LIST);
				intent.putExtra("groupId", groupId);
				intent.putExtra("account", account);
				sendBroadcast(intent);
			}
		});

		showListView();
		refreshBtn.performClick();

	}

	public void changePushable(String groupId, String account, boolean result) {
		SharedPreferences sharedPrefs = getSharedPreferences(
				Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
		String owner = sharedPrefs.getString(Constants.USER_ACCOUNT, "");
		Intent intent = new Intent(Constants.ACTION_CHANGE_PUSHABLE_4_GROUP);
		intent.putExtra("groupId", groupId);
		intent.putExtra("account", account);
		String resultStr = result?"1":"0";
		intent.putExtra("result", resultStr);
		intent.putExtra("owner", owner);
		sendBroadcast(intent);
	}

	public void outMember(String groupId, String account) {
		SharedPreferences sharedPrefs = getSharedPreferences(
				Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
		String owner = sharedPrefs.getString(Constants.USER_ACCOUNT, "");
		if ("".equals(owner)) {
			Toast.makeText(GroupMemberListActivity.this, "未知错误，请重新登录",
					Toast.LENGTH_LONG).show();
			return;
		} else {
			Intent intent = new Intent(Constants.ACTION_OUT_MEMBER_4_GROUP);
			intent.putExtra("groupId", groupId);
			intent.putExtra("account", account);
			intent.putExtra("owner", owner);
			sendBroadcast(intent);
		}
	}

	public void showListView() {
		SharedPreferences sharedPrefs = getSharedPreferences(
				Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
		String receiver = sharedPrefs.getString(Constants.USER_ACCOUNT, "");
		if ("".equals(receiver)) {
			Toast.makeText(this, "发生未知错误，请重新登录", Toast.LENGTH_LONG).show();
			return;
		}
		list = DataSupport.where("groupId= ?", groupId).find(GroupMemberItem.class);
		Log.d("TAG", "notification list size" + list.size());
		listView = (ListView) findViewById(R.id.group_member_list_view);
		mAdapter = new GroupMemberListAdapter(this, 0, list);
		listView.setAdapter(mAdapter);
		listView.setSelection(ListView.FOCUS_DOWN);
		// setListViewHeightBasedOnChildren(listView);

		// 点击
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				GroupMemberItem notificationItem = list.get(arg2);
				String groupId = notificationItem.getGroupId();

			}
		});
	}

	@Override
	public void finish() {
		unregisterReceiver(groupMemberListBroadcastReceiver);
		super.finish();
	}

	public void refresh() {
		refreshBtn.performClick();
	}
}

class GroupMemberListAdapter extends ArrayAdapter<GroupMemberItem> {

	GroupMemberListActivity activity;

	public GroupMemberListAdapter(Context context, int textViewResourceId,
			List<GroupMemberItem> objects) {
		super(context, textViewResourceId, objects);
		this.activity = (GroupMemberListActivity) context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final GroupMemberItem item = getItem(position);
		View view;
		if (convertView == null) {
			view = LayoutInflater.from(getContext()).inflate(
					R.layout.group_member_list_item, null);
		} else {
			view = convertView;
		}
		TextView accountText = (TextView) view
				.findViewById(R.id.group_member_list_item_account);
		TextView pushableText = (TextView) view
				.findViewById(R.id.group_member_list_item_pushable_tips);
		Button pushableBtn = (Button) view
				.findViewById(R.id.group_member_list_item_pushable_button);
		Button outBtn = (Button) view
				.findViewById(R.id.group_member_list_out_button);

		accountText.setText(item.getAccount());
		if (item.isPushable()) {
			pushableText.setText("有发消息权限");
		} else {
			pushableText.setText("无发消息权限");
		}
		pushableBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				activity.changePushable(item.getGroupId(), item.getAccount(),
						!item.isPushable());
			}
		});

		outBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				activity.outMember(item.getGroupId(), item.getAccount());
			}
		});
		
		SharedPreferences sharedPrefs = activity.getSharedPreferences(
				Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
		String account = sharedPrefs.getString(Constants.USER_ACCOUNT, "");
		if(!account.equals(item.getOwner())){
			//不是群主，隐藏权限和踢出按钮
			pushableBtn.setVisibility(View.INVISIBLE);
			outBtn.setVisibility(view.INVISIBLE);
		}
		if(account.equals(item.getAccount())){
			//不可以操作本人
			pushableBtn.setVisibility(View.INVISIBLE);
			outBtn.setVisibility(view.INVISIBLE);
		}
		
		return view;
	}
}

class GroupMemberListBroadcastReceiver extends BroadcastReceiver {

	GroupMemberListActivity activity;

	public GroupMemberListBroadcastReceiver(GroupMemberListActivity activity) {
		this.activity = activity;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String method = intent.getStringExtra("method");
		if ("groupmemberlist".equals(method)) {
			activity.showListView();
		} else if ("changepushable4gorup".equals(method)) {
			String ecode = intent.getStringExtra("ecode");
			String emsg = intent.getStringExtra("emsg");
			Toast.makeText(activity, emsg, Toast.LENGTH_LONG).show();
			activity.refresh();
		} else if ("outmember4group".equals(method)) {
			String ecode = intent.getStringExtra("ecode");
			String emsg = intent.getStringExtra("emsg");
			Toast.makeText(activity, emsg, Toast.LENGTH_LONG).show();
			activity.refresh();
		}
	}
}
