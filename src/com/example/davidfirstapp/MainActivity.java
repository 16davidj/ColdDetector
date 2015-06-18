package com.example.davidfirstapp;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
public class MainActivity extends SensorActivity {


	public final static String EXTRA_MESSAGE = "com.example.ColdDetector.MESSAGE";
	public final static String EXTRA_MESSAGE2 = "com.example.ColdDetector.MESSAGE2";
	SensorActivity sensor = new SensorActivity();
	findLocation loc = new findLocation();
    public static final String PREFS_NAME = "Temperature";
    public static final String PREFS_NAME2 = "Timer";
    public int y = 5;

    Alarm alarm = new Alarm();
	
    public void onReceive(Context context, Intent intent)
    {   
    	if(getStoredTemperature() == null)
    	{
    		return;
    	}
    	else if (check())
        {
    		return;
        }
    }
	public void alert()
	{
		NotificationCompat.Builder mBuilder =
		        new NotificationCompat.Builder(this)
		        .setSmallIcon(R.drawable.ic_launcher)
		        .setContentTitle("It's too cold!")
		        .setContentText("Find a warmer place, it's colder than " + Integer.parseInt(getStoredTemperature()) + "¡F");
		Intent resultIntent = new Intent(this, MainActivity.class);
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		stackBuilder.addParentStack(MainActivity.class);
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(1, mBuilder.build());
	}
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Intent intent = new Intent(this, MainActivity.class);
		SharedPreferences settings = getApplicationContext().getSharedPreferences(PREFS_NAME, 0);
	    getActionBar().setDisplayHomeAsUpEnabled(true);
        Handler handlerTimer = new Handler();
	    if(getStoredTemperature()!="DNE")
	    {
	        changeTime();
	    }
	}
	public void changeTime()
	{
		if(getStoredTime().length()>0 && getStoredTime() != "DNE")
		{
			y = Integer.parseInt(getStoredTime());
		}
	}
	public boolean check()
	{
		int y=0;
		if(getStoredTemperature().length()>0)
		{
			y = Integer.parseInt(getStoredTemperature());
		}
		if((getRealTemperature()<=y) && y>0)
		{
			// add comment
			alert();
			alarm.setAlarm(getBaseContext());
			return true;
		}
		return false;
	}
	public void edit()
	{
		SharedPreferences settings = getApplicationContext().getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("Temperature", getIntent().getStringExtra(MainActivity.EXTRA_MESSAGE));
		// Apply the edits!
		editor.apply();
	}
	public void editTime()
	{
		SharedPreferences settings = getApplicationContext().getSharedPreferences(PREFS_NAME2, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("Timer", getIntent().getStringExtra(MainActivity.EXTRA_MESSAGE2));

		// Apply the edits!
		editor.apply();
	}
	public String getStoredTime()
	{
		// Get from the SharedPreferences
		SharedPreferences settings = getApplicationContext().getSharedPreferences(PREFS_NAME2, 0);
		String Time = settings.getString("Timer", "DNE");
		return Time;
	}
	public String getStoredTemperature()
	{
		// Get from the SharedPreferences
		SharedPreferences settings = getApplicationContext().getSharedPreferences(PREFS_NAME, 0);
		String Temperature = settings.getString("Temperature", "DNE");
		return Temperature;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main_activity_actions, menu);
	    return super.onCreateOptionsMenu(menu);

	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
	    // Handle presses on the action bar items
	    switch (item.getItemId()) 
	    {
	        case R.id.action_search:
	            return true;
	        case R.id.action_settings:
	        	startActivity(new Intent(this, MainActivity.class));
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	public void changeTimer(View view)
	{
		Intent intent = new Intent(this, ResetTime.class);
	    EditText editText = (EditText) findViewById(R.id.edit_text);
	    String message = editText.getText().toString();
	    intent.putExtra(EXTRA_MESSAGE2, message);
	    startActivity(intent);
	}
	public void sendMessage(View view)
	{
		Intent intent = new Intent(this, ResetTemp.class);
	    EditText editText = (EditText) findViewById(R.id.edit_message);
	    String message = editText.getText().toString();
	    intent.putExtra(EXTRA_MESSAGE, message);
	    edit();
	    startActivity(intent);
	}
	public void checkMessage(View view)
	{
		Intent intent = new Intent(this, DisplayMessageActivity.class);
	    EditText editText = (EditText) findViewById(R.id.edit_message);
	    String message = editText.getText().toString();
	    intent.putExtra(EXTRA_MESSAGE, message);
	    check();
	}
	public void killAlarm(View view)
	{
		alarm.cancelAlarm(getBaseContext());
	}
	public int getRealTemperature()
	{
		String real = loc.find_Location(getApplicationContext());
		int y = Integer.parseInt(real);
		return (int) ((1.8*y)+32.0);
	}
	public String getLocation()
	{
		return loc.ConvertPointToLocation(loc.getLatitude(), loc.getLongitude());
	}
	
	
}
