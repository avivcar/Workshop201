package forumSystemCore;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses({ 
	testMessage.class, 
	testSubForum.class, 
})
public class forumSystemCoreTests {

}
