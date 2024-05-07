package com.yychat.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import com.yychat.model.*;
import com.yychat.control.*;
import java.io.*;

public class FriendList extends JFrame implements ActionListener, MouseListener,WindowListener {
    // 声明各种组件和变量
	public static HashMap<String,FriendChat>hmFriendChat=new HashMap<String,FriendChat>();
	
    JPanel friendPanel; 
    JButton myFriendButton1;
    JButton mystrangerButton1;
    JButton blackListButton1;
    JScrollPane friendListScrollPane;
    JPanel friendListPanel;
    final int MYFRIENDCOUNT = 50;
    JLabel friendLabel[] = new JLabel[MYFRIENDCOUNT];

    JPanel strangerPanel;
    JButton myFriendButton2;
    JButton mystrangerButton2;
    JButton blackListButton2;
    JScrollPane strangerListScrollPane;
    JPanel strangerListPanel;
    final int STRANGERCOUNT = 20;
    JLabel strangerLabel[] = new JLabel[STRANGERCOUNT];

    CardLayout cl; // 卡片布局管理器
    User user; // 用户名
    
    JPanel addFriendJPanel;
    JButton addFriendButton;
    
    private static final long serialVersionUID = 1L;

    public FriendList(User user, String allFriend) {
        this.user = user;
        initializeComponents(allFriend); // 初始化界面组件
        setWindowProperties(); // 设置窗口属性
    }

    private void initializeComponents(String allFriend) {
        // 初始化好友面板及其组件
        initializeFriendPanel(allFriend);
        // 初始化陌生人面板及其组件
        initializeStrangerPanel();
    }

    private void initializeFriendPanel(String allFriend) {
        friendPanel = new JPanel(new BorderLayout());
        myFriendButton1 = new JButton("我的好友");
        
        addFriendJPanel = new JPanel(new GridLayout(2, 1));
        addFriendButton = new JButton("添加好友");
        addFriendButton.addActionListener(this);
        friendPanel.add(addFriendButton, BorderLayout.NORTH);
        
        friendListPanel = new JPanel();
        showAllFriend(allFriend);
        
        friendListScrollPane = new JScrollPane(friendListPanel);
        friendPanel.add(friendListScrollPane, BorderLayout.CENTER);
        
        mystrangerButton1 = new JButton("陌生人");
        mystrangerButton1.addActionListener(this);
        blackListButton1 = new JButton("黑名单");
        JPanel stranger_BlackPanel = new JPanel(new GridLayout(2, 1));
        stranger_BlackPanel.add(mystrangerButton1);
        stranger_BlackPanel.add(blackListButton1);
        friendPanel.add(stranger_BlackPanel, BorderLayout.SOUTH);
    }

    private void initializeStrangerPanel() {
        strangerPanel = new JPanel(new BorderLayout());
        myFriendButton2 = new JButton("我的好友");
        myFriendButton2.addActionListener(this);
        mystrangerButton2 = new JButton("陌生人");
        JPanel friend_strangerPanel = new JPanel(new GridLayout(2, 1));
        friend_strangerPanel.add(myFriendButton2);
        friend_strangerPanel.add(mystrangerButton2);
        strangerPanel.add(friend_strangerPanel, BorderLayout.NORTH);
        
        strangerListPanel = new JPanel(new GridLayout(STRANGERCOUNT, 1));
        for (int i = 0; i < strangerLabel.length; i++) {
            // 添加陌生人列表项
            strangerLabel[i] = new JLabel(i + "号陌生人", new ImageIcon("images/tortoise.gif"), JLabel.LEFT);
            strangerListPanel.add(strangerLabel[i]);
        }
        strangerListScrollPane = new JScrollPane(strangerListPanel);
        strangerPanel.add(strangerListScrollPane, BorderLayout.CENTER);
        
        blackListButton2 = new JButton("黑名单");
        strangerPanel.add(blackListButton2, BorderLayout.SOUTH);
    }

    private void setWindowProperties() {
        // 设置窗口属性
        cl = new CardLayout(); // 创建卡片布局管理器
        this.setTitle("ID："+user.getUserID()+"昵称："+user.getUserName()+"的好友列表");
        this.setLayout(cl); // 设置布局管理器
        this.add(friendPanel, "card1"); // 添加好友面板到主面板，并指定卡片名称为 "card1"
        this.add(strangerPanel, "card2"); // 添加陌生人面板到主面板，并指定卡片名称为 "card2"
        cl.show(this.getContentPane(), "card1"); // 默认显示好友面板
        this.setIconImage(new ImageIcon("images/duck2.gif").getImage());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setBounds(800, 600, 350, 250);
        this.setVisible(true);
        this.addWindowListener(this);
    }

