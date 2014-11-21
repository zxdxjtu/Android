package com.greenhand.your_choice;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBhelper{

	private static final String DATABASE_NAME = "DBforChoice.db";  
	private static final int DATABASE_VERSION = 1;
	SQLiteDatabase db; 
	DBhelper(Context context){
		db = context.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE,null); 
		CreateTable();   
	}
	
	private void CreateTable() {
		// TODO Auto-generated method stub
		String createstr = "create table if not exists t_gather (ID INTEGER PRIMARY KEY autoincrement, gather text)";
        db.execSQL(createstr);
        createstr = "create table if not exists t_hot (name text, hotcount integer)";
        db.execSQL(createstr);
	}

	public void clear()
	{
		db.execSQL("delete from t_gather");
		db.execSQL("delete from t_hot");
		db.close();
	}
	public void save(String s) throws JSONException{
		int datacount = 0;
		String sql;
		int idfirst = 0;
		int idsecond;
		String name;
		int hotcount = 0;
		String findstrfirst = null;
		String findstrsecond = null;
		Cursor c;
		String element;
		String element2;
		JSONArray sumjson;
		JSONArray tempjson;
		boolean check = false;
		Gson gson = new Gson();
		ArrayList<String> list = gson.fromJson(s, new TypeToken<ArrayList<String>>(){}.getType());
		
		for(int i=0;i<list.size();i++)
		{
			name = list.get(i);
			sql = "select count(*) from t_hot where name = '" + name + "'";
			c = db.rawQuery(sql,null);
			while(c.moveToNext())
			{
				datacount = c.getInt(0);
				System.out.println("显示行数" + datacount);
			}
			if(datacount == 0)
			{
				sql = "insert into t_hot (name) values ('"+ name +"')";
				db.execSQL(sql);
			}
			else
			{
				sql = "select * from t_hot where name = '" + name + "'";
				c = db.rawQuery(sql,null);
				while(c.moveToNext())
				{
					hotcount = c.getInt(c.getColumnIndex("hotcount"));
					hotcount++;
					System.out.println(hotcount);
				}
				db.execSQL("update t_hot set hotcount = " + hotcount + " where name = '" + name + "'");
			}		
		}
		
		sql = "select * from t_gather";
		c = db.rawQuery(sql, null);
		while(c.moveToNext())
		{
			idsecond = c.getInt(c.getColumnIndex("ID"));
			findstrsecond = c.getString(c.getColumnIndex("gather"));
//			System.out.println(idsecond + " " + findstrsecond);
			for(int i=0;i<list.size();i++)
			{
				element = list.get(i);
				if(findstrsecond.contains(element))
				{
					check = true;
					if(findstrfirst == null && idfirst == 0)
					{
						sumjson = new JSONArray(findstrsecond);
						for(int j=0;j<list.size();j++)
						{
							if(j != i)
							{
								element2 = list.get(j);
								if(!findstrsecond.contains(element2))
								{
									sumjson.put(element2);
								}
							}
						}
						findstrsecond = sumjson.toString();
						db.execSQL("update t_gather set gather ='" + findstrsecond +"' where ID = " + idsecond);
						System.out.println("第一种添加 "+findstrsecond);
						findstrfirst = findstrsecond;
						idfirst = idsecond;
						break;
					}
					else if(findstrfirst != null && idfirst != 0)
					{
						sumjson = new JSONArray(findstrsecond);
						ArrayList<String> templist = gson.fromJson(findstrfirst, new TypeToken<ArrayList<String>>(){}.getType());
						for(int k=0;k<templist.size();k++)
						{
							if(!element.equals(templist.get(k)))
							{
//								System.out.println("get is "+templist.get(k)+"element is " + element);
								sumjson.put(templist.get(k));
							}
						}
						findstrsecond = sumjson.toString();
						db.execSQL("update t_gather set gather ='" + findstrsecond +"' where ID = " + idsecond);
						System.out.println("第二种添加" + findstrsecond);
						db.execSQL("delete from t_gather where ID = " + idfirst);
						findstrfirst = findstrsecond;
						idfirst = idsecond;
						break;
					}
				}
			}
		}
		if(!check)
		{
			db.execSQL("insert into t_gather (gather) values ('"+ s +"')");
			System.out.println("从来没有的添加"+ s);
		}
		c.close();
		db.close();
	}
	
	public String search(String s) throws JSONException{
		String findstr = null;
		boolean check = false;
		String sql = "select * from t_gather";
		Cursor c = db.rawQuery(sql, null);
		while(c.moveToNext()){
			findstr = c.getString(c.getColumnIndex("gather"));
			if(findstr.contains(s))
			{
				check = true;
				break;
			}
		}
		c.close();
		db.close();
		if(!check)
			findstr = null;
		return findstr;
	}
	
	public ArrayList<String> searchhot()
	{
		ArrayList<String> list = null;
		int datacount = 0;
		String name;
		String sql = "select count(*) from t_hot";
		Cursor c = db.rawQuery(sql, null);
		while(c.moveToNext())
		{
			datacount = c.getInt(0);
		}
		if(datacount != 0)
		{
			sql = "select * from t_hot order by hotcount desc";
			list = new ArrayList<String>();
			c = db.rawQuery(sql, null);
			while(c.moveToNext())
			{
				name = c.getString(c.getColumnIndexOrThrow("name"));
				list.add(name);
			}
		}
		c.close();
		db.close();
		return list;
	}
}