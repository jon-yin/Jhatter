package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.function.BiFunction;

import client.CCommand;
import client.ClientMessage;
import helpers.ResourceGetter;

public class ChatServer {

	private int port;
	private ConcurrentHashMap<String, User> everyone;
	private ConcurrentSkipListSet<Chatroom> chatrooms;
	private Chatroom global;
	private ClientHandler handler;
	private boolean[] done;
	//10 seconds per room clear.
	public final int CLEANUP_INTERVAL = 15000;

	public ChatServer(int port) {
		global = new Chatroom("Global");
		this.port = port;
		everyone = new ConcurrentHashMap<String, User>();
		handler = new ClientHandler();
		chatrooms = new ConcurrentSkipListSet<>();
		done = new boolean[1];
		done[0] = false;
		Timer cleanup = new Timer(true);
		cleanup.scheduleAtFixedRate(new CleanUpTask(), 0, CLEANUP_INTERVAL);
	}

	public void startListening() {
		int i = 0;
		try (ServerSocket sSocket = new ServerSocket(port)) {
			boolean[] done = new boolean[1];
			done[0] = false;
			Thread stopThread = new Thread(() -> {
				Scanner read = new Scanner(System.in);
				while (read.hasNextLine()) {
					String command = read.nextLine();
					if (command.equals("Quit")) {
						done[0] = true;
						read.close();
						break;
					}
				}
				done[0] = true;
				read.close();
			});
			stopThread.start();
			while (!done[0]) {
				Socket client = sSocket.accept();
				//System.out.println("Client Recieved");
				Thread t = new Thread(new ServerThreadHandler(client, i));
				t.start();
				i++;
			}

		} catch (IOException ex) {
			ex.printStackTrace();
		}

	}

	/**
	 * The purpose of this class is to clean up empty chatrooms every 10
	 * seconds.
	 * 
	 * @author Jonathan Yin
	 *
	 */
	private class CleanUpTask extends TimerTask {

		@Override
		public void run() {
			for (Chatroom chatroom : chatrooms) {
				if (chatroom.isEmpty()) {
					chatrooms.remove(chatroom);
				}
			}
		}

	}

	/**
	 * Represents the various unique ways to respond to a Client Message based
	 * on the code given from it. The purpose of this class is to prevent
	 * registering unsafe handlers if given only the class files but also enable
	 * developers to easily expand and register new/ modify old handlers.
	 * 
	 * @author Jonathan Yin
	 *
	 */
	private class ClientHandler {
		private Map<CCommand, BiFunction<String, User, ServerMessage>> handlers;

		private ClientHandler() {
			handlers = new HashMap<>();
			populateMap();
		}

