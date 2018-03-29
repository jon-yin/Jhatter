package samplegui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class LoginDialog extends JDialog {

	private InetAddress ipaddress;
	private int port;
	public boolean valid = false;
	
	public LoginDialog(JFrame parent)
	{
		super(parent, "Supply a connection", true);
		JPanel components = new JPanel();
		components.setLayout(new BorderLayout());
		JPanel buttons = new JPanel();
		JButton startConnect = new JButton("Connect");
		JButton exit = new JButton("Exit");
		exit.addActionListener(e -> setVisible(false));
		buttons.add(startConnect);
		buttons.add(exit);
		JPanel connect = new JPanel();
		connect.setLayout(new GridLayout(2, 2));
		JLabel ip = new JLabel("IP Address/Host Name");
		JTextField ipField = new JTextField(10);
		JLabel port = new JLabel("Port");
		JTextField portField = new JTextField(10);
		connect.add(ip);
		connect.add(ipField);
		connect.add(port);
		connect.add(portField);
		components.add(connect, BorderLayout.CENTER);
		components.add(buttons, BorderLayout.SOUTH);
		add(components, BorderLayout.CENTER);
		startConnect.addActionListener((event) -> {checkInput(ipField.getText(), portField.getText()); });
		pack();
		setLocationRelativeTo(parent);	
	}
	
	private void checkInput(String IPaddress, String port)
	{
		try 
		{
			InetAddress address = InetAddress.getByName(IPaddress);
			int portnum = Integer.parseInt(port);
			if (portnum < 0 || portnum > Math.pow(2, 32))
			{
				JOptionPane.showMessageDialog(this, "Port value out of range", "Port value incorrect!", JOptionPane.ERROR_MESSAGE);
			}
			else
			{
				valid = true;
				ipaddress = address;
				this.port = portnum;
				setVisible(false);
				return;
			}
		}
		catch (UnknownHostException e)
		{
			JOptionPane.showMessageDialog(this, "IP address posted was incorrect Format is ###.###.###.###", "Can't Find Host!", JOptionPane.ERROR_MESSAGE);
		}
		catch (NumberFormatException ex)
		{
			JOptionPane.showMessageDialog(this, "Port is not a number", "Port value incorrect!", JOptionPane.ERROR_MESSAGE);
		}
		valid = false;
	}
	
	public InetAddress getIP()
	{
		if (valid)
			return ipaddress;
		else
			return null;
	}
	
	public int getPort()
	{
		if (valid)
			return port;
		else
			return -1;
	}
	
	
	
	

	
}
