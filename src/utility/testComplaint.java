package utility;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Test;

import user.*;


public class testComplaint {

	@Test
	public void testEditComplaint() {
		Rank r = new Rank("A");
		User u1 = new User("u1@yahoo.com","ben","ben123","12345",r);
		User u2 = new User("u2@yahoo.com","tom","tom400","12345",r);
		
		Complaint com = new Complaint(u1, u2, "tom400 was spamming", new Date());
		assertTrue(com.editComplaint(u1, u2, "sorry mistake"));
		assertFalse(com.editComplaint(u1, u2, null));
		assertEquals("sorry mistake", com.getComplaintMessage());
		assertFalse(com.editComplaint(u2, u2, "bla"));
		
	}

}
