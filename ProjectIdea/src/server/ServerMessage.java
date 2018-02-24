package server;

import java.io.Serializable;

public class ServerMessage implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3396390753355555317L;
	private SCode responseCode;
	private String body;
	
	public ServerMessage(SCode code, String body)
	{
		this.responseCode = code;
		this.body = body;
	}
	
	public SCode getCode()
	{
		return responseCode;
	}
	
	public String getBody()
	{
		return body;
	}
	
}
