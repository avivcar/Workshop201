
/**
 * 
 * Runs all acceptance tests
 * 
 */

package acceptanceTests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ 
	initSystemTests.class, 
	RegLoginLogoutTest.class,
	SubForumTest.class, 
	writeMessageTest.class,
	DataBaseTest.class,
	notificationTest.class,
	overloadTest.class,
	initReactorTest.class,
	
})
public class AllTests {

}
