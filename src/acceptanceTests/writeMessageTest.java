package acceptanceTests;

import junit.framework.TestCase;

import org.junit.Test;

import forumSystemCore.*;
import user.*;

public class writeMessageTest extends TestCase {

	protected  ForumSystem sys = new ForumSystem();
	
	
public writeMessageTest(){
	super();
	}
	
@Test
public void testWriteMessage(){
	User admin = this.sys.startSystem("halevm@post.aliza.com","halevm","katriel","halev em");

	String forum=this.sys.createForum("newforum",admin);
	User newuser = this.sys.signup("miko@m.com","yaquir","york","agudayev",forum);
	String subForumId=this.sys.createSubForum(admin,newuser,"flowers",forum);
	
	assertTrue(this.sys.createMessage(forum,subForumId,newuser,"new title","hello all!!") != null);
	assertFalse(this.sys.createMessage(forum,subForumId,newuser,"","") != null);

	
}



}