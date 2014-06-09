package webServer;

import forumSystemCore.ForumSystem;
import user.User;

public class WebProtocol {
	
	public static String getResponse(httpRequest request, User user, ForumSystem sys) {
		String ans = "Command not found. sorry!";
		String pageReq = request.getPath();
		switch (pageReq) {
			case "login":
				break;
			//forums req
			case "index":
			case "":
				break;
			//get forum subforums
			case "forum":
				break;
			//get subforum messages
			case "subforum":
				break;
			//get 
		}
		
		return ans;
	}
	
}
