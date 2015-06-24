package org.weixiao.activity;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.weixiao.client.Constants;

import com.wyx.utils.MD5;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {
	public static String CLASS_NAME = "org.weixiao.activity.LoginActivity";
	private RegisterBroadcastReceiver registerBroadcastReceiver;
	private LoginBroadcastReceiver loginBroadcastReceiver;
	private String account;
	private String password;
	Button registerBtn;
	Button loginBtn;
	EditText accountText;
	EditText passwordText;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_register);

		// 登录结果监听
		IntentFilter loginFilter = new IntentFilter();
		loginFilter.addAction(Constants
				.getActionReply(Constants.ACTION_USER_LOGIN));
		loginBroadcastReceiver = new LoginBroadcastReceiver(this);
		registerReceiver(loginBroadcastReceiver, loginFilter);

		// 注册结果监听
		IntentFilter registerFilter = new IntentFilter();
		registerFilter.addAction(Constants
				.getActionReply(Constants.ACTION_USER_REGISTER));
		registerBroadcastReceiver = new RegisterBroadcastReceiver(this);
		registerReceiver(registerBroadcastReceiver, registerFilter);
		
		accountText = (EditText) findViewById(R.id.accountText);
		passwordText = (EditText) findViewById(R.id.passwordText);

		// 登陆按钮点击事件
		loginBtn = (Button) findViewById(R.id.loginButton);
		loginBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				account = accountText.getText().toString().trim();
				password = passwordText.getText().toString().trim();
				
				LoginActivity.this.setAccount(account);

				if (account.equals("") || account == null) {
					Toast.makeText(LoginActivity.this, "账号不能为空",
							Toast.LENGTH_LONG).show();
					return;
				}
				String timestamp;
				String passwordMD5;
				if (password.equals("") || password == null) {
					Toast.makeText(LoginActivity.this, "密码不能为空",
							Toast.LENGTH_LONG).show();
					return;
				}else{
					SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
					Date date = new Date();
					timestamp = sdf.format(date);
					MD5 md5 = new MD5();
					passwordMD5 = md5.getMD5(md5.getMD5(password)+timestamp); 
					
				}


				Intent intent = new Intent(Constants.ACTION_USER_LOGIN);
				intent.putExtra("account", account);
				intent.putExtra("password", passwordMD5);
				intent.putExtra("timestamp", timestamp);
				sendBroadcast(intent);

				// Toast.makeText(getApplicationContext(),
				// "account:" + account + "\npassword:" + password,
				// Toast.LENGTH_SHORT).show();
			}
		});

		// 注册按钮点击事件
		registerBtn = (Button) findViewById(R.id.registerButton);
		registerBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String account = accountText.getText().toString();
				String password = passwordText.getText().toString();

				if (account.equals("") || account == null) {
					Toast.makeText(LoginActivity.this, "账号不能为空",
							Toast.LENGTH_LONG).show();
					return;
				}
				String passwordMD5;
				if (password.equals("") || password == null) {
					Toast.makeText(LoginActivity.this, "密码不能为空",
							Toast.LENGTH_LONG).show();
					return;
				}else{
					//密码加密
					MD5 md5 = new MD5();
					passwordMD5 = md5.getMD5(password);
				}

				Intent intent = new Intent(Constants.ACTION_USER_REGISTER);
				intent.putExtra("account", account);
				intent.putExtra("password", passwordMD5);
				sendBroadcast(intent);

			}
		});
		
		SharedPreferences sharedPrefs = getSharedPreferences(
				Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
		String sfAccount = sharedPrefs.getString(Constants.USER_ACCOUNT, "");
		String sfPassword = sharedPrefs.getString(Constants.USER_PASSWORD, "");
		if(!"".equals(sfAccount) && !"".equals(sfPassword)){
			//已经存在账号密码，自动登录
			String truePsd = sfPassword.substring(3, sfPassword.length());
			accountText.setText(sfAccount);
			passwordText.setText(truePsd);
			loginBtn.performClick();
		}
		
	}

	@Override
	public void finish() {
		unregisterReceiver(registerBroadcastReceiver);
		unregisterReceiver(loginBroadcastReceiver);
		super.finish();
	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		startActivity(intent);
	}
	
	

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}
	
	public String getPassword(){
		//伪加密
		String str = "123" + password;
		return str;
	}
	
}

class RegisterBroadcastReceiver extends BroadcastReceiver {
	private Activity activity;

	public RegisterBroadcastReceiver(Activity activity) {
		this.activity = activity;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String emsg = intent.getStringExtra("emsg");
		Toast.makeText(context, emsg, Toast.LENGTH_LONG).show();
	}

}

class LoginBroadcastReceiver extends BroadcastReceiver {
	private Activity activity;

	public LoginBroadcastReceiver(Activity activity) {
		this.activity = activity;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String ecode = intent.getStringExtra("ecode");
		String emsg = intent.getStringExtra("emsg");
		

		if ("300".equals(ecode)) {
			Toast.makeText(context, emsg, Toast.LENGTH_LONG).show();
			
			SharedPreferences sharedPrefs = activity.getSharedPreferences(
					Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
			Editor editor = sharedPrefs.edit();
			LoginActivity loginActivity = (LoginActivity) activity;
			
	        editor.putString(Constants.USER_ACCOUNT, loginActivity.getAccount());
	        editor.putString(Constants.USER_PASSWORD, loginActivity.getPassword());
	        editor.commit();
			
			Intent intent2Main = new Intent();
			intent2Main.setClassName(context, MainActivity.CLASS_NAME);
			context.startActivity(intent2Main);
			activity.finish();
		} else {
			Toast.makeText(context, emsg, Toast.LENGTH_LONG).show();
		}
	}

}