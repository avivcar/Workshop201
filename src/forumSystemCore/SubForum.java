package forumSystemCore;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import utility.*;
import user.*;

public class SubForum {
	
	private String subject;
	private List<User> moderators;
	private List<Complaint> complaints;
	private List<Message> messages;
	private List<Suspended> suspendedUsers;
	private String id;
	private static int NEXT_ID = 1;
	
	
	public SubForum(String subject, User admin){
		this.subject = subject;
		moderators = new ArrayList<User>();
		complaints = new ArrayList<Complaint>();
		messages = new ArrayList<Message>();
		suspendedUsers = new ArrayList<Suspended>();
		
		this.id = String.valueOf(NEXT_ID);
		NEXT_ID++;
		save();
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
	public boolean addModerator(User mod){
		if(isModerator(mod)) return false;
		this.moderators.add(mod);
		save();
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
		save();
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
		return true;
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
		 Message m = new Message(user, title, content);
		 this.messages.add(m);
		 save();
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
		Complaint com = new Complaint(complainer, complainee, complaint, new Date());
		this.complaints.add(com);
		save();
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
		save();
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
	
	public boolean existComplaint(int id) {
		for (int i=0; i < complaints.size(); i++) {
			if (complaints.get(i).getId() == id) return true;
		}
		return false;
	}
	
	public Message getMessageById(String id) {
		for (int i=0; i<messages.size(); i++) if (id.equals(messages.get(i).getId())) return messages.get(i);
		return null;
	}
	
}

	