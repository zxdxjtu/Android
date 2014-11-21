package com.greenhand.your_choice;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class Login extends Activity{

	private final int SPLASH_DISPLAY_LENGHT = 1500;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		 new Handler().postDelayed(new Runnable() {

             @Override
             public void run() 
             {
                     Intent intent = new Intent(Login.this,MainPage.class);
                     startActivity(intent);
                     overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                     Login.this.finish();
                     finish();
             }
		 }, SPLASH_DISPLAY_LENGHT);	
		 
	}

}
