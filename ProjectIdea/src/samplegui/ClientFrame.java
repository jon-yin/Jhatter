package samplegui;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

public class ClientFrame extends JFrame {

	private JDialog errorDialog;
	private JDialog loginDialog;
	
	public ClientFrame()
	{
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new GridBagLayout());
		GridBagConstraints gb = new GridBagConstraints();
		//Components to be added.
		String[] sample = {"Sample Room 1", "Sample Room 2", "Sample Room 3"};
		JList<String> rooms = new JList<>(sample);
		JList<String> users = new JList<>();
		JTextArea recievedChat = new JTextArea(20, 20);
		JScrollPane extendChat = new JScrollPane(recievedChat);
		extendChat.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		recievedChat.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
		JTextField sendText = new JTextField(50);
		JLabel chatLabel = new JLabel("Chat:");
		JLabel roomsLabel = new JLabel("Rooms");
		roomsLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 25));
		JLabel userLabel = new JLabel("Users");
		//Add the components. First the text area + rooms label.
		gb.gridx = 0;
		gb.gridy = 0;
		gb.gridheight = 2;
		gb.gridwidth = 2;
		gb.weightx = 100;
		gb.weighty = 100;
		gb.fill = GridBagConstraints.BOTH;
		add(extendChat, gb);
		Insets border = new Insets(10, 10, 10, 10);
		gb.gridx = 2;
		gb.gridy = 0;
		gb.gridheight = 1;
		gb.gridwidth = 1;
		gb.weightx = 0;
		gb.weighty = 0;
		gb.fill = GridBagConstraints.NONE;
		gb.anchor = GridBagConstraints.PAGE_START;
		gb.insets = border;
		add(roomsLabel, gb);
		/********************* ROW 2: ROOM LIST **********************/
		gb.gridx = 2;
		gb.gridy = 1;
		gb.gridheight = 1;
		gb.gridwidth = 1;
		gb.weightx = 0;
		gb.weighty = 0;
		gb.fill = GridBagConstraints.VERTICAL;
		gb.anchor = GridBagConstraints.PAGE_START;
		gb.insets = new Insets(10,10,10,10);
		add(rooms, gb);
		
		
	}
	
	public static void main(String[] args)
	{
		ClientFrame frame = new ClientFrame();
		frame.pack();
		frame.setVisible(true);
	}

}
