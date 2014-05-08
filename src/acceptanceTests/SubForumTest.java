package acceptanceTests;

import junit.framework.TestCase;

import org.junit.Test;

import forumSystemCore.*;
import user.*;

public class SubForumTest extends TestCase {

	protected  ForumSystem sys = new ForumSystem();
	
	public SubForumTest(){
		super();
	}
	
	
	
	@Test
	public void testSubForumCreation(){
		User admin = this.sys.startSystem("halevm@post.aliza.com","halevm","katriel","halev em");
		String forum=this.sys.createForum("newforum",admin);
		User newuser = this.sys.signup("miko@m.com","yaquir","york","agudayev",forum);//user reg
		
		//currect input
		String subForumId=this.sys.createSubForum(admin,newuser,"flowers",forum);
		assertTrue(this.sys.existSubForum(forum,subForumId));
		assertFalse(this.sys.existSubForum(forum,"slamauns"));
		
		//illigal input
		String subForumId2=this.sys.createSubForum(admin,newuser,"",forum);//empty subforum name
		assertNull(subForumId2);
		
		String subForumId3=this.sys.createSubForum(admin,null,"soccer",forum);//no moderator chosen
		assertFalse(this.sys.existSubForum(forum,subForumId3));

		
	}
	
	
	
	
	
	
	
	
	
	
	
	
}
