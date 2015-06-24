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
package org.weixiao.activity;

import org.weixiao.client.Constants;
import org.weixiao.service.ServiceManager;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

/**
 *欢迎画面
 *
 */
public class WelcomeActivity extends Activity {
	private MyBroadcastReceiver myBroadcastReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("WelcomeActivity", "onCreate()...");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);
        

        // Start the service
        ServiceManager serviceManager = new ServiceManager(this);
        serviceManager.setNotificationIcon(R.drawable.notification);
        serviceManager.startService();
        
        //intent过滤器
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.ACTION_CONNECT_LOGIN_ACK);
        myBroadcastReceiver = new MyBroadcastReceiver(this);
        //该方法为Context方法，需要继承
        registerReceiver(myBroadcastReceiver, intentFilter);
        
        Intent intent = new Intent(Constants.ACTION_CONNECT_LOGIN);
        sendBroadcast(intent);
    }

	@Override
	public void finish() {
		unregisterReceiver(myBroadcastReceiver);
		super.finish();
	}
    
    

}
//监听连接登录成功，跳转
class MyBroadcastReceiver extends BroadcastReceiver{
	private Activity activity;
	
	public MyBroadcastReceiver(Activity activity) {
		this.activity = activity;
	}

	@Override
	public void onReceive(Context context, Intent intent) {

		Log.d("TAG","MyBroadcastReceiver");
		String ecode = intent.getStringExtra("ecode");
		if("100".equals(ecode)){
			Intent intent2Login = new Intent();
			intent2Login.setClassName(context, LoginActivity.CLASS_NAME);
			context.startActivity(intent2Login);
			activity.finish();
			
		}
	}
	
}