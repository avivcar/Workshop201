package user;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import utility.*;

public class testUser {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testChangeDetails() {
		User user = new User("halevm@mi.com","name","username","pass",new Rank(""));
        user.changeDetails("new@new.com","hi","new","newnew");
        assertTrue(user.getMail().equals("new@new.com"));
        assertTrue(user.getName().equals("hi"));
        assertTrue(user.getUsername().equals("new"));
        assertTrue(user.getPassword().equals("newnew"));
	}
	@Test
	public void testFriends(){
		User user = new User("he77m@mi.com","name","username","pass",new Rank(""));
		User slamauns = new User("hvm@mi.com","name2","halevm","pass",new Rank(""));
		user.sendFriendRequest(slamauns);
		slamauns.approveFriend(user);
		assertTrue(slamauns.isFriend(user));
		assertTrue(user.isFriend(slamauns));
		
	}

}
