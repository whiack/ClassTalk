package com.chao.classtalk;


import com.chao.classtalk.R;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.widget.RelativeLayout;

public class MainSplashScreen extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_splash_screen);
		RelativeLayout rl = (RelativeLayout) findViewById(R.id.SplashScreen);
		rl.setBackgroundResource(R.drawable.thefirst);
		
		Thread background = new Thread() {
			public void run() {
				try {
					sleep(5*500);
					
					Intent i = new Intent(getBaseContext(), Login.class);
					startActivity(i);
					
					finish();
				} catch (Exception e) {
					
				}
			}
		};
			
		background.start();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}