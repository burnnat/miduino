package com.burnskids.miduino;

public interface MiduinoStatusListener {
	public void onLoad(MiduinoController source);
	public void setStatus(boolean busy);
}
