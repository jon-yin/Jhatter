package samplegui;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.prefs.Preferences;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import client.CCommand;
import client.ClientMessage;
import server.SCode;
import server.ServerMessage;

public class ClientFrame extends JFrame implements PreferencesVisitor{

	private LoginDialog loginDialog;
	private PreferencesDialog prefsDialog;
	private JTextField sendText;
	private JTextArea recievedChat;
	private JList<String> rooms;
	private JList<String> users;
	private JLabel status;
	private boolean isConnected = false;
	private Socket socket;
	private ObjectInputStream readMessages;
	private ObjectOutputStream sendMessages;
	public static final Dimension MIN_DIMENSION;
	public final static String PREFS_PATH = "/Jhatter/Preferences";
	public final int DEFAULT_TIMEOUT = 15000;
	private Preferences storedPrefs;
	private int socTimeout;

	static {
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		MIN_DIMENSION = new Dimension(d.width / 3, d.height / 3);
	}

	public ClientFrame() {
		setMinimumSize(MIN_DIMENSION);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new GridBagLayout());
		GridBagConstraints gb = new GridBagConstraints();
		// Components to be added.
		String[] sample = { "Sample Room 1", "Sample Room 2", "Sample Room 3" };
		String[] sampleUsers = { "1", "2", "3", "4" };
		status = new JLabel("Status: Unconnected");
		status.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 25));
		rooms = new JList<>(sample);
		users = new JList<>(sampleUsers);
		rooms.setPrototypeCellValue("##############");
		users.setPrototypeCellValue("##############");
		JScrollPane roomsList = new JScrollPane(rooms);
		JScrollPane usersList = new JScrollPane(users);
		recievedChat = new JTextArea(10, 20);
		recievedChat.setEditable(false);
		JScrollPane extendChat = new JScrollPane(recievedChat);
		extendChat.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		recievedChat.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
		sendText = new JTextField(50);
		JLabel chatLabel = new JLabel("Chat:");
		JLabel roomsLabel = new JLabel("Rooms");
		roomsLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 25));
		JLabel userLabel = new JLabel("Users");
		userLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 25));
		JButton refresh = new JButton("refresh");
		JButton refreshUsers = new JButton("refresh");
		JButton addRoom = new JButton("Add Room");
		// Add the components. First the text area + rooms panel.
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
		gb.fill = GridBagConstraints.HORIZONTAL;
		gb.anchor = GridBagConstraints.PAGE_START;
		gb.insets = border;
		add(roomsLabel, gb);
		gb.gridx = 2;
		gb.gridy = 1;
		gb.gridheight = 1;
		gb.gridwidth = 1;
		gb.weightx = 0;
		gb.weighty = 0;
		gb.fill = GridBagConstraints.BOTH;
		gb.anchor = GridBagConstraints.CENTER;
		gb.insets = border;
		add(roomsList, gb);
		gb.gridx = 2;
		gb.gridy = 2;
		gb.gridheight = 1;
		gb.gridwidth = 1;
		gb.weightx = 0;
		gb.weighty = 0;
		gb.fill = GridBagConstraints.HORIZONTAL;
		gb.insets = border;
		add(refresh, gb);

		gb.gridx = 2;
		gb.gridy = 3;
		gb.gridheight = 1;
		gb.gridwidth = 1;
		gb.weightx = 0;
		gb.weighty = 0;
		gb.fill = GridBagConstraints.HORIZONTAL;
		gb.insets = border;
		add(addRoom, gb);
		// Row 2 Add the Users List
		gb.gridx = 3;
		gb.gridy = 0;
		gb.gridheight = 1;
		gb.gridwidth = 1;
		gb.weightx = 0;
		gb.weighty = 0;
		gb.fill = GridBagConstraints.HORIZONTAL;
		gb.insets = border;
		gb.anchor = GridBagConstraints.PAGE_START;
		add(userLabel, gb);
		gb.gridx = 3;
		gb.gridy = 1;
		gb.gridheight = 1;
		gb.gridwidth = 1;
		gb.weightx = 0;
		gb.weighty = 0;
		gb.anchor = GridBagConstraints.PAGE_START;
		gb.fill = GridBagConstraints.BOTH;
		gb.insets = border;
		add(usersList, gb);
		gb.gridx = 3;
		gb.gridy = 2;
		gb.gridheight = 1;
		gb.gridwidth = 1;
		gb.weightx = 0;
		gb.weighty = 0;
		gb.fill = GridBagConstraints.HORIZONTAL;
		gb.anchor = GridBagConstraints.PAGE_START;
		gb.insets = border;
		add(refreshUsers, gb);

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
		gb.weightx = 0;
		gb.weighty = 0;
		gb.fill = GridBagConstraints.HORIZONTAL;
		gb.insets = border;
		add(sendText, gb);
		
		gb.gridx = 0;
		gb.gridy = 3;
		gb.gridheight = 1;
		gb.gridwidth = 2;
		gb.weightx = 0;
		gb.weighty = 0;
		//gb.anchor = GridBagConstraints.LINE_START;
		gb.fill = GridBagConstraints.HORIZONTAL;
		gb.insets = border;
		add(status, gb);
		
		

		// Setting up a menu system.

		JMenuBar menubar = new JMenuBar();
		setJMenuBar(menubar);
		JMenu connect = new JMenu("Connect");
		menubar.add(connect);
		JMenuItem startConnection = new JMenuItem("Open Connection");
		connect.add(startConnection);
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

		startConnection.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK));
		disconnect.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, KeyEvent.CTRL_DOWN_MASK));
		showPreferences.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, KeyEvent.CTRL_DOWN_MASK));

		Preferences prefsRoot = Preferences.userRoot();
		storedPrefs = prefsRoot.node(PREFS_PATH);
		loadPreferences();
		showPreferences.addActionListener(event -> showPreferencesDialog());

		startConnection.addActionListener(event -> showLoginDialog());

		disconnect.addActionListener(event -> disconnect());

		addRoom.addActionListener(event -> addRoom());
		
		sendText.addActionListener(event -> {
			sendMessage(sendText.getText());
		});

	}

	private void addRoom() {
		if (!isConnected)
		{
			JOptionPane.showMessageDialog(this, "You are currently not connected to a server!", "No server detected!", JOptionPane.ERROR_MESSAGE);
		}
		else
		{
			String roomName = JOptionPane.showInputDialog(this, "Enter a room name!");
			ClientMessage cm = new ClientMessage(CCommand.CREATE, roomName);
			try{
				sendMessages.writeObject(cm);
				sendMessages.flush();
			}
			catch (IOException ex)
			{
				ex.printStackTrace();
			}
		}
	}

	//Used to send non-command messages to the server
	private void sendMessage(String text) {
		System.out.println("MESSAGE BEING SENT");
		if (sendMessages != null)
		{
			System.out.println("DEFINITELY!");
			ClientMessage message = new ClientMessage(CCommand.NONE, text);
			try {
				sendMessages.writeObject(message);
				sendMessages.flush();
				System.out.println("Sent...");
			} catch (IOException e) {
				recievedChat.append("PROGRAM: " + e.getStackTrace() + "\n");
				disconnect();
			}
			
		}
		else
		{
			recievedChat.append("PROGRAM: You are currently not connected to any server!\n");
		}
		
	}

	public void loadPreferences() {
		// Load look and feel if it exists
		String laf = storedPrefs.get("laf", null);
		//System.out.println(laf);
		if (laf != null) {
			try {
				for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
					if (info.getName().equals(laf)) {
						UIManager.setLookAndFeel(info.getClassName());
						SwingUtilities.updateComponentTreeUI(this);
						pack();
					}
				}
			} catch (Exception e) {
			}

		}
		int timeout = storedPrefs.getInt("timeout", -1);
		if (timeout != -1) {
			socTimeout = timeout * 1000;
		} else {
			socTimeout = DEFAULT_TIMEOUT;
		}
	}

	private void showPreferencesDialog() {
		if (prefsDialog == null)
		{
			prefsDialog = new PreferencesDialog(this, storedPrefs);
			prefsDialog.registerVisitor(ClientFrame.this);
		}
		prefsDialog.updateView();
		prefsDialog.setVisible(true);
		// Retrieve new preferences
		socTimeout = storedPrefs.getInt("timeout", DEFAULT_TIMEOUT);
		try {
			if (socket != null)
				socket.setSoTimeout(socTimeout);
		} catch (SocketException sc) {
		}
	}

	private void disconnect() {
		if (socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		socket = null;
		readMessages = null;
		sendMessages = null;
		isConnected = false;
		status.setText("Status: Unconnected");

	}

	private void showLoginDialog() {
		if (loginDialog == null) {
			loginDialog = new LoginDialog(ClientFrame.this);
		}
		loginDialog.updateView();
		loginDialog.setVisible(true);
		int closedStatus = loginDialog.getValue();
		if (closedStatus == JOptionPane.OK_OPTION) {
			ConnectInfo info = loginDialog.getConnectInfo();
			socket = new Socket();
			try {
				socket.setSoTimeout(socTimeout);
			} catch (SocketException ex) {
			}
			startConnection(info);

		}
	}

	private void startConnection(ConnectInfo info) {
		try {
			socket.connect(new InetSocketAddress(info.getAddress(), info.getPort()));
			isConnected = true;
			sendMessages = new ObjectOutputStream(socket.getOutputStream());
			sendMessages.flush();
			readMessages = new ObjectInputStream(socket.getInputStream());
			Thread readThread = new Thread(new ReadThread());
			readThread.start();
			status.setText("Status: Connected to " + info.getAddress().toString());

		} catch (IOException e) {
			disconnect();
			JOptionPane.showMessageDialog(this, e.getMessage(), "Something went wrong!", JOptionPane.ERROR_MESSAGE);
		}

	}
	
	private class ServerMessageHandler
	{
		// Client handling server messages will not return anything since client messages are created
		// from user input, thus a Consumer will work.
		private Map<SCode,Consumer<ServerMessage>> handlers;
		public ServerMessageHandler()
		{
			handlers = new HashMap<>();
			populateMap();
		}
		private void populateMap() {
			handlers.put(SCode.ERROR, message -> recievedChat.append("ERROR: " + message.getBody()+ "\n"));
			handlers.put(SCode.RESPONSE_T, message -> recievedChat.append(message.getBody() + "\n"));
			handlers.put(SCode.ROOMS, message -> 
			{
			String[] list = message.getBody().split("\n");
			DefaultListModel<String> model = new DefaultListModel<>();
			for (String roomName : list)
			{
				model.addElement(roomName);
			}
				rooms.setModel(model);
				ClientFrame.this.repaint();
			}	);
			
		}
		
		public void handleMessage(ServerMessage message)
		{
			handlers.get(message.getCode()).accept(message);
		}
	}

	private class ReadThread implements Runnable {

		private ServerMessageHandler handler;
		
		@Override
		public void run() {
			try {
				handler = new ServerMessageHandler();
				while (true) {
					//recievedChat.append("WAITING...\n");
					ServerMessage message = (ServerMessage)readMessages.readObject();
					//recievedChat.append("READ MESSAGE\n");
					handler.handleMessage(message);
				}
			} catch (IOException ex) {
				recievedChat.append(ex.getMessage() + "\n");

			} catch (ClassNotFoundException e) {
				recievedChat.append(e.getMessage() + "READ MESSAGE\n");
				
			}

		}

	}

	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			ClientFrame frame = new ClientFrame();
			frame.pack();
			frame.setVisible(true);
		});
	}

}
