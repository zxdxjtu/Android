package com.greenhand.your_choice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
		public drawAdapter(Context context,
				List<? extends Map<String, ?>> data, int resource,
				String[] from, int[] to) {
			super(context, data, resource, from, to);
			// TODO Auto-generated constructor stub
			mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if(drawItem.get(0).get("drawText") != "")
			{
				convertView = mInflater.inflate(R.layout.drawstyle, null);
				View drawTextContainer = (View)convertView.findViewById(R.id.drawTextContainer);
				int colorpos  = position % 7;
				drawTextContainer.setBackgroundColor(colos[colorpos]);
			}
			return super.getView(position, convertView, parent);
		}
		
	}
	public void preAnim()
	{
		
	}

}
