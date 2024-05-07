package com.yychatserver.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

import com.yychat.control.YychatServer;

public class StartServer extends JFrame implements ActionListener {
    JButton jb1, jb2;
    private static final long serialVersionUID = 1L;
 
    public StartServer() {// 构造函数，创建启动服务器界面
        initComponents();
        setupUI();
    }

    private void initComponents() {
        jb1 = new JButton("启动服务器");
        jb1.setFont(new Font("宋体", Font.BOLD, 25));
        jb1.addActionListener(this);
        
        jb2 = new JButton("停止服务器");
        jb2.setFont(new Font("宋体", Font.BOLD, 25));
    }

    private void setupUI() {
        this.setLayout(new GridLayout(1, 2));
        this.add(jb1);
        this.add(jb2);
        this.setSize(400, 100);
        this.setLocationRelativeTo(null); // 将窗口居中显示
        this.setIconImage(new ImageIcon("images/duck2.gif").getImage()); // 设置窗口图标
        this.setTitle("YYchat服务器"); // 设置窗口标题
        this.setVisible(true); // 显示窗口
    }
    
    public void actionPerformed(ActionEvent arg0) {// 按钮点击事件处理方法
        if (arg0.getSource() == jb1) {
            new YychatServer(); // 启动服务器
        }
    } 
    
    public static void main(String[] args) {// 主方法
        @SuppressWarnings("unused")
		StartServer ss = new StartServer();
    }

}
