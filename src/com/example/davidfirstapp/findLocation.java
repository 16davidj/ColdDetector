package com.example.davidfirstapp;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;

public class findLocation
{
StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

private Context con;
private double longitude;
private double latitude;
LocationManager locationManager;

public findLocation()
{
	StrictMode.setThreadPolicy(policy); 
}
//returns the variable longitude
public double getLongitude()
{
	return longitude;
}
//returns latitude
public double getLatitude()
{
	return latitude;
}
//returns a string of the temperature of the current location
public String find_Location(Context con) {
	String temp_c = "hi";
    Log.d("Find Location", "in find_location");
    this.con = con;
    String location_context = Context.LOCATION_SERVICE;
    locationManager = (LocationManager) con.getSystemService(location_context);
    List<String> providers = locationManager.getProviders(true);
    for (String provider : providers) {
        locationManager.requestLocationUpdates(provider, 1000, 0,new LocationListener() {

                public void onLocationChanged(Location location) {}

                public void onProviderDisabled(String provider) {}

                public void onProviderEnabled(String provider) {}

                public void onStatusChanged(String provider, int status,
                        Bundle extras) {}
            });
        Location location = locationManager.getLastKnownLocation(provider);
        if (location != null) 
        {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            String addr = latitude + "," + longitude;
			temp_c= SendToUrl(addr);
        }
    }
    return temp_c;
}
//Converts the latitude and longitude into a string location
public String ConvertPointToLocation(double pointlat, double pointlog, Context con) {

    String address = "";
    Geocoder geoCoder = new Geocoder(con, Locale.getDefault());
    try {
        List<Address> addresses = geoCoder.getFromLocation(pointlat,pointlog, 1);
        if (addresses.size() > 0) 
        {
            for (int index = 0; index < addresses.get(0).getMaxAddressLineIndex(); index++)
            {
                address += addresses.get(0).getAddressLine(index) + " ";
            }
        }
    }
    catch (IOException e) 
    {
        e.printStackTrace();
    }
    return address;
}  
//Adds the location's string to the url, which helps pull the information about the current
//temperature of the location.
public String  SendToUrl(String string) 
{
    // TODO Auto-generated method stub
	String str="";
   try
   {
       String queryString="http://api.wunderground.com/auto/wui/geo/WXCurrentObXML/index.xml?query="+string;
       URL url= new URL(queryString);

    URLConnection connection = url.openConnection();

    InputStream stream = new BufferedInputStream(connection.getInputStream());
    XmlPullParser xpp = XmlPullParserFactory.newInstance().newPullParser();
    xpp.setInput(stream, null);
    int eventType = xpp.getEventType();
    while(eventType != XmlPullParser.END_DOCUMENT)
    {
          if(eventType == XmlPullParser.START_TAG)
          {
        	  String elementName = xpp.getName();
        	  if("temp_c".equals(elementName))
              {
        	  int acount=xpp.getAttributeCount();        
              str= xpp.nextText();
              return str;
              }
           }
          eventType = xpp.next();        
     }
     
   } 
   catch (IOException e) 
   {
	// TODO Auto-generated catch block
	e.printStackTrace();
   } 
   catch (XmlPullParserException e) 
   {
	// TODO Auto-generated catch block
	e.printStackTrace();
   }
   catch (Exception e)
   {
	   e.printStackTrace();
   }
   return str;
 } 
}
