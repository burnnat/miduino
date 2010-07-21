package com.burnskids.miduino;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;

public class SerialTest implements SerialPortEventListener {
	private static byte MSG_SEND_MORE = (byte) 0xFF;
	private static byte MSG_END_MIDI = (byte) 0xFE;
//	private static byte MSG_PLAY_PRESET = (byte) 0x3C;
	private static byte MSG_PLAY_STREAM = (byte) 0x3E;
	private SerialPort serialPort;

	private static final String PORT_NAMES[] = { 
		"/dev/tty.usbserial-A9007UX1", // Mac OS X
		"/dev/ttyUSB0", // Linux
		"COM3", // Windows
	};

	private InputStream input;
	private OutputStream output;
	private InputStream midiFile;
	private static final int TIME_OUT = 2000; // in milliseconds
	private static final int DATA_RATE = 9600; // baud rate

	public void initialize() {
		CommPortIdentifier portId = null;
		Enumeration<?> portEnum = CommPortIdentifier.getPortIdentifiers();

		// iterate through, looking for the port
		while (portEnum.hasMoreElements()) {
			CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
			System.out.println(currPortId.getName());
			for (String portName : PORT_NAMES) {
				if (currPortId.getName().equals(portName)) {
					portId = currPortId;
					break;
				}
			}
		}

		if (portId == null) {
			System.out.println("Could not find COM port.");
			return;
		}

		try {
			// open serial port, and use class name for the appName.
			serialPort = (SerialPort) portId.open(this.getClass().getName(), TIME_OUT);

			// set port parameters
			serialPort.setSerialPortParams(DATA_RATE,
					SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1,
					SerialPort.PARITY_NONE);

			// open the streams
			input = serialPort.getInputStream();
			output = serialPort.getOutputStream();

			midiFile = new FileInputStream("/home/nat/newMary0.mid");

			// add event listeners
			serialPort.addEventListener(this);
			serialPort.notifyOnDataAvailable(true);
			
			output.write(MSG_PLAY_STREAM);
			output.flush();
			
			sendFilePart();
		} catch (Exception e) {
			System.err.println(e.toString());
		}
	}

	public synchronized void sendFilePart() throws IOException {
		byte[] buffer = new byte[127];
		int len = midiFile.read(buffer);
		
		if(len == -1)
			return;
		
		output.write(buffer, 0, len);
		output.flush();
	}

	/**
	 * This should be called when you stop using the port.
	 * This will prevent port locking on platforms like Linux.
	 */
	public synchronized void close() {
		if (serialPort != null) {
			serialPort.removeEventListener();
			serialPort.close();
		}
	}

	/**
	 * Handle an event on the serial port. Read the data and print it.
	 */
	public synchronized void serialEvent(SerialPortEvent oEvent) {
		if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			try {
				int available = input.available();
				byte chunk[] = new byte[available];
				input.read(chunk, 0, available);

				StringBuffer buffer = new StringBuffer();
				
				for (byte b : chunk) {
					if(b == MSG_SEND_MORE) {
						sendFilePart();
					}
					else if(b == MSG_END_MIDI) {
//						close();
//						System.exit(0);
					}
					else {
						buffer.append((char) b);
					}	
				}
				
				System.out.print(buffer.toString());
			} catch (Exception e) {
				System.err.println(e.toString());
			}
		}
	}

	public static void main(String[] args) throws Exception {
		SerialTest main = new SerialTest();
		main.initialize();
		System.out.println("<< Java Serial Listener Initialized >>");
	}
}
