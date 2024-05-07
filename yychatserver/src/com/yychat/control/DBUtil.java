package com.yychat.control;

import java.sql.*;
import com.yychat.model.*;
import java.util.Date;

public class DBUtil {
	public static String db_url="jdbc:mysql://127.0.0.1:3306/yychat2022s?useUnicode=true&characterEncoding=utf-8";
	public static String db_username="root";
	public static String db_password="";
	public static Connection conn=getConnection();
	
	public static void saveMessage(Message mess) {
		String message_insertInto_str="insert into message(sender,receiver,content,sendtime) values(?,?,?,?)";
		PreparedStatement psmt;
		try {
			psmt=conn.prepareStatement(message_insertInto_str);
        	psmt.setString(1, mess.getSender());
         	psmt.setString(2, mess.getRecevier());
         	psmt.setString(3, mess.getContent());
         	Date sendTime=mess.getSendTime();
         	psmt.setTimestamp(4, new java.sql.Timestamp(sendTime.getTime()));
         	psmt.execute();
		}catch(SQLException e) {
			e.printStackTrace();
		}
	}
	public static int insertIntoFriend(int senderID,int newFriendID,int friendType) {
		int count=0;
		String userrelation_insertInto_str="insert into userrelation(masteruserid,slaveuserid,relation) values(?,?,?)";
		PreparedStatement psmt;
		try {
			psmt=conn.prepareStatement(userrelation_insertInto_str);
        	psmt.setInt(1, senderID);
         	psmt.setInt(2, newFriendID);
         	psmt.setInt(3, friendType);
        	count=psmt.executeUpdate();
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return count;
	}
	public static boolean seekFriend(int senderID,int newFriendID,int friendType) {
		boolean seekSuccess=false;
		String userrelation_query_str="select * from userrelation where masteruserid=? and slaveuserid=? and relation=?";
		PreparedStatement psmt;
		try {
			psmt=conn.prepareStatement(userrelation_query_str);
			psmt.setInt(1, senderID);
			psmt.setInt(2, newFriendID);
			psmt.setInt(3, friendType);
			ResultSet rs=psmt.executeQuery();
			seekSuccess=rs.next();
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return seekSuccess;
	}
	public static String seekAllFriend(int userID,int FriendType) {
		String allFriend="";
		String userrelation_query_str="select slaveuser from userrelation where masteruserID=? and relation=?";
		PreparedStatement psmt;
		try {
			psmt=conn.prepareStatement(userrelation_query_str);
			psmt.setInt(1, userID);
			psmt.setInt(2, FriendType);
			ResultSet rs=psmt.executeQuery();
			while(rs.next()) 
				allFriend=allFriend+" "+rs.getString(1);
			System.out.println(userID+"全部好友"+allFriend);
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return allFriend;
	}
	public static int insertIntoUser(User user) {
		int count=0;
		String user_insert_into_str="insert into user(username,password) values(?,?)";
		PreparedStatement psmt;
		try {
			psmt=conn.prepareStatement(user_insert_into_str);
        	psmt.setString(1, user.getUserName());
         	psmt.setString(2, user.getPassword());
        	count=psmt.executeUpdate();
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return count;
	}
	
	public static Connection getConnection() {
		Connection conn=null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn=DriverManager.getConnection(db_url,db_username,db_password);
		}catch(ClassNotFoundException|SQLException e){
			e.printStackTrace();
		}
		return conn;
	}
	public static boolean loginValidate(User user) {
		boolean loginSuccess=false;
		String user_query_str="select * from user where id=? and password=?";
		PreparedStatement psmt;
		try {
			psmt=conn.prepareStatement(user_query_str);
        	psmt.setInt(1, user.getUserID());
        	psmt.setString(2, user.getPassword());
        	ResultSet rs=psmt.executeQuery();
        	loginSuccess=rs.next();
		}catch(SQLException e){
			e.printStackTrace();
		}
		return loginSuccess;
	}
	public static boolean seekUser(User user) {
		boolean seekSuccess=false;
		String user_query_str="select * from user where id=? and username=?";
		PreparedStatement psmt;
		try {
			psmt=conn.prepareStatement(user_query_str);
        	psmt.setInt(1, user.getUserID());
        	psmt.setString(2,user.getUserName());
        	ResultSet rs=psmt.executeQuery();;
        	seekSuccess=rs.next();
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return seekSuccess;
	}
	public static User setUserInfo(int userID) {
		User user=new User();
		user.setUserID(userID);
		user.setUserType(UserType.USER_LOGIN_VALIDATE);
		PreparedStatement psmt;
		try {
			psmt=conn.prepareStatement("SELECT * FROM user WHERE id = ?;");
			psmt.setInt(1, userID);
			ResultSet rs=psmt.executeQuery();
			if (rs.next()) {
			    user.setUserName(rs.getString("username"));
			    user.setPassword(rs.getString("password"));
			    user.setEmail(rs.getString("email"));
			    user.setTelNumber(rs.getString("telnumber"));
			    System.out.println(user.toString());
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return user;
	}
	public static User setUserInfo(String userName) {
		User user=new User();
		user.setUserName(userName);
		user.setUserType(UserType.USER_LOGIN_VALIDATE);
		PreparedStatement psmt;
		try {
			psmt=conn.prepareStatement("SELECT * FROM user WHERE username = ?;");
			psmt.setString(1, userName);
			ResultSet rs=psmt.executeQuery();
			if (rs.next()) {
			    user.setUserID(rs.getInt("id"));
			    user.setPassword(rs.getString("password"));
			    user.setEmail(rs.getString("email"));
			    user.setTelNumber(rs.getString("telnumber"));
			    System.out.println(user.toString());
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return user;
	}
}
