package com.greenhand.your_choice;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class Setting extends Activity{

	private ImageButton backButton;
	private Button clearButton;
	private Button aboutButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		
		backButton = (ImageButton)findViewById(R.id.settingBack);
		clearButton = (Button)findViewById(R.id.settingClearButton);
		aboutButton = (Button)findViewById(R.id.settingAboutUsButton);
		
		backButton.setOnClickListener(new backButtonListener());
		clearButton.setOnClickListener(new clearButtonListener());
		aboutButton.setOnClickListener(new aboutButtonListener());
	}
	
	class aboutButtonListener implements OnClickListener{

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			Intent intent = new Intent();
			intent.setClass(Setting.this, AboutPage.class);
			startActivityForResult(intent,0);
			overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
		}
		
	}
	class backButtonListener implements OnClickListener{

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			Setting.this.finish();
			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
		}
		
	}
	
	class clearButtonListener implements OnClickListener{

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			DBhelper dbp = new DBhelper(Setting.this);
			dbp.clear();
			Toast toast = Toast.makeText(getApplicationContext(),"¼ÇÂ¼É¾³ýÍê±Ï", Toast.LENGTH_LONG);
			toast.show();
		}
		
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
	}
	

}
