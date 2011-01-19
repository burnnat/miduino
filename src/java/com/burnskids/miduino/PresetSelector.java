package com.burnskids.miduino;

import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

/**
 * A JPanel used to control the playback of a number of preset melodies on
 * the organ controller. Contains a spinner to select a preset by number,
 * converting between the zero-indexed preset numbers used on the device
 * and a one-based system for display, as well as a button used to initiate
 * playback. The playback action should be governed by an {@link ActionListener}
 * attached to this element.
 */
public class PresetSelector extends JPanel {
	
	private static final long serialVersionUID = 1L;
	private JButton playButton;
	private JSpinner spinner;
	private SpinnerNumberModel spinnerModel;
	
	private boolean hasCount = false;

	public PresetSelector(String buttonText) {
		this(0, buttonText);
	}
	
	public PresetSelector(int presetCount, String buttonText) {
		spinnerModel = new SpinnerNumberModel();
		spinner = new JSpinner(spinnerModel);
		playButton = new JButton(buttonText);

		add(spinner);
		add(playButton);
		
		setMaximumSize(getPreferredSize());
		setMinimumSize(getPreferredSize());
		
		setPresetCount(presetCount);
	}
	
	public void setPresetCount(int presetCount) {
		if(presetCount < 1) {
			hasCount = false;
			forceEnabled(false);
			spinnerModel.setMinimum(0);
			spinnerModel.setMaximum(0);
			spinnerModel.setValue(0);
		}
		else {
			hasCount = true;
			setEnabled(isEnabled());
			spinnerModel.setMinimum(1);
			spinnerModel.setMaximum(presetCount);
			spinnerModel.setValue(1);
		}
	}
	
	public void addActionListener(ActionListener listener) {
		playButton.addActionListener(listener);
	}
	
	public int getSelectedPreset(boolean zeroBased) {
		return ((Integer) spinner.getModel().getValue()) - (zeroBased ? 1 : 0);
	}
	
	private void forceEnabled(boolean enabled) {
		spinner.setEnabled(enabled);
		playButton.setEnabled(enabled);
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		if(hasCount) {
			forceEnabled(enabled);
		}
	}
}
