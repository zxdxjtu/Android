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
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class AddPage extends Activity{

	private TextView warning;
	private ListView inputListView;
	private ListView chooseGridView;
	private ImageButton backButton;
	private Button addButton;
	private ArrayList<HashMap<String, Object>> listItem;
	private ArrayList<HashMap<String, Object>> gridItem;
	private HashMap<String, Object> map;
	private HashMap<String, Object> gridmap;
	private SimpleAdapter myAdapter;
	private SimpleAdapter gridAdapter;
	private Button okButton;
//	private Button clearButton;
	private LayoutInflater mInflater;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_page);
		
		warning = (TextView)findViewById(R.id.addpagewarning);
		inputListView = (ListView)findViewById(R.id.inputListView);
		chooseGridView = (ListView)findViewById(R.id.chooseGridView);
		backButton = (ImageButton)findViewById(R.id.addPageBack);
		addButton = (Button)findViewById(R.id.addButton);
		okButton = (Button)findViewById(R.id.okButton);
//		clearButton = (Button)findViewById(R.id.clearButton);
		
		backButton.setOnClickListener(new backButtonListener());
		addButton.setOnClickListener(new addButtonListener());
		okButton.setOnClickListener(new okButtonListener());
//		clearButton.setOnClickListener(new clearButtonListener());
		listItem = new ArrayList<HashMap<String, Object>>();
		gridItem = new ArrayList<HashMap<String, Object>>();
		
		map = new HashMap<String, Object>();
		map.put("itemEditText", "");
		listItem.add(map);
		myAdapter = new myAdapterclass(this, listItem, R.layout.edittextstyle, 
				new String[]{"itemEditText","itemDeleteButton"} ,new int[]{R.id.itemEditText ,R.id.itemDeleteButton});
		inputListView.setAdapter(myAdapter);
		
		gridAdapter = new myGridAdapterclass(this, gridItem, R.layout.addpagechoosestyle1,
				new String[]{"gridItemText"}, new int[]{R.id.addpagechoosetext});
		chooseGridView.setAdapter(gridAdapter);
	}
	
	class myGridAdapterclass extends SimpleAdapter{

		int textId;
		public myGridAdapterclass(Context context,List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) 
		{
			super(context, data, resource, from, to);
			// TODO Auto-generated constructor stub
			textId = to[0];
			mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if((position % 2) == 0)
			{
				convertView = mInflater.inflate(R.layout.addpagechoosestyle1, null);
			}
			else
			{
				convertView = mInflater.inflate(R.layout.addpagechoosestyle2,null);
			}
			TextView gridText = (TextView)convertView.findViewById(textId);
			gridText.setOnClickListener(new gridButtonListener());
			return super.getView(position, convertView, parent);
		}
		
	}
	class myAdapterclass extends SimpleAdapter{

		int buttonId,textId;
		public myAdapterclass(Context context,List<? extends Map<String, ?>> data, int resource,String[] from, int[] to) 
		{
			super(context, data, resource, from, to);
			// TODO Auto-generated constructor stub
			textId = to[0];
			buttonId = to[1];
			mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			convertView = mInflater.inflate(R.layout.edittextstyle, null);
			ImageButton delButton = (ImageButton)convertView.findViewById(buttonId);
			EditText text = (EditText)convertView.findViewById(textId);
			
//			text.setOnFocusChangeListener(new textFocusListener());
			text.addTextChangedListener(new textListener(position));
			delButton.setOnClickListener(new deleteButtonListener(position));
			
			return super.getView(position, convertView, parent);
			
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
			if(!listItem.isEmpty())
			{
				map = listItem.get(pos);
				map.put("itemEditText", s);
				listItem.set(pos, map);
			}		
			if(s.length()==0)
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
	class backButtonListener implements OnClickListener{

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			AddPage.this.finish();
			overridePendingTransition(R.anim.push_back_in,R.anim.push_back_out);
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
			listItem.remove(pos);
			myAdapter.notifyDataSetChanged();
		}
		
	}
	class okButtonListener implements OnClickListener{

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			map = listItem.get(0);
			String str = (String) map.get("itemEditText");
			if(!listItem.isEmpty() && !str.equals(""))
			{
				DBhelper dbp = new DBhelper(AddPage.this);
				ArrayList<String> list = new ArrayList<String>(); 
				for(int i=0;i<listItem.size();i++)
				{
					map = listItem.get(i);
					str = (String) map.get("itemEditText");
					if(!str.equals(""))
						list.add(str);
				}
				Gson g = new Gson();
				String jsonstr = g.toJson(list);
				try {
					dbp.save(jsonstr);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Intent intent = new Intent(); 
				intent.putExtra("result",jsonstr);
				setResult(1, intent); // 设置结果数据  
				AddPage.this.finish(); // 关闭Activity  
				overridePendingTransition(R.anim.push_back_in,R.anim.push_back_out);
			}
			else
			{
				warning.setVisibility(View.VISIBLE);
				AnimationSet animationSet = new AnimationSet(true);
		        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
		        alphaAnimation.setDuration(500);
		        animationSet.addAnimation(alphaAnimation);
		        AlphaAnimation alphaAnimation2 = new AlphaAnimation(1, 0);
		        alphaAnimation2.setDuration(1000);
		        alphaAnimation2.setStartOffset(1000);
		        animationSet.addAnimation(alphaAnimation2);		        
		        animationSet.setAnimationListener(new warningAnimListener());
		        warning.startAnimation(animationSet);
			}
		}
		
	}
	class warningAnimListener implements AnimationListener{

		@Override
		public void onAnimationEnd(Animation arg0) {
			// TODO Auto-generated method stub
			warning.setVisibility(View.GONE);
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
//这里是删除数据库的代码，设置里可以用
//	class clearButtonListener implements OnClickListener{
//		@Override
//		public void onClick(View arg0) {
//			// TODO Auto-generated method stub
//			DBhelper dbp = new DBhelper(AddPage.this);
//			dbp.clear();
//		}
//		
//	}
	class addButtonListener implements OnClickListener{

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			map = new HashMap<String, Object>();
			map.put("itemEditText", "");
			listItem.add(map);
			myAdapter.notifyDataSetChanged();
			
			inputListView.setSelection(inputListView.getBottom());
		}
		
	}
	class gridButtonListener implements OnClickListener{

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			boolean havehole = false;
			String text;
			TextView b = (TextView) arg0;
			String changetext = (String) b.getText();
			for(int i=0;i<listItem.size();i++)
			{
				text = (String) (listItem.get(i)).get("itemEditText");
				if(text.equals(""))
				{
					havehole = true;
					map = new HashMap<String, Object>();
					map.put("itemEditText", changetext);
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
					listItem.add(map);
					myAdapter.notifyDataSetChanged();
				}
			}
			inputListView.setSelection(inputListView.getBottom());
		}
		
	}
	
}
