package user;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import utility.*;
import server.reactor.*;
public class User implements Observer{
	
	public static User Guest = new User();
	
	//user connectionHandler
	private ConnectionHandler handler;
	
	// user mail
	private String mail = null;
	
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
	
	private String forumId;
	
	// an array of notifications that the user received 
	private ArrayList<String> notifications = new ArrayList<String>();
	
	// type of notificatios the users would like to get
	// 0 - all new msgs , 1 - friendOnly msgs, more can be added ...
	private int notifTypes;
	
	
	public String getForumId() {
		return forumId;
	}
	
	/**
	 * Constructor
	 * @param mail
	 * @param name
	 * @param username
	 * @param password
	 * @param rank
	 */
	public User(String mail, String name, String username, String password, Rank rank, String forumId) {
		this.forumId = forumId;
		this.rank = rank;
		this.changeDetails(mail, name, username, password);
	}
	
	/**
	 * Constructor for guest
	 */
	public User() {}
	
	public void recover(ArrayList<User> friends, ArrayList<User> pendingFriendRequests, ArrayList<User> friendRequests) {
		this.friends = friends;
		this.pendingFriendRequests = pendingFriendRequests;
		this.friendRequests = friendRequests;
	}
	
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
		sql.Query.saveFriend("_pendingFriendRequests", this, user);
		user.receiveFriendRequest(this);
	}
	
	/**
	 * Receive friend request
	 * @param user
	 */
	public void receiveFriendRequest(User user) {
		friendRequests.add(user);
		sql.Query.saveFriend("_friendRequests", user, this);
	}
	
	/**
	 * Approve friend request
	 * @param user
	 */
	public void approveFriend(User user) {
		friendRequests.remove(user);
		friends.add(user);
		sql.Query.saveFriend("_friends", this, user);
		user.friendshipApproved(this);
	}
	
	/**
	 * My friend request has been approved
	 * @param user
	 */
	public void friendshipApproved(User user) {
		pendingFriendRequests.remove(user);
		friends.add(user);
		sql.Query.saveFriend("_friends", this, user);
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
 

	//getter to notification type
	public int getNotifType() {
		return notifTypes;
	}

	public ArrayList<String> getNotifications() {
		return notifications;
	}

	public void addNotification(String notification) {
		notifications.add(notification);
	}
	
	//indicates if user connected or not
	public ConnectionHandler getConHndlr(){
		return handler;
	}

	@Override
	//TODO what happens when updates
	public void update(Observable obs, Object msg) {
		if(!(handler==null)) {//user is online
			int i = msg.toString().indexOf(' '); //gets index of first space 
			//checks that the username of the change made is not mine 
			//so that users won't get their own changes notified
			if(!(msg.toString().substring(0, i) == name)){ // 
				handler.sayToMe(msg.toString());
			}
		}
		
	}

	//when user logs in the notifications sent while offline are pushed
	public void checkUpdates() {
		while(!notifications.isEmpty()){
			String msg = notifications.get(notifications.size()-1);
			notifications.remove(notifications.size()-1);
			handler.sayToMe(msg);
		}
	}
	
	
}
