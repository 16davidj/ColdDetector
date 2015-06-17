package com.example.davidfirstapp;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.os.AsyncTask;

public class ReceiveURL extends AsyncTask<String, Void, String> {
	String str = "";
	protected String doInBackground(String... loc) {
		android.os.Debug.waitForDebugger();
		try
		   {
		       String queryString="http://api.wunderground.com/auto/wui/geo/WXCurrentObXML/index.xml?query="+loc;
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
		               for(int x=0;x<acount;x++) 
		               {
		                   str=xpp.getAttributeValue(x);
		                   return str;
		               }  

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
		   return str;
		 }

	protected String onPostExecute()
	{
		return str;
	}

	}

