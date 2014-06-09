package webServer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import user.User;
import forumSystemCore.Forum;
import forumSystemCore.ForumSystem;
import forumSystemCore.SubForum;

public class WebProtocol {
	
	public static String getResponse(httpRequest request, User user, ForumSystem sys) {
		String ans = getHeader();
		String pageReq = request.getPath();
		switch (pageReq) {
			//forums req
			case "signup":
				break;
			case "index":
			case "":
				ans += echoHomepage(sys);
				break;
			//get forum subforums
			case "forum":
			case "login":
				ans += echoForum(sys.getForum(request.getPost("forumId")));
				break;
			//get subforum messages
			case "subforum":
				ans += echoSubForum(sys.getForum(request.getPost("forumId")).getSubForumById(request.getPost("subForumId")));
				break;
			//get 
		}
		
		ans += getFooter();
		return ans;
	}
	
	
	//The builders of this wonderful facility, full factility. i swear.
	private static String echoHomepage(ForumSystem sys){
		String ans = "";
		for (int i=0; i < sys.forums.size(); i++) {
			ans += "<div>" + sys.forums.get(i).getName() + "</div>";
		}
		
		return ans; 
	}
	
	private static String echoForum(Forum forum) {
		String ans = "";
		for (int i=0; i < forum.getSubForums().size(); i++) {
			ans += "<div>" + forum.getSubForums().get(i).getSubject() + "</div>";
		}
		return ans;
	}
	
	private static String echoSubForum(SubForum subForum) {
		String ans = "";
		for (int i=0; i < subForum.getMessages().size(); i++) {
			ans += "<div>" + subForum.getMessages().get(i).getTitle() + "</div>";
			//need to print number of comments to it
			//print the author
			//print last commentor?
		}
		return ans;
	}
	
	
	
	//***AUX - header and footer ****//
	public static String getHeader() {
		return readFile("header.html");
	}
	
	public static String getFooter() {
		return readFile("footer.html");
	}
	
	public static String readFile(String path) {
	    byte[] encoded;
		try {
			encoded = Files.readAllBytes(Paths.get(path));
		} catch (IOException e) {
			System.out.println("cound not open file " + path + ". error: " + e.getMessage());
			return "";
		}
	    return new String(encoded, StandardCharsets.UTF_8);
	}
}
