package forumSystemCore;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import utility.Permissions;
import user.User;
import utility.*;

import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


public class ForumSystem {
	private Logger operationLog;
	private Logger errorLog;
	public	ArrayList<Forum> forums;
	User superuser;
	
	public void recover(ArrayList<Forum> forums, User superuser) {
		this.forums = forums;
		this.superuser = superuser;
	}

	// Constructors:
	public User startSystem(String email, String name, String username,
			String password) {
		if (!TextVerifier.verifyName(username, new Policy())
				|| !TextVerifier.verifyEmail(email,null)
				|| !TextVerifier.verifyPassword(password, new Policy())
				|| name.equals(""))
			return null;
		try {
			sql.Query.initDB();
		} catch (ClassNotFoundException e2) {
			e2.printStackTrace();
		} catch (SQLException e2) {
			System.out.println(e2.getMessage());
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		superuser = new User(email, name, username, password, Rank.rankSuperUser(), null);
		try {
			sql.Query.saveSuper(superuser);
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		this.createlog();
		forums = new ArrayList<Forum>();
		try {
			sql.Query.saveSuper(superuser);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		operlog("Forum System creation");
		return superuser;
	}
	
	//log creation - called in constructor
		private void createlog() {
			this.errorLog=Logger.getLogger("ErrorLog");
			this.operationLog=Logger.getLogger("operationLog");
			
			FileHandler fh;  
			FileHandler fh2;  
		    try {  
		        // This block configure the logger with handler and formatter  
		        fh = new FileHandler(System.getProperty("user.dir")+"/errorLog.log"); 
		        fh2 = new FileHandler(System.getProperty("user.dir")+"/operationLog.log"); 	        
		        this.errorLog.addHandler(fh);
		        this.operationLog.addHandler(fh2);
		        SimpleFormatter formatter = new SimpleFormatter();
		        SimpleFormatter formatter2 = new SimpleFormatter();
		        fh.setFormatter(formatter); 
		        fh2.setFormatter(formatter2); 
		    } catch (SecurityException e) {  
		        e.printStackTrace();  
		    } catch (IOException e) {  
		        e.printStackTrace();  
		    }  
			
		}
		
	private void errorlog(String string) {
		this.errorLog.info(string);
	}
	
	private void operlog(String string) {
		this.operationLog.info(string);
	}


	public String createForum(String name, User admin) {
	//	if (!admin.hasPermission(Permissions.CREATE_FORUM) || name.equals(""))
		//	return null;

		for (int i = 0; i < forums.size(); i++) {
			if (forums.get(i).getName().equals(name))
				return null;
		}
		Forum newForum = new Forum(name, admin);
		forums.add(newForum);
		newForum.save();
		this.operlog("Forum \'"+name+"\' is created");
		return newForum.getId();
	}
	
/*
	 public static void main(String[] args) { // TODO Auto-generated method

			
	 }
*/
	
	/**
	 * check if user is admin of forum(forumId)
	 * 
	 * @param forumId
	 * @param user
	 * @return
	 */
	public boolean isAdmin(String forumId, User user) {
		for (int i = 0; i < forums.size(); i++) {
			if (forums.get(i).getId().equals(forumId))
				return forums.get(i).isAdmin(user);
		}
		return false;
	}

	/**
	 * sign up to forum(forumId) using this parameters
	 * 
	 * @param mail
	 * @param name
	 * @param username
	 * @param pass
	 * @param forumId
	 * @return
	 */
	public User signup(String mail, String name, String username, String pass,
			String forumId) {
		for (int i = 0; i < forums.size(); i++) {
			if (forums.get(i).getId().equals(forumId))
				return forums.get(i).signup(mail, name, username, pass);
		}
		errorlog("signup to forum id "+forumId);
		return null;
	}

	/**
	 * check if user is a member of forum(forumId)
	 * 
	 * @param forumId
	 * @param user
	 * @return
	 */
	public boolean isMember(String forumId, User user) {
		for (int i = 0; i < forums.size(); i++) {
			if (forums.get(i).getId().equals(forumId))
				return forums.get(i).isMember(user);
		}
		return false;

	}
	
	public String getSubForums(String forumId){
		String ans="";
		Forum forum = getForum(forumId);
		if (forum!=null){
			for(int i=0;i<forum.getSubForums().size();i++)
				ans+="^"+forum.getSubForums().get(i).getSubject()+"^"+forum.getSubForums().get(i).getId();
		}	
		return ans;
	}

	
	/**
	 * login with this parameters return the user(actual pointer) to the
	 * appropiant user;
	 * 
	 * @param username
	 * @param password
	 * @param forumId
	 * @return
	 */
	public User login(String username, String password, String forumId) {
		for (int i = 0; i < forums.size(); i++) {
			if (forums.get(i).getId().equals(forumId)){
				
				User newuser =  forums.get(i).login(username, password);
				if (newuser!=null){
					newuser.log("the user is logging in");
				}
				return newuser;
			}
		}
		errorlog("login user: "+username+" in forum id "+forumId);
		return null;
	}

	public String createSubForum(User invoker, User moderator,
			String subForumName, String forumId) {
		for (int i = 0; i < forums.size(); i++) {
			if (forums.get(i).getId().equals(forumId)){
				operlog("subforum \'"+subForumName+"\' was created in \'"+forums.get(i).getName()+"\' forum");
				return forums.get(i).createSubForum(invoker, moderator,
						subForumName);
			}
		}
		errorlog("create sub forum");
		return null;

	}

	public boolean existSubForum(String forumId, String subForumid) {
		for (int i = 0; i < forums.size(); i++) {
			if (forums.get(i).getId().equals(forumId))
				return forums.get(i).existSubForum(subForumid);
		}
		return false;
	}

	public Forum getForum(String forumId) {

		for (int i = 0; i < forums.size(); i++)
			if (forums.get(i).getId().equals(forumId))
				return forums.get(i);
		return null;
	}

	public boolean existForum(String forumId) {
		for (int i = 0; i < forums.size(); i++)
			if (forums.get(i).getId().equals(forumId))
				return true;
		return false;
	}
	
	public boolean addReply(String forumId, String subForumId,String msgId,User user, String title, String content){
		Message msg = this.getMessage(forumId, subForumId, msgId);
		if (msg==null) return false;
		user.log("user is adding a reply");
		msg.addReply(user, title, content);
		return true;
	}

	public String createMessage(String forumId, String subForumId, User user,
			String title, String content) {
		Forum forum = this.getForum(forumId);
		if (forum != null) {
			SubForum subforum = forum.getSubForumById(subForumId);
			if (subforum != null){
				user.log("user is creating a message");
				forum.notifyUsers(user.getName()+" added a new message in '"+subforum.getName()+"' ");
				operlog("new message in subforum \'"+subforum.getName()+"\' in \'"+forum.getName()+"\' forum");
				return subforum.createMessage(user, title, content);
				}
			errorlog("create message, sub-forum doesn't exist");
		}
		errorlog("create message, forum doesn't exist");
		return null;

	}
	
	public Message getMessage(String forumId,String subForumId, String msgId){
		Message msg=null;
		Forum forum=this.getForum(forumId);
		if (forum!=null){
			SubForum subforum=forum.getSubForumById(subForumId);
				if (subforum!=null)
				 msg=subforum.getMessageById(msgId);
			}
		return msg;

		}
	
	/**
	 * returns a list of users that are members in several forums (meaning - duplications of userNames among different forums)
	 * @return List<User>
	 */
	
	public List<User> getMultiForumMembers(String invokerName){
		if (!invokerName.equals(superuser.getUsername())) return null;
		List<User> ans = new ArrayList<User>();
		List<User> userList = new ArrayList<User>();
		for (int i=0; i < forums.size(); i++) { //go over all forums
			//add all members
			userList.addAll(forums.get(i).members);
		}
		//now we have a big, mashed up list of all members, with DUPLICATIONS
		User currentUser;
		int forumMembershipNumber;
		for (int i=0; i < userList.size(); i++) {
			forumMembershipNumber = 0;  //reset counter
			currentUser = userList.get(i); //set current User that is being inspected
			for (int j=0; j < userList.size(); j++) { //go over the user list, search for duplications
				if (currentUser.getUsername().equals(userList.get(i).getUsername())) 
					forumMembershipNumber++;
			}
			if (forumMembershipNumber > 1) { //check is user is in more than 1 forum, if so, add to answer
				ans.add(currentUser);
			}
		}
		return ans;
	}
	
	//get moderators
	public List<User> getModerators(String forumId, String SubforumId){
		List<User> ans = new ArrayList<User>();
		Forum forum = this.getForum(forumId);
		SubForum sub = forum.getSubForumById(SubforumId);
		ans = sub.getModerators();
		if (ans==null)
			this.errorlog("finding moderators");
		return ans;
	}
	
	//get users
	public List<User> getUsersInForum(String forumId){
		List<User> ans = new ArrayList<User>();
		Forum forum = this.getForum(forumId);
		ans = forum.getAdministrators();
		if (ans==null)
			this.errorlog("finding members");
		return ans;
	}
	
	//get admins
	public List<User> getAdminsInForum(String forumId){
		List<User> ans = new ArrayList<User>();
		Forum forum = this.getForum(forumId);
		ans = forum.getMembers();
		if (ans==null)
			this.errorlog("finding admins");
		return ans;
	}
	
	//get ranks
	public List<Rank> getRanksInForum(String forumId){
		List<Rank> ans = new ArrayList<Rank>();
		Forum forum = this.getForum(forumId);
		ans = forum.getRanks();
		if (ans==null)
			this.errorlog("finding ranks");
		return ans;
	}
	
	// removes moderator 
	public boolean removeModerator(String forumId, String subforumId, User invoker, String toRemove){
		if(!invoker.hasPermission(Permissions.REMOVE_MODERATOR)){
			errorlog("no permissions to remove moderator");
			return false;
		}
		Forum forum = this.getForum(forumId);
		SubForum sub = forum.getSubForumById(subforumId);
		User mod = forum.getUserByName(toRemove);
		return sub.removeModerator(mod);
	}
	
	//create complaint
	public boolean createComplaint(String forumId, String subforumId , User invoker, String complainee, String msg){
		Forum forum = this.getForum(forumId);
		SubForum sub = forum.getSubForumById(subforumId);
		User com = forum.getUserByName(complainee);
		if (sub.complain(invoker, com, msg) != null){
			operlog("a new complaint is added");
			return true;
		}
		else{
			errorlog("creating complaint");
			return false;
		}
	}
	
	public String getNumOfForums(String invokerName){
		if (!invokerName.equals(superuser.getUsername())) return null;
		return Integer.toString(forums.size());
	}
	
	//returns true on success , false in fail
	public boolean deleteSubForum(User invoker,String subForumId, String forumId) {
		for (int i = 0; i < forums.size(); i++) {
			if (forums.get(i).getId().equals(forumId)){
				operlog("delete subforum id "+subForumId);
				boolean ans= forums.get(i).deleteSubForum(invoker, subForumId);
				if (ans) {
					invoker.log("user has deleted subforum, subforum id is :"+subForumId);
					return true;
				}
				else return false;
			}
		}
		errorlog("delete subforum id "+subForumId);
		return false;
	}
//return true on success 
	public boolean deleteForum(User invoker, String forumId) {
		//if (!invoker.hasPermission(Permissions.DELETE_FORUM)) return false;
		for (int i = 0; i < forums.size(); i++) {
			if (forums.get(i).getId().equals(forumId)){
				operlog("delete forum id "+forumId);
				//TODO insert SQL queries
				forums.remove(i);
			}
		}
		errorlog("delete forum id "+forumId);
		return false;
	}

	//add moderator
	public boolean addModerator(String forumId, String subforumId, User invoker, String username){
		Forum forum = getForum(forumId);
		SubForum sub = forum.getSubForumById(subforumId);
		User mod = forum.getUserByName(username);
		if (mod == null){
			errorlog("adding moderator");
			return false;
		}
		if (! sub.addModerator(invoker, mod)){
			errorlog("adding moderator");
			return false;
		}
		return true;	
	}
	
	//add admin
	public boolean addAdmin(String forumId, User invoker, String toAdmin){
		Forum forum = getForum(forumId);
		User admin = forum.getUserByName(toAdmin);
		if (! forum.addAdmin(invoker, admin)){
			errorlog("adding admin");
			return false;
		}
		return true;	
	}
	
	//remove admin
	public boolean removeAdmin(String forumId, User invoker, String toRemove){
		Forum forum = getForum(forumId);
		User admin = forum.getUserByName(toRemove);
		if (! forum.removeAdmin(invoker, admin)){
			errorlog("removing admin");
			return false;
		}
		return true;
	}
	
	//set rank
	public boolean setRank(String forumId, User invoker, String username, String rank){
		Forum forum = getForum(forumId);
		User user = forum.getUserByName(username);
		Rank r = null;
		for(int i =0; i< forum.getRanks().size();i++){
			if (forum.getRanks().get(i).getName() == rank)
				r = forum.getRanks().get(i);
		}
		if (r == null){
			errorlog("setting rank");
			return false;
		}
		return forum.setRank(invoker, user, r);
	}
	
	//send friend request
	public boolean friendRequest(String forumId, User invoker, String friend){
		Forum forum = getForum(forumId);
		User toRequest = forum.getUserByName(friend);
		if(!invoker.getForumId().equals(toRequest.getForumId()))
			return false;
		if (invoker.isFriend(toRequest))
			return false;
		invoker.sendFriendRequest(toRequest);
		return true;
	}
}
