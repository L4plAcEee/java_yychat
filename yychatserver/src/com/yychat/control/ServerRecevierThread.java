package com.yychat.control;

import com.yychat.model.*;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.net.Socket;
import java.io.ObjectOutputStream;
import java.util.Set;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.time.*;

public class ServerRecevierThread extends Thread {
	Socket s;
	public ServerRecevierThread(Socket s) {
		this.s=s;
	}
	public void run() {
		while(true) {
			try {
				System.out.println(LocalDateTime.now()+"服务器消息：服务器线程运行中...");
				ObjectInputStream ois=new ObjectInputStream(s.getInputStream());
				Message mess=(Message)ois.readObject();
				
				if(mess.getMessageType().equals(MessageType.UESR_EXIT_SERVER_THREAD_CLOSE)) {
					String sender=mess.getSender();
					mess.setMessageType(MessageType.UESR_EXIT_CLIENT_THREAD_CLOSE);
					sendMessage(s,mess);
					System.out.println(LocalDateTime.now()+"服务器消息："+sender+"用户退出了！正在关闭其服务线程");
					Iterator<Map.Entry<User, Socket>> iterator = YychatServer.hmSocket.entrySet().iterator();
					while (iterator.hasNext()) {
					    Map.Entry<User, Socket> entry = iterator.next();
					    User user = entry.getKey();
					    Socket socket = entry.getValue();

					    // 判断条件：HashMap中的User等于mess中的User
					    if (user.isEqual(mess.getUser())) {
					        // 符合条件，删除键值对
					        iterator.remove();
					    }
					}
					s.close();
					break;
				}
				if(mess.getMessageType().equals(MessageType.ADD_NEW_FRIEND)) {
					int sender=mess.getUser().getUserID();
					int newFriend=Integer.parseInt(mess.getContent());
					if(DBUtil.seekUser(mess.getUser())) {
						if(DBUtil.seekFriend(sender,newFriend,1)) {
							mess.setMessageType(MessageType.ADD_NEW_FRIEND_FAILURE_ALREADY_FRIEND);
						}else {
							DBUtil.insertIntoFriend(sender,newFriend,1);
							String allFriend=DBUtil.seekAllFriend(sender, 1);
							mess.setContent(allFriend);
							mess.setMessageType(MessageType.ADD_NEW_FRIEND_SUCCESS);
						}
					}else {
						mess.setMessageType(MessageType.ADD_NEW_FRIEND_FAILURE_NO_USER);
					}
					Socket ss=(Socket)findSocketByUserName(YychatServer.hmSocket,mess.getSender());
					sendMessage(ss,mess);
				}
				if(mess.getMessageType().equals(MessageType.COMMON_CHAT_MESSAGE)) {
					System.out.println(LocalDateTime.now()+"服务器消息："+mess.getSender()+"对"+mess.getRecevier()+"说："+mess.getContent());
					mess.setSendTime();
					DBUtil.saveMessage(mess);
					String recevier=mess.getRecevier();
					Socket rs=(Socket)findSocketByUserName(YychatServer.hmSocket,recevier);
					System.out.println(LocalDateTime.now()+"服务器消息：接收方"+recevier+"的Socket对象："+rs);
					if(rs!=null) {
						ObjectOutputStream oos=new ObjectOutputStream(rs.getOutputStream());
						oos.writeObject(mess);
					}else
						System.out.println(LocalDateTime.now()+"服务器消息："+recevier+"不在线上");
				}
				if(mess.getMessageType().equals(MessageType.REQUEST_ONLINE_FRIEND)) {
					Set<User> onlineFriendSet=YychatServer.hmSocket.keySet();
					Iterator<User> it=onlineFriendSet.iterator();
					String onlineFriend=" ";
					while(it.hasNext()){
						onlineFriend=" "+it.next().getUserName()+onlineFriend;
					}
					mess.setRecevier(mess.getSender());
					mess.setSender("Server");
					mess.setMessageType(MessageType.RESPONSE_ONLINE_FRIEND);
					mess.setContent(onlineFriend);
					sendMessage(s,mess);
				}
				if(mess.getMessageType().equals(MessageType.NEW_ONLINE_TO_ALL_FRIEND)) {
					mess.setMessageType(MessageType.NEW_ONLINE_FRIEND);
					Set<User> onlineFriendSet=YychatServer.hmSocket.keySet();
					Iterator<User> it=onlineFriendSet.iterator();
					while(it.hasNext()) {
						User recevier=it.next();
						mess.setRecevier(recevier.getUserName());
						Socket rs=(Socket)findSocketByUserName(YychatServer.hmSocket,recevier.getUserName());
						sendMessage(rs,mess);
					}
				}
			}catch(ClassNotFoundException|IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void sendMessage(Socket s,Message mess) {
		ObjectOutputStream oos;
		try {
			oos=new ObjectOutputStream(s.getOutputStream());
			oos.writeObject(mess);
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	public Socket findSocketByUserName(HashMap<User, Socket> hashMap, String userName) {
        // 获取HashMap的entrySet
        Set<Map.Entry<User, Socket>> entrySet = hashMap.entrySet();
        
        // 遍历HashMap的entrySet
        for (Map.Entry<User, Socket> entry : entrySet) {
            User user = entry.getKey(); // 获取键
            Socket socket = entry.getValue(); // 获取值

            // 检查当前User对象的用户名是否与目标用户名匹配
            if (user.getUserName().equals(userName)) {
                // 找到了匹配的User对象，返回对应的Socket对象
                return socket;
            }
        }
        // 没有找到匹配的User对象，返回null
        System.out.println("没有找到Socket");
        return null;
    }
}
