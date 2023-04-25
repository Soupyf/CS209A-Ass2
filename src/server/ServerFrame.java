package server;

import stream.ServerClientConnection;
import user.UserInfo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ServerFrame extends JFrame
{
    private static final long serialVersionUID = 1L;
    private final Map<String, ServerClientConnection> userMap;
    private final Set<UserInfo> userSet;
    private ServerSocket serverSocket;
    private JButton start;
    private final JLabel welcome;
    private final JTextArea hintInfo;
    private final JTextArea onlineUserInfo;
    private final JTextArea startInfoTextArea;

    public ServerFrame()
    {
        super();
        userMap = new HashMap<>();
        userSet = new HashSet<>();
        welcome = new JLabel("Server");
        hintInfo = new JTextArea("Waiting\n");
        startInfoTextArea = new JTextArea();
        startInfoTextArea.setEditable(false);
        hintInfo.setEditable(false);
        onlineUserInfo = new JTextArea();
        onlineUserInfo.setEditable(false);
        createFrame();
        addEventHandler();
    }

    private void createFrame()
    {
        start = new JButton("Start");
        JTabbedPane jTabbedPane = new JTabbedPane(JTabbedPane.TOP);
        JScrollPane hintPanel = new JScrollPane(hintInfo);
        JScrollPane onlineUserInfoPanel = new JScrollPane(onlineUserInfo);
        JScrollPane startInfoScrollPanel = new JScrollPane(startInfoTextArea);
        JPanel welcomePanel = new JPanel();
        welcomePanel.add(welcome);
        JPanel southPanel = new JPanel();
        southPanel.add(start);
        jTabbedPane.add("Status", hintPanel);
        jTabbedPane.add("RunningDetails", startInfoScrollPanel);
        jTabbedPane.add("OnlineUsers", onlineUserInfoPanel);
        setSize(400, 600);
        setVisible(true);
        add(welcomePanel, BorderLayout.NORTH);
        add(jTabbedPane, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        int height = getHeight();
        int width = getWidth();
        int screenWidth = screenSize.width / 2;
        int screenHeight = screenSize.height / 2;
        setLocation(screenWidth - width / 2, screenHeight - height / 2);
    }

    public void addEventHandler()
    {
        start.addActionListener(arg0 -> {
            try
            {
                serverSocket = new ServerSocket(9000);
                new Thread(this::startServer).start();
                JOptionPane.showMessageDialog(null, "Server has started!", "Start", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e)
            {
                JOptionPane.showMessageDialog(null, "Server has started!", "Start", JOptionPane.INFORMATION_MESSAGE);
                e.printStackTrace();
            }
        });

        addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                int t = JOptionPane.showConfirmDialog(null, "Confirm to exit?", "Yes", JOptionPane.OK_CANCEL_OPTION);
                if (t == JOptionPane.OK_OPTION)
                {
                    System.exit(0);
                }
            }
        });
    }

    private void startServer()
    {
        while (true)
        {
            try
            {
                startInfoTextArea.append("Start...\n");
                printServerInfo();
                Socket socket = serverSocket.accept();
                startInfoTextArea.append("From IP: " + socket.getInetAddress().getHostAddress() + ", Port: "
                        + socket.getPort() + "Received...\n");
                new Thread(new ServerThread(new ServerClientConnection(socket), userSet, userMap,
                        hintInfo, startInfoTextArea, onlineUserInfo)).start();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        } // while
    }

    private void printServerInfo()
    {
        hintInfo.setText("Status: Start");
        try
        {
            hintInfo.append("\nServer: " + InetAddress.getLocalHost().getHostName());
            hintInfo.append("\nIP: " + InetAddress.getLocalHost().getHostAddress());
            hintInfo.append("\nPort: 9000");
            hintInfo.append("\nOnline: " + userMap.size());
        } catch (UnknownHostException e)
        {
            e.printStackTrace();
        }
    }
}
