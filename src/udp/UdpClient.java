package udp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class UdpClient {

    MulticastSocket s;
    UDPReceiveThread udpReceiveThread;
    InetAddress groupAddr;
    int groupPort;

    /**
     * constructor of the udpclient.
     * Start a new client by giving the group hostname and the port of the multicast.
     * @param group the group hostname
     * @param port the port of the multicast
     * @throws IOException
     */
    public UdpClient(String group, String port) throws IOException {
        /*
        multicast and broadcast use the particular address
        224.0.0.0 to 239.255.255.255
        224.0.0.0 to 224.0.0.255
        */
        // get the group address and the group port
        groupAddr = InetAddress.getByName(group); // only between certain address
        groupPort = Integer.parseInt(port);

        // create a multicast socket
        s = new MulticastSocket(groupPort);
        // join the group
        s.joinGroup(groupAddr);

        ChatScreen chatScreen = new ChatScreen(this);

        chatScreen.showMessage("you have joined the group !");

        udpReceiveThread = new UDPReceiveThread(s,chatScreen);
        udpReceiveThread.start();

    }

    /**
     * Getter: Obtain the socket which correspond to the client.
     * @return socket
     */
    public MulticastSocket getS() {
        return s;
    }

    /**
     * Method which make the client leaves the multicast socket
     * @throws IOException
     */
    public void leave() throws IOException {
        udpReceiveThread.setExit(true);
        s.leaveGroup(groupAddr);
    }
}


