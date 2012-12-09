package com.udav.mybus.old;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class BusStation implements Externalizable {
	public BusStation() {}
	
	public BusStation(String name, String link){
		this.name = name;
		this.link = link;
	}
	public String name;
	public String link;
	@Override
	public void readExternal(ObjectInput input) throws IOException,
			ClassNotFoundException {
		name = input.readUTF();
		link = input.readUTF();
		
	}
	@Override
	public void writeExternal(ObjectOutput output) throws IOException {
		output.writeUTF(name);
		output.writeUTF(link);
		
	}
}
