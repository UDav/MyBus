package com.udav.mybus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class BookmarksActivity extends Activity{
	final String ATTRIBUTE_TITLE = "title";
	final String ATTRIBUTE_DESCRIPTION = "description";
	final String ATTRIBUTE_IMG = "img";
	private ListView mainListView;	
	private int selectedListViewItem;
	
	/**
	 * connect menu from /menu/main_menu.xml
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main_menu, menu);
		return true;
	}
	
	/**
	 * process actions with main_menu
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_searchbus: {
				new WaitParse(FindBusActivity.class).execute();
				return true;
			}
		
			case R.id.menu_addbookmark: {
				new WaitParse(AddBookmarkActivity.class).execute();
				return true;
			}
			
			case R.id.menu_exit: {
				System.exit(0);
				return true;
			}
			default: return super.onOptionsItemSelected(item);		
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		if (DBHelper.getDB(getBaseContext()).query("Bus", null, null, null, null, null, null).getCount() == 0)
			new WaitParse().execute();
		fillListView();
	}
	
	
	// Контекстнок меню
	@Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		AdapterContextMenuInfo aMenuInfo = (AdapterContextMenuInfo) menuInfo;

        // Получаем позицию элемента в списке
		
        selectedListViewItem = aMenuInfo.position;
        
        menu.add(Menu.NONE, 100, Menu.NONE, "Изменить");
		menu.add(Menu.NONE, 101, Menu.NONE, "Удалить");
    }
	
	private int convertPositionToId(int position) {
		Cursor mCursor = DBHelper.getDB(getBaseContext()).query("Bookmark", new String[] {"id"} , null, null, null, null, null);
    	mCursor.moveToPosition(position);
		return mCursor.getInt(mCursor.getColumnIndex("id"));
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case 100:
				new WaitParse(AddBookmarkActivity.class, convertPositionToId(selectedListViewItem)).execute();
			    break;
		    case 101:
				DBHelper.getDB(getBaseContext()).delete("Bookmark", "id="+convertPositionToId(selectedListViewItem), null);
				fillListView();
				Toast.makeText(getBaseContext(), "Removed...", 1000).show();
			    break;
		    default:
			    return super.onContextItemSelected(item);
		}
		return true;
	}
	
	private void fillListView(){
		
		Cursor mCursor = DBHelper.getDB(getBaseContext()).query("Bookmark", null, null, null, null, null, null); 
		
		ArrayList<Map<String, Object>> data = new ArrayList<Map<String, Object>>(mCursor.getCount());
	    Map<String, Object> m;
	    	    
	    if (mCursor.moveToFirst() ) {
	    	do {
	    		m = new HashMap<String, Object>();
	    		m.put(ATTRIBUTE_TITLE, mCursor.getString(mCursor.getColumnIndex("title")));
	    		m.put(ATTRIBUTE_DESCRIPTION, mCursor.getString(mCursor.getColumnIndex("description")));
	    		m.put(ATTRIBUTE_IMG, R.drawable.bus);
	    		data.add(m);
	    	} while(mCursor.moveToNext());
	    }

	    String from[] = {ATTRIBUTE_TITLE, ATTRIBUTE_DESCRIPTION, ATTRIBUTE_IMG};
	    int to[] = {R.id.title, R.id.description, R.id.img};
	        
	    SimpleAdapter sAdapter = new SimpleAdapter(this, data, R.layout.item, from, to); 
	        
	    mainListView = (ListView)findViewById(R.id.bookmarksList);
	    mainListView.setAdapter(sAdapter);
	}
	

	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bookmarklist);
        
        fillListView();
        mainListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {
				Intent intent = new Intent();
				intent.setClass(getBaseContext(), ResultActivity.class);
				/*Cursor mCursor = DBHelper.getDB(getBaseContext()).query("Bookmark", new String[]{"link"}, "id="+convertPositionToId(position), null, null, null, null);
				mCursor.moveToFirst();*/
				intent.putExtra(ResultActivity.DATA, Integer.toString(convertPositionToId(position))/*mCursor.getString(mCursor.getColumnIndex("link"))*/);
				startActivity(intent);			    
			}
		});

        registerForContextMenu(mainListView); // подписаться на контекстное меню
        
	}
	
	
	/**
	 * class for parallel work main GUI thread and Parse class
	 */
	private class WaitParse extends AsyncTask<Void, Void, Void>{
		
		private Intent intent;
		private Class<?> nextActivity;
		private int bookmarkId = -1;
		private ProgressDialog pd;
		
		public WaitParse(){
			super();
		}
		
		public WaitParse(Class<?> nextActivity) {
			super();
			this.nextActivity = nextActivity;
		}
		public WaitParse(Class<?> nextActivity, int bookmarkId) {
			super();
			this.nextActivity = nextActivity;
			this.bookmarkId = bookmarkId;
		}
		
		@Override
		protected void onPreExecute() {
			if (nextActivity == null) {
				pd = new ProgressDialog(BookmarksActivity.this);
				pd.setTitle("Download content");
				pd.setMessage("Wait while application download content!");
				pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				pd.setMax(9);
				pd.setIndeterminate(true);
				pd.show();
			} else pd = ProgressDialog.show(BookmarksActivity.this, "", "Loading...", true, true);
			if (nextActivity != null) {
				intent = new Intent();
				intent.setClass(getBaseContext(), nextActivity);
			}
			if (bookmarkId != -1){
				intent.putExtra(AddBookmarkActivity.BOOKMARK_ID, bookmarkId);
			}
			super.onPreExecute();
		}
		
		@Override
		protected Void doInBackground(Void... arg0) {	
			if (DBHelper.getDB(getBaseContext()).query("Bus", null, null, null, null, null, null).getCount() == 0) {
				Parser mParser = new Parser(getBaseContext());		
				if (mParser.isNetworkAvailable()) {
					for (int i=0; i<mParser.parseAbstractBus(); i++) {
						mParser.parseBus(i);
						pd.setIndeterminate(false);
						pd.incrementProgressBy(1);
					}
				} else {
					System.exit(0);//Toast.makeText(getBaseContext(), "Can't connect! Check internet connetion!", Toast.LENGTH_LONG);
				}
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result){
			if (pd != null) pd.dismiss();
			if (nextActivity != null) startActivity(intent);
		}
	}
	
	
}
