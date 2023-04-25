package readThread;

import java.net.InetAddress;
import java.net.UnknownHostException;
import javax.swing.JTextArea;
import stream.ClientClientConnection;
import user.UserInfo;

public class ClientMessageThread implements Runnable
{
    private final ClientClientConnection userDataCS;
    private final JTextArea chatTextArea;
    private final UserInfo toUserInfo;
    private volatile boolean stop = false;

    public ClientMessageThread(ClientClientConnection userDataCS, JTextArea chatTextArea,
                               UserInfo toUserInfo)
    {
        super();
        this.userDataCS = userDataCS;
        this.chatTextArea = chatTextArea;
        this.toUserInfo = toUserInfo;
    }

    @Override
    public void run()
    {
        while (!stop)
        {
            synchronized (this)
            {
                if (userDataCS != null)
                {
                    String message = userDataCS.read();
                    if ("%TEST%".equals(message))
                    {
                        System.out.println("IP: " + userDataCS.getHostAddress() + "Port: " + userDataCS.getPort());
                        userDataCS.setHostAddress(userDataCS.getUserReceivePacket().getAddress());
                        userDataCS.setPort(userDataCS.getUserReceivePacket().getPort());
                        System.out.println("IP: " + userDataCS.getHostAddress() + "Port: " + userDataCS.getPort());
                    } else if ("I_HAVE_EXIT_THE_WINDOW".equals(message))
                    {
                        try
                        {
                            userDataCS.setHostAddress(InetAddress.getByName(toUserInfo.getRecenIP()));
                            userDataCS.setPort(toUserInfo.getRecentPort());
                        } catch (UnknownHostException e)
                        {
                            e.printStackTrace();
                        }
                    } else
                    {
                        String[] tString = message.split("-");
                        String tMessage = tString[0] +
                                tString[5];
                        chatTextArea.append(tMessage + "\n");
                    }
                }
            }
        }
    }

    public void stopMe()
    {
        stop = true;
    }
}