package acceptanceTests;

import junit.framework.TestCase;

import org.junit.Test;

import forumSystemCore.*;
import user.*;

public class RegLoginLogoutTest extends TestCase{

	protected  ForumSystem sys = new ForumSystem();
	
	public RegLoginLogoutTest(){
		super();
	}
	
	
	
	@Test

	public void testReg(){
		
	User admin = this.sys.startSystem("halevm@post.aliza.com","halevm","katriel","halev em");
	String forum=this.sys.createForum("newforum",admin);
	User newuser = this.sys.signup("miko@m.com","yaquir","york","agudayev",forum);//user reg
	
	//right registration
	assertTrue(newuser.getName().equals("yaquir"));
	assertTrue(newuser.getUsername().equals("york"));
	assertTrue(newuser.getPassword().equals("agudayev"));
	assertTrue(newuser.getMail().equals("miko@m.com"));
	
	//user is in forum
	assertTrue(this.sys.isMember(forum,newuser));
	
	//wrong input
	assertNull(this.sys.signup("miko@m.com","yaquir","york","",forum));
	assertNull(this.sys.signup("miko@m.com","yaquir","","1245325",forum));
	assertNull(this.sys.signup("miko@m.com","","york","12345",forum));
	assertNull(this.sys.signup("","yaquir","york","1233456789",forum));
	assertNull(this.sys.signup("miko.com","yaquir","york","123455678",forum));//wrong mail

	
	}
	
	
	@Test
	public void testLogin(){
		
	User admin = this.sys.startSystem("halevm@post.aliza.com","miko","halevm","katriel");
	String forum=this.sys.createForum("newforum",admin);
	User newuser = this.sys.signup("miko@m.com","yaquir","york","agudayev",forum);//user reg
		
	assertTrue(this.sys.login("york","agudayev",forum)==newuser);//currect input
	assertNull(this.sys.login("york","geva",forum));// wrong input
	assertNull(this.sys.login("tuki","agudayev",forum));// wrong input

			
	}
	
	
	
	

	
}
