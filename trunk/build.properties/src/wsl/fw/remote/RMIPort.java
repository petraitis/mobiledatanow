package wsl.fw.remote;


import java.rmi.server.RMISocketFactory;
import java.net.Socket;
import java.net.ServerSocket;
import java.io.IOException;


public class RMIPort 
	extends RMISocketFactory 
	implements java.io.Serializable
{

	private int defPort;

	public RMIPort (
	 int port)
	{
		defPort = port;
	}

	public Socket
	createSocket (
	 String host,
	 int port)
		throws IOException 
	{
		return new Socket (host,port);
	}
		
	public ServerSocket
	createServerSocket (
	 int port)
		throws IOException 
	{
		port = (port == 0 ? defPort : port);
		return new ServerSocket (port);
	}

}
