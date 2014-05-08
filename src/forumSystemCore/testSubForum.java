package forumSystemCore;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import utility.*;
import user.User;


public class testSubForum {
	public static ForumSystem sys;
	public static User admin;
	public static User toMod;
	public static Forum forum;
	public static SubForum sf;
	public static Message m;
	
	@Before 
	public static void initialize() {
		sys = new ForumSystem();
		admin = sys.startSystem("yy2006@gmail.com", "Yakir yehuda", "fashizel", "123");
		String forumId = sys.createForum("Games", admin);
		forum = sys.getForum(forumId);
		String sfId = forum.createSubForum(admin, admin, "Monopol");
		
		//***cahnge getSubForumById from protected to public
		sf = forum.getSubForumById(sfId);
		toMod = new User("gg@gmail.com", "matan", "atralicon", "123", Rank.member);
	}
	
	@Test
	public void testAddModerator() {
		assertTrue(sf.isModerator(admin));
		assertFalse(sf.isModerator(toMod));
		sf.addModerator(toMod);
		assertTrue(sf.isModerator(toMod));	
	}
		
	
	@Test
	public void testRemoveModerator() {	
		sf.addModerator(toMod);
		assertTrue(sf.isModerator(toMod));
		assertTrue(sf.removeModerator(toMod));
		assertFalse(sf.isModerator(toMod));
		assertFalse(sf.removeModerator(toMod)); 
	}
	
	@Test
	public void testAddMessage() {
		String messageId = "";
		assertNull(sf.createMessage(admin, "", ""));
		assertNotNull(messageId= sf.createMessage(admin, "title", "dd"));
		assertTrue(sf.existMessage(messageId));
	}
	
	@Test
	public void testAddComplaint() {
		Complaint c = new Complaint(admin,admin,"",null);
		assertFalse(sf.existComplaint(c.getId()));
		assertTrue(sf.existComplaint(sf.complain(admin, admin, "asshole").getId()));		
	}
	
	@Test
	public void testSuspend() {
		assertTrue(sf.suspend(admin, new Date(System.currentTimeMillis()+1000000000)));
		assertFalse(sf.suspend(admin, new Date(System.currentTimeMillis()+1000000000)));
		assertTrue(sf.isSuspended(admin));
				
	}

}
