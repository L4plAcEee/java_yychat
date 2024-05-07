package com.yychat.view;

import java.awt.Color;
import java.awt.event.*;
import javax.swing.*;
import com.yychat.model.*;
import com.yychat.control.YychatClientConnection;
import java.io.OutputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;

public class FriendChat extends JFrame implements ActionListener {
    JTextArea jta;
    JScrollPane jsp;
    JTextField jtf;
    JButton jb;
    String sender;
    String recevier;
    private static final long serialVersionUID = 1L;
  
    public FriendChat(String sender, String recevier) {// 构造函数，创建聊天界面
    	this.sender=sender;
    	this.recevier=recevier;
        // 初始化界面组件
        initUIComponents();
        // 设置窗口属性
        setWindowProperties(sender, recevier);
    }

    private void initUIComponents() {
        // 设置文本域
        jta = new JTextArea();
        jta.setForeground(Color.red);
        jsp = new JScrollPane(jta);
        this.add(jsp, "Center");
        
        // 设置文本框和发送按钮
        jtf = new JTextField(25);
        jb = new JButton("发送");
        jb.addActionListener(this);
        jb.setForeground(Color.blue);
        JPanel jp = new JPanel();
        jp.add(jtf);
        jp.add(jb);
        this.add(jp, "South");
        
        // 设置窗口大小
        this.setSize(400, 300);
    }

    private void setWindowProperties(String sender, String recevier) {
        // 设置窗口位置居中
        this.setLocationRelativeTo(null); 
        // 设置窗口标题
        this.setTitle(sender + " to " + recevier + "的聊天界面");
        // 设置窗口图标
        this.setIconImage(new ImageIcon("images/duck2.gif").getImage());
        // 设置窗口可见性
        this.setVisible(true);
    }

    public void append(Message mess) {//实现append方法
    	jta.append(mess.getSendTime().toString()+"\r\n"+mess.getSender()+"对你说："+mess.getContent()+"\r\n");
    }

    public void actionPerformed(ActionEvent e) {// 发送按钮点击事件处理方法
        if (e.getSource() == jb) {
            Message mess=new Message();
            mess.setSender(sender);
            mess.setRecevier(recevier);
            mess.setContent(jtf.getText());
            mess.setSendTime();
            mess.setMessageType(MessageType.COMMON_CHAT_MESSAGE);
            jta.append(mess.getSendTime().toString()+"\r\n"+jtf.getText()+"\r\n");
            jta.setText(null);
            try {
            	OutputStream os=YychatClientConnection.s.getOutputStream();
            	ObjectOutputStream oos=new ObjectOutputStream(os);
            	oos.writeObject(mess);
            }catch(IOException e1) {
            	e1.printStackTrace();
            }
        }
    }
}
