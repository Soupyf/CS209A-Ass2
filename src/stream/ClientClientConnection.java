package stream;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class ClientClientConnection
{
    private final DatagramPacket userReceivePacket;
    private final DatagramSocket userDataSocket;
    private final byte[] buffer;
    private InetAddress hostAddress;
    private int port;

    public ClientClientConnection(DatagramSocket userDataSocket)
    {
        super();
        this.userDataSocket = userDataSocket;
        buffer = new byte[1024];
        userReceivePacket = new DatagramPacket(buffer, buffer.length);
    }

    public ClientClientConnection(DatagramSocket userDataSocket, InetAddress hostAddress, int port)
    {
        super();
        this.hostAddress = hostAddress;
        this.port = port;
        this.userDataSocket = userDataSocket;
        buffer = new byte[1024];
        userReceivePacket = new DatagramPacket(buffer, buffer.length);
    }

    public void send(String message)
    {
        try
        {
            userDataSocket.send(new DatagramPacket(message.getBytes(), message.getBytes().length, hostAddress, port));
        } catch (IOException e)
        {
            System.out.println("Port is used!");
            e.printStackTrace();
        }
    }

    public String read()
    {
        try
        {
            if (!userDataSocket.isClosed())
            {
                userDataSocket.receive(userReceivePacket);
                return new String(userReceivePacket.getData(), 0, userReceivePacket.getLength(), "GBK");
            }
        } catch (UnsupportedEncodingException e)
        {
            System.out.println("Unsupported encoding!");
            e.printStackTrace();
        } catch (IOException e)
        {
            System.out.println(false);
            e.printStackTrace();
        }
        return null;
    }

    public void setPort(int port)
    {
        this.port = port;
    }

    public String getHostAddress()
    {
        return hostAddress.getHostAddress();
    }

    public void setHostAddress(InetAddress hostAddress)
    {
        this.hostAddress = hostAddress;
    }

    public int getPort()
    {
        return port;
    }

    public DatagramPacket getUserReceivePacket()
    {
        return userReceivePacket;
    }

}
