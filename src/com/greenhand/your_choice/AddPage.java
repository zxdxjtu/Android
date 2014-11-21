package com.greenhand.your_choice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class AddPage extends Activity{

	private ListView inputListView;
	private GridView chooseGridView;
	private Button addButton;
	private ArrayList<HashMap<String, Object>> listItem;
	private ArrayList<HashMap<String, Object>> gridItem;
	private HashMap<String, Object> map;
	private HashMap<String, Object> gridmap;
	private SimpleAdapter myAdapter;
	private SimpleAdapter gridAdapter;
	private Button okButton;
	private Button clearButton;
	private LayoutInflater mInflater;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_page);
		
		inputListView = (ListView)findViewById(R.id.inputListView);
		chooseGridView = (GridView)findViewById(R.id.chooseGridView);
		addButton = (Button)findViewById(R.id.addButton);
		okButton = (Button)findViewById(R.id.okButton);
		clearButton = (Button)findViewById(R.id.clearButton);
		
//		itemDeleteButton.setOnClickListener(new deleteButtonListener());
		addButton.setOnClickListener(new addButtonListener());
		okButton.setOnClickListener(new okButtonListener());
		clearButton.setOnClickListener(new clearButtonListener());
		listItem = new ArrayList<HashMap<String, Object>>();
		gridItem = new ArrayList<HashMap<String, Object>>();
		
		map = new HashMap<String, Object>();
		map.put("itemEditText", "请输入让您纠结的选项");
		map.put("itemDeleteButton", "删除");
		listItem.add(map);
		myAdapter = new myAdapterclass(this, listItem, R.layout.edittextstyle, 
				new String[]{"itemEditText","itemDeleteButton"} ,new int[]{R.id.itemEditText ,R.id.itemDeleteButton});
		inputListView.setAdapter(myAdapter);
		
		gridmap = new HashMap<String, Object>();
		gridmap.put("gridItemText", "");
		gridItem.add(gridmap);
		gridAdapter = new myGridAdapterclass(this, gridItem, R.layout.gridstyle,
				new String[]{"gridItemText"}, new int[]{R.id.gridItemButton});
		chooseGridView.setAdapter(gridAdapter);
	}
	
	class myGridAdapterclass extends SimpleAdapter{

		int textId;
		public myGridAdapterclass(Context context,
				List<? extends Map<String, ?>> data, int resource,
				String[] from, int[] to) {
			super(context, data, resource, from, to);
			// TODO Auto-generated constructor stub
			textId = to[0];
			mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			convertView = mInflater.inflate(R.layout.gridstyle, null);
			Button gridText = (Button)convertView.findViewById(textId);
			gridText.setOnClickListener(new gridButtonListener());
			return super.getView(position, convertView, parent);
		}
		
	}
	class myAdapterclass extends SimpleAdapter{

		int buttonId,textId;
		public myAdapterclass(Context context,
				List<? extends Map<String, ?>> data, int resource,
				String[] from, int[] to) {
			super(context, data, resource, from, to);
			// TODO Auto-generated constructor stub
			textId = to[0];
			buttonId = to[1];
//			System.out.println(buttonId);
			mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			convertView = mInflater.inflate(R.layout.edittextstyle, null);
			Button delButton = (Button)convertView.findViewById(buttonId);
			EditText text = (EditText)convertView.findViewById(textId);
			
			text.setOnFocusChangeListener(new textFocusListener());
			text.addTextChangedListener(new textListener(position));
			delButton.setOnClickListener(new deleteButtonListener(position));
			
			return super.getView(position, convertView, parent);
			
		}
	}
	class textFocusListener implements OnFocusChangeListener{

		@Override
		public void onFocusChange(View arg0, boolean arg1) {
			// TODO Auto-generated method stub
			if(arg1)
			{
				String str = (((EditText)arg0).getText()).toString();
				String yuan = "请输入让您纠结的选项";
				if(str.equals(yuan))
					((EditText)arg0).setText("");
			}
		}
		
	}
	class textListener implements TextWatcher{

		int pos;
		public textListener(int position) {
			// TODO Auto-generated constructor stub
			pos = position;
		}

		@SuppressLint("NewApi")
		@Override
		public void afterTextChanged(Editable arg0) {
			// TODO Auto-generated method stub
			DBhelper dbp = new DBhelper(AddPage.this);
			ArrayList<String> list;
			String gridtextcontent;
			String findstr = null;		
			String s = arg0.toString();
			System.out.println(s);
			map = listItem.get(pos);
			map.put("itemEditText", s);
			listItem.set(pos, map);
			
			if(s.isEmpty())
			{
				list = dbp.searchhot();
				if(list != null)
				{
					gridItem.clear();
					for(int i=0;i<list.size();i++)
					{
						gridtextcontent = list.get(i);
						gridmap = new HashMap<String, Object>();
						gridmap.put("gridItemText", gridtextcontent);
						gridItem.add(gridmap);
					}
				}
				gridAdapter.notifyDataSetChanged();
			}
			else
			{
				try {
					findstr = dbp.search(s);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("grid输出"+findstr);
				Gson gson = new Gson();
				list = gson.fromJson(findstr, new TypeToken<ArrayList<String>>(){}.getType());
				if(list == null)
				{
					dbp = new DBhelper(AddPage.this);
					list = dbp.searchhot();
					if(list != null)
					{
						gridItem.clear();
						for(int i=0;i<list.size();i++)
						{
							gridtextcontent = list.get(i);
							gridmap = new HashMap<String, Object>();
							gridmap.put("gridItemText", gridtextcontent);
							gridItem.add(gridmap);
						}
						gridAdapter.notifyDataSetChanged();
					}
					else
					{
						gridItem.clear();
						gridAdapter.notifyDataSetChanged();
					}
				}
				else
				{
					gridItem.clear();
					for(int i=0;i<list.size();i++)
					{
						gridtextcontent = list.get(i);
						gridmap = new HashMap<String, Object>();
						gridmap.put("gridItemText", gridtextcontent);
						gridItem.add(gridmap);
					}
					gridAdapter.notifyDataSetChanged();
				}
			}
		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
			// TODO Auto-generated method stub
			
		}
		
	}
	class deleteButtonListener implements OnClickListener{

		int pos = 0;
		public deleteButtonListener(int position) {
			// TODO Auto-generated constructor stub
			pos = position;
		}

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
//			System.out.println(pos);
			listItem.remove(pos);
			myAdapter.notifyDataSetChanged();
		}
		
	}
	class okButtonListener implements OnClickListener{

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			DBhelper dbp = new DBhelper(AddPage.this);
			ArrayList<String> list = new ArrayList<String>(); 
			for(int i=0;i<listItem.size();i++)
			{
				map = listItem.get(i);
				String str = (String) map.get("itemEditText");
				list.add(str);
			}
//			net.sf.json.JSONArray json = net.sf.json.JSONArray.fromObject(list);
			Gson g = new Gson();
			String jsonstr = g.toJson(list);
			try {
				dbp.save(jsonstr);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	class clearButtonListener implements OnClickListener{
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			DBhelper dbp = new DBhelper(AddPage.this);
			dbp.clear();
		}
		
	}
	class addButtonListener implements OnClickListener{

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			map = new HashMap<String, Object>();
			map.put("itemEditText", "请输入让您纠结的选项");
			map.put("itemDeleteButton", "删除");
			listItem.add(map);
			myAdapter.notifyDataSetChanged();
		}
		
	}
	class gridButtonListener implements OnClickListener{

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			boolean havehole = false;
			String text;
			Button b = (Button) arg0;
			String changetext = (String) b.getText();
			for(int i=0;i<listItem.size();i++)
			{
				text = (String) (listItem.get(i)).get("itemEditText");
				if(text.equals("") || text.equals("请输入让您纠结的选项"))
				{
					havehole = true;
//					System.out.println("你完蛋啦");
					map = new HashMap<String, Object>();
					map.put("itemEditText", changetext);
					map.put("itemDeleteButton", "删除");
					listItem.set(i, map);
					myAdapter.notifyDataSetChanged();
					break;
				}
			}
			if(!havehole)
			{
				boolean repeat = false;
				for(int i=0;i<listItem.size();i++)
				{
					text = (String) (listItem.get(i)).get("itemEditText");
					if(text.equals(changetext))
					{
						repeat = true;
						break;
					}
				}
				if(!repeat)
				{
					map = new HashMap<String, Object>();
					map.put("itemEditText", changetext);
					map.put("itemDeleteButton", "删除");
					listItem.add(map);
					myAdapter.notifyDataSetChanged();
				}
			}
			
		}
		
	}
	
}
