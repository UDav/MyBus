package com.udav.mybus;

import java.util.ArrayList;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View.OnClickListener;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;


public class AddBookmarkActivity extends Activity {
	public static final String BOOKMARK_ID = "bm_id"; 
	private int bookmarkId;
	private String bookmarkTitle = "";
	private int busId = 0;
	private int dirId;
	private int stId;
	
	private boolean editMode = false;
	
	private Spinner busSpinner;
	private Spinner directionSpinner;
	private Spinner stationSpiner;
	private EditText title;

	@Override 
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.addbookmark);
        
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
        	editMode = true;
        	bookmarkId = extras.getInt(BOOKMARK_ID);
        	Cursor mCursor = DBHelper.getDB(getBaseContext()).query("Bookmark", null, "id="+bookmarkId, null, null, null, null);
        	mCursor.moveToFirst();
        	bookmarkTitle = mCursor.getString(mCursor.getColumnIndex("title"));
        	busId = mCursor.getInt(mCursor.getColumnIndex("busid"));
        	dirId = mCursor.getInt(mCursor.getColumnIndex("directionid"));
        	stId = mCursor.getInt(mCursor.getColumnIndex("busstationid"));
        }
        
        
        final EditText findEditText = (EditText)findViewById(R.id.findEditText);
        findEditText.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
					Cursor mCursor = DBHelper.getDB(getBaseContext()).query("Bus", new String[]{"name"}, null, null, null, null, null);
					mCursor.moveToFirst();
					for (int i=0; i<mCursor.getCount(); i++) {
						String []tmp = mCursor.getString(mCursor.getColumnIndex("name")).split(" ");
						mCursor.moveToNext();
						if (tmp[0].equals(findEditText.getText().toString())) {
							busSpinner.setSelection(i);
							break;
						}
					}
					return true;
				}
				return false;
			}
		});
        
        ArrayList<String> data; 
        data = new ArrayList<String>();
        
        Cursor mCursor = DBHelper.getDB(getBaseContext()).query("Bus", null, null, null, null, null, null);
        mCursor.moveToFirst();
        do {
        	data.add(mCursor.getString(mCursor.getColumnIndex("name")));
        } while (mCursor.moveToNext());
        
        
        ArrayList<String> direction;
        direction = new ArrayList<String>();
        
        ArrayList<String> busStation;
        busStation = new ArrayList<String>();
        
        final ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        
        final ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, direction);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        
        final ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, busStation);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        
        busSpinner = (Spinner) findViewById(R.id.addBookmarkSpinner1);
        busSpinner.setAdapter(adapter1);
        busSpinner.setPrompt("Маршрут");
        
        directionSpinner = (Spinner) findViewById(R.id.addBookmarkSpinner2);
        directionSpinner.setAdapter(adapter2);
        directionSpinner.setPrompt("Направление");
        
        stationSpiner = (Spinner) findViewById(R.id.addBookmarkSpinner3);
        stationSpiner.setAdapter(adapter3);
        stationSpiner.setPrompt("Остановки");
        
        busSpinner.setSelected(false);
        
        title = (EditText) findViewById(R.id.addBookmarkEditText);
        if (!bookmarkTitle.equals("")) title.setText(bookmarkTitle);
        if (editMode) { 
        	busSpinner.setSelection(busId-1);
        	directionSpinner.setSelection(dirId);
    		stationSpiner.setSelection(stId-1);
        }
        
        busSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
        	@Override
        	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        		directionSpinner.setEnabled(false);
        		stationSpiner.setEnabled(false);
        		adapter2.clear();
        		adapter3.clear();
        		directionSpinner.setSelection(0);
        		stationSpiner.setSelection(0);
        		
        		busId = position+1;
        		
        		Cursor mCursor; 
        		for (int i=0; i<2; i++) {
        			mCursor = DBHelper.getDataFromDB(getBaseContext(), "BusStation.name", busId, i); 
        			
        			String tmp;
        			mCursor.moveToFirst();

        			tmp = mCursor.getString(mCursor.getColumnIndex("name"));
        			mCursor.moveToLast();
        			tmp += " => "+mCursor.getString(mCursor.getColumnIndex("name"));
        			adapter2.add(tmp);
        		}
        		if (editMode) directionSpinner.setSelection(dirId);
        		directionSpinner.setEnabled(true);
        		
        		mCursor = DBHelper.getDataFromDB(getBaseContext(), "BusStation.name", busId, directionSpinner.getSelectedItemPosition());
        		mCursor.moveToFirst();
    			do {
    				adapter3.add(mCursor.getString(mCursor.getColumnIndex("name")));
    			}while(mCursor.moveToNext());   		
        		stationSpiner.setEnabled(true);
        		if (editMode) stationSpiner.setSelection(stId-1);
        	}
        	@Override
        	public void onNothingSelected(AdapterView<?> arg0) {}
        });
     
        directionSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
        	@Override
        	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        		stationSpiner.setEnabled(false);
        		adapter3.clear();
        		Cursor mCursor;
        		mCursor = DBHelper.getDataFromDB(getBaseContext(), "BusStation.name", busId, position);
        		mCursor.moveToFirst();
    			do {
    				adapter3.add(mCursor.getString(mCursor.getColumnIndex("name")));
    			}while(mCursor.moveToNext());   	
        		stationSpiner.setEnabled(true);
        		if (editMode) stationSpiner.setSelection(stId-1);
        	}
        	@Override
        	public void onNothingSelected(AdapterView<?> arg0) {}
        });
                
        stationSpiner.setOnItemSelectedListener(new OnItemSelectedListener() {
        	@Override
        	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        		stId = position+1;
        	}
        	@Override
        	public void onNothingSelected(AdapterView<?> arg0) {}
        });
        
        Button addButton = (Button)findViewById(R.id.addButton);
        if (editMode) addButton.setText("Изменить");
        addButton.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				ContentValues mContentValues = new ContentValues();
				mContentValues.put("title", title.getText().toString());
				mContentValues.put("description", busSpinner.getSelectedItem().toString()+" || "+
						directionSpinner.getSelectedItem().toString()+" || "+
						stationSpiner.getSelectedItem().toString());
				Cursor mCursor = DBHelper.getDataFromDB(getBaseContext(), "BusStation.stationlink", busId, directionSpinner.getSelectedItemPosition());
				mCursor.moveToPosition(stationSpiner.getSelectedItemPosition());
				mContentValues.put("link", mCursor.getString(mCursor.getColumnIndex("stationlink")));
				mContentValues.put("busid", busId/*busSpinner.getSelectedItemPosition()*/);
				mContentValues.put("directionid", directionSpinner.getSelectedItemPosition());
				mContentValues.put("busstationid", stId/*stationSpiner.getSelectedItemPosition()*/);
					
								
				if (editMode) {
					DBHelper.getDB(getBaseContext()).update("Bookmark", mContentValues, "id="+bookmarkId, null);
				} else
					DBHelper.getDB(getBaseContext()).insert("Bookmark", null, mContentValues);
					
				finish();
			}
		});
        
	}

}
