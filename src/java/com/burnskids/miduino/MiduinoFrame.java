package com.burnskids.miduino;

import gnu.io.CommPortIdentifier;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;

public class MiduinoFrame extends JFrame implements MiduinoStatusListener {

	private static final long serialVersionUID = 1L;
	
	private MiduinoController controller;
	private List<JButton> buttons = new ArrayList<JButton>();
	
	public MiduinoFrame() {
		super("Miduino");
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		
		SerialPortComboBox portBox = new SerialPortComboBox();
		portBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				MiduinoFrame.this.setSerialPort((CommPortIdentifier) ((SerialPortComboBox) e.getSource()).getSelectedItem());
			}
		});
		portBox.setAlignmentX(CENTER_ALIGNMENT);
		portBox.setMaximumSize(portBox.getMinimumSize());
		
		createButton("Play Preset", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					MiduinoFrame.this.controller.playPreset(0);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		createButton("Stream Midi File", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					MiduinoFrame.this.controller.playFile(new File("/home/nat/newMary0.mid"));
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});

		add(Box.createVerticalGlue());
		add(portBox);
		for (JButton button : buttons) {
			add(Box.createVerticalStrut(5));
			add(button);
		}
		add(Box.createVerticalGlue());
		
		setSize(500, 800);
		setStatus(true);
	}
	
	private void createButton(String text, ActionListener listener) {
		JButton button = new JButton(text);
		button.addActionListener(listener);
		button.setAlignmentX(CENTER_ALIGNMENT);
		buttons.add(button);
	}
	
	public void setSerialPort(CommPortIdentifier portId) {
		if(controller == null || portId != controller.getPortId()) {
			if(controller != null)
				controller.close();
			
			try {
				controller = new MiduinoController(portId, this);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new MiduinoFrame().setVisible(true);
	}

	@Override
	public void setStatus(boolean disabled) {
		for (JButton button : buttons) {
			button.setEnabled(!disabled);
		}
	}
}
