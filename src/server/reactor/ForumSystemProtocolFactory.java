package server.reactor;


import forumSystemCore.ForumSystem;
import server.protocol.AsyncServerProtocol;
import server.protocol.EchoProtocol;
import server.protocol.ServerProtocolFactory;

import user.*;

public class ForumSystemProtocolFactory implements  ServerProtocolFactory {
	private ForumSystem forumSystem;
	
	public ForumSystemProtocolFactory(ForumSystem forumsystem) {
		this.forumSystem=forumsystem;
	}
	
	public AsyncServerProtocol create(user.User user) {
		return new EchoProtocol(user, this.forumSystem);
	}


}