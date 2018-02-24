package server;

public class ChatServerDriver {

	public static final int DEFAULT_PORT = 8000;
	
	public static void main(String[] args) throws Exception{
		if (args.length > 0)
		{
			int port = Integer.parseInt(args[0]);
			ChatServer cs = new ChatServer(port);
			cs.startListening();
		}
		else
		{
			System.out.println("Specify a port in order to start server.");
		}

	}

}
