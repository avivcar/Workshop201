package utility;
import java.sql.SQLException;
import java.util.Date;

import user.*;

public class Complaint {
	private static int next_cpmlnt_id=1;
	private String id; 
	private String subforumId;
	private User complainer;
	private User complainee;
	private String complaintMessage;
	private Date date;
	
	public String getSubforumId() {
		return subforumId;
	}
	
	public Complaint(User complainer,User complainee,String msg, Date date, String subforumId) {
		this.id = String.valueOf(next_cpmlnt_id);
		next_cpmlnt_id++;
		this.subforumId = subforumId;
		this.complainer=complainer;
		this.complainee=complainee;
		this.complaintMessage=msg;
		this.date=date;
	}
	public void recover(String id) {
		setId(id);
	}
	public void setId(String id) {
		this.id = id;
		next_cpmlnt_id = Math.max(next_cpmlnt_id, Integer.valueOf(id) + 1);
	}

	public String getId() {
		return id;
	}

	public User getComplainee() {
		return complainee;
	}

	public User getComplainer() {
		return complainer;
	}

	public String getComplaintMessage() {
		return complaintMessage;
	}

	public String getDate() {
		return this.date.getTime() + "";
	}

	/**
	 * the complainer can edit his complaint content and complainee
	 * @param u the complainer
	 * @param u2 the complainee
	 * @param msg 
	 * @return
	 */
	public boolean editComplaint(User u,User u2,String msg) {
		if(u==this.complainer && msg!=null){
			this.complainee=u2;
			this.complaintMessage = msg;
			save();
			return true;
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
