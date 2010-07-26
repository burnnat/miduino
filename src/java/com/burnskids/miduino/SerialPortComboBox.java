package com.burnskids.miduino;

import gnu.io.CommPortIdentifier;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

public class SerialPortComboBox extends JComboBox {
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	public SerialPortComboBox() {
		Enumeration<CommPortIdentifier> ports = CommPortIdentifier.getPortIdentifiers();
		List<CommPortIdentifier> portsToShow = new ArrayList<CommPortIdentifier>();

		portsToShow.add(null);
		while (ports.hasMoreElements()) {
			portsToShow.add(ports.nextElement());
		}

		this.setModel(new DefaultComboBoxModel(portsToShow.toArray()));
		this.setRenderer(new BasicComboBoxRenderer() {
			private static final long serialVersionUID = 1L;

			@Override
			public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				if (isSelected) {
					setBackground(list.getSelectionBackground());
					setForeground(list.getSelectionForeground());
				} else {
					setBackground(list.getBackground());
					setForeground(list.getForeground());
				}
				
				if(value == null)
					setText("[None]");
				else if(value instanceof CommPortIdentifier)
					setText(((CommPortIdentifier) value).getName());
				else
					setText((value == null) ? "" : value.toString());
				
				return this;
			}
		});
		
		this.setSelectedIndex(0);
	}
}
