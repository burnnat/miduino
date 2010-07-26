package com.burnskids.miduino;

import gnu.io.CommPortIdentifier;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class MiduinoFrame extends JFrame implements MiduinoStatusListener {

	private static final long serialVersionUID = 1L;
	private MiduinoController controller;
	
	private List<JComponent> controls = new ArrayList<JComponent>();
	private PresetSelector presetSpinner;
	private JFileChooser fileBrowser;

	public MiduinoFrame() {
		super("Miduino");

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));

		fileBrowser = new JFileChooser();
		
		presetSpinner = new PresetSelector("Play Preset");
		presetSpinner.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					MiduinoFrame.this.controller.playPreset(presetSpinner.getSelectedPreset(true));
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		presetSpinner.setEnabled(false);
		addControl(presetSpinner);
		
		JButton streamButton = new JButton("Stream Midi File");
		streamButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int result = fileBrowser.showOpenDialog(MiduinoFrame.this);

				if(result == JFileChooser.APPROVE_OPTION) {
					try {
						MiduinoFrame.this.controller.playFile(fileBrowser.getSelectedFile());
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		addControl(streamButton);
		
		JButton exitButton = new JButton("Exit");
		exitButton.setAlignmentX(CENTER_ALIGNMENT);
		exitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				MiduinoFrame.this.processWindowEvent(new WindowEvent(MiduinoFrame.this, WindowEvent.WINDOW_CLOSING));
			}
		});

		add(Box.createVerticalGlue());
		for (JComponent control : controls) {
			add(control);
			add(Box.createVerticalStrut(5));
		}
		add(Box.createVerticalGlue());
		add(exitButton);
		add(Box.createVerticalStrut(5));

		setSize(200, 300);
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((screen.width - getWidth()) / 2, (screen.height - getHeight()) / 2);
		setStatus(true);
	}
	
	private void addControl(JComponent control) {
		control.setAlignmentX(CENTER_ALIGNMENT);
		controls.add(control);
	}

	public void setSerialPort(CommPortIdentifier portId) {
		if(portId != (controller == null ? null : controller.getPortId())) {
			if(controller != null) {
				System.out.println("Closing controller.");
				controller.close();
				controller = null;
			}

			if(portId != null) {
				try {
					System.out.println("New controller.");
					controller = new MiduinoController(portId, this);
					presetSpinner.setPresetCount(controller.getPresetCount());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void setStatus(boolean disabled) {
		for (JComponent control : controls) {
			control.setEnabled(!disabled);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		MiduinoFrame frame = new MiduinoFrame();
		
		Enumeration<CommPortIdentifier> ports = CommPortIdentifier.getPortIdentifiers();
		List<SerialPortIdWrapper> portsToShow = new ArrayList<SerialPortIdWrapper>();
		while (ports.hasMoreElements()) {
			portsToShow.add(new SerialPortIdWrapper(ports.nextElement()));	
		}
		
		Object result = JOptionPane.showInputDialog(frame, "Please choose a port:", "Port Selection", JOptionPane.PLAIN_MESSAGE, null, portsToShow.toArray(), portsToShow.get(0));
		
		if(result != null) {
			frame.setSerialPort(((SerialPortIdWrapper) result).getPortID());
			frame.setVisible(true);
		}
	}
}
