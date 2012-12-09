package com.udav.mybus.old;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;



import java.io.*;
import java.util.ArrayList;


public class Parser{
	private String result = "";
	private ArrayList<String> abstractBus;
	private Context context;
	
	public Parser(Context context) {
		this.context = context;
	}
	
	private boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null;
	}
	
	/**
	 * parse link from main page
	 */
	public void parseAbstractBus() {
		abstractBus = new ArrayList<String>();
		Connection connect = null;
		connect = Jsoup.connect("http://m.bus55.ru/");
		try {
			Document doc = connect.get();
			
			Elements elements = doc.getElementsByAttributeValue("class", "bullet");
			for (int i=0; i<elements.size(); i++){
				abstractBus.add(elements.get(i).select("a").attr("href"));
			}
		} catch (Exception e) {
			System.out.println("Can't connect!");
			result = "error";
			e.printStackTrace();
		}
		
	}
	
	/**
	 * parse links to bus
	 */
	public void parseBus() {
		for (int i=0; i<abstractBus.size(); i++) {
			Connection connect = null;
			connect = Jsoup.connect(abstractBus.get(i));
			try {
				Document doc = connect.get();
			
				Elements elements = doc.getElementsByAttributeValue("class", "bullet");
				for (int j=0; j<elements.size(); j++){
					BusContainer.busArray.add(new Bus(elements.get(j).getElementsByTag("strong").text()+" "+elements.get(j).select("a").select("span").text(), elements.get(j).select("a").attr("href")));
				}
			} catch (Exception e) {
				System.out.println("Can't connect!");
				result = "error";
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * parse bus station for recive id
	 * @param busId id bus in busArray
	 */
	
	public void parseBusStation(int busId){
				Connection connect = null;
				connect = Jsoup.connect(BusContainer.busArray.get(busId).getLink());
				try {
					Document doc = connect.get();

					String text = doc.select("div[class*=pSide5]").html();
					String tmpStr [] = text.split("<h2 class=\"grayTitle\">Направление Б:</h2> ");
					
					Document docA = Jsoup.parse(tmpStr[0]);
					Elements elementsA = docA.getElementsByAttributeValue("class", "bullet");
					for (int j=0; j<elementsA.size(); j++){
						BusContainer.busArray.get(busId).addDirectionA(new BusStation(elementsA.get(j).select("a").text(), elementsA.get(j).select("a").attr("href")));
					}
							 
					Document docB = Jsoup.parse(tmpStr[1]);
					Elements elementsB = docB.getElementsByAttributeValue("class", "bullet");
					for (int j=0; j<elementsB.size(); j++){
						BusContainer.busArray.get(busId).addDirectionB(new BusStation(elementsB.get(j).select("a").text(), elementsB.get(j).select("a").attr("href")));
					}
				
				} catch (Exception e) {
					System.out.println("Can't connect!");
					result = "error";
					e.printStackTrace();
				}
	}
	
	/**
	 * parse time of arrival for recive link
	 * 
	 * @param link
	 *            to the page you want to parse
	 * @return strings text
	 */
	public String parseTime(String link) {
		Connection connect = null;
		if (isNetworkAvailable()) {
			connect = Jsoup.connect(link);
			try {
				Document doc = connect.get();
				Elements elements = doc.getElementsByAttributeValue("class",
						"bullet");
				for (int i = 0; i < elements.size(); i++) {
					String transportType = "";
					String tmp;
					tmp = elements.get(i).html();
					int counter = tmp.indexOf(";") + 1;
					if (counter > 0)
						while (tmp.charAt(counter) != '<') {
							transportType += tmp.charAt(counter);
							counter++;
						}
					result += elements.get(i).getElementsByTag("strong").text()
							+ " "
							+ transportType
							+ " "
							+ elements
									.get(i)
									.getElementsByAttributeValue("class",
											"greenBold").text() + "\n";
				}
			} catch (Exception e) {
				System.out.println("Can't connect!");
				parseTime(link);
				e.printStackTrace();
				parseTime(link);
			}
		} else {
			result =  "Can't connect! check internet connection!";
		}
		return result;
	}
	
	public void parseAll() {
		parseAbstractBus();
		parseBus();
		for (int i=0; i<BusContainer.busArray.size(); i++) {
			parseBusStation(i);
		}
	}
	
	/**
	 * get bus from busArray
	 * @param busId id in busArray
	 * @return object Bus
	 */
	public Bus getBus(int busId) {
		return BusContainer.busArray.get(busId);
	}
	
	/**
	 * serialization busArray in file on SD card
	 */
	public void saveBusArray() {
		BusContainer mBusContainer = new BusContainer();
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(new FileOutputStream("/mnt/sdcard/.MyBus/BusArray.ser"));
			oos.writeObject(mBusContainer);
	        oos.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (oos != null) oos.close();
			} catch (Exception e) {e.printStackTrace();}
		}
	}
	
	/**
	 * deserialization busArray from file on SD card
	 * @return complite ok
	 */
	public boolean loadBusArray() {
		if (new File("/mnt/sdcard/.MyBus/BusArray.ser").exists()) {
			try {
				ObjectInputStream ois = new ObjectInputStream(new FileInputStream("/mnt/sdcard/.MyBus/BusArray.ser"));
				BusContainer mBusContainer = new BusContainer();
				mBusContainer = (BusContainer)ois.readObject();
			} catch (Exception e) {e.printStackTrace();}
			return true;
		} else return false;
	}

}
