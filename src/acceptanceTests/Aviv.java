package acceptanceTests;

import junit.framework.TestCase;

import org.junit.Test;

import forumSystemCore.*;
import user.*;

public class Aviv extends TestCase {
	protected  ForumSystem sys = new ForumSystem();
	
	public Aviv(){
		super();
	}
	
	//cheching forum creation
	@Test
	public void testForumCreation(){
		
		User admin=this.sys.startSystem("halevm@post.aliza.com","halevm","katriel","hi el");
		
		String forum=this.sys.createForum("newforum",admin);
		
		User newuser = this.sys.signup("miko@m.com","yaquir","york","agudayev",forum);//user reg

		Forum f = sys.getForum(forum);
		String sfId = f.createSubForum(admin, admin, "hiii");
		SubForum sf = f.getSubForumById(sfId);
		String adminMsg = sf.createMessage(admin, "message from admin", "hihihi admin");
		sf.createMessage(newuser, "message from member", "hihihi member");
		Message adminMsgObject = sf.getMessageById(adminMsg);
		adminMsgObject.addReply(newuser, "comment!", "comment content");
		
	
	}
	

}
