package user;

public class UserInfo
{
    private String name;
    private String account;
    private String password;
    private String IP;
    private int port;
    private int recentPort;
    private String recenIP;
    private int userPortraitNum;

    public UserInfo()
    {
        super();
    }

    public UserInfo(String name, String account, String IP, int port, int userPortraitNum, String recentIP,
                    int recentPort)
    {
        super();
        this.name = name;
        this.account = account;
        this.IP = IP;
        this.port = port;
        this.recenIP = recentIP;
        this.recentPort = recentPort;
        this.userPortraitNum = userPortraitNum;
    }

    public int getRecentPort()
    {
        return recentPort;
    }

    public void setRecentPort(int recentPort)
    {
        this.recentPort = recentPort;
    }

    public String getRecenIP()
    {
        return recenIP;
    }

    public void setRecenIP(String recenIP)
    {
        this.recenIP = recenIP;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getAccount()
    {
        return account;
    }

    public void setAccount(String acocount)
    {
        this.account = acocount;
    }

    public String getPassword()
    {
        return password;
    }

    public String getIP()
    {
        return IP;
    }

    public void setIP(String iP)
    {
        IP = iP;
    }

    public int getPort()
    {
        return port;
    }

    public void setPort(int port)
    {
        this.port = port;
    }

    public int getUserPortraitNum()
    {
        return userPortraitNum;
    }

    public void setUserPortraitNum(int userPortraitNum)
    {
        this.userPortraitNum = userPortraitNum;
    }

    public void setInfo(String message)
    {
        this.IP = message.split(":")[1];
        this.port = Integer.parseInt(message.split(":")[2]);
        this.name = message.split(":")[3];
        this.account = message.split(":")[4];
        this.password = message.split(":")[5];
        this.userPortraitNum = Integer.parseInt(message.split(":")[6]);
    }
}
