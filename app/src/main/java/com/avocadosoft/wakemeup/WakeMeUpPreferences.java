package com.avocadosoft.wakemeup;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;

public class WakeMeUpPreferences
{
	private static Context mContext;
	private static Resources res;
	private static SharedPreferences settings;
	
	private static Date beginDate;
	private static Date endDate;
	
	private static String sUserPreferencesFileName;
	private static String sUserPreferenceBeginTimeUserValue;
	private static String sUserPreferenceEndTimeUserValue;
	
	private static Date lastCallDateTime;
	
	private static int iUserPreferenceCallCountThresholdActualValue;
	private static int iCallCount;
	private static Boolean bActive;
	private static Boolean bAlwaysOn;
	
	public static Boolean bWindowChangedSinceLastCall;

	public WakeMeUpPreferences(Context context)
	{
		mContext = context;
		initializeVariables();
	}
	
	private void initializeVariables()
	{
		res = mContext.getResources();
		
		setUserPreferenceFileName(res.getString(R.string.userPreferencesFileName));
		settings = mContext.getSharedPreferences(sUserPreferencesFileName, 0);
		setActive(settings.getBoolean(String.format(res.getString(R.string.userPreferencesActiveKeyName)), true));
		setAlwaysOn(settings.getBoolean(String.format(res.getString(R.string.userPreferencesAlwaysOnKeyName)), false));
		setBeginWindowTime(settings.getString(String.format(res.getString(R.string.userPreferencesBeginTimeKeyName)), res.getString(R.string.userPreferencesBeginTimeDefaultValue)));
		setEndWindowTime(settings.getString(String.format(res.getString(R.string.userPreferencesEndTimeKeyName)), res.getString(R.string.userPreferencesEndTimeDefaultValue)));
		setCallCountThreshold(settings.getInt(res.getString(R.string.userPreferencesCallCountThresholdKeyName), 3));
		setCallCount(settings.getInt(res.getString(R.string.callCount), 0));
		
		String lastCallDateTime = settings.getString(String.format(res.getString(R.string.lastCallDateTime)), "");
		if (lastCallDateTime.length() > 0)
		{
			SimpleDateFormat  format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
			try 
			{  
			    Date date = format.parse(lastCallDateTime);  
			    setLastCallDateTime(date);
			    System.out.println(date);  
			} catch (ParseException e) 
			{  
			    // TODO Auto-generated catch block  
			    e.printStackTrace();  
			}
		}
		
		setBeginWindowDate();
		setEndWindowDate();
		
	}
	
	//SharedPreferences file filename
		public static void setUserPreferenceFileName(String value)
		{
			sUserPreferencesFileName = value;
		}
		
		public static String getUserPreferenceFileName()
		{
			if (sUserPreferencesFileName.length() > 0)
			{
				return sUserPreferencesFileName;
			}
			else
			{
				return "";
			}
		}
		
		
		public static void setAlwaysOn(Boolean value)
		{
			Editor edit = settings.edit();
			
			edit.putBoolean(res.getString(R.string.userPreferencesAlwaysOnKeyName), value);
			edit.commit();
			bAlwaysOn = value;
		}
		
		public static Boolean getAlwaysOn()
		{
			return bAlwaysOn;
		}
		
		
		public static void setActive(Boolean value)
		{
			Editor edit = settings.edit();
			
			edit.putBoolean(res.getString(R.string.userPreferencesActiveKeyName), value);
			edit.commit();
			bActive = value;
		}
		
		public static Boolean getActive()
		{
			return bActive;
		}
		
		//Begin Window
		public static void setBeginWindowTime(String value)
		{
			Editor edit = settings.edit();
			
			edit.putString(res.getString(R.string.userPreferencesBeginTimeKeyName), value);
			edit.commit();
			sUserPreferenceBeginTimeUserValue = value;
			
			
			
			setBeginWindowDate();
			
		}
		
		public static String getBeginWindowTime()
		{
			if (sUserPreferenceBeginTimeUserValue.length() > 0)
				return sUserPreferenceBeginTimeUserValue;
			else
				return "";

		}
		
		//End Window
		public static void setEndWindowTime(String value)
		{
			Editor edit = settings.edit();
			
			edit.putString(res.getString(R.string.userPreferencesEndTimeKeyName), value);
			edit.commit();
			sUserPreferenceEndTimeUserValue = value;
			
			setEndWindowDate();
		}
		
		public static String getEndWindowTime()
		{
			if (sUserPreferenceEndTimeUserValue.length() > 0)
				return sUserPreferenceEndTimeUserValue;
			else
				return "";
		}
		
		public static void setCallCountThreshold(Integer value)
		{
			
			Editor edit = settings.edit();
			edit.putInt(res.getString(R.string.userPreferencesCallCountThresholdKeyName), value);
			edit.commit();
			iUserPreferenceCallCountThresholdActualValue = value; 
					
		}
		
		public static int getCallCountThreshold()
		{
			return iUserPreferenceCallCountThresholdActualValue;
		}
		
		private static void setBeginWindowDate()
		{
			Date beginWindow = new Date();
			
			beginWindow.setHours(getBeginWindowHour());
			beginWindow.setMinutes(getBeginWindowMinute());
			
			beginDate = beginWindow;
		}
		
		public static Date getBeginWindowDate()
		{
			return beginDate;
		}
	
		private static void setEndWindowDate()
		{
			Date endWindow = new Date();
			
			endWindow.setHours(getEndWindowHour());
			endWindow.setMinutes(getEndWindowMinute());
			
			if (endWindow.before(getBeginWindowDate()))
				endWindow.setDate(endWindow.getDate() + 1);
			endDate = endWindow;
			
		}
		
		public static Date getEndWindowDate()
		{
			return endDate;
		}
		
		public static int getBeginWindowHour()
		{
			return Integer.parseInt(getBeginWindowTime().substring(0, getBeginWindowTime().indexOf(":")));
		}
		
		public static int getBeginWindowMinute()
		{
			return Integer.parseInt(getBeginWindowTime().substring(getBeginWindowTime().indexOf(":") + 1, getBeginWindowTime().length()));
		}
		
		public static int getEndWindowHour()
		{
			return Integer.parseInt(getEndWindowTime().substring(0, getEndWindowTime().indexOf(":")));
		}
		
		public static int getEndWindowMinute()
		{
			return Integer.parseInt(getEndWindowTime().substring(getEndWindowTime().indexOf(":") + 1, getEndWindowTime().length()));
		}
		
		public static void setLastCallDateTime(Date date)
		{
			Editor edit = settings.edit();
			edit.putString(res.getString(R.string.lastCallDateTime), date.toString());
			edit.commit();
			lastCallDateTime = date;
		}
		
		public static Date getLastCallDateTime()
		{
			return lastCallDateTime;
		}
		
		public static void setCallCount(int value)
		{
			Editor edit = settings.edit();
			edit.putInt(res.getString(R.string.userPreferencesMissedCallCount), value);
			edit.commit();
			iCallCount = value;
		}
		
		public static int getCallCount()
		{
			return iCallCount;
		}
}
