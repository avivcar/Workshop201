package forumSystemCore;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import utility.*;
import user.*;

public class Forum {
	private String name;
	private Policy policy;
	private ArrayList<User> administrators;
	private ArrayList<User> members;
	private ArrayList<SubForum> subForums;
	private ArrayList<Rank> ranks;
	private String id;
	
	//constructor
	public Forum(String name, User admin){
		setId();
		this.name = name; 
		this.policy = new Policy();
		this.administrators = new ArrayList<User>();
		this.members = new ArrayList<User>();
		this.subForums = new ArrayList<SubForum>();
		this.ranks = new ArrayList<Rank>();
		administrators.add(admin);
		save();
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
					members.get(i).getPassword().equals(password)) return members.get(i);
		return null;
	}
	
	public String createSubForum(User invoker ,User moderator, String subForumName) {
		if (!invoker.hasPermission(Permissions.CREATE_SUB_FORUM) || !TextVerifier.verifyName(subForumName, new Policy()) || moderator == null) return null;
		
		SubForum newSF = new SubForum(subForumName, moderator);
		subForums.add(newSF);
		save();
		return newSF.getId();
	}
	
	public boolean deleteSubForum(User invoker, String subForumId) {
		if (!invoker.hasPermission(Permissions.DELETE_SUB_FORUM)) return false;
		boolean found = false; 
		for (int i=0; i<subForums.size() && !found; i++) {
			if (subForums.get(i).getId().equals(subForumId)) {
				found = true;
				subForums.remove(i);
				save();
			}
		}
		return found; 
	}
	
	public boolean addAdmin(User invoker, User adminToAdd) {
		if (!invoker.hasPermission(Permissions.ADD_ADMIN)) return false;
		administrators.add(adminToAdd);
		save();
		return true;
	}
	
	public boolean removeAdmin(User invoker, User adminToRemove) {
		if (!invoker.hasPermission(Permissions.REMOVE_ADMIN)) return false;
		boolean found = false; 
		for (int i=0; i<administrators.size() && !found; i++) {
			if (administrators.get(i) == adminToRemove) {
				found = true;
				administrators.remove(i);
				save();
			}
		}
		return true;		
	}
	
	public boolean addModerator(String subForumId, User invoker, User moderator) {
		if (!invoker.hasPermission(Permissions.ADD_MODERATOR)) return false;
		getSubForumById(subForumId).addModerator(moderator);
		save();
		return true; 
	}
	
	public boolean removeModerator(String subForumId, User invoker, User moderator) {
		if (!invoker.hasPermission(Permissions.REMOVE_MODERATOR)) return false;
		getSubForumById(subForumId).removeModerator(moderator);
		save();
		return true; 
	}
	
	public boolean suspend(String subforumId, User admin, User toSuspend, Date until) {
		if (!isAdmin(admin)) return false;
		return getSubForumById(subforumId).suspend(toSuspend, until);
	}
	
	public User signup(String mail, String name, String username, String password) {
		if (!TextVerifier.verifyEmail(mail) || !TextVerifier.verifyName(username, policy) || !TextVerifier.verifyPassword(password, policy) || name.equals("")) return null;
		User member = new User(mail, name, username, password, Rank.member);
		this.members.add(member);
		save();
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
	private void setId() {
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
	
	public void save() {
		try {
			sql.Query.save(this);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}
