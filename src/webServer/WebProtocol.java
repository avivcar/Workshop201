package webServer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import user.User;
import forumSystemCore.Forum;
import forumSystemCore.ForumSystem;
import forumSystemCore.Message;
import forumSystemCore.SubForum;

public class WebProtocol {
	
	public static String getResponse(httpRequest request, User user, ForumSystem sys) {
		if (request.hasPost("sideEffect")) {
			if (request.getPost("sideEffect").equals("addReply")) {
				if (!request.hasPost("id")) return getHeader() + getRedirect("/forum") + getFooter();
				if (!request.hasPost("title") || !request.hasPost("content")) return getHeader() + getRedirect("/reply?id=" + request.getPost("id")) + getFooter();
				sys.getMessageById(request.getPost("id")).addReply(user, request.getPost("title"), request.getPost("content"));
			}
			if (request.getPost("sideEffect").equals("addMessage")) {
				if (!request.hasPost("id")) return getHeader() + getRedirect("/forum") + getFooter();
				if (!request.hasPost("title") || !request.hasPost("content")) return getHeader() + getRedirect("/add?id=" + request.getPost("id")) + getFooter();
				sys.getSubForumById(request.getPost("id")).createMessage(user, request.getPost("title"), request.getPost("content"));
			}
		}
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
				if (!request.hasGet("forumId")) ans += getRedirect("/forum");
				else ans += echoForum(sys.getForum(request.getGet("forumId")));
				break;
			//get subforum messages
			case "subforum":
				if (!request.hasGet("forumId") || !request.hasGet("subForumId")) ans += getRedirect("/forum");
				else ans += echoSubForum(sys.getForum(request.getGet("forumId")).getSubForumById(request.getPost("subForumId")));
				break;
			case "message":
				if (!request.hasGet("id")) ans += getRedirect("/forum");
				else ans += echoMsg(sys.getMessageById(request.getGet("id")));
				break;
			case "login":
				if (!request.hasGet("id")) ans += getRedirect("/forum");
				else ans += echoLogin(request.getGet("id"));
				break;
			case "signup":
				if (!request.hasGet("id")) ans += getRedirect("/forum");
				else ans += echoSignup(request.getGet("id"));
				break;
			case "add":
				if (!request.hasGet("id")) ans += getRedirect("/forum");
				else ans += echoAddMessage(request.getGet("id"));
				break;
			case "reply":
				if (!request.hasGet("id")) ans += getRedirect("/forum");
				else ans += echoAddReply(request.getGet("id"));
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
			ans += "<div><a href=\"/forum/login?forumId=" + sys.forums.get(i).getId() + "\">" + sys.forums.get(i).getName() + "</a></div>";
		}
		return ans; 
	}
	
	
	private static String echoLogin(String forumId){
		String ans = "<h1>Login</h1>";
		ans += "<form action=\"/forum/forum?id=" + forumId + "\" method=\"post\">";
		ans += "<input type=\"hidden\" name=\"sideEffect\" value=\"login\">";
		ans += "<div>Username:<input type=\"text\" name=\"username\"></div>";
		ans += "<div>Password:<input type=\"text\" name=\"password\"></div>";
		ans += "<input type=\"hidden\" name=\"forumId\" value=\"" + forumId + "\">";
		ans += "<button>Login</button>";
		ans += "</form>";
		ans += "<a href=\"/forum/signup\">Sign Up!</a>";
		return ans;
	}
	
	
	private static String echoAddMessage(String subforumId){
		String ans = "<h1>Add Message</h1>";
		ans += "<form action=\"/forum/subforum?id=" + subforumId + "\" method=\"post\">";
		ans += "<input type=\"hidden\" name=\"sideEffect\" value=\"addMessage\">";
		ans += "<input type=\"hidden\" name=\"id\" value=\"" + subforumId + "\">";
		ans += "<div>Title:<input type=\"text\" name=\"title\"></div>";
		ans += "<div>Content:</div>";
		ans += "<textarea name=\"content\"></textarea>";
    	ans += "<button>Subimt</button>";
    	ans += "</form>";
    	return ans;
	}
	
	
	private static String echoAddReply(String msgId){
		String ans = "<h1>Add Reply</h1>";
		ans += "<form action=\"/forum/subforum?id=" + msgId + "\" method=\"post\">";
		ans += "<input type=\"hidden\" name=\"sideEffect\" value=\"addReply\">";
		ans += "<input type=\"hidden\" name=\"id\" value=\"" + msgId + "\">";
		ans += "<div>Title:<input type=\"text\" name=\"title\"></div>";
		ans += "<div>Content:</div>";
		ans += "<textarea name=\"content\"></textarea>";
    	ans += "<button>Subimt</button>";
    	ans += "</form>";
    	return ans;
	}
	
	
	private static String echoSignup(String forumId){
		String ans = "<h1>Sign Up</h1>";
		ans += "<form action=\"/forum/forum?id=" + forumId + "\" method=\"post\">";
		ans += "<input type=\"hidden\" name=\"sideEffect\" value=\"signup\">";
		ans += "<div>Mail:<input type=\"text\" name=\"mail\"></div>";
		ans += "<div>Name:<input type=\"text\" name=\"name\"></div>";
		ans += "<div>Username:<input type=\"text\" name=\"username\"></div>";
		ans += "<div>Password:<input type=\"text\" name=\"password\"></div>";
		ans += "<input type=\"hidden\" name=\"forumId\" value=\"" + forumId + "\">";
		ans += "<button>Signup!</button>";
		ans += "</form>";
		return ans;
	}
	
	
	private static String echoMsg(Message msg){
		String ans = "<h1>Message - Message Title || Untitled Message</h1><div>Message Content</div>";
		for (int i=0; i<msg.getReplies().size(); i++) {
			ans += "<div><a href=\"/forum/message?id=" + msg.getReplies().get(i).getId() + "\">" + (msg.getReplies().get(i).getTitle().equals("") ? "Untitled Message" : msg.getReplies().get(i).getTitle()) + "</a></div>";
		}
		ans += "<div><a href=\"/forum/reply?id=" + msg.getId() + "\">Reply</a></div>";
		return ans;
	}
	
	private static String echoForum(Forum forum) {
		if (forum == null) return getRedirect("/forum/");
		String ans = "<h1>Forum - " + forum.getName() + "</h1>";
		for (int i=0; i < forum.getSubForums().size(); i++) {
			ans += "<div><a href=\"/forum/subforum?forumId=" + forum.getId() + "&subForumId=" + forum.getSubForums().get(i).getId() + "\">" + forum.getSubForums().get(i).getSubject() + "</a></div>";
		}
		return ans;
	}
	
	private static String echoSubForum(SubForum subForum) {
		if (subForum == null) return getRedirect("/forum/");
		String ans = "<h1>SubForum - " + subForum.getSubject() + "</h1>";
		for (int i=0; i < subForum.getMessages().size(); i++) {
			ans += "<div><a href=\"/forum/message?id=" + subForum.getMessages().get(i).getId() + "\">" + subForum.getMessages().get(i).getTitle() + "</a></div>";
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
