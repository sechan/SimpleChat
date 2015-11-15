package server;
import common.ChatIF;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import MainPackage.EchoServer;
public class ServerConsole implements ChatIF{
	
	EchoServer server;
	final public static int DEFAULT_PORT = 5555;
	public ServerConsole(int port) {
		try {
			server = new EchoServer(port, this);
		} catch (IOException e) {
			System.out.println("Server could not be started");
			System.exit(0);
		}
	}
	
	public void display(String message) {
		server.sendToAllClients("SERVER msg>" + message);
	}
	
	public void accept() {
		try{
			BufferedReader fromConsole = 
					new BufferedReader(new InputStreamReader(System.in));
			String message;
			
			while(true)
			{
				message = fromConsole.readLine();
				if(!message.substring(0,1).equals("#")){
					server.sendToAllClients("SERVER msg>" + message);
				}else{
					server.handleConsoleCommand(message);
				}
			}
		}catch (Exception ex) {
			System.out.println("Unex[ected error while reading from console!");
		}
	}
	
	public static void main(String[] args) {
		int port=0;
		try
	    {
	    	port = Integer.parseInt(args[1]);
	    }
	    catch(ArrayIndexOutOfBoundsException e)
	    {
	    	port=DEFAULT_PORT;
	    }
		
		ServerConsole console = new ServerConsole(port);
		console.accept();
	}
}
