package forumSystemCore;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.mysql.jdbc.Messages;

import utility.*;
import user.*;

public class SubForum {
	
	private String subject;
	private List<User> moderators;
	private List<Complaint> complaints;
	private List<Message> messages;
	private List<Suspended> suspendedUsers;
	private String forumId;
	private String id;
	private static int NEXT_ID = 1;
	
	public String getForumId() {
		return forumId;
	}
	
	public SubForum(String subject, User admin, String forumId){
		this(subject, admin, forumId, String.valueOf(NEXT_ID++));
	}
	
	public SubForum(String subject, User admin, String forumId, String id){
		this.id = id;
		this.subject = subject;
		this.forumId = forumId;
		moderators = new ArrayList<User>();
		complaints = new ArrayList<Complaint>();
		messages = new ArrayList<Message>();
		suspendedUsers = new ArrayList<Suspended>();
		this.moderators.add(admin);
		sql.Query.saveModerator(this.id, admin);
	}
	public void recover(List<User> moderators, List<Complaint> complaints, List<Message> messages, List<Suspended> suspendedUsers, String id) {
		this.subject = subject;
		this.moderators = moderators;
		this.complaints = complaints;
		this.messages = messages;
		this.suspendedUsers = suspendedUsers;
		NEXT_ID = Math.max(NEXT_ID, Integer.valueOf(id) + 1);
	}
	public void setId(String id) {
		this.id = id;
		NEXT_ID = Math.max(NEXT_ID, Integer.valueOf(id) + 1);
	}
	public String getId() {return this.id;}
	public String getSubject() {return subject;}
	public List<User> getModerators() {return moderators;}
	public List<Complaint> getComplaints() {return complaints;}
	public List<Message> getMessages() {return messages;}
	public List<Suspended> getSuspendedUsers() {return suspendedUsers;}
	
	/**
	 * upgrade user to moderator
	 * @param mod
	 * @return
	 */
	
	
	
	public boolean addModerator(User invoker, User mod){
		if(!invoker.hasPermission(Permissions.ADD_MODERATOR))
			return false;
		if(isModerator(mod)) return false;
		this.moderators.add(mod);
		sql.Query.saveModerator(this.id, mod);
		return true;
	}
	/**
	 * user will no longer be a modarator
	 * @param user
	 * @return
	 */
	public boolean removeModerator(User user){
		if (moderators.size() <= 1) return false;
		moderators.remove(user);
		sql.Query.removeModerator(this.id, user);
		return true;
	}
	/**
	 * chack if the user is a moderator
	 * @param user
	 * @return
	 */
	public boolean isModerator(User user){
		for (int i = 0; i < moderators.size(); i++) {
			if(moderators.get(i) == user) return true;
		}
		return false;
	}
	/**
	 * creating a new message adding it to the subforum
	 * @param user
	 * @param title
	 * @param content
	 * @return
	 */

	public String createMessage(User user, String title, String content){
		//input check needed
		if (!(title.equals("")) || !(content.equals(""))){
		 Message m = new Message(user, title, content, this.id, null);
		 this.messages.add(m);
		 m.save();

		 return m.getId();
		}
		return null;
	}
	
	/**
	 * adding a new complaint in this subforum
	 * @param complainer
	 * @param complainee
	 * @param complaint
	 * @return
	 */
	public Complaint complain(User complainer, User complainee, String complaint){
		if(!this.getModerators().contains(complainee))
			return null;
		Complaint com = new Complaint(complainer, complainee, complaint, new Date(), this.id);
		this.complaints.add(com);
		com.save();
		return com;
	}
	
	/**
	 * suspend a user from a rank
	 * @param toSuspend
	 * @param until
	 * @return
	 */
	public boolean suspend(User toSuspend, Date until){
		if(isSuspended(toSuspend)) return false; 
		Suspended sus = new Suspended(toSuspend, until);
		this.suspendedUsers.add(sus);
		sql.Query.saveSuspended(this.id, toSuspend, until.getTime() + "");
		return true;
	}
	
	/**
	 * check to see if a certain user is suspended
	 * @param user
	 * @return
	 */
	public boolean isSuspended(User user){
		for (int i = 0; i < suspendedUsers.size(); i++) {
			if(suspendedUsers.get(i).getUser() == user) return true;
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
	
	public boolean existMessage(String id) {
		for (int i=0; i < messages.size(); i++) {
			if (messages.get(i).getId() == id) return true;
		}
		return false;
	}
	
	public boolean existComplaint(String id) {
		for (int i=0; i < complaints.size(); i++) {
			if (complaints.get(i).getId().equals(id)) return true;
		}
		return false;
	}
	
	public Message getMessageById(String id) {
		Message m=null;
		for (int i=0; i<messages.size(); i++){
			if (id.equals(messages.get(i).getId())) return messages.get(i);
			else {
			  m = 	messages.get(i).getReplyById(id);
			 if (m!=null) return m;
				
			}
		}
		return null;
	}


	public String getName() {
		return this.subject;
	}
	/**
	 * message will be deleted from subforum
	 * @param user
	 * @return true on success, else false
	 */
	public boolean removeMessage(User invoker, String msgId){
		Message m = getMessageById(msgId);
		if (m==null) 
			return false;
		if(!(invoker == m.getUser()))  //not the creator
			if(!invoker.hasPermission(Permissions.DELETE_MESSAGE)) //not admin\moderator
				return false;
		messages.remove(m);
		sql.Query.remove(m);
		return true;
	}

	//TODO SQL Query! - insert permission to delete!!!
	public boolean deleteMessage(Message msg,User invoker) {
		if(msg.getUser()!=invoker){
			invoker.log("trying to delete message without authorization");
			return false;
		}
		this.messages.remove(msg);
		sql.Query.remove(msg);
		return true;

		
	}
	
}

	