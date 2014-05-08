package forumSystemCore;
import junit.framework.TestCase;

import org.junit.Test;

import utility.*;
import user.*;

public class testForum extends TestCase {
	User myUser = new User("hi", "hi", "hi", "hi", new Rank("gever"));
	protected Forum forum = new Forum("The kings", myUser);

	
	//checking initialization 
	@Test
	public void testForumBasics(){
		assertEquals(this.forum.getName(), "The kings");
		assertEquals(this.forum.getId(), "1");
		assertNull(this.forum.signup("kakadu", "matan", "kdk", ""));
	}
	
	@Test
	public void testForumAdmins(){
		assertTrue(this.forum.isAdmin(myUser));
		assertFalse(this.forum.removeAdmin(myUser, myUser));
	}
	
	@Test
	public void testForum(){
		assertEquals(this.forum.getName(), "The kings");
		assertNull(this.forum.createSubForum(myUser, myUser, "hihi"));
	}
	
	

}
