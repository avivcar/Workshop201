package forumSystemCore;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import user.User;

public class testMessage {
	public static ForumSystem sys;
	public static User admin;
	public static User u;
	public static Forum forum;
	public static Message m;
	
	@Before 
	public static void initialize() {
		sys = new ForumSystem();
		admin = sys.startSystem("yy2006@gmail.com", "Yakir yehuda",
										"fashizel", "123");
		m = new Message(admin, "dd", "ff");
		forum = new Forum("lolz",admin);
		u = forum.signup("motek@walla.co.il", "Matan Carmis", "mamush", "12345");
		
	}

	@Test
	public void testRemoveReply() {
		Message r = m.addReply(admin, "dd", "ff");
		assertTrue(m.removeReply(admin, r));

	}
	
	@Test
	public void testIsWriter() {
		assertTrue(m.isWriter(admin));

	}

	@Test
	//add gotNotified method in user , that gets the msg id to notify
	public void testNotifications(){
		//assretTrue(u.gotNotified(m.getId()));
		//assertFalse(admin.gotNotified(m.getId()));
	}
	
	@Test
	//need to add user in edit message method signature
	public void testEditMsg(){
		assertTrue(m.editMessage("dd", "gg!!!"));
		assertFalse(m.editMessage("", ""));
		assertFalse(m.editMessage(null, null));
	}
}
