package com.udav.mybus;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.IBinder;

public class MyBusService extends Service {
	private String result = "";
	private Parser parser;
	private String link;
	private Intent i;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	public int onStartCommand(Intent intent, int flags, int startId){
		int answer = intent.getIntExtra("command", 0);
		link = intent.getStringExtra("link");
		i = new Intent("com.sonyericsson.extras.liveview.plugins.mybus");
		
		parser = new Parser(getBaseContext());
			
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
		Bundle b = new Bundle();
		b.putStringArray("titleArr", titleArr);
		b.putStringArray("linkArr", linkArr);
		b.putString("result", "");
		i.putExtras(b);
			
		switch (answer) {
			case 0:
				i.putExtra("command", 0);
				sendBroadcast(i);
				break;
			case 1:
				new Thread() {
					@Override
					public void run() {
						result = parser.parseTime(link);
						i.putExtra("command", 1);
						i.putExtra("result", result);
						sendBroadcast(i);
						super.run();
					}
				}.start();
				break;
		}	

		return super.onStartCommand(intent, flags, startId);
	}

}
