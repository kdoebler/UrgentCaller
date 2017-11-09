package com.avocadosoft.wakemeup;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

public class BootCompletedIntentReceiver extends BroadcastReceiver
{
	//When the phone boots, it sends out a "broadcast" to any apps subscribed to receive it.
	//This method receives that broadcast, and starts the "IncomingCallReceiver" service. 
	@Override
	public void onReceive(Context context, Intent intent)
	{
		if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction()))
		{			
			WakeMeUpAlarmReceiver wakeMeUpReceiver = new WakeMeUpAlarmReceiver();
			wakeMeUpReceiver.SetAlarm(context);
			Log.i("Avocado", "Boot Received");
			//Intent pushIntent = new Intent(context, IncomingCallReceiverService.class);
			//context.startService(pushIntent);
		}
	}
}
