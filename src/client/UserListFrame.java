package client;

import readThread.ServerMessageThread;
import stream.ClientClientConnection;
import stream.ServerClientConnection;
import tools.MyMap;
import user.UserInfo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UserListFrame extends JFrame
{
    private static final long serialVersionUID = 2L;
    private MyMap isOnlineMap;
    private MyMap isOpenMap;
    private final ServerClientConnection userCS;
    private JList<String> currentOnlineUserList;
    private Map<String, String> userInfoMap;
    private DatagramSocket dataSocket;
    private final UserInfo userInfo;
    private ServerMessageThread serverMessageThread;
    private Thread readMessagehread;
    private ClientFrame clientFrame;
    private final Map<String, ClientFrame> chatRoomMap = new HashMap<>();

    public UserListFrame(ServerClientConnection userCS, UserInfo userInfo) throws HeadlessException
    {
        super("Chat");
        this.userCS = userCS;
        this.userInfo = userInfo;
        initData();
        createFrame();
        addEventHandler();
        autoListening();
    }

    private void initData()
    {
        isOnlineMap = new MyMap();
        isOpenMap = new MyMap();
        userInfoMap= new HashMap<>();
        try
        {
            dataSocket = new DatagramSocket(userInfo.getPort());
        } catch (SocketException e)
        {
            e.printStackTrace();
        }
    }

    private void createFrame()
    {
        currentOnlineUserList = new JList<>();
        currentOnlineUserList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JLabel portraItImageLable = new JLabel();
        JScrollPane listScrollPane = new JScrollPane(currentOnlineUserList);
        JPanel centerPanel = new JPanel(new BorderLayout());
        JPanel northPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        northPanel.add(portraItImageLable, BorderLayout.NORTH);
        JPanel userInfoPanel = new JPanel(new GridLayout(2, 2, 5, 10));
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        JLabel userInfoLable = new JLabel(hour < 6 ? "Good night, " + userInfo.getName()
                : (hour < 12 ? "Morning, " : (hour < 18 ? "Good afternoon, " : "Good Evening, ")) + userInfo.getName());
        userInfoPanel.add(userInfoLable);
        SimpleDateFormat simpleDateFormat = (SimpleDateFormat) DateFormat.getInstance();
        simpleDateFormat.applyPattern("yyyy/ MM/ dd");
        JLabel userTimeLable = new JLabel(simpleDateFormat.format(new Date()));
        userInfoPanel.add(userTimeLable);
        northPanel.add(userInfoPanel);
        centerPanel.add(new JLabel("OnlineUserList"), BorderLayout.CENTER);
        centerPanel.add(listScrollPane, BorderLayout.CENTER);
        add(northPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        int width = getWidth();
        int screenWidth = screenSize.width / 2;
        setLocation(screenWidth - width / 2, 0);
        setResizable(false);
    }

    public void autoListening()
    {
        final ClientClientConnection userDataSoketCS = new ClientClientConnection(dataSocket);
        new Thread(() -> {
            while (true)
            {
                String string = userDataSoketCS.read();
                if (!"%TEST%".equals(string) && !"I_HAVE_EXIT_THE_WINDOW".equals(string))
                {
                    String[] tString = string.split("-");
                    if (isOpenMap.getValue(tString[1]))
                    {
                        chatRoomMap.get(tString[1]).getUserDataCS()
                                .setHostAddress(userDataSoketCS.getUserReceivePacket().getAddress());
                        chatRoomMap.get(tString[1]).getUserDataCS()
                                .setPort(userDataSoketCS.getUserReceivePacket().getPort());
                        chatRoomMap.get(tString[1]).getUserDataCS().send("%TEST%");
                        chatRoomMap.get(tString[1]).getChatTextArea().append(string + "\n");

                    } else
                    {
                        int t = JOptionPane.showConfirmDialog(null, tString[0] + " Send a message to you, receive?", "Receive",
                                JOptionPane.YES_NO_OPTION);
                        if (t == JOptionPane.YES_OPTION)
                        {
                            UserInfo toUserInfo = new UserInfo(tString[0], tString[1],
                                    userDataSoketCS.getUserReceivePacket().getAddress().getHostAddress(),
                                    userDataSoketCS.getUserReceivePacket().getPort(), Integer.parseInt(tString[2]),
                                    tString[3], Integer.parseInt(tString[4]));
                            clientFrame = new ClientFrame(toUserInfo, userInfo, isOpenMap).showMe();
                            String tMessage = tString[0] +
                                    tString[5];
                            clientFrame.getChatTextArea().append(tMessage + "\n");
                            isOpenMap.replace(tString[1], true);
                            chatRoomMap.put(tString[1], clientFrame);
                        }
                    }
                }

            }
        }).start();
    }

    private void addEventHandler()
    {
        currentOnlineUserList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting())
            {
                String tString = currentOnlineUserList.getSelectedValue();
                if (tString != null)
                {
                    String[] t = tString.split("（");
                    String[] string = t[1].split("）");
                    if (string[0].equals(userInfo.getAccount()))
                    {
                        JOptionPane.showMessageDialog(null, "You cannot choose yourself", "Wrong", JOptionPane.WARNING_MESSAGE);
                    } else
                    {
                        String[] tempUserInfo = userInfoMap.get(string[0]).split("-");
                        UserInfo toUserInfo = new UserInfo(tempUserInfo[3], tempUserInfo[2], tempUserInfo[0],
                                Integer.parseInt(tempUserInfo[1]), Integer.parseInt(tempUserInfo[4]), tempUserInfo[0],
                                Integer.parseInt(tempUserInfo[1]));
                        clientFrame = new ClientFrame(toUserInfo, userInfo, isOpenMap);
                        clientFrame.showMe();
                        chatRoomMap.put(tempUserInfo[1], clientFrame);
                        isOpenMap.replace(tempUserInfo[1], true);
                    }
                }
            }

        });

        addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                int t = JOptionPane.showConfirmDialog(null, "Confirm to exit?", "Yes",
                        JOptionPane.OK_CANCEL_OPTION);
                if (t == JOptionPane.OK_OPTION)
                {
                    readMessagehread.interrupt();
                    serverMessageThread.stopMe();
                    userCS.send("%EXIT%:" + userInfo.getAccount());
                    try
                    {
                        Thread.sleep(100);
                    } catch (InterruptedException e1)
                    {
                        e1.printStackTrace();
                    }
                    userCS.send("%EXIT%:" + userInfo.getAccount());
                    try
                    {
                        Thread.sleep(100);
                    } catch (InterruptedException e1)
                    {
                        e1.printStackTrace();
                    }
                    System.exit(0);
                }
            }
        });
    }

    public void showMe()
    {
        setSize(340, 700);
        setVisible(true);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        serverMessageThread = new ServerMessageThread(userCS,  userInfoMap, currentOnlineUserList,
                isOnlineMap, isOpenMap);
        readMessagehread = new Thread(serverMessageThread);
        readMessagehread.start();
    }
}
