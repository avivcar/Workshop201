package acceptanceTests;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import user.User;
import forumSystemCore.Forum;
import forumSystemCore.ForumSystem;

@RunWith(Suite.class)
@SuiteClasses({ 
	DataBaseTest.class,
	
})
public class DBtestRun {
	private static ForumSystem sys = new ForumSystem();
	private static User admin;
	private static User u1;
	private static String fId;
	private static String sfId;
	private static String sfId2;
	private static Forum forum;
	
	@BeforeClass
	public void init() {
		System.out.println("k");
		admin = sys.startSystem("katrina@walla.com", "Katrina Tros", "Katkat", "ass1234");
		fId = sys.createForum("testers4life", admin);
		forum = sys.getForum(fId);
		u1 = sys.signup("halevav@post.aliza.com","halevav","katriel","halev av", fId);
		sfId = sys.createSubForum(admin, u1, "loozers", fId);
		sfId2 = sys.createSubForum(admin, admin, "eggs", fId);
		sys = new ForumSystem();
	}

}
