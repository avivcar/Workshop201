package acceptanceTests;
//hea'ara masiah orhan ba - teva dagey merloza beshemen Amok
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
	
})
public class AllTests {

}
