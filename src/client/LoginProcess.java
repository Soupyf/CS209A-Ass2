package client;

import javax.swing.*;
import java.awt.*;

public class LoginProcess extends JFrame
{
    private static final long serialVersionUID = 1L;

    public LoginProcess(int number)
    {
        super();
        setTitle("Login");
        JLabel imageLable = new JLabel();
        JLabel portraItImageLable = new JLabel();
        JPanel mainLayout = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        JPanel jPanel_0 = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        JPanel jPanel_1 = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        JPanel jPanel_2 = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        JButton login = new JButton("Processing...");
        login.setPreferredSize(new Dimension(170, 30));
        login.setForeground(Color.white);
        login.setBackground(new Color(30, 144, 255));
        jPanel_2.add(login);
        jPanel_0.add(imageLable);
        mainLayout.add(jPanel_0);
        jPanel_1.add(portraItImageLable);
        JPanel jPanel_3 = new JPanel(new GridLayout(2, 1, 5, 5));
        jPanel_3.add(jPanel_1);
        jPanel_3.add(jPanel_2);
        mainLayout.add(jPanel_3);
        add(mainLayout);
        setSize(445, 350);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        int height = getHeight();
        int width = getWidth();
        int screenWidth = screenSize.width / 2;
        int screenHeight = screenSize.height / 2;
        setLocation(screenWidth - width / 2, screenHeight - height / 2);
        setVisible(true);
    }
}
