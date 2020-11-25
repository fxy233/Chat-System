/***
 * ClientThread
 * Example of a TCP server
 * Date: 14/12/08
 * Authors:
 */

package stream;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class ClientThread
	extends Thread {
	
	private Socket clientSocket;
	private static ArrayList<Socket> listSockets = new ArrayList<>();
	PrintStream socOut = null;
	BufferedReader socIn = null;

	/**
	 * constructor of this thread for client.
	 * Add the new client socket to a arraylist which is used to stock all the sockets.
	 * @param s the new socket
	 */
	ClientThread(Socket s) {
		this.clientSocket = s;
		listSockets.add(s);
	}

 	/**
  	* receives a request from client then sends an echo to the client
  	**/
	public void run() {
    	  try {
    		socIn = null;
    		socIn = new BufferedReader(
    			new InputStreamReader(clientSocket.getInputStream()));    

    		String line;
    		while (true) {

    			if (socIn.ready()) {
					line=socIn.readLine();
					String[] list = line.split(" ");
					//System.out.println("echo: " + line);
					File historyFile = new File("history.txt");
					if (!historyFile.exists()) {
						try {
							historyFile.createNewFile();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}

					FileWriter fileWriter = new FileWriter(historyFile.getName(), true);
					BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
					bufferedWriter.write(line+"\n");
					bufferedWriter.close();

					for(Socket s : listSockets) {
						/*
						if (clientSocket.getPort() == s.getPort()) {
							continue;
						}
						if (list[0].equals("TO")) {
							if (!Integer.toString(s.getPort()).equals(list[1])) {
								continue;
							}
						}
						*/
						socOut = new PrintStream(s.getOutputStream());
						System.out.println(s.getLocalPort());
						socOut.println(line);
					}

				}

    		}
    	} catch (Exception e) {
        	System.err.println("Error in EchoServer:" + e); 
        }
	}
  
  }

  
