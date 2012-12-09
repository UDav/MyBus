package com.udav.mybus;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.res.Resources.Theme;
import android.database.Cursor;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.view.View.OnClickListener;

public class ResultActivity extends Activity{
	public static final String DATA = "data";
	private String bookmarkID;
	private TextView resultTextView;
	private Button refreshButton;
	private String tmp;
	@Override 
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.result);
       
        Bundle extras = getIntent().getExtras();
        bookmarkID = extras.getString(DATA);
        
        resultTextView = (TextView)findViewById(R.id.resultTextView);
        resultTextView.setMovementMethod(new ScrollingMovementMethod());
       
        refreshButton = (Button)findViewById(R.id.resultRefresh);
        
        loadingInfo();
        
        refreshButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				loadingInfo();
			}
		});
	}
	
	private void loadingInfo() {
		final ProgressDialog progressDialog = ProgressDialog.show(ResultActivity.this, "", "Loading...", true, true);
		
		new Thread() {
			public void run() {
				try{
					Parser mParser = new Parser(getBaseContext());

					Cursor mCursor = DBHelper.getDB(getBaseContext()).query("Bookmark", new String[]{"link"}, "id="+bookmarkID, null, null, null, null);
					mCursor.moveToFirst();
					tmp = mParser.parseTime(mCursor.getString(mCursor.getColumnIndex("link")));
					mCursor = DBHelper.getDB(getBaseContext()).rawQuery("SELECT Bus.name AS name, Bookmark.directionid AS dir FROM Bookmark INNER JOIN Bus ON Bookmark.busid=Bus.id WHERE " +
							"Bookmark.id=?", new String[] {bookmarkID});
					mCursor.moveToFirst();
					
					String name = mCursor.getString(mCursor.getColumnIndex("name"));
					String tmp2[] = name.split(" ");
					String typeTransport="";
					if (tmp2[1].equals("автобус")) typeTransport = "avto";
					if (tmp2[1].equals("трамвай")) typeTransport = "tram";
					if (tmp2[1].equals("троллейбус")) typeTransport = "trol";
					String direction="";
					switch (mCursor.getInt(mCursor.getColumnIndex("dir"))){
							case 0: 
								direction = "AB";
								break;
							case 1: 
								direction = "BA";
								break;
					}							
												
					tmp += "\n"+mParser.parseSchedule(tmp2[0], typeTransport, direction);

					resultTextView.post(new Runnable(){
						@Override
						public void run() {
							resultTextView.setText(tmp);						
						}
					});
				} catch (Exception e){}  
				progressDialog.dismiss();
			}
		}.start();
	}
}
