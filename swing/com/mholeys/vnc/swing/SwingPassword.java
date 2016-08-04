package com.mholeys.vnc.swing;

import javax.swing.JOptionPane;

import com.mholeys.vnc.display.IPasswordRequester;

public class SwingPassword implements IPasswordRequester {

	@Override
	public String getPassword() {
		String password = JOptionPane.showInputDialog("Password"); 
		return password;
	}

}
