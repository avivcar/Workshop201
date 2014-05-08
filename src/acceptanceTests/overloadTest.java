package acceptanceTests;

import org.junit.Before;
import org.junit.Test;

import user.User;
import forumSystemCore.Forum;
import forumSystemCore.ForumSystem;
import junit.framework.TestCase;

public class overloadTest extends TestCase {
	private static ForumSystem sys = new ForumSystem();
	private static User admin;
	private static User u1;
	private static String fId;
	private static String sfId;
	private static String sfId2;
	private static Forum forum;
	
	public overloadTest(){
		super();
	}
	
	@Before
	public void init(){
		admin = sys.startSystem("katrina@walla.com", "Katrina Tros", "Katkat", "ass1234");
		fId = sys.createForum("testers4life", admin);
		forum = sys.getForum(fId);
		u1 = sys.signup("halevav@post.aliza.com","halevav","katriel","halev av", fId);
		sfId = sys.createSubForum(admin, u1, "loozers", fId);
		sfId2 = sys.createSubForum(admin, admin, "eggs", fId);
		
	}
	
	@Test
	public void testOverload(){
		//TODO
	}


}
