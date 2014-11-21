package com.greenhand.your_choice;

import android.os.Parcel;
import android.os.Parcelable;

public class Options implements Parcelable{

	private String s1;
	
	public Options()
	{
		super();
	}
	
	public void setString(String s)
	{
		s1 = s;
	}
	
	public String getString()
	{
		return s1;
	}
	
	@Override
	public int describeContents() 
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel arg0, int arg1) 
	{
		// TODO Auto-generated method stub
		arg0.writeString(s1);
	}
	
	public static final Parcelable.Creator<Options> CREATOR = new Parcelable.Creator<Options>() 
	{  
        public Options createFromParcel(Parcel in) 
        {  
            return new Options(in);  
        }  
  
        public Options[] newArray(int size) {  
            return new Options[size];  
        }  
    };  

    private Options(Parcel in) 
    {  
        s1 = in.readString();  
    }  
	
}
