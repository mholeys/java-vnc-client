package com.mholeys.vnc.swing;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.mholeys.vnc.data.EncodingSettings;
import com.mholeys.vnc.data.PixelFormat;
import com.mholeys.vnc.display.input.IConnectionInformation;
import com.mholeys.vnc.display.input.IPasswordRequester;

public class SwingConnection implements IConnectionInformation {

	private InetAddress address;
	private int port = -1;
	private EncodingSettings settings;
	private IPasswordRequester password;
	private PixelFormat format;
	
	public SwingConnection(EncodingSettings settings, PixelFormat format, IPasswordRequester password) {
		this.settings = settings;
		this.format = format;
		this.password = password;
	}
	
	@Override
	public InetAddress getAddress() {
		if (address == null) {
			JPanel panel = new JPanel();
			JLabel label = new JLabel("Enter the address:");
			JTextField addressField = new JTextField(20);
			
			panel.add(label);
			panel.add(addressField);
			
			addressField.addAncestorListener(new RequestFocusListener());
			
			String[] options = new String[] { "OK", "Cancel" };
			int option = JOptionPane.showOptionDialog(null, panel, "VNC Address", JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
			if (option == 0) {
				try {
					address = InetAddress.getByName(addressField.getText());
				} catch (UnknownHostException e) {
					System.exit(0);
				}
				return address;
			}
		System.exit(0);
		}
		return address;
	}

	@Override
	public int getPort() {
		if (port == -1) {
			JPanel panel = new JPanel();
			JLabel label = new JLabel("Enter the port:");
			JTextField portField = new JTextField(5);
			
			panel.add(label);
			panel.add(portField);
			
			portField.addAncestorListener(new RequestFocusListener());
			
			String[] options = new String[] { "OK", "Cancel" };
			int option = JOptionPane.showOptionDialog(null, panel, "VNC Port", JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
			if (option == 0) {
				port = Integer.parseInt(portField.getText());
				return port;
			}
		System.exit(0);
		}
		return port;
	}

	@Override
	public boolean hasPrefferedFormat() {
		return format != null;
	}

	@Override
	public PixelFormat getPrefferedFormat() {
		return format;
	}

	@Override
	public boolean hasPrefferedEncoding() {
		return settings != null;
	}

	@Override
	public EncodingSettings getPrefferedEncoding() {
		return settings;
	}

	@Override
	public IPasswordRequester getPasswordRequester() {
		return password;
	}

}
