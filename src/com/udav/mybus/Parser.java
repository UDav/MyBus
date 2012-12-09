package com.udav.mybus;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.udav.mybus.old.Bus;
import com.udav.mybus.old.BusContainer;

import android.content.ContentValues;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;



import java.io.*;
import java.util.ArrayList;
import java.util.Calendar;


public class Parser{
	private String result = "";
	private ArrayList<String> abstractBus;
	private Context context;
	
	public Parser(Context context) {
		this.context = context;
	}
	
	public boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null;
	}
	
	/**
	 * parse link from main page
	 */
	public int parseAbstractBus() {
		abstractBus = new ArrayList<String>();
		Connection connect = Jsoup.connect("http://m.bus55.ru/");
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
		return abstractBus.size();
	}
	
	/**
	 * parse links to bus
	 */
	public void parseBus(int i) {
		//for (int i=0; i<abstractBus.size(); i++) {
		int size = 0;
			Connection connect = Jsoup.connect(abstractBus.get(i));
			try {
				Document doc = connect.get();
			
				Elements elements = doc.getElementsByAttributeValue("class", "bullet");
				for (int j=0; j<elements.size(); j++){
					ContentValues mContentValues = new ContentValues();
					mContentValues.put("name", elements.get(j).getElementsByTag("strong").text()+
							" "+elements.get(j).select("a").select("span").text());
					mContentValues.put("buslink", elements.get(j).select("a").attr("href"));
										
					parseBusStation(elements.get(j).select("a").attr("href"), DBHelper.getDB(context).insert("Bus", null, mContentValues));
				}
				//BookmarksActivity.pd.incrementProgressBy(1);
			} catch (Exception e) {
				System.out.println("Can't connect!");
				result = "error";
				e.printStackTrace();
			}
		//}
	}
	
	/**
	 * parse bus station for recive id
	 * @param busId id bus in busArray
	 */
	
	private void parseBusStation(String link, long busId){
				Connection connect = null;
				connect = Jsoup.connect(link);
				try {
					Document doc = connect.get();

					String text = doc.select("div[class*=pSide5]").html();
					String tmpStr [] = text.split("<h2 class=\"grayTitle\">Направление Б:</h2> ");
					
					Document docA = Jsoup.parse(tmpStr[0]);
					Elements elementsA = docA.getElementsByAttributeValue("class", "bullet");
					for (int j=0; j<elementsA.size(); j++){
						ContentValues mContentValues = new ContentValues();
						mContentValues.put("name", elementsA.get(j).select("a").text());
						mContentValues.put("stationlink", elementsA.get(j).select("a").attr("href"));
						mContentValues.put("busid", busId);
						mContentValues.put("typedirection", 0);
						DBHelper.getDB(context).insert("BusStation", null, mContentValues);
					}
							 
					Document docB = Jsoup.parse(tmpStr[1]);
					Elements elementsB = docB.getElementsByAttributeValue("class", "bullet");
					for (int j=0; j<elementsB.size(); j++){
						ContentValues mContentValues = new ContentValues();
						mContentValues.put("name", elementsB.get(j).select("a").text());
						mContentValues.put("stationlink", elementsB.get(j).select("a").attr("href"));
						mContentValues.put("busid", busId);
						mContentValues.put("typedirection", 1);
						DBHelper.getDB(context).insert("BusStation", null, mContentValues);
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
			}
		} else {
			result =  "Can't connect! check internet connection!";
		}
		return result;
	}
	
	public String parseSchedule(String busNumber, String typeTransport, String direction) {
		if (isNetworkAvailable()) {
			Connection connect = null;
			Calendar c = Calendar.getInstance(); 
			String hour = Integer.toString(c.get(Calendar.HOUR_OF_DAY));
			String link = "http://transport.admomsk.ru/v2/php/schedule/wap/?transport="
					+typeTransport+"&route="+busNumber+"&direction="+direction+"&datemask=1111100&houp="+hour+"&step=4";
			System.out.println(link);
			connect = Jsoup.connect(link);
			try {
				Document doc = connect.get();
				result = doc.text();
			} catch (Exception e) {e.printStackTrace();}
		} else result = "check internet connection!";
		
		return result;
	}
	
	public void parseAll() {
		/*parseAbstractBus();
		parseBus();*/
	}

}
