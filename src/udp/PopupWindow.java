package udp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * A little popup window which let the client to enter the port and the groupId of the multicast.
 */
class PopupWindow extends JFrame {

    JFrame jf = new JFrame();
    JButton valid = new JButton("Join the Group");
    JLabel group = new JLabel("Group Address : ");
    JLabel port = new JLabel("Port Number : ");
    JTextField groupField = new JTextField(30);
    JTextField portField = new JTextField(10);
    JPanel buttons = new JPanel();


    /**
     * The constructor of the pop-up window,
     * which asks the user to enter the group address and the port number.
     * Initial value: 224.0.0.2 for group and 3000 for port.
     */
    public PopupWindow() {
        groupField.setText("224.0.0.2");
        portField.setText("3000");

        JPanel pick = new JPanel();

        pick.add(group);
        pick.add(groupField);
        jf.add(pick, BorderLayout.NORTH);

        JPanel deli = new JPanel();
        pick.add(port);
        pick.add(portField);
        jf.add(deli);

        buttons.add(valid);
        jf.add(buttons, BorderLayout.SOUTH);

        valid.addActionListener(new validButtonListener());

        jf.pack();
        jf.setVisible(true);
        jf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }


    /**
     * Get the value of the champ groupField
     * @return String in the champ groupField
     */
    public String getGroup() {
        return groupField.getText();
    }

    /**
     * Get the value of the champ portField
     * @return String in the champ portField
     */
    public String getPort() {
        return portField.getText();
    }


    /**
     * ActionListener of the button valid,
     * which uses the group address and the port number to create a new UdpClient objet,
     * then the pop-up window disposes.
     */
    class validButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String group = getGroup();
            String port = getPort();
            try {
                UdpClient client = new UdpClient(group, port);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }

            jf.dispose();
        }
    }

}
