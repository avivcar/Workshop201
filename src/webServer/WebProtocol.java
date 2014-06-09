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
			case "index":
			case "":
				ans += echoHomepage(sys);
				break;
			//get forum subforums
			case "forum":
				ans += echoForum(sys.getForum(request.getGet("forumId")));
				break;
			//get subforum messages
			case "subforum":
				ans += echoSubForum(sys.getForum(request.getGet("forumId")).getSubForumById(request.getPost("subForumId")));
				break;
			case "message":
				ans += echoMsg(sys);
				break;
			case "login":
				ans += echoLogin(sys);
				break;
			case "signup":
				ans += echoSignup(sys);
				break;
			case "add":
				ans += echoAddMessage(sys);
				break;
			case "reply":
				ans += echoAddReply(sys);
				break;
			//get 
		}
		
		ans += getFooter();
		return ans;
	}
	
	
	//The builders of this wonderful facility, full factility. i swear.
	private static String echoHomepage(ForumSystem sys){
		String ans = "<h1>Forum System</h1>";
		for (int i=0; i < sys.forums.size(); i++) {
			ans += "<a href=\"/forum/login?forumId=" + sys.forums.get(i).getId() + "\">" + sys.forums.get(i).getName() + "</a>";
		}
		return ans; 
	}
	
	
	private static String echoLogin(ForumSystem sys){
		return "";
	}
	
	
	private static String echoAddMessage(ForumSystem sys){
		return "";
	}
	
	
	private static String echoAddReply(ForumSystem sys){
		return "";
	}
	
	
	private static String echoSignup(ForumSystem sys){
		return "";
	}
	
	
	private static String echoMsg(ForumSystem sys){
		return "";
	}
	
	private static String echoForum(Forum forum) {
		if (forum == null) return getRedirect("/forum/");
		String ans = "<h1>Forum - " + forum.getName() + "</h1>";
		for (int i=0; i < forum.getSubForums().size(); i++) {
			ans += "<a href=\"/forum/subforum?forumId=" + forum.getId() + "&subForumId=" + forum.getSubForums().get(i).getId() + "\">" + forum.getSubForums().get(i).getSubject() + "</a>";
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
	
	public static String getRedirect(String href) {
		return "<script>window.location = '" + href + "'</script>";
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
