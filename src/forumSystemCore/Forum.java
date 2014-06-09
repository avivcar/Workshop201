package forumSystemCore;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Observable;

import utility.*;
import user.*;

public class Forum extends Observable{
	private String name;
	public Policy policy;
	private ArrayList<User> administrators;
	protected ArrayList<User> members;
	private ArrayList<SubForum> subForums;
	private ArrayList<Rank> ranks;
	private String id;
	
	//constructor
	public Forum(String name, User admin){
		putId();
		this.name = name; 
		this.policy = new Policy();
		this.administrators = new ArrayList<User>();
		this.members = new ArrayList<User>();
		this.subForums = new ArrayList<SubForum>();
		this.ranks = new ArrayList<Rank>();
		administrators.add(admin);
		members.add(admin);
	}
	
	public void recover(ArrayList<User> administrators, ArrayList<User> members, ArrayList<SubForum> subForums, ArrayList<Rank> ranks, String id) {
		this.administrators = administrators;
		this.members = members;
		this.subForums = subForums;
		this.ranks = ranks;
		setId(id);
	}
	
	public void setId(String id) {
		this.id = id;
		nextId = Math.max(nextId, Integer.valueOf(id) + 1);
	}
	//Getters:
	public String getName(){
		return name;
	}
	
	public String getId(){
		return id;
	}
	public ArrayList<User> getAdministrators() {return administrators;}
	public ArrayList<User> getMembers() {return members;}
	public ArrayList<SubForum> getSubForums() {return subForums;}
	public ArrayList<Rank> getRanks() {return ranks;}
	
	//Methods:

	
	public User login(String username, String password) {
		for (int i=0; i<members.size(); i++) 
			if (members.get(i).getUsername().equals(username) && 
					members.get(i).getPassword().equals(password)){
				members.get(i).checkUpdates();
				return members.get(i);
			}
		return null;
	}
	
	public String createSubForum(User invoker ,User moderator, String subForumName) {
		if (!invoker.hasPermission(Permissions.CREATE_SUB_FORUM) || !TextVerifier.verifyName(subForumName, new Policy()) || moderator == null) return null;
		
		SubForum newSF = new SubForum(subForumName, moderator, this.id);
		subForums.add(newSF);
		newSF.save();
		return newSF.getId();
	}
	
	public boolean deleteSubForum(User invoker, String subForumId) {
		if (!invoker.hasPermission(Permissions.DELETE_SUB_FORUM)) return false;
		boolean found = false; 
		for (int i=0; i<subForums.size() && !found; i++) {
			if (subForums.get(i).getId().equals(subForumId)) {
				found = true;
				sql.Query.remove(subForums.get(i));
				subForums.remove(i);
			}
		}
		return found; 
	}
	
	public boolean addAdmin(User invoker, User adminToAdd) {
		if (!invoker.hasPermission(Permissions.ADD_ADMIN)) return false;
		administrators.add(adminToAdd);
		sql.Query.saveAdmin(this.id, adminToAdd);
		return true;
	}
	
	public boolean removeAdmin(User invoker, User adminToRemove) {
		if (!invoker.hasPermission(Permissions.REMOVE_ADMIN)) return false;
		boolean found = false; 
		for (int i=0; i<administrators.size() && !found; i++) {
			if (administrators.get(i) == adminToRemove) {
				found = true;
				sql.Query.removeAdmin(this.id, administrators.get(i));
				administrators.remove(i);
			}
		}
		return true;		
	}
	
	public boolean addModerator(String subForumId, User invoker, User moderator) {
		if (!invoker.hasPermission(Permissions.ADD_MODERATOR)) return false;
		getSubForumById(subForumId).addModerator(invoker, moderator);
		return true; 
	}
	
	public boolean removeModerator(String subForumId, User invoker, User moderator) {
		if (!invoker.hasPermission(Permissions.REMOVE_MODERATOR)) return false;
		getSubForumById(subForumId).removeModerator(moderator);
		return true; 
	}
	
	public boolean suspend(String subforumId, User admin, User toSuspend, Date until) {
		if (!isAdmin(admin)) return false;
		return getSubForumById(subforumId).suspend(toSuspend, until);
	}
	
	public User signup(String mail, String name, String username, String password) {
		if (!TextVerifier.verifyEmail(mail,this) || 
			!TextVerifier.verifyName(username, policy) || 
			!TextVerifier.verifyPassword(password, policy) || 
			name.equals("")) 
			return null;
		User member = new User(mail, name, username, password, Rank.member, this.id);
		this.members.add(member);
		member.save();
		return member;
	}
	
