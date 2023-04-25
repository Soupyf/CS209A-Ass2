package server;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Set;
import javax.swing.JTextArea;
import stream.ServerClientConnection;
import user.UserInfo;

public class ServerThread implements Runnable
{
    private final ServerClientConnection userCS;
    private final UserInfo userInfo;
    private final Set<UserInfo> userSet;
    private final Map<String, ServerClientConnection> userMap;
    private final StringBuffer userList;
    private final JTextArea hintInfo;
    private final JTextArea startInfoTextArea;
    private final JTextArea onlineUserInfo;

    public ServerThread(ServerClientConnection userCS, Set<UserInfo> userSet,
                        Map<String, ServerClientConnection> userMap, JTextArea hintInfo, JTextArea startInfoTextArea,
                        JTextArea onlineUserInfo)
    {
        super();
        this.userCS = userCS;
        this.userSet = userSet;
        this.userMap = userMap;
        this.hintInfo = hintInfo;
        this.startInfoTextArea = startInfoTextArea;
        this.onlineUserInfo = onlineUserInfo;
        userList = new StringBuffer();
        userInfo = new UserInfo();
    }

    @Override
    public void run()
    {
        while (true)
        {
            String message = userCS.read();
            if (message.indexOf("%LOGIN%") == 0)
            {
                userInfo.setInfo(message);
                if (userMap.containsKey(userInfo.getAccount()))
                {
                    userCS.send("LOGIN_FIAL");
                } else if (userInfo.getName().equals(""))
                {
                    userCS.send("NAME_IS_NULL");
                } else
                {
                    userMap.put(userInfo.getAccount(), userCS);
                    userSet.add(userInfo);
                    userCS.send("LOGIN_SUCESSFULLY");
                    startInfoTextArea.append("Account: " + userInfo.getAccount() + "Login!\n");
                    userList.append(userInfo.getAccount()).append("&LOGIN:");
                    sendCurrentUserList();
                }
            } else if (message.indexOf("%EXIT%") == 0)
            {
                startInfoTextArea.append("Account: " + userInfo.getAccount() + "Exit!\n");
                userMap.remove(userInfo.getAccount());
                userSet.remove(userInfo);
                userList.append(userInfo.getAccount()).append("&EXIT:");
                sendCurrentUserList();
                startInfoTextArea.append("Send to: " + userInfo.getAccount() + "UserList!\n");
                break;
            }
        }
        userCS.close();
    }

    public void sendCurrentUserList()
    {
        onlineUserInfo.setText("");
        for (UserInfo userInfo : userSet)
        {
            onlineUserInfo.append("Account: " + userInfo.getAccount() + ", Password: " + userInfo.getPassword() + ", UserName: "
                    + userInfo.getName() + ", IP: " + userInfo.getIP() + ", Port: " + userInfo.getPort() + "\n");
            userList.append(userInfo.getIP()).append("-").append(userInfo.getPort()).append("-").append(userInfo.getAccount()).append("-").append(userInfo.getName()).append("-").append(userInfo.getUserPortraitNum()).append(",");
        }

        for (String string : userMap.keySet())
        {
            userMap.get(string).send("%USER_LIST%:" + userList);
        }
        startInfoTextArea.append("Send to: " + userInfo.getAccount() + "UserList!\n");
        userList.delete(0, userList.length());
        printServerInfo();
        startInfoTextArea.append("Status Updated!\n");
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