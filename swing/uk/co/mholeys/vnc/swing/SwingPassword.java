package uk.co.mholeys.vnc.swing;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

import uk.co.mholeys.vnc.display.input.IPasswordRequester;

public class SwingPassword implements IPasswordRequester {

	@Override
	public String getPassword() {
		JPanel panel = new JPanel();
		JLabel label = new JLabel("Enter the password:");
		JPasswordField passwordField = new JPasswordField(30);
		
		panel.add(label);
		panel.add(passwordField);
		
		passwordField.addAncestorListener(new RequestFocusListener());
		
		String[] options = new String[] { "OK", "Cancel" };
		int option = JOptionPane.showOptionDialog(null, panel, "VNC Password", JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
		if (option == 0) {
			return new String(passwordField.getPassword());
		}
		System.exit(0);
		return "";
	}

}
