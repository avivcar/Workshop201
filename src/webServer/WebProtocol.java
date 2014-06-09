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
			if ((request.getPost("sideEffect").equals("login") || request.getPost("sideEffect").equals("login")) && user == null) return getHeader(user) + "<h2 style=\"color: red;\">Sorry, the details you entered were incorrect, please try again</h2>" + echoHomepage(sys) + getFooter();
			if (request.getPost("sideEffect").equals("addReply")) {
				if (!request.hasPost("id")) return getHeader(user) + getRedirect("/forum") + getFooter();
				if (!request.hasPost("title") || !request.hasPost("content")) return getHeader(user) + getRedirect("/reply?id=" + request.getPost("id")) + getFooter();
				sys.getMessageById(request.getPost("id")).addReply(user, request.getPost("title"), request.getPost("content"));
			}
			if (request.getPost("sideEffect").equals("addMessage")) {
				if (!request.hasPost("id")) return getHeader(user) + getRedirect("/forum") + getFooter();
				if (!request.hasPost("title") || !request.hasPost("content")) return getHeader(user) + getRedirect("/add?id=" + request.getPost("id")) + getFooter();
				sys.getSubForumById(request.getPost("id")).createMessage(user, request.getPost("title"), request.getPost("content"));
			}
		}
		String ans = getHeader(user);
		String pageReq = request.getPath();
		switch (pageReq) {
			//forums req
			case "index":
			case "":
				ans += echoHomepage(sys);
				break;
			//get forum subforums
			case "forum":
				if (!request.hasGet("id")) ans += getRedirect("/forum");
				else ans += echoForum(sys.getForum(request.getGet("id")));
				break;
			//get subforum messages
			case "subforum":
				if (!request.hasGet("id")) ans += getRedirect("/forum");
				else ans += echoSubForum(sys.getSubForumById(request.getGet("id")), sys);
				break;
			case "message":
				if (!request.hasGet("id")) ans += getRedirect("/forum");
				else ans += echoMsg(sys.getMessageById(request.getGet("id")), sys);
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
		String ans = "</span></div><div style=\"clear: both;\"></div><h2>Forum System</h2>";
		for (int i=0; i < sys.forums.size(); i++) {
			ans += "<div><a class=\"item\"  href=\"/forum/login?id=" + sys.forums.get(i).getId() + "\">" + sys.forums.get(i).getName() + "</a></div>";
		}
		return ans; 
	}
	
	
	private static String echoLogin(String forumId){
		String ans = "</span></div><div style=\"clear: both;\"></div><h2>Login</h2>";
		ans += "<form action=\"/forum/forum?id=" + forumId + "\" method=\"post\">";
		ans += "<input type=\"hidden\" name=\"sideEffect\" value=\"login\">";
		ans += "<div><label>Username:</label><input type=\"text\" name=\"username\"></div>";
		ans += "<div><label>Password:</label><input type=\"text\" name=\"password\"></div>";
		ans += "<input type=\"hidden\" name=\"forumId\" value=\"" + forumId + "\">";
		ans += "<button>Login</button>";
		ans += "</form>";
		ans += "<a href=\"/forum/signup?id=" + forumId + "\">Sign Up!</a>";
		return ans;
	}
	
	
	private static String echoAddMessage(String subforumId){
		String ans = "</span></div><div style=\"clear: both;\"></div><h2>Add Message</h2>";
		ans += "<form action=\"/forum/subforum?id=" + subforumId + "\" method=\"post\">";
		ans += "<input type=\"hidden\" name=\"sideEffect\" value=\"addMessage\">";
		ans += "<input type=\"hidden\" name=\"id\" value=\"" + subforumId + "\">";
		ans += "<div><label>Title:</label><input type=\"text\" name=\"title\"></div>";
		ans += "<div><label>Content:</label></div>";
		ans += "<textarea name=\"content\"></textarea>";
    	ans += "<button>Subimt</button>";
    	ans += "</form>";
    	return ans;
	}
	
	
	private static String echoAddReply(String msgId){
		String ans = "</span></div><div style=\"clear: both;\"></div><h2>Add Reply</h2>";
		ans += "<form action=\"/forum/message?id=" + msgId + "\" method=\"post\">";
		ans += "<input type=\"hidden\" name=\"sideEffect\" value=\"addReply\">";
		ans += "<input type=\"hidden\" name=\"id\" value=\"" + msgId + "\">";
		ans += "<div><label>Title:</label><input type=\"text\" name=\"title\"></div>";
		ans += "<div><label>Content:</label></div>";
		ans += "<textarea name=\"content\"></textarea>";
    	ans += "<button>Subimt</button>";
    	ans += "</form>";
    	return ans;
	}
	
	
	private static String echoSignup(String forumId){
		String ans = "</span></div><div style=\"clear: both;\"></div><h2>Sign Up</h2>";
		ans += "<form action=\"/forum/forum?id=" + forumId + "\" method=\"post\">";
		ans += "<input type=\"hidden\" name=\"sideEffect\" value=\"signup\">";
		ans += "<div><label>Mail:</label><input type=\"text\" name=\"mail\"></div>";
		ans += "<div><label>Name:</label><input type=\"text\" name=\"name\"></div>";
		ans += "<div><label>Username:</label><input type=\"text\" name=\"username\"></div>";
		ans += "<div><label>Password:</label><input type=\"text\" name=\"password\"></div>";
		ans += "<input type=\"hidden\" name=\"forumId\" value=\"" + forumId + "\">";
		ans += "<button>Signup!</button>";
		ans += "</form>";
		return ans;
	}
	
	
	private static String echoMsg(Message msg, ForumSystem sys){
		if (msg == null) return getRedirect("/forum/");
		Message parent = msg;
		while (parent.getSubforumId().equals("0")) parent = sys.getMessageById(msg.getMsgRel());
		SubForum fs = sys.getSubForumById(parent.getSubforumId());
		Forum f = sys.getForum(fs.getForumId());
		String ans = "<div id=\"bread-crumbs\"><a href=\"/forum/forum?id=" + f.getId() + "\">" + f.getName() + "</a> >> <a href=\"" + fs.getId() + "\">" + fs.getSubject() + "</a> >> <span>" + msg.getTitle() + "</span></div><div style=\"clear: both;\"></div>";
		ans += "<h2>" + (msg.getTitle().equals("") ? "Untitled Message" : msg.getTitle()) + "</h2><div>" + msg.getContent() + "</div><h2>Replies:</h2>";
		for (int i=0; i<msg.getReplies().size(); i++) {
			ans += "<div><a class=\"item\"  href=\"/forum/message?id=" + msg.getReplies().get(i).getId() + "\">" + (msg.getReplies().get(i).getTitle().equals("") ? "Untitled Message" : msg.getReplies().get(i).getTitle()) + "</a></div>";
		}
		ans += "<h2><a href=\"/forum/reply?id=" + msg.getId() + "\">Add Reply</a></h2>";
		return ans;
	}
	
	private static String echoForum(Forum forum) {
		if (forum == null) return getRedirect("/forum/");
		String ans = "</span></div><div style=\"clear: both;\"></div><h2>Forum - " + forum.getName() + "</h2>";
		for (int i=0; i < forum.getSubForums().size(); i++) {
			ans += "<div><a class=\"item\"  href=\"/forum/subforum?id=" + forum.getSubForums().get(i).getId() + "\">" + forum.getSubForums().get(i).getSubject() + "</a></div>";
		}
		return ans;
	}
	
	private static String echoSubForum(SubForum subForum, ForumSystem sys) {
		Forum f = sys.getForum(subForum.getForumId());
		if (subForum == null) return getRedirect("/forum/");
		String ans = "<div id=\"bread-crumbs\"><a href=\"/forum/forum?id=" + f.getId() + "\">" + f.getName() + "<span>" + subForum.getSubject() + "</span></div><div style=\"clear: both;\"></div>";
		ans += "<h2>SubForum - " + subForum.getSubject() + "</h2>";
		for (int i=0; i < subForum.getMessages().size(); i++) {
			ans += "<div><a class=\"item\"  href=\"/forum/message?id=" + subForum.getMessages().get(i).getId() + "\">" + subForum.getMessages().get(i).getTitle() + "</a></div>";
		}
		ans += "<h2><a href=\"/forum/add?id=" + subForum.getId() + "\">Add A New Thread</a></h2>";
		return ans;
	}
	
	public static String getRedirect(String href) {
		return "<script>window.location = '" + href + "'</script>";
	}
	
	
	
	//***AUX - header and footer ****//
	public static String getHeader(User user) {
		return readFile("header.html") + "<div id=\"user-status\">" + (user == null ? "Hello, Guest" : ("Hello, " + user.getName())) + "</div>";
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
