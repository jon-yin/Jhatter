package samplegui;

import java.net.InetAddress;

public class ConnectInfo {
	
	private InetAddress address;
	private int port;
	
	public ConnectInfo(InetAddress address, int port) {
		super();
		this.address = address;
		this.port = port;
	}
	public InetAddress getAddress() {
		return address;
	}
	public void setAddress(InetAddress address) {
		this.address = address;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	
	

}
