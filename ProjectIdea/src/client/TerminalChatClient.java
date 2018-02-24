package client;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import server.SCode;
import server.ServerMessage;

public class TerminalChatClient {

	private InetAddress toAddress;
	private int port;
	private boolean[] error;

	public TerminalChatClient(InetAddress address, int port) {
		toAddress = address;
		this.port = port;
		error = new boolean[1];
		error[0] = false;
	}

	public void start() {
		try (Socket server = new Socket();
				Scanner userText = new Scanner(System.in);) {
			server.setSoTimeout(15000);
			server.connect(new InetSocketAddress(toAddress, port));
			InputStream in = server.getInputStream();
			OutputStream out = server.getOutputStream();
			ObjectOutputStream sendMessages = new ObjectOutputStream(out);
			sendMessages.flush();
			ObjectInputStream recMessages = new ObjectInputStream(in);
			Thread read = new Thread(new ReadThread(recMessages));
			read.start();
			String response = null;
			while (userText.hasNextLine() && !error[0]) {
				response = userText.nextLine();
				if (response.toLowerCase().equals("quit"))
				{
					break;
				}
				else
				{
					ClientMessage message = new ClientMessage(response);
					sendMessages.writeObject(message);
					sendMessages.flush();
				}
			}
			System.out.println("Client Exiting...");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Client Exiting...");
		}
	}

	public static void main(String[] args) {
		try {
			if (args.length == 2) {
				InetAddress ip = InetAddress.getByName(args[0]);
				int port = Integer.parseInt(args[1]);
				TerminalChatClient client = new TerminalChatClient(ip, port);
				client.start();
			} else {
				System.out.println("Only 2 Arguments expected");
				System.out.println("Syntax for using program: java client.TerminalChatClient [IP Address] [port]");
			}
		} catch (UnknownHostException ex) {
			System.out.println("Could not connect to specified IP address");
		} catch (NumberFormatException nfe) {
			System.out.println("Could not parse port number");
		}

	}

	private class ReadThread implements Runnable {
		private ObjectInputStream st;

		public ReadThread(ObjectInputStream stream) {
			st = stream;
		}

		public void run() {
			try {
				while (true) {
					ServerMessage message = (ServerMessage) (st.readObject());
					if (message.getCode() == SCode.ERROR) {
						System.err.println(message.getBody());
					}
					else
					{
						System.out.println(message.getBody());
					}
				}
			} catch (Exception e) {
				error[0]=true;
				System.out.println("Quitting client");
			}
		}

	}

}
