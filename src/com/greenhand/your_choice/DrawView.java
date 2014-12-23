package com.greenhand.your_choice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.renren.api.connect.android.Renren;
import com.renren.api.connect.android.exception.RenrenAuthError;
import com.renren.api.connect.android.view.RenrenAuthListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class DrawView 
{
	private ArrayList<HashMap<String, Object>> drawItem;
	private HashMap<String, Object> map;
	private GridView drawGridView;
	private MainPage mainpage;
	private int[] colos = new int[] {0xfed9c960,0xfe57c8c8,0xfe9fe558,0xfef6b000,0xfef46212,0xfecf2911,0xfe9d3011 };
	private LayoutInflater mInflater;
	private int[] order1;
	private int[] order2;
	private int[] order3;
	private AlertDialog mydialog;
	private String dialogMessage;
	final Renren renren;
	
	public DrawView(MainPage m, Renren rr) {
		// TODO Auto-generated constructor stub
		if(drawItem == null)
		{
			drawItem = new ArrayList<HashMap<String, Object>>();
			map = new HashMap<String, Object>();
			map.put("drawText","");
			for(int i=0;i<9;i++)
			{
				drawItem.add(map);
			}
		}
		mainpage = m;
		renren = rr;
	}
	
	public void setDrawView()
	{
		drawAdapter myAdapter = new drawAdapter(mainpage, drawItem, R.layout.drawstyle, 
				new String[]{"drawText"}, new int[]{R.id.drawText});
		drawGridView = (GridView)mainpage.findViewById(R.id.drawcontainer);
		drawGridView.setAdapter(myAdapter);
	}
	
	public void setDrawItem(ArrayList<String> list) {
		// TODO Auto-generated method stub
		if(list != null)
		{
			String temptext;
			drawItem = new ArrayList<HashMap<String, Object>>();
			for(int i=0;i<list.size();i++)
			{
				map = new HashMap<String, Object>();
				temptext = list.get(i);
				if(temptext.length() > 5)
					temptext = temptext.substring(0, 4) + "…";
				map.put("drawText", temptext);
				drawItem.add(map);
			}
		}
	}
	
	public class drawAdapter extends SimpleAdapter
	{
		Context c;
		public drawAdapter(Context context,
				List<? extends Map<String, ?>> data, int resource,
				String[] from, int[] to) {
			super(context, data, resource, from, to);
			// TODO Auto-generated constructor stub
			mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			c = context;
			order1 = new int[drawItem.size()];
			order2 = new int[drawItem.size()];
			order3 = new int[drawItem.size()];
			order1 = setorder(order1);
			order2 = setorder(order2);
			order3 = setorder(order3);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if(drawItem.get(0).get("drawText") != "")
			{
				convertView = mInflater.inflate(R.layout.drawstyle, null);
				View drawTextContainer = (View)convertView.findViewById(R.id.drawTextContainer);
				View drawTextBg = (View)convertView.findViewById(R.id.drawTextBg);
				TextView drawText = (TextView)convertView.findViewById(R.id.drawText);
				int colorpos  = position % 7;
				drawTextBg.setBackgroundColor(colos[colorpos]);
				Animation animation = AnimationUtils.loadAnimation(c, R.anim.back_scale);
				animation.setAnimationListener(new drawToBackAnimListener(drawTextContainer,drawTextBg,drawText,position));
				drawTextContainer.startAnimation(animation);
			}
			return super.getView(position, convertView, parent);
		}
		
	}
	
	public class drawAdapter2 extends SimpleAdapter
	{
		Context c;
		public drawAdapter2(Context context,
				List<? extends Map<String, ?>> data, int resource,
				String[] from, int[] to) {
			super(context, data, resource, from, to);
			// TODO Auto-generated constructor stub
			mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			c = context;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			convertView = mInflater.inflate(R.layout.drawstyle, null);
			View drawTextContainer = (View)convertView.findViewById(R.id.drawTextContainer);
			TextView drawText = (TextView)convertView.findViewById(R.id.drawText);
			drawText.setVisibility(View.GONE);
			drawTextContainer.setOnClickListener(new cardClickListener(convertView));
			return super.getView(position, convertView, parent);
		}
		
	}
	
	class drawToBackAnimListener implements AnimationListener{

		View container;
		View bg;
		TextView text;
		int pos;
		public drawToBackAnimListener(View drawTextContainer, View drawTextBg, TextView drawText, int position) 
		{
			// TODO Auto-generated constructor stub
			pos = position;
			container = drawTextContainer;
			bg = drawTextBg;
			text = drawText;
		}
		@Override
		public void onAnimationEnd(Animation arg0) {
			// TODO Auto-generated method stub
			bg.setBackgroundResource(R.drawable.cover);
			text.setVisibility(View.GONE);
			TranslateAnimation ta = changePostionAnim(pos, order1[pos]);
			container.startAnimation(ta);
			ta.setAnimationListener(new TransAnimListener1(container,pos));
		}
		@Override
		public void onAnimationRepeat(Animation arg0) {
			// TODO Auto-generated method stub		
		}
		@Override
		public void onAnimationStart(Animation arg0) {
			// TODO Auto-generated method stub	
		}
	}

	class cardClickListener implements OnClickListener{
		
		View c;
		public cardClickListener(View convertView) {
			// TODO Auto-generated constructor stub
			c = convertView;
		}

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			View drawTextBg = (View)c.findViewById(R.id.drawTextBg);
			View drawText = (View)c.findViewById(R.id.drawText);
			Animation animation = AnimationUtils.loadAnimation(mainpage,R.anim.front_scale);
			animation.setAnimationListener(new drawToFrontAnimListener(drawTextBg,drawText));
			arg0.startAnimation(animation);
		}
		
	}
	class drawToFrontAnimListener implements AnimationListener{

		View textbg;
		View text;
		public drawToFrontAnimListener(View drawTextBg, View drawText) {
			// TODO Auto-generated constructor stub
			textbg = drawTextBg;
			text = drawText;
		}

		@Override
		public void onAnimationEnd(Animation arg0) {
			// TODO Auto-generated method stub
			textbg.setBackgroundColor(colos[(int) (Math.random()*7)]);
			text.setVisibility(View.VISIBLE);
			TextView text1 = (TextView) text;
			dialogMessage = (String) text1.getText();
			showdialog();
		}

		@Override
		public void onAnimationRepeat(Animation arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onAnimationStart(Animation arg0) {
			// TODO Auto-generated method stub
			
		}
		
	}
	class TransAnimListener1 implements AnimationListener{

		View c;
		int pos;
		public TransAnimListener1(View container, int positon) {
			// TODO Auto-generated constructor stub
			c = container;
			pos = positon;
		}
		@Override
		public void onAnimationEnd(Animation arg0) {
			// TODO Auto-generated method stub
			TranslateAnimation ta = changePostionAnim(pos, order2[pos]);
			ta.setAnimationListener(new TransAnimListener2(c,pos));
			c.startAnimation(ta);
		}
		@Override
		public void onAnimationRepeat(Animation arg0) {
			// TODO Auto-generated method stub	
		}
		@Override
		public void onAnimationStart(Animation arg0) {
			// TODO Auto-generated method stub	
		}
	}
	class TransAnimListener2 implements AnimationListener{

		View c;
		int pos;
		public TransAnimListener2(View container, int positon) {
			// TODO Auto-generated constructor stub
			c = container;
			pos = positon;
		}

		@Override
		public void onAnimationEnd(Animation arg0) {
			// TODO Auto-generated method stub
			TranslateAnimation ta = changePostionAnim(pos, order3[pos]);
			if(pos == (drawItem.size()-1))
				ta.setAnimationListener(new TransAnimListener3());
			c.startAnimation(ta);
		}

		@Override
		public void onAnimationRepeat(Animation arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onAnimationStart(Animation arg0) {
			// TODO Auto-generated method stub
			
		}
		
	}
	class TransAnimListener3 implements AnimationListener{

		@Override
		public void onAnimationEnd(Animation arg0) {
			// TODO Auto-generated method stub	
			Collections.shuffle(drawItem);
			drawAdapter2 myAdapter2 = new drawAdapter2(mainpage, drawItem, R.layout.drawstyle, 
				new String[]{"drawText"}, new int[]{R.id.drawText});
			drawGridView.setAdapter(myAdapter2);
		}
		@Override
		public void onAnimationRepeat(Animation arg0) {
			// TODO Auto-generated method stub	
		}
		@Override
		public void onAnimationStart(Animation arg0) {
			// TODO Auto-generated method stub
		}
	}
	class drawClickListener implements OnClickListener{

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			
		}
		
	}
	public TranslateAnimation changePostionAnim(int pos1,int pos2)
	{
		mypointclass p1 = new mypointclass(pos1);
		mypointclass p2 = new mypointclass(pos2);
		TranslateAnimation ta1 = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF, p2.x-p1.x, 
				Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF, p2.y-p1.y);
		ta1.setDuration(300);
//		ta1.setStartOffset(pos1*150);
		ta1.setFillAfter(true);
		return ta1;
	}

	class mypointclass
	{
		float x;
		float y;
		mypointclass()
		{
			x=0;
			y=0;
		}
		mypointclass(int pos)
		{
			y = pos/3;
			x = pos%3;
		}
	}

	public int[] setorder(int[] order)
	{
		int size = drawItem.size();
		int nowsize = size;
		int target;
		int randomnum;
		for(int i=0;i<size;i++)
			order[i] = -1;
		for(int i=0;i<size;i++)
		{
			if(order[i] == -1)
			{
				randomnum = (int) (Math.random()*nowsize);
//				System.out.println("randomnum = " + randomnum);
				for(target=0;target<size;target++)
				{
					if(order[target] == -1)
					{
						randomnum--;
						if(randomnum == -1)
							break;
					}
				}
//				System.out.println(target);
				order[target] = i;
				order[i] = target;
				if(target == i)
					nowsize--;
				else
					nowsize = nowsize-2;
			}
		}
//		for(int i=0;i<drawItem.size();i++)
//			System.out.println(order[i] + ",");
		return order;
	}
	private void showdialog()
	{
		mydialog = new AlertDialog.Builder(mainpage).create();
		mydialog.setOnCancelListener(new cancelListener());
		mydialog.show();
		Window dialogwindow = mydialog.getWindow();
		dialogwindow.setContentView(R.layout.result_dialog);
		Button ok = (Button)dialogwindow.findViewById(R.id.dialogok);
		Button cancel = (Button)dialogwindow.findViewById(R.id.dialogcancel);
		TextView message = (TextView)dialogwindow.findViewById(R.id.dialogmessage);
		message.setText(dialogMessage);	
		ok.setOnClickListener(new dialogOkListener());
		cancel.setOnClickListener(new dialogCancelListener());
	}
	class cancelListener implements OnCancelListener{

		@Override
		public void onCancel(DialogInterface arg0) {
			// TODO Auto-generated method stub
			setDrawView();
		}
		
	}
	class dialogCancelListener implements OnClickListener{

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			mydialog.dismiss();
			setDrawView();
		}
		
	}
	class dialogOkListener implements OnClickListener{

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			//分享的代码
			ConnectivityManager con=(ConnectivityManager)mainpage.getSystemService(Activity.CONNECTIVITY_SERVICE);  
			boolean wifi=con.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();  
			boolean internet=con.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();  
			if(wifi|internet){  
			    //执行相关操作  
			
			Toast.makeText(mainpage, "renren is"+renren, Toast.LENGTH_SHORT).show();
			
			if (renren.isSessionKeyValid()==false) {
				renren.authorize(mainpage, new String[]{"status_update","photo_upload"},new RenrenAuthListener(){
					@Override
					public void onComplete(Bundle values) {
						Bundle bundle=new Bundle();
						bundle.putString("method", "status.set");
						bundle.putString("status","YOUR CHOICE帮我选中了" + dialogMessage + "!妈妈再也不用担心我的选择强迫症啦！！");
						renren.requestJSON(bundle);
						Toast.makeText(mainpage, "onComplete", Toast.LENGTH_SHORT).show();
					}
					@Override
					public void onRenrenAuthError(RenrenAuthError renrenAuthError) {
						Toast.makeText(mainpage, "onRenrenAuthError", Toast.LENGTH_SHORT).show();
						System.out.println(renrenAuthError.getError()+renrenAuthError.getMessage()+renrenAuthError.getErrorDescription()+renrenAuthError.getLocalizedMessage());
					}
					@Override
					public void onCancelLogin() {
						Toast.makeText(mainpage, "onCancelLogin", Toast.LENGTH_SHORT).show();
					}
					@Override
					public void onCancelAuth(Bundle values) {
						Toast.makeText(mainpage, "onCancelAuth", Toast.LENGTH_SHORT).show();
					}
				});
				
			
				
			}
			else
			{
				Bundle bundle=new Bundle();
				bundle.putString("method", "status.set");
				bundle.putString("status","YOUR CHOICE帮我选中了" + dialogMessage + "!妈妈再也不用担心我的选择强迫症啦！！");
				renren.requestJSON(bundle);
			}
			
			}
			else{  
			    Toast.makeText(mainpage.getApplicationContext(),  
			            "请您检查您的网络连接", Toast.LENGTH_LONG)  
			            .show();  
			}
			mydialog.dismiss();
			setDrawView();
		}
		
	}

}
