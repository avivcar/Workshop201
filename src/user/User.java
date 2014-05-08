package user;
import java.sql.SQLException;
import java.util.ArrayList;

import utility.*;
import server.reactor.*;
public class User {
	
	public User Guest = new User();
	
	//user connectionHandler
	private ConnectionHandler handler;
	
	// user mail
	private String mail;
	
	// user name
	private String name;

	// user username
	private String username;
	
	// user password
	private String password;

	// an array of user friends
	private ArrayList<User> friends = new ArrayList<User>();
	
	// an array of friend requests that the user SENT
	private ArrayList<User> pendingFriendRequests = new ArrayList<User>();
	
	// an array of friend requests that the user RECEIVED
	private ArrayList<User> friendRequests = new ArrayList<User>();
	
	// user rank
	private Rank rank;
	
	/**
	 * Constructor
	 * @param mail
	 * @param name
	 * @param username
	 * @param password
	 * @param rank
	 */
	public User(String mail, String name, String username, String password, Rank rank) {
		this.rank = rank;
		changeDetails(mail, name, username, password);
		save();
	}
	
	/**
	 * Constructor for guest
	 */
	public User() {}
	
	/**
	 * Changes user details
	 * @param mail
	 * @param name
	 * @param username
	 * @param password
	 */
	public void changeDetails(String mail, String name, String username, String password) {
		this.mail = mail;
		this.name = name;
		this.username = username;
		this.password = password;
		save();
	}
	
	/**
	 * Send friend request
	 * @param user
	 */
	public void sendFriendRequest(User user) {
		pendingFriendRequests.add(user);
		user.receiveFriendRequest(this);
		save();
	}
	
	/**
	 * Receive friend request
	 * @param user
	 */
	public void receiveFriendRequest(User user) {
		friendRequests.add(user);
		save();
	}
	
	/**
	 * Approve friend request
	 * @param user
	 */
	public void approveFriend(User user) {
		friendRequests.remove(user);
		friends.add(user);
		user.friendshipApproved(this);
		save();
	}
	
	/**
	 * My friend request has been approved
	 * @param user
	 */
	public void friendshipApproved(User user) {
		pendingFriendRequests.remove(user);
		friends.add(user);
		save();
	}
	
	/**
	 * Whether or not the user has a certain permission
	 * @param perm
	 * @return
	 */
	public boolean hasPermission(Permissions perm) {
		return rank.hasPermission(perm);
	}
	
	
	public boolean isFriend(User user){
		for (int i=0; i<friends.size();i++)
			if (friends.get(i)==user) return true;
		return false;
		
	}
	
// g & s
	
	public String getName() {
		return name;
	}
	
	public String getMail() {
		return mail;
	}
	
	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}
	
	public Rank getRank() {
		return rank;
	}
	
	public ArrayList<User> getFriends() { return friends; }
	public ArrayList<User> getPendingFriendRequests() { return pendingFriendRequests; }
	public ArrayList<User> getFriendRequests() { return friendRequests; }
	
	public void save() {
		try {
			sql.Query.save(this);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	//Connectionhandler methods
	public void addHandler(ConnectionHandler newConnection) {
		this.handler=newConnection;
	}
	
	public void sayToMe(String say) {
		 this.handler.sayToMe(say);
		
	}
	
}
