package samplegui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

public class ClientFrame extends JFrame {

	private LoginDialog loginDialog;
	
	public ClientFrame()
	{
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new GridBagLayout());
		GridBagConstraints gb = new GridBagConstraints();
		//Components to be added.
		String[] sample = {"Sample Room 1", "Sample Room 2", "Sample Room 3"};
		String[] sampleUsers = {"1","2","3","4"};
		JList<String> rooms = new JList<>(sample);
		JList<String> users = new JList<>(sampleUsers);
		rooms.setPrototypeCellValue("##############");
		users.setPrototypeCellValue("##############");
		JScrollPane roomsList = new JScrollPane(rooms);
		JScrollPane usersList = new JScrollPane(users);
		JTextArea recievedChat = new JTextArea(10, 20);
		JScrollPane extendChat = new JScrollPane(recievedChat);
		extendChat.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		recievedChat.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
		JTextField sendText = new JTextField(50);
		JLabel chatLabel = new JLabel("Chat:");
		JLabel roomsLabel = new JLabel("Rooms");
		roomsLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 25));
		JLabel userLabel = new JLabel("Users");
		userLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 25));
		JPanel roomsPanel = new JPanel();
		roomsPanel.setLayout(new BorderLayout(0,10));
		roomsPanel.add(roomsLabel, BorderLayout.NORTH);
		roomsPanel.add(roomsList, BorderLayout.CENTER);
		JPanel usersPanel = new JPanel();
		usersPanel.setLayout(new BorderLayout(0,10));
		usersPanel.add(userLabel, BorderLayout.NORTH);
		usersPanel.add(usersList, BorderLayout.EAST);
		//Add the components. First the text area + rooms panel.
		Insets border = new Insets(10, 10, 10, 10);
		gb.gridx = 0;
		gb.gridy = 0;
		gb.gridheight = 2;
		gb.gridwidth = 2;
		gb.weightx = 100;
		gb.weighty = 100;
		gb.fill = GridBagConstraints.BOTH;
		gb.insets = border;
		add(extendChat, gb);
		gb.gridx = 2;
		gb.gridy = 0;
		gb.gridheight = 1;
		gb.gridwidth = 1;
		gb.weightx = 0;
		gb.weighty = 0;
		gb.fill = GridBagConstraints.BOTH;
		gb.insets = border;
		add(roomsPanel, gb);
		// Row 2 Add the Users List
		gb.gridx = 2;
		gb.gridy = 1;
		gb.gridheight = 1;
		gb.gridwidth = 1;
		gb.weightx = 0;
		gb.weighty = 0;
		gb.fill = GridBagConstraints.BOTH;
		gb.insets = border;
		add(usersPanel, gb);
		// Row 3 Add the Users List and the chat field
		gb.gridx = 0;
		gb.gridy = 2;
		gb.gridheight = 1;
		gb.gridwidth = 1;
		gb.weightx = 0;
		gb.weighty = 0;
		gb.fill = GridBagConstraints.NONE;
		gb.insets = border;
		add(chatLabel, gb);
		
		gb.gridx = 1;
		gb.gridy = 2;
		gb.gridheight = 1;
		gb.gridwidth = 1;
		gb.weightx = 100;
		gb.weighty = 100;
		gb.fill = GridBagConstraints.HORIZONTAL;
		gb.insets = border;
		add(sendText, gb);
		
		//Setting up a menu system.
		
		JMenuBar menubar = new JMenuBar();
		setJMenuBar(menubar);
		JMenu connect = new JMenu("Connect");
		menubar.add(connect);
		JMenuItem startConnection = new JMenuItem("Open Connection");
		connect.add(startConnection);
		startConnection.addActionListener((event) -> {showLoginDialog();});
		JMenuItem disconnect = new JMenuItem("Disconnect");
		connect.add(disconnect);
		JMenu help = new JMenu("Help");
		JMenuItem man = new JMenuItem("Manual");
		help.add(man);
		menubar.add(help);
		JMenu preferences = new JMenu("Preferences");
		JMenuItem showPreferences = new JMenuItem("Open Preferences");
		menubar.add(preferences);
		preferences.add(showPreferences);
	}
	
	private void showLoginDialog() {
		if (loginDialog == null)
		{
			loginDialog = new LoginDialog(ClientFrame.this);
		}
		loginDialog.setVisible(true);
	}

	public static void main(String[] args)
	{
		EventQueue.invokeLater(() ->{
		ClientFrame frame = new ClientFrame();
		frame.pack();
		frame.setVisible(true);
		}
	);
	}

}
