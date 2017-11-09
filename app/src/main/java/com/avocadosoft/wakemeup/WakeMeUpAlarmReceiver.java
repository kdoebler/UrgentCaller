package com.avocadosoft.wakemeup;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class WakeMeUpAlarmReceiver extends BroadcastReceiver
{

	private final String REMINDER_BUNDLE = "MyReminderBundle";
	private Date date;
	private Date dateCompareOne;
	private Date dateCompareTwo;

	
	public WakeMeUpAlarmReceiver()
	{
		
	}

	
	
	public void SetAlarm(Context context)
    {
		
        AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, WakeMeUpAlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 60 * 1, pi); // Millisec * Second * Minute
    }

	Resources resources;
	SharedPreferences preferences;
	public static final String inputFormat = "HH:mm";
	
	SimpleDateFormat inputParser = new SimpleDateFormat(inputFormat, Locale.US);
	
	@Override
	public void onReceive(Context context, Intent intent)
	{
		WakeMeUpPreferences wakeMeUpPreferences = new WakeMeUpPreferences(context);
		
		
		resources = context.getResources();
		preferences = context.getSharedPreferences(resources.getString(R.string.userPreferencesFileName), 0);
		Boolean active = preferences.getBoolean(resources.getString(R.string.userPreferencesActiveKeyName), true);
		Boolean alwaysOn = preferences.getBoolean(resources.getString(R.string.userPreferencesAlwaysOnKeyName), true);
		
		if (active)
		{
			if (alwaysOn)
			{
				startService(context);
			}
			else
			{
				Boolean withinWindow = compareDates();
				if (withinWindow)
				{
					startService(context);
				}
				else
				{
					stopService(context);
				}
			}
		}
		else
		{
			stopService(context);
		}

		
		
	}
	
	private void startService(Context context)
	{
		Intent pushIntent = new Intent(context, IncomingCallReceiverService.class);
		context.startService(pushIntent);
	}
	
	private void stopService(Context context)
	{
		Intent pushIntent = new Intent(context, IncomingCallReceiverService.class);
		context.stopService(pushIntent);
	}
	

	private Date parseDate(String date)
	{
		try
		{
			return inputParser.parse(date);
		}
		catch (java.text.ParseException e)
		{
			return new Date(0);
		}
	}
	
	private Boolean compareDates()
	{
		
		//A whole lot of date work that can probably be improved.
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		String str = sdf.format(new Date());
		date = parseDate(str);

		Date currentDateTime = new Date();

		//Set up date object for the beginning of the user window.
		Date beginWindowDate = new Date();
		String beginWindowTime = preferences.getString(resources.getString(R.string.userPreferencesBeginTimeKeyName), "23:00");
		int beginWindowHour = Integer.parseInt(beginWindowTime.substring(0, beginWindowTime.indexOf(":")));
		int beginWindowMinute = Integer.parseInt(beginWindowTime.substring(beginWindowTime.indexOf(":") + 1, beginWindowTime.length()));
		beginWindowDate.setHours(beginWindowHour);
		beginWindowDate.setMinutes(beginWindowMinute);

		//Set up date object for the end of the user window.
		Date endWindowDate = new Date();
		String endWindowTime = preferences.getString(resources.getString(R.string.userPreferencesEndTimeKeyName), "23:00");
		int endWindowHour = Integer.parseInt(endWindowTime.substring(0, endWindowTime.indexOf(":")));
		int endWindowMinute = Integer.parseInt(endWindowTime.substring(endWindowTime.indexOf(":") + 1, endWindowTime.length()));
		endWindowDate.setHours(endWindowHour);
		endWindowDate.setMinutes(endWindowMinute);
		//We only store the time in the preferences file. Given this, sometimes the end window time is before the begin window time.
		//In this case, we need to add a day onto the end date so it makes sense.
		if (endWindowDate.before(beginWindowDate))
			endWindowDate.setDate(endWindowDate.getDate() + 1);

		// Now that we've set up our date objects, we need to compare them to the current date time.  
		// Return true if we're within the date window, false if we're not.
		if (beginWindowDate.before(currentDateTime) && endWindowDate.after(currentDateTime))
		{
			return true;
		}
		else
		{
			WakeMeUpPreferences.setCallCount(0);
			return false;
		}
	}

	

}
