package acceptanceTests;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import server.protocol.EchoProtocol;
import server.reactor.Reactor;
import user.User;
import forumSystemCore.ForumSystem;
import junit.framework.TestCase;

public class overloadTest extends TestCase {
	private static ForumSystem forumSystem;
	private static EchoProtocol echo;
	private static EchoProtocol echo2;
	private static User admin;
	private static String forumID, forumID2, subForumId;
	private static User user1;
	private static Thread thread;
	
	private static int X = 100; 
	
	public overloadTest() throws SecurityException, IOException{
		super();
		int port =1234 ;
		int poolSize =10; 
		//init forum sys 
		forumSystem = new ForumSystem();
		admin= forumSystem.startSystem("halevm@em.walla.com", "firstname", "admin", "1234");
		forumID = forumSystem.createForum("name", admin);
		forumID2 = forumSystem.createForum("two", admin);
		user1 = forumSystem.signup("mami@walla.com", "Mamuta Cohen", "Mamutit", "1234", forumID);
		subForumId=forumSystem.createSubForum(admin,user1,"flowers",forumID);
		
		//init reactor			
		Reactor reactor = Reactor.startEchoServer(port, poolSize, forumSystem);
		thread = new Thread(reactor);
		echo = new EchoProtocol(admin, forumSystem);
		echo2 = new EchoProtocol(user1, forumSystem);
	}
	
	@Test
	public void testMessages(){
		User u, u2;
		for(int i=0;i<=X;i++){
			String mail = "lala" + Integer.toString(i) + "@aliza.com";	
			String username = "katriel"+Integer.toString(i) +""; 
			u = forumSystem.signup(mail ,"Kat", username,"12345", forumID);
			String title = "hi"+Integer.toString(i)+"";
			assertNotNull(forumSystem.createMessage(forumID, subForumId, u, title, "wasup?"));
		}
	}

}