		/**
		 * Method to perform some action with regards to a ClientMessage and then return an appropriate
		 * response to the server.
		 * @param cm The ClientMessage sent by the client.
		 * @param user The User object which represents this client.
		 * @return A ServerMessage object with an appropriate response code and message.
		 */
		public ServerMessage handleCMessage(ClientMessage cm, User user) {
			return handlers.get(cm.getCommand()).apply(cm.getBody(), user);
		}

		
		/**
		 * Inner method to abstract away the specific handlers for each different type of CCommand enum type
		 * It's simple to extend, it simple needs to add an entry to handlers for the specified enum type
		 * to a Bifunction<String, User, ServerMessage>. Do all the important processing in the Bifunction
		 * and return a ServerMessage.
		 */
		private void populateMap() {

			handlers.put(CCommand.NONE, (input, user) -> {
				if (user.getRoom() == null)
					return new ServerMessage(SCode.ERROR, "You are currently not in a room!");
				else {
					ServerMessage message = new ServerMessage(SCode.RESPONSE_T, user.getName() + ": " + input);
					Chatroom currentRoom = user.getRoom();
					Set<User> users = currentRoom.getAllUsers();
					for (User recipient : users) {
						try {
							recipient.writeMessage(message);
						} catch (IOException ex) {
							ex.printStackTrace();
						}

					}
					return null;
				}
			});

			handlers.put(CCommand.CREATE, (input, user) -> {
				input = input.trim();
				if (input.isEmpty()) {
					ServerMessage message = new ServerMessage(SCode.ERROR, "Cannot create a room with an empty name!");
					return message;
				}
				for (Chatroom c : chatrooms) {
					if (c.getName().equals(input)) {
						ServerMessage message = new ServerMessage(SCode.ERROR,
								"Failed to create room. This chatroom name already exists!");
						return message;
					}
				}
				Chatroom newRoom = new Chatroom(input);
				chatrooms.add(newRoom);
				ServerMessage message = new ServerMessage(SCode.RESPONSE_T, "Successfully created room " + input);
				return message;
			});

			handlers.put(CCommand.HELP, (input, user) -> {
				StringBuilder commands = new StringBuilder();
				try {
					List<String> help = ResourceGetter.getHelpText();
					for (String line : help) {
						commands.append(line);
						commands.append("\n");
					}

				} catch (IOException ex) {
					commands.append("Sorry but the Help file could not properly loaded!");
				}
				return new ServerMessage(SCode.RESPONSE_T, commands.toString());
			});

			handlers.put(CCommand.LEAVE, (input, user) -> {
				if (user.getRoom() == global) {
					return new ServerMessage(SCode.ERROR, "You're currently not in a room!");
				} else {
					user.getRoom().removeUser(user);
					user.setRoom(global);
					global.addUser(user);
					return new ServerMessage(SCode.RESPONSE_T, "Successfully left room");
				}
			});

			handlers.put(CCommand.WHISPER, (input, user) -> {
				String[] args = input.split("\\s+");
				if (args.length < 2) {
					ServerMessage error = new ServerMessage(SCode.ERROR,
							"Whispers must contain a body to send to user");
					return error;
				}
				String name = args[0];
				String message = input.substring(name.length()).trim();
				if (user.getName().equals(name))
				{
					ServerMessage prohibitSelf = new ServerMessage(SCode.ERROR, "SERVER: You cannot whisper to yourself");
					return prohibitSelf;
				}
				if (everyone.containsKey(name)) {
					ServerMessage whisper = new ServerMessage(SCode.RESPONSE_T,
							user.getName() + " (whisper): " + message);
					User recipient = everyone.get(name);
					if (recipient != null) {
						try {
							recipient.writeMessage(whisper);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					return whisper;
				} else {
					ServerMessage error = new ServerMessage(SCode.ERROR, name + " does not exist.");
					return error;
				}
			});

			handlers.put(CCommand.JOIN, (input, user) -> {
				String roomName = input;
				for (Chatroom room : chatrooms) {
					synchronized (room) {
						if (room.getName().equals(roomName)) {
							room.addUser(user);
							user.setRoom(room);
							global.removeUser(user);
							ServerMessage message = new ServerMessage(SCode.RESPONSE_T,
									"Successfully entered room " + roomName);
							return message;
						}
					}
				}
				ServerMessage error = new ServerMessage(SCode.ERROR, "Room " + roomName + " Doesn't exist!");
				return error;
			});

			handlers.put((CCommand.WHO), (input, user) -> {
				if (user.getRoom() != null) {
					Chatroom target = user.getRoom();
					if (target == global)
					{
						StringBuilder builder = new StringBuilder();
						for (User present : global.getAllUsers()) {
							builder.append(present.getName());
							builder.append("\n");
						}
						return new ServerMessage(SCode.WHO, "LIST OF USERS\n" + builder.toString());
					}
					for (Chatroom c : chatrooms) {
						if (c.getName().equals(target.getName())) {
							StringBuilder builder = new StringBuilder();
							for (User present : c.getAllUsers()) {
								builder.append(present.getName());
								builder.append("\n");
							}
							return new ServerMessage(SCode.WHO, "LIST OF USERS\n" + builder.toString());
						}
						return new ServerMessage(SCode.ERROR, "This shouldn't happen.");
					}
				}
				return new ServerMessage(SCode.ERROR, "You are not currently in a room!");
			});

			handlers.put(CCommand.ROOMS, (input, user) -> {
				StringBuilder builder = new StringBuilder();
				for (Chatroom room : chatrooms) {
					builder.append(room.getName());
					builder.append("\n");
				}
				return new ServerMessage(SCode.ROOMS, "LIST OF ROOMS\n" + builder.toString());
			});

		}
	}

	private class ServerThreadHandler implements Runnable {

		Socket client;
		ObjectInputStream input;
		ObjectOutputStream output;
		int id;
		User user;

		public ServerThreadHandler(Socket sock, int id) throws IOException {
			//System.out.println("Creating new thread handler!");
			this.id = id;
			client = sock;
			output = new ObjectOutputStream(client.getOutputStream());
			output.flush();
			input = new ObjectInputStream(client.getInputStream());
			// everyone.put(id, output);

		}

		@Override
		public void run() {
			try {
				// Introductory messages between client and server (setting up a name)
				getClientInfo();
				// Main processing loop.
				processMessages();
			} catch (IOException ex) {
				if (user != null) {
					everyone.remove(user.getName());
					if (user.getRoom() != null) {
						user.getRoom().removeUser(user);
					}
				}
				try {
					client.close();
				} catch (IOException e) {
					//e.printStackTrace();
				}
				return;
			}
			try {
				client.close();
			} catch (IOException io) {
				System.out.println(io);
			}
			everyone.remove(user.getName());

		}

		private void processMessages() throws IOException {

			while (true) {
				ClientMessage message = user.getMessage();
				ServerMessage sendMessage = handler.handleCMessage(message, user);
				if (sendMessage != null) {
					user.writeMessage(sendMessage);
					if (sendMessage.getCode() == SCode.ERROR)
						user.writeMessage(new ServerMessage(SCode.RESPONSE_T, "For more help, enter the HELP command"));
				}
			}
		}

		private void getClientInfo() throws IOException {
			try {
				while (true) {
					ServerMessage introMessage = new ServerMessage(SCode.RESPONSE_T,
							"Welcome to the Server!, what's your name?");
					output.writeObject(introMessage);
					output.flush();
					String name ="";
					while (true){
					ClientMessage CMessage = (ClientMessage) (input.readObject());
					if (CMessage.getCommand() == CCommand.NONE){
						name = CMessage.getBody();
						break;
						}
					ServerMessage sm = new ServerMessage(SCode.ERROR, "Please enter a name before doing anything.");
					output.writeObject(sm);
					output.flush();
					}
					user = new User(id, output, input, name);
					if (everyone.putIfAbsent(name, user) == null) {
						//System.out.println(everyone.keySet());
						break;
					}
					ServerMessage errorMessage = new ServerMessage(SCode.ERROR,
							"Sorry this name has been taken already, please enter another name.");
					output.writeObject(errorMessage);
					output.flush();
				}
				user.writeMessage(
						new ServerMessage(SCode.RESPONSE_T, "Welcome to the server: " + user.getName() + "!"));
				global.addUser(user);
				user.setRoom(global);
			} catch (Exception e) {
				throw new IOException();
			}

		}
		/**
		 * private void informEveryone(String line) { for (PrintWriter pw:
		 * everyone.values()) { pw.println(name + ": " + line); pw.flush(); } }
		 */

	}

}
