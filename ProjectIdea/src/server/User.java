package server;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import client.ClientMessage;

public class User implements Comparable<User>{

	private Chatroom curRoom;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private int id;
	private String name;
	
	public User(int id, ObjectOutputStream out, ObjectInputStream in, String name)
	{
		this.id=id;
		output = out;
		input = in;
		curRoom = null;
		this.name = name;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName()
	{
		
	}
	
	public synchronized void writeMessage(ServerMessage message) throws IOException
	{
		output.writeObject(message);
		output.flush();	
	}
	
	public ClientMessage getMessage() throws IOException
	{
		try{
			return (ClientMessage)(input.readObject());
		}
		catch (ClassNotFoundException | IOException ex)
		{
			if (ex instanceof IOException)
			{
				//No easy way to handle a socket closing on the client side, throw IOException to signal clean up of resources.
				//System.out.println("SOCKET EXCEPTION THROWN");
				throw (IOException)(ex);
			}
			else{
			ex.printStackTrace();
			return null;
			}
		}
	}
	
	public Chatroom getRoom()
	{
		return curRoom;
	}
	
	public void setRoom(Chatroom room)
	{
		curRoom = room;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public int compareTo(User other) {
		return name.compareTo(other.getName());
	}
	
	
	
}
