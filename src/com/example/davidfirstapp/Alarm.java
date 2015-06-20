package com.example.davidfirstapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

public class Alarm extends BroadcastReceiver 
{    
     @Override
     public void onReceive(Context context, Intent intent) 
     {   
         PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
         PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
         wl.acquire();
         // Put here YOUR code
         Toast.makeText(context, "Temperature is too low!", Toast.LENGTH_LONG).show(); // For example
         //main.alert();
         Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
         Ringtone r = RingtoneManager.getRingtone(context, notification);
         r.play();
         wl.release();
     }

 public void setAlarm(Context c)
 {
     AlarmManager am=(AlarmManager)c.getSystemService(Context.ALARM_SERVICE);
     Intent i = new Intent(c, Alarm.class);
     PendingIntent pi = PendingIntent.getBroadcast(c, 0, i, 0);
     am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pi); // Millisec * Second * Minute
 }
 public void cancelAlarm(Context c)
 {
     AlarmManager alarmManager = (AlarmManager)c.getSystemService(Context.ALARM_SERVICE);
	 Intent intent = new Intent(c, Alarm.class);
     PendingIntent pi = PendingIntent.getBroadcast(c, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
     alarmManager.cancel(pi);
     pi.cancel();
 }
}
