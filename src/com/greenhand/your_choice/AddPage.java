package com.greenhand.your_choice;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class AddPage extends Activity{

	private Button confirm;
	private EditText input;
	private String s;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_page);
		
		confirm = (Button)findViewById(R.id.buttonConfirm);
		confirm.setOnClickListener(new ButtonListener());
	}
	
	class ButtonListener implements OnClickListener
	{

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			input = (EditText)findViewById(R.id.edittext);
			s = input.getText().toString();
			Options options = new Options();
			options.setString(s);
			Bundle bundle = new Bundle();
			bundle.putParcelable("options", options);
			Intent intent = getIntent();
			intent.putExtras(bundle);
			setResult(1,intent);
			AddPage.this.finish();
		}
	}
	
}
