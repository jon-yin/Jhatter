package samplegui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class LoginDialog extends JDialog {

	private ConnectInfo info;
	private int closedStatus;
	// Pattern to match IP addresses and hostnames to prevent costly domain lookups on wrong syntaxes.
	private Pattern ipregex = Pattern.compile("(((\\d+\\.){3}\\d+))|(\\w+\\.)+\\D+|.{0}");
	public LoginDialog(JFrame parent)
	{
		super(parent, "Supply a connection", true);
		JPanel components = new JPanel();
		components.setLayout(new BorderLayout());
		JPanel buttons = new JPanel();
		JButton startConnect = new JButton("Connect");
		JButton exit = new JButton("Exit");
		exit.addActionListener(e -> {closedStatus = JOptionPane.CANCEL_OPTION; setVisible(false);});
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
		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				closedStatus = 3;
			}
			
		});
		pack();
		setMinimumSize(new Dimension(getWidth(), getHeight()));
		setLocationRelativeTo(parent);	
		
	}
	
	private void checkInput(String IPaddress, String port)
	{
		try 
		{
			if (!ipregex.matcher(IPaddress).matches())
				throw new UnknownHostException();
			InetAddress address = InetAddress.getByName(IPaddress);
			int portnum = Integer.parseInt(port);
			if (portnum < 0 || portnum > Math.pow(2, 16))
			{
				JOptionPane.showMessageDialog(this, "Port value out of range", "Port value incorrect!", JOptionPane.ERROR_MESSAGE);
			}
			else
			{
				info = new ConnectInfo(address, portnum);
				closedStatus = JOptionPane.OK_OPTION;
				setVisible(false);
				return;
			}
		}
		catch (UnknownHostException e)
		{
			JOptionPane.showMessageDialog(this, "IP address/hostname posted was either unfindable or not inputted correctly.", "Can't Find Host!", JOptionPane.ERROR_MESSAGE);
		}
		catch (NumberFormatException ex)
		{
			JOptionPane.showMessageDialog(this, "Port is not a number", "Port value incorrect!", JOptionPane.ERROR_MESSAGE);
		}
		info = null;
	}
	
	public ConnectInfo getConnectInfo()
	{
		return info;
	}
	public int getValue()
	{
		return closedStatus;
	}
	
	public void updateView()
	{
		SwingUtilities.updateComponentTreeUI(this);
	}
	
	
	

	
}
