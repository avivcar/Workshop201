package utility;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;


public class testTextVerifier {

	@Test
	public void testVerifyName() {
		Policy p = new Policy();
		assertFalse(TextVerifier.verifyName(null, p));
		assertTrue(TextVerifier.verifyName("marina", p));
		
	}
	@Test
	public void testVerifyPass() {
		Policy p = new Policy();
		assertFalse(TextVerifier.verifyPassword(null, p));
		assertTrue(TextVerifier.verifyName("12345", p));
	}
	@Test
	public void testVerifyEmail() {
		assertFalse(TextVerifier.verifyEmail(""));
		assertFalse(TextVerifier.verifyEmail("hitfdg"));
		assertTrue(TextVerifier.verifyEmail("hi@hello.com"));
	}
	
}
