package com.greenhand.your_choice;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class Setting extends Activity{

	private ImageButton backButton;
	private Button musicButton;
	private boolean musicon = true;
	private Button soundButton;
	private boolean soundon = true;
	private Button clearButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		
		backButton = (ImageButton)findViewById(R.id.settingBack);
		musicButton = (Button)findViewById(R.id.settingMusicButton);
		soundButton = (Button)findViewById(R.id.settingSoundButton);
		clearButton = (Button)findViewById(R.id.settingClearButton);
		
		backButton.setOnClickListener(new backButtonListener());
		musicButton.setOnClickListener(new musicButtonListener());
		soundButton.setOnClickListener(new soundButtonListener());
		clearButton.setOnClickListener(new clearButtonListener());
	}
	
	class backButtonListener implements OnClickListener{

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			Setting.this.finish();
			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
		}
		
	}
	class musicButtonListener implements OnClickListener{

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			if(musicon)
			{
				musicButton.setText("音乐:关");
				musicon = false;
			}
			else
			{
				musicButton.setText("音乐:开");
				musicon = true;
			}
		}
		
	}
	class soundButtonListener implements OnClickListener{

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			if(soundon)
			{
				soundButton.setText("音效:关");
				soundon = false;
			}
			else
			{
				soundButton.setText("音效:开");
				soundon = true;
			}
		}
		
	}
	class clearButtonListener implements OnClickListener{

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			DBhelper dbp = new DBhelper(Setting.this);
			dbp.clear();
			Toast toast = Toast.makeText(getApplicationContext(),"记录删除完毕", Toast.LENGTH_LONG);
			toast.show();
		}
		
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
//		Setting.this.finish();
		overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
	}
	

}
