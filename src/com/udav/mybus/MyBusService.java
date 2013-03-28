package com.udav.mybus;

import java.util.HashMap;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;

public class MyBusService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	public int onStartCommand(Intent intent, int flags, int startId){
		String result;
		int command = intent.getIntExtra("command", 0);
		String link = intent.getStringExtra("link");
		Intent i = new Intent("path to service");
		
		Parser parser = new Parser(getBaseContext());
		
		if (command != 0 ) {
			Cursor mCursor = DBHelper.getDB(getBaseContext()).query("Bookmark", null, null, null, null, null, null);
			String titleArr[] = new String[mCursor.getCount()];
			String linkArr[] = new String[mCursor.getCount()];
			int counter = 0;
			if (mCursor.moveToFirst() ) {
		    	do {
		    		titleArr[counter] = mCursor.getString(mCursor.getColumnIndex("title"));
		    		linkArr[counter] =  mCursor.getString(mCursor.getColumnIndex("link"));
		    		counter++;
		    	} while(mCursor.moveToNext());
		    }
			i.putExtra("titleArr", titleArr);
			i.putExtra("linkArr", linkArr);
			sendBroadcast(i);
		} else
		if (link != null) {
			result = parser.parseTime(link);
			i.putExtra("result", result);
		}
			
		/*switch (command) {
			case 1:
				Cursor mCursor = DBHelper.getDB(getBaseContext()).query("Bookmark", null, null, null, null, null, null);
				if (mCursor.moveToFirst() ) {
			    	do {
			    		mCursor.getString(mCursor.getColumnIndex("title"));
			    		link = mCursor.getString(mCursor.getColumnIndex("link"));
			    	} while(mCursor.moveToNext());
			    }
				
				link = "link";
				break;
			case 2:
				
				break;
			default: 
				break;
		}*/
		return super.onStartCommand(intent, flags, startId);
	}

}
