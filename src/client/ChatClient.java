// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package client;

import ocsf.client.*;
import common.*;
import java.io.*;
import java.util.StringTokenizer; 

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 * @version July 2000
 */
public class ChatClient extends AbstractClient
{
  //Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF clientUI; 

  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */
  
  public ChatClient(String host, int port, ChatIF clientUI) 
    throws IOException 
  {
	
    super(host, port); //Call the superclass constructor
    this.clientUI = clientUI;
    openConnection();
  }

  
  //Instance methods ************************************************
    
  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg) 
  {
    clientUI.display(msg.toString());
  }

  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromClientUI(String message)
  {
    try
    {
      sendToServer(message);
    }
    catch(IOException e)
    {
      clientUI.display
        ("Could not send message to server.  Terminating client.");
      quit();
    }
  }
  
  public void handleConsoleCommand(String message) {
	  String arg = null;
	  String command;
	  StringTokenizer token = new StringTokenizer(message);
	  command = token.nextToken();
	  if (token.hasMoreTokens())
		  arg = token.nextToken();
	  switch(command) {
	  case "#quit":
		  quit();
		  break;
	  case "#logoff":
		  if(isConnected()) {
			try {
				closeConnection();
			} catch (IOException e) {
				connectionException(e);
			}
		  }else{
			  clientUI.display("Not currently logged in.");
		  }
		  break;
	  case "#setHost":
		  if(!isConnected()) {
			  setHost(arg);
		  }
		  else {
			  clientUI.display("***ERROR*** Cannot change host while connected to server");
		  }
		  break;
	  case "#setPort":
		  if (!isConnected()) {
			  setPort(Integer.parseInt(arg));
		  }
		  else {
			  clientUI.display("***ERROR*** Cannot change port while connected to server");
		  }
		  break;
	  case "#getHost" :
		  clientUI.display("Host: " +getHost());
		  break;
	  case "#getPort" :
		  clientUI.display("Port: " + getPort());
		  break;
	  case "#login" :
		  if(!isConnected()) {
			  try {
				  clientUI.display("Logging on to " + getHost() + ":" +getPort());
				  openConnection();
			  } catch (IOException e) {
				  connectionException(e);
			  }
		  }else{
			  clientUI.display("Already logged in.");
		  }
		  break;
	  }
  }
  
  public void connectionException(Exception exception) {
	  clientUI.display("Lost connection to server.\nQuitting...");
	  this.quit();
  }
  
  public void connectionClosed() {
	  clientUI.display("***Connection to " + getHost() + ":" + getPort() + " closed.");
  }
  
  /**
   * This method terminates the client.
   */
  public void quit()
  {
    try
    {
      closeConnection();
    }
    catch(IOException e) {}
    clientUI.display("Quiting SimpleChat client.");
    System.exit(0);
  }
}
//End of ChatClient class
