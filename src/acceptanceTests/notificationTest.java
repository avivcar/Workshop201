package acceptanceTests;

import org.junit.Before;
import org.junit.Test;

import user.User;
import forumSystemCore.Forum;
import forumSystemCore.ForumSystem;
import forumSystemCore.SubForum;
import junit.framework.TestCase;
import forumSystemCore.Forum;

public class notificationTest extends TestCase {
	protected static ForumSystem sys = new ForumSystem();
	private static User admin;
	private static Forum forum;
	private static User u1;
	private static User u2;
	private static SubForum sf;
	private static String fId;
	private static String sfId;
	
	
	public notificationTest(){
		super();
	}
	
	@Before
	public void init(){
		admin = sys.startSystem("mtos@walla.com", "Marina Tost", "mtost", "12345");
		fId = sys.createForum("Birds", admin);
		forum = sys.getForum(fId);
		u1 = sys.signup("halevm@post.aliza.com","halevm","katriel","halev em",fId);
		u2 = sys.signup("katrina@walla.com", "Katrina Tros", "Katkat", "ass1234" , fId);
		sfId = forum.createSubForum(admin, admin, "Parrots");
		sf = forum.getSubForumById(sfId);
	}
	
	@Test
	public void testNotifications(){
		String mId = sys.createMessage(fId, sfId, admin, "Welcome", "hi everyone");	
		//assertTrue(sys.getMessageById(mId).notify());
		//TODO
	}
}
	
	
