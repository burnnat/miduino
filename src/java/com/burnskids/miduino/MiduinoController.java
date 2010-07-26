package com.burnskids.miduino;

import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.TooManyListenersException;

public class MiduinoController implements SerialPortEventListener {
	// Outgoing commands
	private static final byte MSG_CHECK_ALIVE = (byte) 0x10;
	private static final byte MSG_PLAY_PRESET = (byte) 0x3C;
	private static final byte MSG_PLAY_STREAM = (byte) 0x3E;

	// Incoming messages
	private static final byte MSG_CONFIRM_ALIVE = (byte) 0x11;
	private static final byte MSG_ERR_MIDI_FORMAT = (byte) 0x51;
	private static final byte MSG_SEND_MORE = (byte) 0xFF;
	private static final byte MSG_END_MIDI = (byte) 0xFE;

	private static final int TIME_OUT = 2000; // in milliseconds
	private static final int DATA_RATE = 9600; // baud rate

	private CommPortIdentifier portId;
	private MiduinoStatusListener listener;
	private boolean busy = false;

	private SerialPort serialPort;
	private InputStream input;
	private OutputStream output;
	private int presetCount = 0;

	private InputStream midiFile;

	public MiduinoController(CommPortIdentifier portId) throws PortInUseException, UnsupportedCommOperationException, IOException, TooManyListenersException {
		this(portId, null);
	}

	public MiduinoController(CommPortIdentifier portId, MiduinoStatusListener listener) throws PortInUseException, UnsupportedCommOperationException, IOException, TooManyListenersException {
		this.portId = portId;
		this.listener = listener;

		// open serial port, and use class name for the appName.
		serialPort = (SerialPort) portId.open(this.getClass().getName(), TIME_OUT);

		// set port parameters
		serialPort.setSerialPortParams(DATA_RATE, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,	SerialPort.PARITY_NONE);

		// open the streams
		input = serialPort.getInputStream();
		output = serialPort.getOutputStream();

		// add event listeners
		serialPort.addEventListener(this);
		serialPort.notifyOnDataAvailable(true);
		System.out.println("<< Java Serial Listener Initialized >>");

		checkAlive();
		System.out.print("Checking connection...");

	}
	
	public int getPresetCount() {
		return presetCount;
	}

	public synchronized void checkAlive() throws IOException {
		checkBusy();

		setBusy(true);
		output.write(MSG_CHECK_ALIVE);
	}

	public synchronized void playPreset(int presetNumber) throws IOException {
		checkBusy();

		setBusy(true);
		output.write(MSG_PLAY_PRESET);
		output.write(presetNumber);
	}

	public synchronized void playFile(File file) throws IOException {
		checkBusy();

		setBusy(true);
		this.midiFile = new FileInputStream(file);

		output.write(MSG_PLAY_STREAM);
		output.flush();

		sendFilePart();
	}

	public synchronized void sendFilePart() throws IOException {
		byte[] buffer = new byte[127];
		int len = midiFile.read(buffer);

		if(len == -1)
			return;

		output.write(buffer, 0, len);
		output.flush();
	}

	public synchronized void close() {
		if (serialPort != null) {
			serialPort.removeEventListener();
			serialPort.close();
		}
	}

	public synchronized void serialEvent(SerialPortEvent oEvent) {
		if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			try {
				int available = input.available();
				byte chunk[] = new byte[available];
				input.read(chunk, 0, available);

				StringBuffer buffer = new StringBuffer();

				for (int i = 0; i < chunk.length; i++) {
					byte b = chunk[i];
					
					switch (b) {
					case MSG_SEND_MORE:
						sendFilePart();
						break;
					case MSG_CONFIRM_ALIVE:
						presetCount = chunk[++i]; 
						System.out.println(" confirmed alive.");
						setBusy(false);
						break;
					case MSG_ERR_MIDI_FORMAT:
						System.out.println("MIDI file not format 0.");
						setBusy(false);
						break;
					case MSG_END_MIDI:
						setBusy(false);
						break;
					default:
						buffer.append((char) b);
						break;
					}
				}

				System.out.print(buffer.toString());
			} catch (Exception e) {
				System.err.println(e.toString());
			}
		}
	}

	public CommPortIdentifier getPortId() {
		return portId;
	}

	private synchronized void setBusy(boolean busy) {
		if(this.busy != busy) {
			this.busy = busy;
			if(listener != null)
				listener.setStatus(busy);
		}
	}

	private synchronized void checkBusy() throws ConnectionBusyException {
		if(busy)
			throw new ConnectionBusyException();
	}
}
