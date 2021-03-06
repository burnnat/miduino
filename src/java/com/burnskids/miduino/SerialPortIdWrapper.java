package com.burnskids.miduino;

import gnu.io.CommPortIdentifier;

/**
 * Convenience class wrapping a {@link CommPortIdentifier}, with a nicer
 * {@link #toString()} method for easier debugging.
 */
public class SerialPortIdWrapper {
	private CommPortIdentifier portId;
	
	public SerialPortIdWrapper(CommPortIdentifier portId) {
		this.portId = portId;
	}
	
	public CommPortIdentifier getPortID() {
		return portId;
	}
	
	@Override
	public String toString() {
		return portId == null ? "" : portId.getName();
	}
}
