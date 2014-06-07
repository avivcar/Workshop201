package acceptanceTests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

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
	
	public initReactorTest() {
	}

	@Before
	public void init() throws InterruptedException {
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

		Thread thread = new Thread(reactor);
		thread.start();
		thread.join();
		
		echo = new EchoProtocol(admin, forumSystem);
		echo2 = new EchoProtocol(user1, forumSystem);
	}
	
	@Test
	public void testAdmin() {
		assertEquals("ERR_NOT_ENOUGH_PARAMETERS",echo.processMessage(Constants.ISADMIN +""));
		assertEquals("SUCC_TRUE",echo.processMessage(Constants.ISADMIN + "^"+forumID).toUpperCase());
		assertEquals("SUCC_FALSE",echo2.processMessage(Constants.ISADMIN + "^"+forumID).toUpperCase()); //user1 is not admin
	}
	@Test
	public void testSignUp() {
		assertEquals("ERR_NOT_ENOUGH_PARAMETERS",echo.processMessage(Constants.SIGNUP +"^"+forumID)); //not good
		assertEquals("SUCC_SIGNUP",echo.processMessage(Constants.SIGNUP +"^"+"lala@mail.com^Lolit^Lolit12^12345^"+forumID)); //good
		assertEquals("ERR_SIGNUP",echo.processMessage(Constants.SIGNUP +"^"+"^Lolit^Lolit12^12345^"+forumID));
		assertEquals("ERR_SIGNUP",echo.processMessage(Constants.SIGNUP +"^"+"lala@mail.com^Lolit^Lolit12^^"+forumID));
		assertEquals("ERR_SIGNUP",echo.processMessage(Constants.SIGNUP +"^"+"lala@mail.com^Lolit^Lolit12^12345^"));
		assertEquals("ERR_SIGNUP",echo.processMessage(Constants.SIGNUP +"^"+"lala@mail.com^Lolit^Lolit12^12345^"+forumID)); //double reg
	}
	@Test
	public void testMember(){
		assertEquals("SUCC_TRUE",echo.processMessage(Constants.ISMEMBER +"^"+forumID));
		assertEquals("SUCC_TRUE",echo2.processMessage(Constants.ISMEMBER +"^"+forumID));
		assertEquals("SUCC_FALSE",echo2.processMessage(Constants.ISMEMBER +"^"+forumID2)); //not member
		assertEquals("ERR_NOT_ENOUGH_PARAMETERS",echo.processMessage(Constants.ISMEMBER +""));
	}
	@Test
	public void testCreateForum(){
		assertEquals("ERR_CANNOT_CREATE",echo.processMessage(Constants.CREATE_FORUM+"^name^")); //same name exists
		assertNotEquals("ERR_CANNOT_CREATE",echo.processMessage(Constants.CREATE_FORUM+"^diffrent^"));
		assertEquals("ERR_NOT_ENOUGH_PARAMETERS",echo.processMessage(Constants.CREATE_FORUM+"^"));
		
	}

}
