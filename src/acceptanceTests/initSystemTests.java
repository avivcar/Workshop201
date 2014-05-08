package acceptanceTests;

import junit.framework.TestCase;

import org.junit.Test;

import forumSystemCore.*;
import user.*;

public class initSystemTests extends TestCase {
	protected  ForumSystem sys = new ForumSystem();
	
	public initSystemTests(){
		super();
	}
	
	
	
	//checking initialization 
	@Test
	public void testinit(){
		assertTrue(this.sys.startSystem("halevm@post.aliza.com","halevm","katriel","halev em")!=null);
		assertNull(this.sys.startSystem("hpost.aliza.com","halevm","katriel",""));
		assertNull(this.sys.startSystem("halevm@post.aliza.com","halevm","","halevm em"));
		assertNull(this.sys.startSystem("halevm@post.aliza.com","","yakuni","halevm em"));
	}

	
	//cheching forum creation
	@Test
	public void testForumCreation(){
		
	User admin=this.sys.startSystem("halevm@post.aliza.com","halevm","katriel","hi el");
	
	String forum2=this.sys.createForum("newforum",admin);
	assertTrue(sys.existForum(forum2));  //forum name has been added
	assertTrue(sys.isAdmin(forum2,admin));  //admin has been added
	
	User newuser = this.sys.signup("miko@m.com","yaquir","york","agudayev",forum2);//user reg

	String forum=this.sys.createForum("newforumYakuni",newuser);
	assertFalse(sys.existForum(forum));//didnt added
	assertNull(forum);   //null forum
	
	}
	
	

}
