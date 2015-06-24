package org.weixiao.activity;

import java.util.ArrayList;
import java.util.List;

import org.litepal.crud.DataSupport;
import org.weixiao.client.Constants;
import org.weixiao.db.GroupRequestItem;

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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class GroupRequestListActivity extends Activity {
	public static String CLASS_NAME = "org.weixiao.activity.GroupRequestListActivity";
	private ListView listView;
	private GroupRequestAdapter mAdapter;
	private List<GroupRequestItem> list = new ArrayList<GroupRequestItem>();
	private GroupRequestListBroadcastReceiver groupRequestListBroadcastReceiver;
	private Button refreshBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.group_request_activity);

		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Constants
				.getActionReply(Constants.ACTION_HANDLE_GROUP_REQUEST));
		intentFilter.addAction(Constants
				.getActionConfig(Constants.ACTION_GROUP_REQUEST_LIST));
		groupRequestListBroadcastReceiver = new GroupRequestListBroadcastReceiver(
				this);
		registerReceiver(groupRequestListBroadcastReceiver, intentFilter);

		Button back = (Button) findViewById(R.id.group_request_back);
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				GroupRequestListActivity.this.finish();
			}
		});

		refreshBtn = (Button) findViewById(R.id.group_request_refresh);
		refreshBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SharedPreferences sharedPrefs = getSharedPreferences(
						Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
				String account = sharedPrefs.getString(Constants.USER_ACCOUNT,
						"");
				if (account == null) {
					Toast.makeText(GroupRequestListActivity.this, "请重新登录",
							Toast.LENGTH_LONG).show();
				} else {
					Intent intent = new Intent(
							Constants.ACTION_GROUP_REQUEST_LIST);
					intent.putExtra("account", account);
					sendBroadcast(intent);
				}
			}
		});
		showListView();
		refreshBtn.performClick();
	}
	
	public Button getRefreshBtn() {
		return refreshBtn;
	}

	@Override
	public void onBackPressed() {
		finish();
	}

	@Override
	public void finish() {
		unregisterReceiver(groupRequestListBroadcastReceiver);
		super.finish();
	}

	public void showListView() {
		list = DataSupport.findAll(GroupRequestItem.class);
		Log.d("TAG", "groupRequest list size" + list.size());
		listView = (ListView) findViewById(R.id.group_request_list_view);
		mAdapter = new GroupRequestAdapter(this, 0, list);
		listView.setAdapter(mAdapter);
		// listView.setSelection(ListView.FOCUS_DOWN);
	}

	public void handleGroupRequest(String requester, String groupId,
			String result) {
		if (!"agree".equals(result) && !"disagree".equals(result)) {
			return;
		}
		List<GroupRequestItem> list = DataSupport.where(
				"requester = ? and groupId = ?", requester, groupId).find(
				GroupRequestItem.class);
		GroupRequestItem groupRequestItem;
		if (list == null || list.isEmpty()) {
			return;
		} else {
			groupRequestItem = list.get(0);

			Intent intent = new Intent(Constants.ACTION_HANDLE_GROUP_REQUEST);
			intent.putExtra("requester", requester);
			intent.putExtra("groupId", groupId);
			intent.putExtra("result", result);
			sendBroadcast(intent);

			groupRequestItem.delete();
		}

	}
}

class GroupRequestAdapter extends ArrayAdapter<GroupRequestItem> {
	GroupRequestListActivity activity;

	public GroupRequestAdapter(Context context, int textViewResourceId,
			List<GroupRequestItem> objects) {
		super(context, textViewResourceId, objects);
		this.activity = (GroupRequestListActivity) context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final GroupRequestItem item = getItem(position);
		View view;
		if (convertView == null) {
			view = LayoutInflater.from(getContext()).inflate(
					R.layout.group_request_list_item, null);
		} else {
			view = convertView;
		}
		String tips = item.getRequester() + "请求加入" + item.getGroupName()
				+ "(群号：" + item.getGroupId() + ")。以下为验证消息。";
		TextView tipsText = (TextView) view
				.findViewById(R.id.group_request_item_tips);
		TextView messageText = (TextView) view
				.findViewById(R.id.group_request_item_message);
		
		Button agreeBtn = (Button) view.findViewById(R.id.group_request_agree);
		Button disagreeBtn = (Button) view.findViewById(R.id.group_request_disagree);
		agreeBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				activity.handleGroupRequest(item.getRequester(), item.getGroupId(), "agree");
			}
		});
		disagreeBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				activity.handleGroupRequest(item.getRequester(), item.getGroupId(), "disagree");
			}
		});
		
		
		tipsText.setText(tips);
		messageText.setText(item.getMessage());

		return view;
	}
}

class GroupRequestListBroadcastReceiver extends BroadcastReceiver {
	GroupRequestListActivity activity;

	public GroupRequestListBroadcastReceiver(GroupRequestListActivity activity) {
		this.activity = activity;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String method = intent.getStringExtra("method");
		if ("handlegrouprequest".equals(method)) {
			String emsg = intent.getStringExtra("emsg");
			Toast.makeText(activity, emsg, Toast.LENGTH_LONG).show();
			activity.getRefreshBtn().performClick();
		}else if("grouprequestlist".equals(method)){
			activity.showListView();
		}
	}

}