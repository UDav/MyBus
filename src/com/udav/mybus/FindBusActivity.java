package com.udav.mybus;


import java.util.ArrayList;


import android.app.Activity;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class FindBusActivity extends Activity {
	
	private Spinner busSpinner;
	private Spinner directionSpinner;
	private Spinner stationSpinner;
	private TextView resultTextView;
	private Button refreshButton;
	
	private int busId = 0;
	
	
	private void loadingInfo(final String link) {
		final ProgressDialog progressDialog = ProgressDialog.show(FindBusActivity.this, "", "Loading...", true, true);
		
		new Thread() {
			public void run() {
				try{
					Parser mParser = new Parser(getBaseContext());
					final String tmp = mParser.parseTime(link);
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
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.findbus);
        
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
        
        resultTextView = (TextView)findViewById(R.id.FindBusResultTextView);
        resultTextView.setMovementMethod(new ScrollingMovementMethod());
        refreshButton = (Button)findViewById(R.id.button1);
        refreshButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Cursor mCursor = DBHelper.getDataFromDB(getBaseContext(), "BusStation.stationlink", busId, directionSpinner.getSelectedItemPosition());
        		mCursor.moveToPosition(stationSpinner.getSelectedItemPosition());
        		loadingInfo(mCursor.getString(mCursor.getColumnIndex("stationlink")));
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
        
        final ArrayAdapter<String> busAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data);
        busAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        
        final ArrayAdapter<String> directionAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, direction);
        directionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        
        final ArrayAdapter<String> stationAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, busStation);
        stationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        
        busSpinner = (Spinner) findViewById(R.id.spinner1);
        busSpinner.setAdapter(busAdapter);
        busSpinner.setPrompt("Маршрут");
        
        directionSpinner = (Spinner) findViewById(R.id.spinner2);
        directionSpinner.setAdapter(directionAdapter);
        directionSpinner.setPrompt("Направление");
        
        stationSpinner = (Spinner) findViewById(R.id.spinner3);
        stationSpinner.setAdapter(stationAdapter);
        stationSpinner.setPrompt("Остановки");
        
        busSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
        	@Override
        	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        		directionSpinner.setEnabled(false);
        		stationSpinner.setEnabled(false);
        		directionAdapter.clear();
        		stationAdapter.clear();
        		directionSpinner.setSelection(0);
        		stationSpinner.setSelection(0);
        		
        		busId = position+1;
        		
        		Cursor mCursor; 
        		for (int i=0; i<2; i++) {
        			mCursor = DBHelper.getDataFromDB(getBaseContext(), "BusStation.name", busId, i); 
        			
        			String tmp;
        			System.out.println(mCursor.getCount());
        			mCursor.moveToFirst();

        			tmp = mCursor.getString(mCursor.getColumnIndex("name"));
        			mCursor.moveToLast();
        			tmp += " => "+mCursor.getString(mCursor.getColumnIndex("name"));
        			directionAdapter.add(tmp);
        		}
        		directionSpinner.setEnabled(true);
        		
        		mCursor = DBHelper.getDataFromDB(getBaseContext(), "BusStation.name", busId, directionSpinner.getSelectedItemPosition());
        		mCursor.moveToFirst();
    			do {
    				stationAdapter.add(mCursor.getString(mCursor.getColumnIndex("name")));
    			}while(mCursor.moveToNext());   		
        		stationSpinner.setEnabled(true);
        	}
        	@Override
        	public void onNothingSelected(AdapterView<?> arg0) {}
        });
     
        directionSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
        	@Override
        	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        		stationSpinner.setEnabled(false);
        		stationAdapter.clear();
        		Cursor mCursor = DBHelper.getDataFromDB(getBaseContext(), "BusStation.name", busId, directionSpinner.getSelectedItemPosition());
        		mCursor.moveToFirst();
    			do {
    				stationAdapter.add(mCursor.getString(mCursor.getColumnIndex("name")));
    			}while(mCursor.moveToNext()); 
        		stationSpinner.setEnabled(true);
        	}
        	@Override
        	public void onNothingSelected(AdapterView<?> arg0) {}
        });
                
        stationSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
        	@Override
        	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {	
        		Cursor mCursor = DBHelper.getDataFromDB(getBaseContext(), "BusStation.stationlink", busId, directionSpinner.getSelectedItemPosition());
        		mCursor.moveToPosition(position);
        		loadingInfo(mCursor.getString(mCursor.getColumnIndex("stationlink")));
      
        	}
        	@Override
        	public void onNothingSelected(AdapterView<?> arg0) {}
        });
        
    }

}