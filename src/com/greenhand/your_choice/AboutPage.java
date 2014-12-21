package com.greenhand.your_choice;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class AboutPage extends Activity{

	private ImageButton backButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		
		backButton = (ImageButton)findViewById(R.id.settingBack);
		backButton.setOnClickListener(new backButtonListener());
	}
	class backButtonListener implements OnClickListener{

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			AboutPage.this.finish();
			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
		}
		
	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
	}
}
