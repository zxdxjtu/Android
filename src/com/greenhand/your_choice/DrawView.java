package com.greenhand.your_choice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.R.dimen;
import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class DrawView 
{
	private ArrayList<HashMap<String, Object>> drawItem;
	private HashMap<String, Object> map;
	private GridView drawGridView;
	private MainPage mainpage;
	private int[] colos = new int[] {0xfed9c960,0xfe57c8c8,0xfe9fe558,0xfef6b000,0xfef46212,0xfecf2911,0xfe9d3011 };
	private LayoutInflater mInflater;
	
	public DrawView(MainPage m) {
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
			drawItem = new ArrayList<HashMap<String, Object>>();
			for(int i=0;i<list.size();i++)
			{
				map = new HashMap<String, Object>();
				map.put("drawText", list.get(i));
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
			container.startAnimation(changePostionAnim(pos, 4));
			
			
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
	public  Animation changePostionAnim(int pos1,int pos2)
	{
		mypointclass p1 = new mypointclass(pos1);
		mypointclass p2 = new mypointclass(pos2);
		mypointclass p = new mypointclass();
		TranslateAnimation ta1 = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF, p2.x-p1.x, 
				Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF, p2.y-p1.y);
		ta1.setDuration(400);
		ta1.setStartOffset(pos1*200);
		ta1.setFillAfter(true);
		TranslateAnimation ta2 = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, p2.x-p1.x,Animation.RELATIVE_TO_SELF, 0f, 
				Animation.RELATIVE_TO_SELF, p2.y-p1.y,Animation.RELATIVE_TO_SELF, 0f);
		ta2.setDuration(400);
		ta2.setStartOffset(pos1*200+(drawItem.size()+1)*200);
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

}
