package client;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;
import readThread.ClientMessageThread;
import stream.ClientClientConnection;
import tools.MyMap;
import user.UserInfo;

public class ClientFrame extends JFrame implements ActionListener {
    private static final long serialVersionUID = 1L;
    private JTextArea chatTextArea;
    private JButton send;
    private JTextField inputField;
    private final SimpleDateFormat simpleDateFormat;
    private final UserInfo myUserInfo;
    private final UserInfo toUserInfo;
    private ClientClientConnection userDataCS;
    private ClientMessageThread clientMessageThread;
    private Thread readMessageThread;
    private final MyMap isOpenMap;

    // 姓名 账户 IP
    public ClientFrame(UserInfo toUserInfo, UserInfo myUserInfo, MyMap isOpenMap)
            throws HeadlessException {
        super("Chatting with" + toUserInfo.getName() + "(" + toUserInfo.getAccount() + ")");
        this.toUserInfo = toUserInfo;
        this.myUserInfo = myUserInfo;
        this.isOpenMap = isOpenMap;
        DatagramSocket dataSocket = null;
        try {
            dataSocket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        try {
            userDataCS = new ClientClientConnection(dataSocket, InetAddress.getByName(toUserInfo.getIP()),
                    toUserInfo.getPort());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        userDataCS.send("%TEST%");
        simpleDateFormat = (SimpleDateFormat) DateFormat.getInstance();
        simpleDateFormat.applyPattern("yyyy/MM/dd HH:mm:ss");
        createFrame();
        addEventHandler();
    }

    private void createFrame() {
        chatTextArea = new JTextArea(25, 60);
        chatTextArea.setEditable(false);
        JTextArea userInfoListArea = new JTextArea();
        userInfoListArea.setEditable(false);
        JScrollPane centerScrollPane = new JScrollPane(chatTextArea);
        send = new JButton("Send");
        inputField = new JTextField(35);
        JLabel currentUserNameTitleLabel = new JLabel(toUserInfo.getName() + "(" + toUserInfo.getAccount() + ")");
        currentUserNameTitleLabel.setFont(new Font("微软雅黑", Font.PLAIN, 25));
        JPanel userInfoPanel = new JPanel(new GridLayout(2, 2, 5, 6));
        userInfoPanel.add(currentUserNameTitleLabel);
        JLabel userSignatureLable = new JLabel();
        userInfoPanel.add(userSignatureLable);
        userInfoListArea.setText("IP: " + toUserInfo.getIP() + "\nPort " + toUserInfo.getPort() + "\nUsername: "
                + toUserInfo.getName() + "\nAccount: " + toUserInfo.getAccount());
        JLabel portraItImageLable = new JLabel();
        JPanel northPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        northPanel.add(portraItImageLable, BorderLayout.NORTH);
        JPanel southPanel = new JPanel();
        northPanel.add(userInfoPanel);
        southPanel.add(inputField);
        southPanel.add(send);
        JScrollPane listScrollPane = new JScrollPane(userInfoListArea);
        JPanel eastPanel = new JPanel(new BorderLayout());
        eastPanel.add(new JLabel("UserInfoList: "), BorderLayout.NORTH);
        eastPanel.add(listScrollPane, BorderLayout.CENTER);
        add(eastPanel, BorderLayout.EAST);
        add(northPanel, BorderLayout.NORTH);
        add(centerScrollPane, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String message = inputField.getText();
        if (message == null || message.trim().equals("")) {
            JOptionPane.showMessageDialog(this, "You cannot send empty message!");
        } else {
            String time = simpleDateFormat.format(new Date());
            if (userDataCS != null) {
                userDataCS.send(myUserInfo.getName() + "-" + myUserInfo.getAccount() + "-"
                        + myUserInfo.getUserPortraitNum() + "-" + myUserInfo.getRecenIP() + "-"
                        + myUserInfo.getRecentPort() + "-" + "(" + time + ")\n" + message);
                chatTextArea.append("You" + "(" + time + ")\n" + message + "\n");
                inputField.setText("");
            }
        }
    }

    private void addEventHandler() {
        inputField.addActionListener(this);
        send.addActionListener(this);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int t = JOptionPane.showConfirmDialog(null, "Confirm to exit?", "Exit", JOptionPane.OK_CANCEL_OPTION);
                if (t == JOptionPane.OK_OPTION) {
                    userDataCS.send("I_HAVE_EXIT_THE_WINDOW");
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    readMessageThread.interrupt();
                    clientMessageThread.stopMe();
                    isOpenMap.replace(toUserInfo.getAccount(), false);
                    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
                }
            }
        });
    }

    public ClientFrame showMe() {
        pack();
        setVisible(true);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        clientMessageThread = new ClientMessageThread(userDataCS, chatTextArea, toUserInfo);
        readMessageThread = new Thread(clientMessageThread);
        readMessageThread.start();
        return this;
    }

    public JTextArea getChatTextArea() {
        return chatTextArea;
    }

    public ClientClientConnection getUserDataCS() {
        return userDataCS;
    }

}