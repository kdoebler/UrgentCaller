package com.avocadosoft.wakemeup;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

//This service is called by both the BootCompletetedIntentReceiver and the SettingsActivity.
//When this service is created, it creates an instance of of the IncomingCallReceiver class, which monitors for incoming calls.
public class IncomingCallReceiverService extends Service
{
	private static NotificationManager mNM;
	private static int NOTIFICATION = R.string.localservicestarted;
	
	
	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}

	@Override
	public void onDestroy()
	{
		Log.i("Avocado", "Stopping Urgent Caller");
		killNotification();
	}


	@Override
	public void onCreate()
	{
		Log.i("Avocado", "Starting Urgent Caller (Create)");
		 mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

	}
	

	//This is the actual method which creates the IncomingCallReceiver class, which listens for calls.
	//This is run anytime the IncomingCallReceiverService is started.
	//The START_STICKY flag is important because it tells the Android system to try and restart this service
	//if it ever has to stop the service (due to low memory or other system problems)
	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		Log.i("Avocado", "Starting Urgent Caller (Start");
		//if we're within the window, start it. Otherwise, do nothing
		//if already started, do nothing
		
		TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		IncomingCallReceiver incomingCallReceiver = new IncomingCallReceiver(tm, this);
		showNotification();
		
		return START_STICKY;
	}
	
	public void showNotification()
	{
		CharSequence title = getText(R.string.notificationTitle);
		CharSequence text = getText(R.string.notificationText);
		String ns = this.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);

		int icon = R.drawable.ic_launcher;
		
		long when = System.currentTimeMillis();

		Notification notification = new Notification(icon, title, when);

		Context context = getApplicationContext();
		CharSequence contentTitle = title;
		CharSequence contentText = text;
		Intent notificationIntent = new Intent(this, SettingsActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
		// Send the notification.
        mNM.notify(NOTIFICATION, notification);
	}
	
	public static void killNotification()
	{
		mNM.cancel(NOTIFICATION);
	}

}
