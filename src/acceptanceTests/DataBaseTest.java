
/**
 * DataBase Test
 * 
 * DESCRIPTION:
 * -------------------------------------------------------------------------------------------------------
 * This test initializes the Database, then inserts data to forum system, then cleans data and recovers it,
 * then asserts data exists.
 */

package acceptanceTests;

import java.sql.SQLException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import user.User;
import forumSystemCore.Forum;
import forumSystemCore.ForumSystem;
import forumSystemCore.Message;
import junit.framework.TestCase;

public class DataBaseTest extends TestCase {
	private static ForumSystem sys;
	private static User admin;
	private static User u1;
	private static String fId;
	private static String sfId;
	private static String sfId2;
	private static String msgId;
	private static Forum forum;
	private boolean initialized = false; 
	
	
	public DataBaseTest() throws ClassNotFoundException, SQLException {
		super();
		System.out.println("Run DataBaseTest **********************");
		sql.Query.truncateDB();
		sys = new ForumSystem();
		admin = sys.startSystem("katrina@walla.com", "Katrina Tros", "Katkat", "ass1234");
		fId = sys.createForum("testers4life", admin);
		forum = sys.getForum(fId);
		u1 = sys.signup("halevav@post.aliza.com","halevav","katriel","halevav", fId);
		sys.login("katriel","halevav",fId,  u1.getMailCode());
		sfId = sys.createSubForum(admin, admin, "loozers", fId);
		sfId2 = sys.createSubForum(admin, admin, "eggs", fId);
		msgId = sys.getSubForumById(sfId).createMessage(u1, "title", "content");
		sys = new ForumSystem();
		sql.Query.load(sys);
	}

	
	@Test
	public void testDb() {
		// forum saved
		assertTrue(sys.getForum(fId) != null);
		// subforums saved
		assertTrue(sys.getSubForums(sfId) != null);
		assertTrue(sys.getSubForums(sfId2) != null);
		// message saved
		Message msg = sys.getMessageById(msgId);
		assertTrue(msg.getTitle().equals("title"));
		assertTrue(msg.getContent().equals("content"));
		// user is saved
		assertNull(sys.login("katriel", "hale", fId, ""));
		assertTrue(sys.login("katriel", "halevav", fId, "") != null);	
	}

}
