/***
 * EchoClient
 * Example of a TCP client 
 * Date: 10/01/04
 * Authors:
 */
package stream;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.ArrayList;


public class EchoClient {


    private static String username = "";
    static Socket echoSocket = null;
    static PrintStream socOut = null;
    static BufferedReader stdIn = null;
    static BufferedReader socIn = null;
    static ArrayList<String> userList = new ArrayList<>();
    static boolean leave = false;
    static ChatScreen chatScreen;
  /**
  *  main method
  *  accepts a connection, receives a message from client then sends an echo to the client
  **/
    public static void main(String[] args) throws IOException {


        if (args.length != 2) {
          System.out.println("Usage: java EchoClient <EchoServer host> <EchoServer port>");
          System.exit(1);
        }

        try {
      	    // creation socket ==> connexion
      	    echoSocket = new Socket(args[0],new Integer(args[1]).intValue());

      	    socIn = new BufferedReader(
	    		          new InputStreamReader(echoSocket.getInputStream()));    
	        socOut= new PrintStream(echoSocket.getOutputStream());

	        stdIn = new BufferedReader(new InputStreamReader(System.in));

            System.out.println(echoSocket.getLocalPort());

            // connexion with user nom
            saveUser();
            getUserList();
            //
            chatScreen = new ChatScreen();


        } catch (UnknownHostException e) {
            System.err.println("Don't know about host:" + args[0]);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for "
                               + "the connection to:"+ args[0]);
            System.exit(1);
        }
                             
        String line;
        while (true) {

            if(socIn.ready()) {
                if (leave) {
                    break;
                }
                line = socIn.readLine();
                String[] list = line.split(" ");
                if (list[0].equals("TO")) {
                    if (list[1].equals(username)) {
                        line = "";
                        for(int i = 2; i < list.length; ++i) {
                            line += list[i];
                            line += " ";
                        }
                        showMessage(line);
                    }
                } else {
                    showMessage(line);
                }
            }

        }
        socOut.close();
        socIn.close();
        stdIn.close();
        echoSocket.close();
    }

    /**
     * Static method to send a message from one client.
     * Get the string one client put on the screen and then transpose to another client.
     */
    public static void sendMessage() {
        String line = chatScreen.jta.getText();

        if (line.equals("exit")) leave = true;
        if (leave) {
            showMessage("you are leaving..............\n");
            return;
        }

        chatScreen.jta.setText("");
        socOut.println(line + " from " + username);
    }

    /**
     * create and save the user's name in the file 'user.txt'.
     * If the file 'user.txt' doesn't exist, we create the file.
     * @throws IOException
     */
    public static void saveUser() throws IOException {
        System.out.println("enter your username...");
        String user = stdIn.readLine();
        username = user;

        File userFile = new File("user.txt");
        if (!userFile.exists()) {
            try {
                userFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        FileWriter fileWriter = new FileWriter(userFile.getName(), true);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        bufferedWriter.write(user+"\n");
        bufferedWriter.close();
    }

    /**
     * Obtain the list of all the users by reading a file 'user.txt'
     * @throws IOException
     */
    public static void getUserList() throws IOException {
        File userFile = new File("user.txt");
        if (userFile.exists()) {
            InputStreamReader reader = new InputStreamReader(new FileInputStream(userFile));
            BufferedReader br = new BufferedReader(reader);
            String rd = br.readLine();
            while(rd != null) {
                if(!userList.contains(rd)) {
                    userList.add(rd);
                }
                rd = br.readLine();
            }
        }
    }

    /**
     * Show a message on the screen for the user
     * @param msg the message we want to show
     */
    public static void showMessage(String msg) {
        chatScreen.jl1.setText(chatScreen.jl1.getText() + msg + "\n");
    }

    /**
     * The IHM for the chat screen
     */
    public static class ChatScreen extends JFrame {
        JFrame jf = new JFrame("Wechat");
        JPanel jp = new JPanel();
        JPanel bottom = new JPanel();
        DefaultListModel dlm = new DefaultListModel();
        JTextField jta = new JTextField(50);

        JButton send = new JButton("Send");
        JButton refresh = new JButton("Refresh");

        JList jl = new JList();
        JScrollPane jsp = new JScrollPane(jl);
        ArrayList<JLabel> listLabel;
        JTextArea jl1 = new JTextArea();

        /**
         * constructor of the IHM where we put all the components SWING on the window
         * @throws IOException
         */
        public ChatScreen() throws IOException {
            jp.setPreferredSize(new Dimension(400,600));
            jp.setLayout(new BoxLayout(jp,BoxLayout.Y_AXIS));

            jl.setBorder(BorderFactory.createEmptyBorder(0,0,0, 50));
            jl1.setSize(200,30);

            jl1.setEditable(false);
            jl1.setAlignmentX(0);

            jp.add(jl1);
            jl1.setAlignmentX(Component.RIGHT_ALIGNMENT);
            jp.add(Box.createVerticalStrut(20));

            jp.add(Box.createVerticalStrut(20));
            jf.add(jp);

            bottom.add(jta);
            bottom.add(send);
            bottom.add(refresh);
            jf.add(bottom,BorderLayout.SOUTH);

            for(String s : userList) {
                dlm.addElement(s);
            }
            jl.setModel(dlm);

            jf.add(jl,BorderLayout.WEST);

            send.addActionListener(e -> {
                sendMessage();
            });

            refresh.addActionListener(e -> {
                try {
                    getUserList();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                dlm.removeAllElements();
                for(String s : userList) {
                    dlm.addElement(s);
                }
                jl.setModel(dlm);
            });

            getHistory();

            jf.setVisible(true);
            jf.pack();
            jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }

        /**
         * Read the file 'history.txt' which contains all the information in the history only associate with this client.
         * Then put the content on the screen.
         * @throws IOException
         */
        public void getHistory() throws IOException {
            String history = "chat\n\n";
            history += "****** history ******";
            history += "\n";
            File historyFile = new File("history.txt");
            if (historyFile.exists()) {
                InputStreamReader reader = new InputStreamReader(new FileInputStream(historyFile));
                BufferedReader br = new BufferedReader(reader);
                String rd = br.readLine();
                while(rd != null) {
                    String[] list = rd.split(" ");
                    if (list[0].equals("TO")) {
                        if (list[1].equals(username) || list[list.length-1].equals(username)) {
                            rd = "";
                            for(int i = 2; i < list.length; ++i) {
                                rd += list[i];
                                rd += " ";
                            }
                            history += rd;
                            history += "\n";
                        }
                    } else {
                        history += rd;
                        history += "\n";
                    }
                    rd = br.readLine();
                }
            }
            history += "****** history ******";
            history += "\n";
            jl1.setText(jl1.getText()+history);

        }

    }

}