	//AUX Methods:
	public boolean isAdmin(User user) {
		return administrators.contains(user);
	}
	
	public SubForum getSubForumById(String id) {
		for (int i=0; i<subForums.size(); i++) if (id.equals(subForums.get(i).getId())) return subForums.get(i);
		return null;
	}
	
	private static int nextId = 1;
	private void putId() {
		this.id = nextId++ + "";
	}
	public boolean isMember(User user) {
		if (this.isAdmin(user)) return true;
		for (int i = 0; i < members.size(); i++) {
			if(members.get(i)==user) return true;
		}
		return false;
	}
	public boolean existSubForum(String subForumid) {
		for (int i = 0; i < subForums.size(); i++) {
			if(subForums.get(i).getId().equals(subForumid)) return true;
		}
		return false;
	}
	
	//recieves a subforum id, and returns the total number of messages in it
	public int totalSubForumMsgNum(int subForumId, User invoker){
		if (!this.isAdmin(invoker)) return 0;
		for (int i=0; i < subForums.size(); i++) {
			if (subForums.get(i).getId() == id) return subForums.get(i).getMessages().size();
		}
		return 0;
	}
	
	public void save() {
		try {
			sql.Query.save(this);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	
	//creates a new type of rank for this forum
		//perms array contains all permissions for this rank
		//returns true on success, false on fail
		public boolean addRank(User invoker, String name, ArrayList<Permissions> perms){
			if (!invoker.hasPermission(Permissions.SET_RANKS))
				return false;
			if (perms == null || name == null)
				return false;
			for(int i=0; i<ranks.size(); i++){
				if(ranks.get(i).getName() == name)
					return false;
			}
			Rank newRank = new Rank(name,this.getId());
			newRank.setPermissions(perms);
			ranks.add(newRank);
			save();
			return true;
		}
		
		//sets the rank of the user
		public boolean setRank(User invoker, User toRank, Rank rank){
			if (!invoker.hasPermission(Permissions.SET_USER_RANK))
				return false;
			if (!toRank.getRank().equals(rank))
				toRank.setRank(rank);
			return true;
		}
		
		//type indicates type of notfication
		//newMsg - all new msgs, friendMsg - only friends msgs, ... more to come
		//checks policy of forum (to send offline users or not)
		//checks user preferences
		public boolean notifyUsers(String msg){
			if(msg == null)
				return false;
			
			int index = msg.toString().indexOf(' '); 
			String username = msg.substring(0,index);
			User invoker = getUserByName(username);
			
			for (int i=0; i<members.size(); i++){
				User receiver = members.get(i);
				int type = receiver.getNotifType();
					switch(type){
					// All
					case 0: 
						//online
						if (receiver.getConHndlr()!= null) {
							//if friends 
							if(receiver.isFriend(invoker))
								receiver.update(this, msg);
						}
						//offline & policy sends to offliners
						else if (policy.ruleActive(PolicyRules.OFFLINE_NOTIFICATIONS))
							receiver.addNotification(msg);
						
					//friends Only	
					case 1:
						//online
						if (receiver.getConHndlr()!= null) 
							receiver.update(this, msg);
						//offline & policy sends to offliners
						else if (policy.ruleActive(PolicyRules.OFFLINE_NOTIFICATIONS))
							receiver.addNotification(msg);		
					}
				}
			return true;	
		}
		
		//Matan's Additions
		//This method returns a list of all the messages of a user, if the invoker is an admin
		public ArrayList<Message> getUserMessages(String userName, String invokerString) {
			User invoker = getUserByName(invokerString);
			if (!this.isAdmin(invoker)) return null;
			ArrayList<Message> ans = new ArrayList<Message>();
			for (int i=0; i < subForums.size(); i++) { //go over all sub forums
				int numOfmsgs = subForums.get(i).getMessages().size();
				for (int j=0; j < numOfmsgs; i++) { //go over this subforum's msgs
					if (subForums.get(i).getMessages().get(j).getUser().getUsername().equals(userName)) 
						ans.add(subForums.get(i).getMessages().get(j));
				}
			}
			return ans;
		}
		
		//method that returns the user object, according to the user name
		//returns Null if the username could not be found
		public User getUserByName(String userName) {
			User ans = null;
			for (int i=0; i < members.size(); i++) 
				if (userName.equals(members.get(i).getUsername()))
					return members.get(i);
			
			for (int i=0;i<administrators.size();i++)
				if (userName.equals(administrators.get(i).getUsername()))
					 return administrators.get(i); 
			return ans;
		}
		
	}

