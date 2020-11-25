package udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;


class UDPReceiveThread extends Thread {

    MulticastSocket socket;
    byte[] buf = new byte[1000];
    DatagramPacket receivePacket = new DatagramPacket(buf, buf.length);
    boolean exit = false;
    ChatScreen chatScreen;

    /**
     * For each client who want to connect to the Multicast, we need to start a thread to listen to its actions.
     * @param s The multicast socket
     * @param chatScreen the instance of the chatScreen where we display the chat
     */
    UDPReceiveThread(MulticastSocket s, ChatScreen chatScreen) {
        socket = s;
        this.chatScreen = chatScreen;
    }

    /**
     * Setter:Set the boolean true to signify that the client has quit the multicast socket
     * @param exit
     */
    public void setExit(boolean exit) {
        this.exit = exit;
    }

    /**
     * receives a request from client then sends an echo to the client
     **/
    public void run() {
        // receive the message
        while (true) {

            if (exit) {
                break;
            }

            try {
                socket.receive(receivePacket);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (receivePacket.getLength() > 0) {
                byte[] message = receivePacket.getData();
                chatScreen.showMessage(new String(message));
            }
        }
    }

}
