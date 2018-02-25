package samplegui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ClientFrame extends JFrame {

	private JDialog errorDialog;
	private JDialog loginDialog;
	
	public ClientFrame()
	{
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new GridBagLayout());
		GridBagConstraints gb = new GridBagConstraints();
		//Components to be added.
		JList<String> rooms = new JList<>();
		JList<String> users = new JList<>();
		JTextArea recievedChat = new JTextArea(50, 50);
		JTextField sendText = new JTextField(50);
		
		
		
	}

}
