package org.weixiao.activity;
///*
// * Copyright (C) 2010 Moduad Co., Ltd.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package org.weixiao.activity;
//
//import org.weixiao.client.WXStrings;
//import org.weixiao.service.ServiceManager;
//
//import android.app.Activity;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.TextView;
//import android.widget.Toast;
//
///**
// * This is an androidpn client demo application.
// * 
// * @author Sehwan Noh (devnoh@gmail.com)
// */
//public class DemoAppActivity extends Activity {
//	private MyBroadcastReceiver myBroadcastReceiver;
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        Log.d("DemoAppActivity", "onCreate()...");
//
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.main);
//        
////        TextView textView = (TextView) findViewById(R.id.text_view);
////        textView.setText("changed");
//        
//        // Settings
//        Button okButton = (Button) findViewById(R.id.btn_settings);
//        okButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View view) {
//                ServiceManager.viewNotificationSettings(DemoAppActivity.this);
//            }
//        });
//
//        // Start the service
//        ServiceManager serviceManager = new ServiceManager(this);
//        serviceManager.setNotificationIcon(R.drawable.notification);
//        serviceManager.startService();
//        
//        //intent过滤器
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(WXStrings.RECIVER_ACTOIN);
//        myBroadcastReceiver = new MyBroadcastReceiver(this);
//        //该方法为Context方法，需要继承
//        registerReceiver(myBroadcastReceiver, intentFilter);
//    }
//
//	@Override
//	public void finish() {
//		unregisterReceiver(myBroadcastReceiver);
//		super.finish();
//	}
//    
//    
//
//}
//
//class MyBroadcastReceiver extends BroadcastReceiver{
//	private Activity activity;
//	
//	public MyBroadcastReceiver(Activity activity) {
//		this.activity = activity;
//	}
//
//	@Override
//	public void onReceive(Context context, Intent intent) {
//
//		Log.d("TAG","MyBroadcastReceiver");
//		String str = intent.getStringExtra(WXStrings.CONNECT_STATUS);
//		if(str.equals(WXStrings.CONNECT_STATUS_LOGIN)){
//			Intent intent2Login = new Intent();
//			intent2Login.setClassName(context, LoginActivity.CLASS_NAME);
//			context.startActivity(intent2Login);
//			activity.finish();
//			
//		}
//		Toast.makeText(context, "MyBroadcastReceiver ", Toast.LENGTH_LONG).show();
//	}
//	
//}