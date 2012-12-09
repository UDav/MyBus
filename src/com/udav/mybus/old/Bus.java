package com.udav.mybus.old;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.ArrayList;



public class Bus implements Externalizable {
	private String busNumber;
	private String link;
	private ArrayList<BusStation> directionA;
	private ArrayList<BusStation> directionB;
	
	public Bus () {
		directionA = new ArrayList<BusStation>();
		directionB = new ArrayList<BusStation>();
	}
	
	public Bus(String busNumber, String link) {
		this.busNumber = busNumber;
		this.link = link;
		directionA = new ArrayList<BusStation>();
		directionB = new ArrayList<BusStation>();
	}
	
	public void addDirectionA(BusStation bs) {
		directionA.add(bs);
	}
	
	public void addDirectionB(BusStation bs) {
		directionB.add(bs);
	}
	
	public ArrayList<BusStation> getDirectionA() {
		return directionA;
	}
	
	public ArrayList<BusStation> getDirectionB() {
		return directionB;
	}
	
	public String getBusNumber() {
		return busNumber;
	}
	public String getLink() {
		return link;
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		busNumber = in.readUTF();
		link = in.readUTF();
		
		int count = in.readInt();
        for(int i=0; i<count; i++){
            BusStation bs = new BusStation();
            bs.readExternal(in);
            directionA.add(bs);
        }
        
        int count2 = in.readInt();
        for (int i=0; i<count2; i++) {
        	BusStation bs = new BusStation();
        	bs.readExternal(in);
        	directionB.add(bs);
        }
		
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeUTF(busNumber);
		out.writeUTF(link);
		out.writeInt(directionA.size());
		for (Externalizable ext : directionA) ext.writeExternal(out);
		out.writeInt(directionB.size());
		for (Externalizable ext : directionB) ext.writeExternal(out);
		
	}

}
