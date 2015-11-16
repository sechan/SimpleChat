// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 
package MainPackage;

import java.io.*;
import ocsf.server.*;
import common.ChatIF;
import java.util.StringTokenizer;

/**
 * This class overrides some of the methods in the abstract superclass in order
 * to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 * @version July 2000
 */
public class EchoServer extends AbstractServer {
	// Class variables *************************************************

	/**
	 * The default port to listen on.
	 */
	final public static int DEFAULT_PORT = 5555;
	ChatIF clientUI;
	// Constructors ****************************************************

	/**
	 * Constructs an instance of the echo server.
	 *
	 * @param port
	 *            The port number to connect on.
	 */
	public EchoServer(int port, ChatIF clientUI) throws IOException {
		super(port);
		this.clientUI = clientUI;
		try {
			listen(); // Start listening for connections
		} catch (Exception ex) {
			System.out.println("ERROR - Could not listen for clients!");
		}
	}

	public EchoServer(int port) throws IOException {
		super(port);
		try {
			listen(); // Start listening for connections
		} catch (Exception ex) {
			System.out.println("ERROR - Could not listen for clients!");
		}
	}

	// Instance methods ************************************************

	/**
	 * This method handles any messages received from the client.
	 *
	 * @param msg
	 *            The message received from the client.
	 * @param client
	 *            The connection from which the message originated.
	 */
	public void handleMessageFromClient(Object msg, ConnectionToClient client) {
		System.out.println("Message received: " + msg + " from " + client);
		String message = msg.toString();
		if (message.substring(0, 6).equals("#login") && client.getInfo("loginID") == null) {
			client.setInfo("loginID", message.substring(7));
			String loginMessage = client.getInfo("loginID") + " has logged in.";
			this.sendToAllClients(loginMessage);
			return;
		}else if (message.substring(0, 6).equals("#login")){
			String errorMsg = "***ERROR*** You are already logged in. Disconnecting from server.";
			try {
				client.sendToClient(errorMsg);
				client.close();
			} catch (IOException e) {
				System.out.println("Message could not be sent to " + client.getInfo("loginID"));
			}
		}
		message = client.getInfo("loginID") + ": " + msg.toString();
		this.sendToAllClients(message);
	}

	/**
	 * This method overrides the one in the superclass. Called when the server
	 * starts listening for connections.
	 */
	protected void serverStarted() {
		System.out.println("Server listening for connections on port " + getPort());
	}

	/**
	 * This method overrides the one in the superclass. Called when the server
	 * stops listening for connections.
	 */
	protected void serverStopped() {
		System.out.println("Server has stopped listening for connections.");
	}

	// Class methods ***************************************************

	/**
	 * This method is responsible for the creation of the server instance (there
	 * is no UI in this phase).
	 *
	 * @param args[0]
	 *            The port number to listen on. Defaults to 5555 if no argument
	 *            is entered.
	 */
	public static void main(String[] args) {
		int port = 0; // Port to listen on

		try {
			port = Integer.parseInt(args[0]); // Get port from command line
		} catch (Throwable t) {
			port = DEFAULT_PORT; // Set port to 5555
		}

		EchoServer sv = null;
		try {
			sv = new EchoServer(port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(0);
		}

	}

	protected void clientConnected(ConnectionToClient client) {

	}

	/**
	 * Hook method called each time a client disconnects. The default
	 * implementation does nothing. The method may be overridden by subclasses
	 * but should remains synchronized.
	 *
	 * @param client
	 *            the connection with the client.
	 */
	synchronized protected void clientDisconnected(ConnectionToClient client) {

	}

	public void handleConsoleCommand(String message) {
		String arg = null;
		String command;
		StringTokenizer token = new StringTokenizer(message);
		command = token.nextToken();
		if (token.hasMoreTokens())
			arg = token.nextToken();
		switch (command) {
		case "#quit":
			quit();
			break;
		case "#stop":
			if (isListening()) {
				stopListening();
			} else {
				clientUI.display("Not currently logged in.");
			}
			break;
		case "#close":
			if (!isListening()) {
				try {
					sendToAllClients("Server is closing down.");
					close();
				} catch (IOException e) {
					System.out.println("Something went wrong, closing the server.");
				}
			} else {
				clientUI.display("***ERROR*** Cannot change host while connected to server");
			}
			break;
		case "#setPort":
			if (!isListening()) {
				setPort(Integer.parseInt(arg));
			} else {
				clientUI.display("***ERROR*** Cannot change port while server is listnening.");
			}
			break;
		case "#start":
			try {
				listen();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Something went wrong, closing the server.");
				quit();
			}
			break;
		case "#getport":
			clientUI.display("Port: " + getPort());
			break;
		}
	}

	public void quit() {
		try {
			close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Quiting...");
		System.exit(0);
	}

}
// End of EchoServer class
