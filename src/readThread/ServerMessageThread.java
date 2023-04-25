package readThread;

import java.util.Map;

import javax.swing.JList;
import javax.swing.JOptionPane;

import stream.ServerClientConnection;
import tools.MyMap;

public class ServerMessageThread implements Runnable {
    private final ServerClientConnection userCS;
    private String[] onlineUserList;
    private final MyMap isOnlineMap;
    private final JList<String> currentOnlineUserList;
    private volatile boolean stop = false;
    private final MyMap isOpenMap;
    private final Map<String, String> userInfoMap;
    private String loginName;

    public ServerMessageThread(ServerClientConnection userCS,
                               Map<String, String> userInfoMap, JList<String> currentOnlineUserList,
                               MyMap isOnlineMap, MyMap isOpenMap) {
        super();
        this.userCS = userCS;
        this.userInfoMap=userInfoMap;
        this.isOnlineMap = isOnlineMap;
        this.isOpenMap = isOpenMap;
        this.currentOnlineUserList = currentOnlineUserList;
    }

    public void refreshGUIUserList() {
        StringBuilder t = new StringBuilder();
        for (String string : onlineUserList)
        {
            String[] per = string.split("-");
            if (!per[2].equals(loginName))
            {
                isOnlineMap.put(per[2], false);
                isOpenMap.put(per[2], false);
            }
            t.append(per[3]).append("（").append(per[2]).append("）").append(":");
            userInfoMap.put(per[2], string);
        }
        String[] tt = t.toString().split(":");
        currentOnlineUserList.setListData(tt);
    }

    @Override
    public void run()
    {
        while (!stop)
        {
            String message = userCS.read();
            if (message != null)
            {
                String[] perMessage = message.split(":");
                loginName = perMessage[1];
                if ("%USER_LIST%".equals(perMessage[0]))
                {
                    onlineUserList = perMessage[2].split(",");
                    if ("LOGIN".equals(perMessage[1].split("&")[1]))
                    {
                        isOnlineMap.put(perMessage[1].split("&")[0], true);
                        isOpenMap.put(perMessage[1].split("&")[0], false);
                    } else if ("EXIT".equals(perMessage[1].split("&")[1]))
                    {
                        if (isOpenMap.getValue(perMessage[1].split("&")[0]))
                        {
                            JOptionPane.showMessageDialog(null, "User offline!");
                        }
                        isOnlineMap.replace(perMessage[1].split("&")[0], false);
                        isOpenMap.replace(perMessage[1].split("&")[0], false);
                    }
                    refreshGUIUserList();
                }
            }

        }
    }

    public void stopMe() {
        stop = true;
    }
}
