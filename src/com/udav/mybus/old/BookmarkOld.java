package com.udav.mybus.old;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class BookmarkOld implements Serializable{

	private String title;
	private int busId;
	public int getBusId() {
		return busId;
	}

	private int directionId;
	public int getDirectionId() {
		return directionId;
	}

	private int busStationId;
	public int getBusStationId() {
		return busStationId;
	}

	private String description;
	private String busStationLink;
	
	public static ArrayList<BookmarkOld> bookmarks = new ArrayList<BookmarkOld>();
	
	public BookmarkOld(String title, String description, String link, int busId, int directionId, int busStationId){
		this.title = title;
		this.description = description;
		this.busStationLink = link;
		this.busId = busId;
		this.directionId = directionId;
		this.busStationId = busStationId;
		
		bookmarks.add(this);
		saveBookmarks();
	}
	
	public BookmarkOld(String title, String description, String link, int busId, int directionId, int busStationId, int bookmarkId){
		this.title = title;
		this.description = description;
		this.busStationLink = link;
		this.busId = busId;
		this.directionId = directionId;
		this.busStationId = busStationId;
		
		bookmarks.set(bookmarkId, this);
		saveBookmarks();
	}
	
		
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getBusStationLink() {
		return busStationLink;
	}

	public void setBusStationLink(String busStationLink) {
		this.busStationLink = busStationLink;
	}

	
	public static void saveBookmarks() {
		FileOutputStream fos = null;
		ObjectOutputStream os = null;
		try {
			File myDir = new File("/mnt/sdcard/.MyBus");
			if (!myDir.exists()){
			    myDir.mkdir();
			}
			fos = new FileOutputStream("/mnt/sdcard/.MyBus/Bookmarks");
			os = new ObjectOutputStream(fos);
			os.writeObject(bookmarks);
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (os != null) os.close();
				if (fos != null) fos.close();
			} catch (Exception e) {e.printStackTrace();}
		}
	}
	
	public static boolean loadBokmarks() {
		if (new File("/mnt/sdcard/.MyBus/Bookmarks").exists()) {
			FileInputStream fis = null;
			ObjectInputStream is = null;
			try {
				fis =  new FileInputStream("/mnt/sdcard/.MyBus/Bookmarks");
				is = new ObjectInputStream(fis);
				bookmarks = (ArrayList<BookmarkOld>) is.readObject();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (is != null) is.close();
					if (fis != null) fis.close();
				} catch (Exception e) {e.printStackTrace();}
			}
			return true;
		} else return false;
	}

}
