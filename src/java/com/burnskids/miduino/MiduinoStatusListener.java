package com.burnskids.miduino;

/**
 * Interface to listen for Miduino controller events.
 */
public interface MiduinoStatusListener {
	/**
	 * Called when the Miduino controller successfully initiates a connection with the client.
	 * 
	 * @param source
	 */
	public void onLoad(MiduinoController source);
	
	/**
	 * Called when the Miduino controller changes state, with <code>busy</code> as <code>false</code>
	 * if playback has just finished, or <code>true</code> if playback has just begun.
	 * 
	 * @param busy
	 */
	public void setStatus(boolean busy);
}
