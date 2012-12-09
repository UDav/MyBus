package com.udav.mybus;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DBHelper extends SQLiteOpenHelper{
	private static DBHelper mDBHelper;
	private Context context;
	
	private DBHelper(Context context) {
		super(context, "MyBusDB", null, 1);
		this.context = context;
	}
	
	public static SQLiteDatabase getDB(Context context) {
		if (mDBHelper == null) mDBHelper = new DBHelper(context);
		return mDBHelper.getWritableDatabase();
	}
	
	public static Cursor getDataFromDB(Context context, String selectColumn, int busId, int direction) {
		Cursor mCursor = getDB(context).rawQuery("SELECT "+selectColumn+ 
				" FROM Bus INNER JOIN BusStation ON Bus.id = BusStation.busid WHERE Bus.id=? AND BusStation.typedirection=?;", new String[]{ ""+busId, ""+direction});
		return mCursor;
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table Bookmark(id integer primary key autoincrement, title text, " +
				"description text, link text, busid integer, " +
				"directionid integer, busstationid integer);");
		db.execSQL("CREATE TABLE Bus(id integer primary key autoincrement, name text, buslink text);");
		db.execSQL("CREATE TABLE BusStation(id integer primary key autoincrement, name text, stationlink text, busid integer, typedirection integer);");
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}
		

}
