package forumSystemCore;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Observable;

import user.*;
import utility.*;

public class Message extends Observable{
	private static int NEXT_ID=1;
	private String subforumId;
	private String msgRel;
	private Date date;
	private String content;
	private String title;
	private List<Message> replies;
	private User writer;
	//special id
	private String id;
	
	public String getMsgRel() {
		return msgRel;
	}
	public String getSubforumId() {
		return subforumId;
	}
	
	public Message(User user, String title, String content, String subforumId, String msgRel) {
		this.subforumId = subforumId;
		this.msgRel = msgRel;
		this.writer=user;
		this.title = title;
		this.content = content;
		this.date = new Date();
		this.replies = new ArrayList<Message>();
		
		this.id = String.valueOf(NEXT_ID);
		NEXT_ID++;
	}
	
	public void recover(List<Message> replies, Date date, String id) {
		this.replies = replies;
		this.date = date;
		setId(id);
	}
	
	public void setId(String id) {
		this.id = id;
		NEXT_ID = Math.max(NEXT_ID, Integer.valueOf(id) + 1);
	}
	public String getId(){return this.id;}
	public String getDate() {
		return date.getTime() + "";
	}
	public String getContent() {
		return content;
	}
	public String getTitle() {
		return title;
	}
	public User getUser() {
		return writer;
	}
	public List<Message> getReplies() {
		return this.replies;
	}

	
	/**
	 * adding a new reply to this message
	 * @param user
	 * @param title
	 * @param content
	 * @return
	 */
	public Message addReply(User user, String title, String content){
		Message m = new Message(user, title, content, null, this.id);
		replies.add(m);
		m.save();
		setChanged();
		notifyObservers(user.getName()+" replied to your discussion");
		this.addObserver(user); 
		return m;
	}
	/**
	 * remove reply to this message
	 * @param user
	 * @param message
	 * @return
	 */
	public boolean removeReply(User user, Message message){
		if(user.hasPermission(Permissions.DELETE_MESSAGE) || message.isWriter(user)){
			replies.remove(message);
			sql.Query.remove(message);
			return true;
		}
		return false;
	}
	/**
	 * return true if user is this message writer, false otherwise
	 * @param user
	 * @return
	 */
	public boolean isWriter(User user){
		return this.writer==user;
	}
	
	/**
	 * edit  the info
	 * @param title
	 * @param content
	 * @return
	 */
	public boolean editMessage(User Invoker, String title, String content){
		if(!Invoker.equals(this.getUser())) return false;
		if(title!=null) this.title =title;
		if(content!=null) this.content = content;
		save();
		setChanged();
		notifyObservers(this.getUser()+" edited the discussion you're part of");
		return true;
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
	public Message getReplyById(String id2) {
		Message m=null;
		if (this.getReplies().size()>0){
			for (int i=0;i<this.getReplies().size() && m==null;i++){
				if(this.getReplies().get(i).getId().equals(id2)) m= this.getReplies().get(i);
				else m= this.getReplies().get(i).getReplyById(id2);
			}
		}
		return m;
	}
	

}
