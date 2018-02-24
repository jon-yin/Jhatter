package client;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

import helpers.ResourceGetter;

public enum CCommand implements Serializable{
	
	NONE,JOIN,LEAVE,WHISPER,HELP,CREATE,WHO, ROOMS;
	public static Map<String, CCommand> commands;
	
	static
	{
		try
		{
			commands = ResourceGetter.getTrans();
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
			commands = null;
		}
	}
	
	
	
	
}
