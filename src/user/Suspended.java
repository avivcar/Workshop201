package user;
import java.util.Date;


public class Suspended {
	private User user;
	private Date date;
	public Suspended(User user, Date until) {
		this.user = user;
		this.date = until;
	}
	public User getUser() {
		return user;
	}
	public String getDate() {
		return date.getTime() + "";
	}
}
