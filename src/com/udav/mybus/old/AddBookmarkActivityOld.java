package com.udav.mybus.old;

import java.util.ArrayList;

import com.udav.mybus.DBHelper;
import com.udav.mybus.R;
import com.udav.mybus.R.id;
import com.udav.mybus.R.layout;

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


public class AddBookmarkActivityOld extends Activity {
	public static final String BOOKMARK_ID = "bm_id"; 
	private int bookmarkId;
	private String bookmarkTitle = "";
	private int busId = -1;
	private int dirId;
	private int stId;
	
	private Spinner busSpinner;
	private Spinner directionSpinner;
	private Spinner stationSpiner;
	private EditText title;
	private Parser mParser;
	private Bus mBus;
	@Override 
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.addbookmark);
        
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
        	bookmarkId = extras.getInt(BOOKMARK_ID);
        	Cursor mCursor = DBHelper.getDB(getBaseContext()).query("Bookmark", null, "id="+bookmarkId, null, null, null, null);
        	mCursor.moveToFirst();
        	bookmarkTitle = mCursor.getString(mCursor.getColumnIndex("title"));
        	busId = mCursor.getInt(mCursor.getColumnIndex("busid"));
        	dirId = mCursor.getInt(mCursor.getColumnIndex("directionid"));
        	stId = mCursor.getInt(mCursor.getColumnIndex("busstationid"));
        }
        
        mParser = new Parser(getBaseContext());
        
        final EditText findEditText = (EditText)findViewById(R.id.findEditText);
        findEditText.setOnKeyListener(new View.OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
					for (int i=0; i<BusContainer.busArray.size(); i++) {
						String [] tmp = BusContainer.busArray.get(i).getBusNumber().split(" ");
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
        for (int i=0; i<BusContainer.busArray.size(); i++){
        	data.add(BusContainer.busArray.get(i).getBusNumber());
        }
        
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
        busSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
        	@Override
        	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        		directionSpinner.setEnabled(false);
        		stationSpiner.setEnabled(false);
        		adapter2.clear();
        		adapter3.clear();
        		directionSpinner.setSelection(0);
        		stationSpiner.setSelection(0);
        		      		
        		if (busId != -1) {
        			mBus = mParser.getBus(busId);
        			busId = position;
        		} else
        			mBus = mParser.getBus(position);
        		if (mBus != null) {
        			adapter2.add(mBus.getDirectionA().get(0).name+" => "+mBus.getDirectionA().get(mBus.getDirectionA().size()-1).name);
        			adapter2.add(mBus.getDirectionB().get(0).name+" => "+mBus.getDirectionB().get(mBus.getDirectionB().size()-1).name);
        		}
        		directionSpinner.setEnabled(true);
        		
        		if (mBus != null && directionSpinner.getSelectedItemPosition()==0) {
        			for (int i=0; i<mBus.getDirectionA().size(); i++)
        				adapter3.add(mBus.getDirectionA().get(i).name);
        		}
        		if (mBus != null && directionSpinner.getSelectedItemPosition()==1) {
        			for (int i=0; i<mBus.getDirectionB().size(); i++)
        				adapter3.add(mBus.getDirectionB().get(i).name);
        		}
        		stationSpiner.setEnabled(true);
        		
        		busSpinner.setSelection(busId);
        		directionSpinner.setSelection(dirId);
        		stationSpiner.setSelection(stId);
        	}
        	@Override
        	public void onNothingSelected(AdapterView<?> arg0) {}
        });
     
        directionSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
        	@Override
        	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        		stationSpiner.setEnabled(false);
        		adapter3.clear();
        		if (mBus != null && position==0) {
        			for (int i=0; i<mBus.getDirectionA().size(); i++)
        				adapter3.add(mBus.getDirectionA().get(i).name);
        		}
        		if (mBus != null && position==1) {
        			for (int i=0; i<mBus.getDirectionB().size(); i++)
        				adapter3.add(mBus.getDirectionB().get(i).name);
        		}
        		stationSpiner.setEnabled(true);
        	}
        	@Override
        	public void onNothingSelected(AdapterView<?> arg0) {}
        });
                
        /*stationSpiner.setOnItemSelectedListener(new OnItemSelectedListener() {
        	@Override
        	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        		
        	}
        	@Override
        	public void onNothingSelected(AdapterView<?> arg0) {}
        });*/
        
        Button addButton = (Button)findViewById(R.id.addButton);
        if (busId != -1) addButton.setText("Изменить");
        addButton.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				ContentValues mContentValues = new ContentValues();
				mContentValues.put("title", title.getText().toString());
				if (directionSpinner.getSelectedItemPosition() == 0) {
					mContentValues.put("description", busSpinner.getSelectedItem().toString()+" || "+
						directionSpinner.getSelectedItem().toString()+" || "+
						mBus.getDirectionA().get(stationSpiner.getSelectedItemPosition()).name);
					mContentValues.put("link", mBus.getDirectionA().get(stationSpiner.getSelectedItemPosition()).link);
				} else {
					mContentValues.put("description", busSpinner.getSelectedItem().toString()+" || "+
							directionSpinner.getSelectedItem().toString()+" || "+
							mBus.getDirectionB().get(stationSpiner.getSelectedItemPosition()).name);
					mContentValues.put("link", mBus.getDirectionB().get(stationSpiner.getSelectedItemPosition()).link);
				}
				mContentValues.put("busid", busSpinner.getSelectedItemPosition());
				mContentValues.put("directionid", directionSpinner.getSelectedItemPosition());
				mContentValues.put("busstationid", stationSpiner.getSelectedItemPosition());
					
				if (busId == -1) {
					DBHelper.getDB(getBaseContext()).insert("Bookmark", null, mContentValues);
				} else
					DBHelper.getDB(getBaseContext()).update("Bookmark", mContentValues, "id="+bookmarkId, null);
					
				finish();
			}
		});
        
	}

}
