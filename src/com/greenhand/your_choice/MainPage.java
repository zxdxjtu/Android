package com.greenhand.your_choice;

import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.greenhand.your_choice.SlyderView;
import com.greenhand.your_choice.R;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainPage extends Activity {

	private ImageButton buttonToRight;
	private ImageButton buttonToLeft;
	private ImageButton addButton;
	private ImageButton setButton;
	private RelativeLayout action;
	private View first;
	private View second;
	private TextView showInf;
	private String s;
	ArrayList<String> list;
	LinearLayout shan;
	SlyderView slyder;
	GestureDetector gestures;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);			
		setContentView(R.layout.activity_main_page);
		
		second = View.inflate(MainPage.this, R.layout.activity_second_page, null);
		first = View.inflate(this, R.layout.activity_first_page, null);
		
		action = (RelativeLayout)findViewById(R.id.action);
		action.addView(first);
		
		shan = (LinearLayout)findViewById(R.id.shan);
		slyder = new SlyderView(this);
		shan.addView(slyder);
		
		showInf = (TextView)findViewById(R.id.InfTextView);
		showInf.setText("您输入的选项为："+s);
		
		buttonToRight = (ImageButton)findViewById(R.id.buttonToRight);
		buttonToRight.setOnClickListener(new ButtonListenerToRight());
		
		buttonToLeft = (ImageButton)findViewById(R.id.buttonToLeft);
		buttonToLeft.setImageDrawable(getResources().getDrawable(R.drawable.toleft1));
		buttonToLeft.setOnClickListener(new ButtonListenerToLeft());
		
		addButton = (ImageButton)findViewById(R.id.addButton);
		addButton.setOnClickListener(new ButtonListenerAdd());	
		
		setButton = (ImageButton)findViewById(R.id.setButton);
		setButton.setOnClickListener(new ButtonListenerSet());	//暂时将设置按钮用作数据库的清空按钮
		
		gestures = new GestureDetector(this,new turnTableListener());
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		return gestures.onTouchEvent(event);
	}

	class turnTableListener extends SimpleOnGestureListener{
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			// TODO Auto-generated method stub
			float centerx = (getWindowManager().getDefaultDisplay().getWidth())/2;
			float centery = (getWindowManager().getDefaultDisplay().getHeight())/2;
			float r = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 150, getResources().getDisplayMetrics());
			float xleft = centerx - r;
			float xright = centerx + r;
			float ytop = centery - r;
			float ybottom = centery + r;
			if((xleft < e1.getX()) && (e1.getX()<xright) && (e1.getY() > ytop) && (e1.getY() < ybottom))
			{
				slyder.showAnimation(slyder);
			}
			return super.onFling(e1, e2, velocityX, velocityY);
		}
	}
	class ButtonListenerToRight implements OnClickListener
	{

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			buttonToLeft.setImageDrawable(getResources().getDrawable(R.drawable.toleft));
			buttonToRight.setImageDrawable(getResources().getDrawable(R.drawable.toright1));
			action.removeAllViews();
			action.addView(second);
			showInf = (TextView)findViewById(R.id.InfTextView);
    		showInf.setText("您输入的选项为："+s);
		}
		
	}
	
	class ButtonListenerToLeft implements OnClickListener
	{

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			buttonToLeft.setImageDrawable(getResources().getDrawable(R.drawable.toleft1));
			buttonToRight.setImageDrawable(getResources().getDrawable(R.drawable.toright));
			action.removeAllViews();
			action.addView(first);
			showInf = (TextView)findViewById(R.id.InfTextView);
    		showInf.setText("您输入的选项为："+s);
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
			overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
		}
		
	}
	
	//暂时将设置按钮用作数据库的清空按钮
	class ButtonListenerSet implements OnClickListener{
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			DBhelper dbp = new DBhelper(MainPage.this);
			dbp.clear();
		}
	}
	
	class ButtonListenerForRoll implements OnClickListener{
		SlyderView tmp;
		public ButtonListenerForRoll(SlyderView slyder) {
			tmp = slyder;
		}
		public void onClick(View arg0){
			tmp.showAnimation(tmp);
		}
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		switch (resultCode) 
		{   
        	case 1:
        		s = data.getStringExtra("result");
        		showInf = (TextView)findViewById(R.id.InfTextView);
        		showInf.setText("您输入的选项为："+s);
        		if(s != null)
        		{
        			Gson gson = new Gson();
        			list = gson.fromJson(s, new TypeToken<ArrayList<String>>(){}.getType());
        			slyder.setdrgrees(list);
        		}
//        		shan.removeAllViews();
//        		shan.addView(slyder);
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
