package com.udav.mybus.old;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;


public class BusContainer implements Externalizable {

	public static ArrayList<Bus> busArray = new ArrayList<Bus>();

	
	public static void addBus(Bus mBus) {
		busArray.add(mBus);
	}
	
	public static Bus getBus(int busId) {
		return busArray.get(busId);
	}
	
	@Override
	public void readExternal(ObjectInput input) throws IOException,
			ClassNotFoundException {
		int count = input.readInt();
		for (int i=0; i<count; i++) {
			Bus mBus = new Bus();
			mBus.readExternal(input);
			busArray.add(mBus);
		}
		
	}

	@Override
	public void writeExternal(ObjectOutput output) throws IOException {
		output.writeInt(busArray.size());
		for (Externalizable ext : busArray) {
			ext.writeExternal(output);
		}
		
	}

}
