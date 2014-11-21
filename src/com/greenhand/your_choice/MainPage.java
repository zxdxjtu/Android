package com.greenhand.your_choice;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainPage extends Activity {

	private ImageButton buttonToRight;
	private ImageButton buttonToLeft;
	private ImageButton addButton;
	private RelativeLayout action;
	private View first;
	private View second;
	private TextView showInf;
	private Bundle bundle;
	private Options option;
	private String s;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);			
		setContentView(R.layout.activity_main_page);
		
		second = View.inflate(MainPage.this, R.layout.activity_second_page, null);
		first = View.inflate(this, R.layout.activity_first_page, null);
		
		action = (RelativeLayout)findViewById(R.id.action);
		action.addView(first);
		
		showInf = (TextView)findViewById(R.id.InfTextView);
		showInf.setText("�������ѡ��Ϊ��"+s);
		
		buttonToRight = (ImageButton)findViewById(R.id.buttonToRight);
		buttonToRight.setOnClickListener(new ButtonListenerToRight());
		
		buttonToLeft = (ImageButton)findViewById(R.id.buttonToLeft);
		buttonToLeft.setOnClickListener(new ButtonListenerToLeft());
		
		addButton = (ImageButton)findViewById(R.id.addButton);
		addButton.setOnClickListener(new ButtonListenerAdd());				
	}

	class ButtonListenerToRight implements OnClickListener
	{

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			action.removeAllViews();
			action.addView(second);
			showInf = (TextView)findViewById(R.id.InfTextView);
    		showInf.setText("�������ѡ��Ϊ��"+s);
		}
		
	}
	
	class ButtonListenerToLeft implements OnClickListener
	{

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			action.removeAllViews();
			action.addView(first);
			showInf = (TextView)findViewById(R.id.InfTextView);
    		showInf.setText("�������ѡ��Ϊ��"+s);
		}
		
	}
	
	class ButtonListenerAdd implements OnClickListener
	{

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			Intent intent = new Intent();
			intent.setClass(MainPage.this, AddPage.class);
			startActivityForResult(intent,0);
			
		}
		
	}
	
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		switch (resultCode) 
		{   
        	case 1:     
        		bundle = data.getExtras();
        		option = bundle.getParcelable("options");
        		s = option.getString();
        		showInf = (TextView)findViewById(R.id.InfTextView);
        		showInf.setText("�������ѡ��Ϊ��"+s);
            	break;  
        	default:  
        		break;  
        }     
	}
	
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) 
	{
        if (keyCode == KeyEvent.KEYCODE_BACK) 
        {
            new AlertDialog.Builder(this)
            	.setTitle("prompt")
            	.setMessage("are you sure to exit?")
            	.setNegativeButton("cancle",
            			new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,int which) 
                                {
                                	
                                }
                            })
                .setPositiveButton("yes",
                		new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int whichButton) 
                                {
                                	MainPage.this.finish();
                                   // finish();
                                }
                            }).show();
            return true;
        }
        else 
        {
            return super.onKeyDown(keyCode, event);
        }
    }


	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		System.exit(0);
	}

}