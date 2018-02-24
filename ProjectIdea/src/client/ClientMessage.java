package client;

import java.io.IOException;
import java.io.Serializable;
import java.util.Set;

public class ClientMessage implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private CCommand command;
	private String body;
	
	public ClientMessage(String input) throws IOException
	{
		if (CCommand.commands == null)
			throw new IOException("Could not load translations file");
		processInput(input);
	}
	
	public void processInput(String input)
	{
		Set<String> validCommands = CCommand.commands.keySet();
		String parsedCommand = input.split("\\s+")[0];
		if (validCommands.contains(parsedCommand))
		{
			command = CCommand.commands.get(parsedCommand);
			//For now we are not sending any binary data, thus we can just send the String literally with command stripped off.
			//This will be changed later.
			int parsedLength = parsedCommand.length();
			body = input.substring(parsedLength).trim();
		}
		else
		{
			command = CCommand.NONE;
			body = input.trim();
			// If no command, simply send the String literally.
		}
	}
	
	public CCommand getCommand()
	{
		return command;
	}
	
	public String getBody()
	{
		return body;
	}
	
	/*
	public void setBody(Object[] body)
	{
		this.body = body;
	}
	*/
	
}
