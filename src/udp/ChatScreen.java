package udp;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.DatagramPacket;

class ChatScreen extends JFrame {

    UdpClient client;

    JFrame jf = new JFrame("Wechat");
    JPanel jp = new JPanel();
    JPanel bottom = new JPanel();
    JTextField jTSend = new JTextField(50);

    JButton send = new JButton("Send");


    JTextArea jTextArea = new JTextArea();

    /**
     * constructor of the IHM where we put all the components SWING on the window
     * @throws IOException
     */
    public ChatScreen(UdpClient client) throws IOException {

        this.client = client;

        jp.setPreferredSize(new Dimension(400, 600));
        jp.setLayout(new BoxLayout(jp, BoxLayout.Y_AXIS));

        jTextArea.setSize(200, 30);

        jTextArea.setEditable(false);
        jTextArea.setAlignmentX(0);

        jp.add(jTextArea);
        jTextArea.setAlignmentX(Component.RIGHT_ALIGNMENT);
        jp.add(Box.createVerticalStrut(20));

        jp.add(Box.createVerticalStrut(20));
        jf.add(jp);

        send.addActionListener(e -> {
            try {
                sendMessage();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
        getHistory();
        bottom.add(jTSend);
        bottom.add(send);

        jf.add(bottom, BorderLayout.SOUTH);


        jf.setVisible(true);
        jf.pack();
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /**
     * Show a message on the screen for the user
     * @param msg the message we want to show
     */
    public void showMessage(String msg) {
        jTextArea.setText(jTextArea.getText() + msg + "\n");
    }

    /**
     * Method to send a message from one client.
     * Get the string one client put on the screen and then transpose to another client.
     */
    public void sendMessage() throws IOException {
        String line = jTSend.getText();

        if (line.equals("exit")) {
            client.leave();
            showMessage("you are leaving..............\n");
            return;
        }

        File historyFile = new File("historyUDP.txt");
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

        jTSend.setText("");
        DatagramPacket msgPacket = new DatagramPacket(line.getBytes(), line.length(), client.groupAddr, client.groupPort);
        client.getS().send(msgPacket);
    }

    /**
     * Read the file 'historyUDP.txt' which contains all the information in the history only associate with this client.
     * Then put the content on the screen.
     * @throws IOException
     */
    public void getHistory() throws IOException {
        String history = "chat\n\n";
        history += "****** history ******";
        history += "\n";
        File historyFile = new File("historyUDP.txt");
        if (historyFile.exists()) {
            InputStreamReader reader = new InputStreamReader(new FileInputStream(historyFile));
            BufferedReader br = new BufferedReader(reader);
            String rd = br.readLine();
            while(rd != null) {
                history += rd;
                history += "\n";
                rd = br.readLine();
            }
        }
        history += "****** history ******";
        history += "\n";
        jTextArea.setText(jTextArea.getText()+history);

    }
}
