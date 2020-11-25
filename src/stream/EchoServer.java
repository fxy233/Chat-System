/***
 * EchoServer
 * Example of a TCP server
 * Date: 10/01/04
 * Authors:
 */

package stream;

import java.io.*;
import java.net.*;

public class EchoServer  {

 	/**
  	* receives a request from client then sends an echo to the client
  	* @param clientSocket the client socket
  	**/
	static void doService(Socket clientSocket) {

		PrintStream socOut = null;
		BufferedReader stdIn = null;
		BufferedReader socIn = null;

    	  try {

    		socIn = new BufferedReader(
    			new InputStreamReader(clientSocket.getInputStream()));    
    		socOut = new PrintStream(clientSocket.getOutputStream());
    		stdIn = new BufferedReader(new InputStreamReader(System.in));

    		String line;
    		while (true) {
				System.out.println("echo: " + socIn.readLine());

    			line=stdIn.readLine();
    		  	socOut.println(line);
    		}

    	  } catch (Exception e) {
        	System.err.println("Error in EchoServer:" + e); 
    	  }

	}
  
 	/**
  	* main method
  	* 
  	**/
 	public static void main(String args[]){
        ServerSocket listenSocket;
        
  		if (args.length != 1) {
        	  System.out.println("Usage: java EchoServer <EchoServer port>");
        	  System.exit(1);
  		}


  		try {
			listenSocket = new ServerSocket(Integer.parseInt(args[0])); //port

			while (true) {
				Socket clientSocket = listenSocket.accept();
				//System.out.println();
				System.out.println("connexion from:" + clientSocket.getInetAddress());
				doService(clientSocket);
			}


        } catch (Exception e) {
            System.err.println("Error in EchoServer:" + e);
        }


 	}
}

  
