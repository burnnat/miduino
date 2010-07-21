package com.burnskids.miduino;

import java.io.FileInputStream;

public class MidiString {

	public static void main(String[] args) throws Exception {
		FileInputStream midiFile = new FileInputStream("/home/nat/newMary0.mid");
		
		int data;
		int count = 0;
		String acc = "";
		while((data = midiFile.read()) != -1) {
			acc += String.format("\\%1$03o", data);
			count++;
			
			if(count >= 16) {
			  System.out.println("  \"" + acc + "\"");
			  acc = "";
			  count = 0;
			}
		}
	}
}
