package acceptanceTests;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import user.User;
import forumSystemCore.Forum;
import forumSystemCore.ForumSystem;
import junit.framework.TestCase;

public class DataBaseTest extends TestCase {
	private static ForumSystem sys = new ForumSystem();
	private static User admin;
	private static User u1;
	private static String fId;
	private static String sfId;
	private static String sfId2;
	private static Forum forum;
	private boolean initialized = false; 
	
	public DataBaseTest() {
		super();
	}
	
	
	/**
	* @throws java.lang.Exception
	*/
	@BeforeClass
	public static void init() {
		System.out.println("*****************k***************");
		admin = sys.startSystem("katrina@walla.com", "Katrina Tros", "Katkat", "ass1234");
		fId = sys.createForum("testers4life", admin);
		forum = sys.getForum(fId);
		u1 = sys.signup("halevav@post.aliza.com","halevav","katriel","halev av", fId);
		sfId = sys.createSubForum(admin, u1, "loozers", fId);
		sfId2 = sys.createSubForum(admin, admin, "eggs", fId);
		sys = new ForumSystem();
	}

	
	@Test
	public void testAdminSaved() {
		if (initialized == false) { initialized = true; init(); }
		assertTrue(sys.isAdmin(fId, admin));
		assertFalse(sys.isAdmin(fId, u1));	
	}
	@Test
	public void testForumSaved() {
		if (initialized == false) { initialized = true; init(); }
		assertNull(sys.createForum("testers4life", admin));
		assertEquals(forum, sys.getForum(fId));
		assertTrue(sys.existForum(fId));
	}
	@Test
	public void testSubForumSaved() {
		if (initialized == false) { initialized = true; init(); }
		assertTrue(sys.existSubForum(fId, sfId));
		assertTrue(sys.existSubForum(fId, sfId2));
		assertFalse(sys.existSubForum(fId, "000"));
		assertTrue(sys.existSubForum("000", sfId2));
		assertNull(sys.createSubForum(admin, admin, "loozers", fId));
	}
	@Test
	public void testSavedMembers() {
		if (initialized == false) { initialized = true; init(); }
		assertTrue(sys.isMember(fId, u1));
		assertTrue(sys.isMember(fId, admin));
	}
	@Test
	public void testMembersLoginData() {
		if (initialized == false) { initialized = true; init(); }
		assertNull(sys.login("katriel", "hale", fId)); // wrong password
		assertEquals(u1, sys.login("katriel", "halev av", fId)) ; //correct	
	}



}
