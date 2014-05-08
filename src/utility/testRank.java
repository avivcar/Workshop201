package utility;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;



public class testRank {

	@Test
	public void test() {
		ArrayList<Permissions> tmp = new ArrayList<Permissions>();
		tmp.add(Permissions.CREATE_MESSAGE);
		Rank rank = new Rank("A");
		rank.setPermissions(tmp);
		assertTrue(rank.hasPermission(Permissions.CREATE_MESSAGE));
		assertFalse(rank.hasPermission(Permissions.CREATE_FORUM));
	}

}
