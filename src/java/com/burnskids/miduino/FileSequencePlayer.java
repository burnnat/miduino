package com.burnskids.miduino;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileSequencePlayer implements MiduinoStatusListener {
	private MiduinoController controller;
	private List<File> files;
	int currentIndex = 0;
	private boolean autoLoop = false;
	private boolean inSequence = false;
	
	public FileSequencePlayer(MiduinoController controller) {
		this.controller = controller;
		this.files = new ArrayList<File>();
	}
	
	public void setAutoLoop(boolean loop) {
		autoLoop = loop;
	}
	
	public void reset() {
		currentIndex = 0;
	}
	
	public void clear() {
		files.clear();
		this.reset();
	}
	
	public void addFile(File file) {
		files.add(file);
	}
	
	public void startSequence() {
		if(inSequence)
			return;
		
		controller.addStatusListener(this);
		inSequence = true;
		playNextFile();
	}
	
	public void stopSequence() {
		controller.removeStatusListener(this);
		inSequence = false;
	}
	
	public boolean isInSequence() {
		return inSequence;
	}
	
	public void playNextFile() {
		if(currentIndex >= files.size()) {
			if(autoLoop == false) {
				stopSequence();
				return;
			}
			else {
				currentIndex = 0;
			}
		}
		
		try {
			controller.playFile(files.get(currentIndex));
			currentIndex++;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setStatus(boolean busy) {
		if(!busy) {
			try {
				Thread.sleep(6000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			if(isInSequence())
				playNextFile();
		}
	}

	@Override
	public void onLoad(MiduinoController source) {}
}
