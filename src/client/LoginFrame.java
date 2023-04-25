package client;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;
import javax.swing.*;
import stream.ServerClientConnection;
import user.UserInfo;

public class LoginFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    private ServerClientConnection userCS;
    private String userName;
    private int userPort;
    private String message;
    private JTextField accountField;
    private JPasswordField passwordField;
    private JButton login;
    private UserInfo userInfo;

    public LoginFrame() throws HeadlessException {
        initData();
        createFrame();
        addHandlerEvent();
    }

    @SuppressWarnings("resource")
    private void initData() {
        userInfo = new UserInfo();
        try {
            DatagramSocket d = new DatagramSocket();
            userPort = d.getLocalPort();
            d.close();
            InetAddress SERVER_INETADRESS = InetAddress.getLocalHost();
            InetAddress LOCAL_INETADRESS = InetAddress.getLocalHost();
            int SERVER_PORT = 9000;
            Socket socket = new Socket(SERVER_INETADRESS, SERVER_PORT, LOCAL_INETADRESS, new DatagramSocket().getLocalPort());
            userCS = new ServerClientConnection(socket);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void createFrame() {
        setTitle("Login");
        Random random = new Random();
        JLabel imageLable = new JLabel();
        int num = (random.nextInt(38) + 1);
        userInfo.setUserPortraitNum(num);
        JLabel portraItImageLable = new JLabel();
        JPanel mainLayout = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        accountField = new JTextField(15);
        login = new JButton("Login");
        login.setBackground(new Color(30, 144, 255));
        login.setForeground(Color.white);
        login.setPreferredSize(new Dimension(170, 30));
        passwordField = new JPasswordField(15);
        JPanel jPanel_0 = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        JPanel accRes = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        JPanel passRes = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        JPanel accPassHintPanel = new JPanel(new GridLayout(3, 15, 0, 0));
        accRes.add(accountField);
        passRes.add(passwordField);
        accPassHintPanel.add(accRes);
        accPassHintPanel.add(passRes);
        JPanel jPanel_1 = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        JPanel jPanel_2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 1));
        JPanel jPanel_3 = new JPanel();
        jPanel_0.add(imageLable);
        jPanel_3.add(login);
        accPassHintPanel.add(jPanel_2);
        mainLayout.add(jPanel_0);
        jPanel_1.add(portraItImageLable);
        jPanel_1.add(accPassHintPanel);
        mainLayout.add(jPanel_1);
        mainLayout.add(jPanel_3);
        add(mainLayout);
        setVisible(true);
        setSize(445, 350);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        int height = getHeight();
        int width = getWidth();
        int screenWidth = screenSize.width / 2;
        int screenHeight = screenSize.height / 2;
        setLocation(screenWidth - width / 2, screenHeight - height / 2);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void addHandlerEvent() {
        login.addActionListener(arg0 -> {
            try {
                userCS.send("%LOGIN%:" + InetAddress.getLocalHost().getHostAddress() + ":" + userPort + ":" + ":" + accountField.getText() + ":" + String.valueOf(passwordField.getPassword()).trim()
                        + ":" + userInfo.getUserPortraitNum());
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }

            new Thread(() -> {
                while (true) {
                    message = userCS.read();
                    if ("LOGIN_FIAL".equals(message)) {
                        JOptionPane.showMessageDialog(null, "Account Existed!", "Wrong", JOptionPane.WARNING_MESSAGE);
                        accountField.setText("");
                        passwordField.setText("");
                    } else if ("NAME_IS_NULL".equals(message)) {
                        // setVisible(false);
                        dispose();
                        userName = JOptionPane.showInputDialog(null, "UserName", "Input username", JOptionPane.INFORMATION_MESSAGE);
                        try {
                            userCS.send("%LOGIN%:" + InetAddress.getLocalHost().getHostAddress() + ":"
                                    + userPort + ":" + userName + ":" + accountField.getText() + ":"
                                    + String.valueOf(passwordField.getPassword()).trim() + ":"
                                    + userInfo.getUserPortraitNum());
                            userInfo.setIP(InetAddress.getLocalHost().getHostAddress());
                            userInfo.setRecenIP(InetAddress.getLocalHost().getHostAddress());
                            userInfo.setRecentPort(userPort);
                        } catch (UnknownHostException e) {
                            e.printStackTrace();
                        }
                    } else if ("LOGIN_SUCESSFULLY".equals(message)) {
                        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
                        userInfo.setAccount(accountField.getText());
                        userInfo.setName(userName);
                        userInfo.setPort(userPort);
                        LoginProcess loginProcess = new LoginProcess(userInfo.getUserPortraitNum());
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        loginProcess.dispose();
                        new UserListFrame(userCS, userInfo).showMe();
                        JOptionPane.showMessageDialog(null, "Login successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }).start();
        });
        addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                System.exit(0);
            }
        });
    }
}
