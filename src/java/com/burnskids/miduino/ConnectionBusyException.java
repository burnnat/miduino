package com.burnskids.miduino;

import java.io.IOException;

public class ConnectionBusyException extends IOException {
	
	private static final long serialVersionUID = 1L;

    public ConnectionBusyException() {
	super();
    }

    public ConnectionBusyException(String message) {
	super(message);
    }
}
