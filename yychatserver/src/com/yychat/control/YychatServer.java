package com.yychat.control;

import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import com.yychat.model.*;

public class YychatServer {
    ServerSocket ss;
    Socket s;
    public static HashMap<User,Socket> hmSocket=new HashMap<>();
    // 构造函数，创建YYchat服务器
    public YychatServer() {
        try {
            // 创建服务器套接字并监听指定端口
            ss = new ServerSocket(3456);
            System.out.println(LocalDateTime.now()+"服务器消息："+"服务器已启动，正在监听3456端口...");
            // 循环监听客户端连接
            while (true) {
                s = ss.accept();
                System.out.println(LocalDateTime.now()+"服务器消息："+"连接成功: " + s);
                // 获取客户端发送的用户登录信息
                ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
                User user = (User) ois.readObject();
                int userID = user.getUserID();
                String password = user.getPassword();
                System.out.println(LocalDateTime.now()+"服务器消息："+"服务器端接收到的客户端登陆信息userName : " + userID + " password: " + password);
                // 创建消息对象
                boolean loginSuccess=DBUtil.loginValidate(user);
                
                ObjectOutputStream oos=new ObjectOutputStream(s.getOutputStream());
                Message mess = new Message();
                
                if(user.getUserType().equals(UserType.USER_REGISTER)) {
                	if(DBUtil.seekUser(user)) {
                		mess.setMessageType(MessageType.USER_REGISTER_FAILURE);
                	}else {
                		DBUtil.insertIntoUser(user);
                		mess.setUser(user);
                		mess.setMessageType(MessageType.USER_REGISTER_SUCCESS);
                		oos.writeObject(mess);
                		s.close();
                	}
                }
                
                if(user.getUserType().equals(UserType.USER_LOGIN_VALIDATE)) {
	                if (loginSuccess) {
	                    System.out.println(LocalDateTime.now()+"服务器消息："+"密码验证通过！");
	                    
	                    String allFriend=DBUtil.seekAllFriend(userID,1);
	                    mess.setContent(allFriend);
	                    mess.setMessageType(MessageType.LOGIN_VALIDATE_SUCCESS);
	                    oos.writeObject(mess);
	                    user = DBUtil.setUserInfo(userID);
	                    hmSocket.put(user, s);
	                    
	                    oos.writeObject(user);
	                    new ServerRecevierThread(s).start();
	                    System.out.println(LocalDateTime.now()+"服务器消息："+"启动线程成功！");
	                } else {
	                    System.out.println(LocalDateTime.now()+"服务器消息："+"密码验证失败！");
	                    mess.setMessageType(MessageType.LOGIN_VALIDATE_FAILURE);
	                    oos.writeObject(mess);
	                    s.close();
	                }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
       
    }
}