    public void showAllFriend(String allFriend) {
    	String[] myFriend=allFriend.split(" ");
    	friendListPanel.removeAll();
    	friendListPanel.setLayout(new GridLayout(myFriend.length-1, 1));
	      for(int i=1;i<myFriend.length;i++) {
	    	String imageStr="images/"+i%6+".jpg";
	    	ImageIcon imageIcon = new ImageIcon(imageStr);
	    	friendLabel[i] = new JLabel(myFriend[i], imageIcon, JLabel.LEFT);
	    	friendLabel[i].addMouseListener(this);
	    	friendListPanel.add(friendLabel[i]);
	      }
	    friendListPanel.revalidate();  
    }
    
    public void activeNewOnlineFriendIcon(String newOnlineFriend) {
    	this.friendLabel[Integer.valueOf(newOnlineFriend)].setEnabled(true);
    }
    
    public void activeOnlineFriendIcon(Message mess) {
    	String onlineFriend=mess.getContent();
    	String[] onlineFriendName=onlineFriend.split(" ");
    	for(int i=1;i<onlineFriendName.length;i++) {
    		this.friendLabel[Integer.valueOf(onlineFriendName[i])].setEnabled(true);
    	}
    }

    
    public void actionPerformed(ActionEvent arg0) {// 按钮点击事件处理方法
    	if (arg0.getSource() == addFriendButton) {
    		String newFriend=JOptionPane.showInputDialog("请输入新好友的ID：");
    		System.out.println("newFriend:"+newFriend);
    		if(newFriend!=null) {
    			Message mess=new Message();
    			mess.setUser(user);
    			mess.setSender(user.getUserName());
    			mess.setRecevier("Server");
    			mess.setContent(newFriend);
    			mess.setMessageType(MessageType.ADD_NEW_FRIEND);
    			try {
    				ObjectOutputStream oos=new ObjectOutputStream(YychatClientConnection.s.getOutputStream());
    				oos.writeObject(mess);
    			}catch(IOException e) {
    				e.printStackTrace();
    			}
    	}
        if (arg0.getSource() == myFriendButton2)
            cl.show(this.getContentPane(),"card1"); // 显示好友面板
        if (arg0.getSource() == mystrangerButton1)
            cl.show(this.getContentPane(),"card2"); // 显示陌生人面板
        }
    }

    public void mouseClicked(MouseEvent arg0) {// 鼠标点击事件处理方法
        if (arg0.getClickCount() == 2) {
            JLabel jl = (JLabel) arg0.getSource();
            String toName = jl.getText();
            // new FriendChat(name + " to " + toName); // 双击好友名称打开聊天窗口
            //new FriendChat(name,toName);
            FriendChat fc=new FriendChat(user.getUserName(),toName);
            hmFriendChat.put(user.getUserName()+"to"+toName, fc);
        }
    }

    public void mouseEntered(MouseEvent arg0) {// 鼠标进入事件处理方法
        JLabel jl = (JLabel) arg0.getSource();
        jl.setForeground(Color.red); // 鼠标进入时字体颜色改为红色
    }
    
    public void mouseExited(MouseEvent arg0) {// 鼠标退出事件处理方法
        JLabel jl = (JLabel) arg0.getSource();
        jl.setForeground(Color.blue); // 鼠标退出时字体颜色改为蓝色
    }

    public void mousePressed(MouseEvent arg0) {
    }

    public void mouseReleased(MouseEvent arg0) {
    }
    
    public void windowClosing(WindowEvent arg0) {
    	System.out.println(user.getUserName()+"准备关闭客户端...");
    	Message mess=new Message();
    	mess.setUser(user);
    	mess.setSender(user.getUserName());
    	mess.setRecevier("Server");
    	mess.setMessageType(MessageType.UESR_EXIT_SERVER_THREAD_CLOSE);
    	ObjectOutputStream oos;
    	try {
    		oos=new ObjectOutputStream(YychatClientConnection.s.getOutputStream());
    		oos.writeObject(mess);
    	}catch(IOException e) {
    		e.printStackTrace();
    	}
    	System.exit(0);
    }
    public void windowOpened(WindowEvent e) {}
    @Override
    public void windowClosed(WindowEvent e) {}
    @Override
    public void windowIconified(WindowEvent e) {}
    @Override
    public void windowDeiconified(WindowEvent e) {}
    @Override
    public void windowActivated(WindowEvent e) {}
    @Override
    public void windowDeactivated(WindowEvent e) {}
    
}

    

