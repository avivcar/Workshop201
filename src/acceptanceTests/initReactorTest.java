
/**
 * Initialize Reactor Test
 * 
 * DESCRIPTION:
 * -------------------------------------------------------------------------------------------------------
 * Tests the reactors connection abilities. goes over varius msg types, sends them to the server, and makes sure
 * the response is correct. 
 */

package acceptanceTests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import forumSystemCore.ForumSystem;
import server.protocol.EchoProtocol;
import server.reactor.Reactor;
import user.User;
import server.protocol.*;

public class initReactorTest{
	private static ForumSystem forumSystem;
	private static EchoProtocol echo;
	private static EchoProtocol echo2;
	private static User admin;
	private static String forumID, forumID2;
	private static User user1;
	private static Thread thread;
	
	public initReactorTest() throws InterruptedException, SecurityException, IOException {
		super();
		sql.Query.truncateDB();
		int port =1234 ;
		int poolSize =10; 
		//init forum sys 
		forumSystem = new ForumSystem();
		admin= forumSystem.startSystem("halevm@em.walla.com", "firstname", "admin", "1234");
		forumID = forumSystem.createForum("name", admin);
		forumID2 = forumSystem.createForum("two", admin);
		user1 = forumSystem.signup("mami@walla.com", "Mamuta Cohen", "Mamutit", "1234", forumID);
		
		//init reactor			
		Reactor reactor = Reactor.startEchoServer(port, poolSize, forumSystem);

		thread = new Thread(reactor);


		echo = new EchoProtocol(admin, forumSystem);
		echo2 = new EchoProtocol(user1, forumSystem);
	}
	
	@Test
	public void testAdmin() throws InterruptedException {

		thread.start();
		assertEquals("ERR_PARAM",echo.processMessage(Constants.ISADMIN +""));
		assertEquals("SUCC_TRUE",echo.processMessage(Constants.ISADMIN + "^"+forumID).toUpperCase());
		assertEquals("SUCC_FALSE",echo2.processMessage(Constants.ISADMIN + "^"+forumID).toUpperCase()); //user1 is not admin
		thread.join();
		
	}
	@Test
	public void testSignUp() throws InterruptedException {
		assertEquals("SIGNUP^ERR_PARAM",echo.processMessage(Constants.SIGNUP +"^"+forumID)); //not good
		assertEquals("SIGNUP^SUCC_^true",echo.processMessage(Constants.SIGNUP +"^"+"lala@mail.com^Lolit^Lolit12^12345^"+forumID)); //good
		assertEquals("SIGNUP^SUCC_^false",echo.processMessage(Constants.SIGNUP +"^"+"^Lolit^Lolit12^12345^"+forumID));
		assertEquals("SIGNUP^SUCC_^false",echo.processMessage(Constants.SIGNUP +"^"+"lala@mail.com^Lolit^Lolit12^^"+forumID));
	}
	@Test
	public void testMember() throws InterruptedException{
		assertEquals("SUCC_true",echo.processMessage(Constants.ISMEMBER +"^"+forumID));
		assertEquals("SUCC_true",echo2.processMessage(Constants.ISMEMBER +"^"+forumID));
		assertEquals("SUCC_false",echo2.processMessage(Constants.ISMEMBER +"^"+forumID2)); //not member
		assertEquals("ERR_PARAM",echo.processMessage(Constants.ISMEMBER +""));
	}
	
	@Test
	public void testCreateForum() throws InterruptedException{
		assertEquals("ADDFORUM^SUCC_^false",echo.processMessage(Constants.ADDFORUM+"^name^")); //same name exists
		assertNotEquals("ADDFORUM^SUCC_^false",echo.processMessage(Constants.ADDFORUM+"^diffrent^"));
		assertEquals("ERR_PARAM",echo.processMessage(Constants.ADDFORUM+"^"));
	}

}
