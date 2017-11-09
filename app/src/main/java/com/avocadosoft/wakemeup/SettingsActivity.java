package com.avocadosoft.wakemeup;


import java.util.Calendar;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.widget.Toast;

//This is our main UI class.  This is a simple app, as the UI only consists of a settings form.
public class SettingsActivity extends PreferenceActivity
{
	Intent pushIntent;
	final int HELLO_ID = 1;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		//Create the IncomingCallReceiverService intent.  This is later used to start the service
		pushIntent = new Intent(this, IncomingCallReceiverService.class);
		super.onCreate(savedInstanceState);
		
		//This sets up the UI, which is read from the preferences XML layout file.
		addPreferencesFromResource(R.layout.preferences);

		//Look at the userPreferencesActive field to see if we should start the process to monitor calls.
		Preference preferenceActive = getPreferenceScreen().findPreference("userPreferencesActive");
		Preference preferenceAlwaysOn = getPreferenceScreen().findPreference("userPreferencesAlwaysOn");

        Preference preferenceBeginTime = getPreferenceScreen().findPreference("userPreferencesBeginTime");
        Preference preferenceEndTime = getPreferenceScreen().findPreference("userPreferencesEndTime");

        Resources resources = getResources();
        SharedPreferences preferences = getSharedPreferences(resources.getString(com.avocadosoft.wakemeup.R.string.userPreferencesFileName), 0);
        Boolean alwaysOn = preferences.getBoolean(resources.getString(R.string.userPreferencesAlwaysOnKeyName), true);

        //Attempt to set the preference begin time text equal to text plus the actual time

        //preferenceBeginTime.setTitle(preferenceBeginTime.getTitle() + " TEST");
        //preferenceBeginTime.setEnabled(!alwaysOn);
        //preferenceEndTime.setEnabled(!alwaysOn);

		//We also need to monitor this field in case it is changed.
		//If they deactivate it, we stop the service
		preferenceActive.setOnPreferenceChangeListener(activeChangeListener);
		preferenceAlwaysOn.setOnPreferenceChangeListener(alwaysOnChangeListener);

		SharedPreferences sharedPreferences = getSharedPreferences(resources.getString(R.string.userPreferencesFileName), 0);
		Boolean ranFromUI = sharedPreferences.getBoolean(resources.getString(R.string.serviceStartedFromUI), false);
		
		//The service should always be running, because it's initiated on boot.  However, after first install, the service wont be. So start it here.
		if (!ranFromUI)
		{
			WakeMeUpAlarmReceiver wakeMeUpReceiver = new WakeMeUpAlarmReceiver();
			wakeMeUpReceiver.SetAlarm(this);	
			Editor edit = sharedPreferences.edit();
			edit.putBoolean(resources.getString(R.string.serviceStartedFromUI), true);
			edit.commit();
		}
		
		
		
		
		/*
		//Only start if preference says active=true;
		Resources res = this.getResources();
		SharedPreferences prefs = this.getSharedPreferences(res.getString(R.string.userPreferencesFileName), 0);
		Boolean startService = prefs.getBoolean(res.getString(R.string.userPreferencesActiveKeyName), true);
		
		if (startService)
		{
			this.startService(pushIntent);	
		}
		*/
	}

	//Class which listens for changes to the "active" preference field.
	Preference.OnPreferenceChangeListener activeChangeListener = new OnPreferenceChangeListener()
	{
		@Override
		public boolean onPreferenceChange(Preference preference, Object newValue)
		{
			//If the user sets Active=false, we need to stop the service from running otherwise we might slow their phone down needlessly.
			boolean bActive = (Boolean) newValue;
			if (bActive)
			{
				
				SettingsActivity.this.startService(SettingsActivity.this.pushIntent);
				Resources resources = getResources();
				SharedPreferences preferences = getSharedPreferences(resources.getString(com.avocadosoft.wakemeup.R.string.userPreferencesFileName), 0);
				Boolean alwaysOn = preferences.getBoolean(resources.getString(com.avocadosoft.wakemeup.R.string.userPreferencesAlwaysOnKeyName), true);
				if (alwaysOn)
				{
					
				}
				
			}
			else
			{
				IncomingCallReceiverService.killNotification();
				
			}
			return true;
		}
	};
	
	//Class which listens for changes to the "always on" preference field.
		Preference.OnPreferenceChangeListener alwaysOnChangeListener = new OnPreferenceChangeListener()
		{
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue)
			{
				//If the user sets Active=false, we need to stop the service from running otherwise we might slow their phone down needlessly.
				boolean bAlwaysOn = (Boolean) newValue;
                //Preference preferenceBeginTime = getPreferenceScreen().findPreference("userPreferencesBeginTime");
                //Preference preferenceEndTime = getPreferenceScreen().findPreference("userPreferencesEndTime");

                if (bAlwaysOn)
				{

                   // preferenceBeginTime.setEnabled(false);
                   // preferenceEndTime.setEnabled(false);

					//Check "Active preference to see if we should start it.
					Resources resources = getResources();
					SharedPreferences preferences = getSharedPreferences(resources.getString(com.avocadosoft.wakemeup.R.string.userPreferencesFileName), 0);
					Boolean currentlyActive = preferences.getBoolean(resources.getString(com.avocadosoft.wakemeup.R.string.userPreferencesActiveKeyName), true);

					if (currentlyActive)
					{

						
					}
				}
				else
				{
                //    preferenceBeginTime.setEnabled(true);
                 //   preferenceEndTime.setEnabled(true);
					//IncomingCallReceiverService.killNotification();
				}

				return true;
			}
		};
		
		
		
		
}