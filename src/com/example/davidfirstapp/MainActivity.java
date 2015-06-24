package com.example.davidfirstapp;
import java.util.List;

import com.example.davidfirstapp.R;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
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
	private Context con;
    private static Ringtone r;
    Location location;
	
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
    //Sets up a notification system whenever the temperature outside is lower than the standard temperature.
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
	//This is the main method in determining whether the alarm rings or not. If the temperature is cooler than
	//the set temperature, a ringtone is played.
	public boolean check()
	{
		int y=0;
		if(getStoredTemperature().length()>0)
		{
			y = Integer.parseInt(getStoredTemperature());
		}
		if((getRealTemperature()<=y) && y>0)
		{
		    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
		    r = RingtoneManager.getRingtone(getBaseContext(), notification);
		    //playing sound alarm
		    r.play();
		    alert();
			return true;
		}
		return false;
	}
	//The method that allows the user to edit the standard temperature
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
	//returns the temperature that the user inputted
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
	//Determines what to do when different buttons are pressed
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
	//Starts the activity in the ResetTemp class, which sets the alarm for the temperature entered
	public void sendMessage(View view)
	{
		Intent intent = new Intent(this, ResetTemp.class);
	    EditText editText = (EditText) findViewById(R.id.edit_message);
	    String message = editText.getText().toString();
	    intent.putExtra(EXTRA_MESSAGE, message);
	    edit();
	    startActivity(intent);
	}
	//Starts the activity in the DisplayMessageActivity class, which displays numbers of the
	//current temperatures around the phone, and the temperature the alarm is set for
	public void checkMessage(View view)
	{
		Intent intent = new Intent(this, DisplayMessageActivity.class);
	    EditText editText = (EditText) findViewById(R.id.edit_message);
	    String message = editText.getText().toString();
	    intent.putExtra(EXTRA_MESSAGE, message);
	    check();
	    startActivity(intent);
	}
	//Stops the ringtone
	public void killAlarm(View view)
	{
		r.stop();
	}
	//Finds the temperature of the precise GPS location the phone is located in
	public int getRealTemperature()
	{
		String real = loc.find_Location(getApplicationContext());
		int y = Integer.parseInt(real);
		return (int) ((1.8*y)+32.0);
	}
	//returns the location 
	public String getLocation(Context con)
	{
	    this.con = con;
	    String location_context = Context.LOCATION_SERVICE;
	    LocationManager locationManager = (LocationManager) con.getSystemService(location_context);
	    List<String> providers = locationManager.getProviders(true);
	    for (String provider : providers) {
	        locationManager.requestLocationUpdates(provider, 1000, 0,new LocationListener() {

	                public void onLocationChanged(Location location) {}

	                public void onProviderDisabled(String provider) {}

	                public void onProviderEnabled(String provider) {}

	                public void onStatusChanged(String provider, int status,
	                        Bundle extras) {}
	            });
	    location = locationManager.getLastKnownLocation(provider);
        }
		return loc.ConvertPointToLocation(location.getLatitude(), location.getLongitude(), this.getApplicationContext());
	}
}
