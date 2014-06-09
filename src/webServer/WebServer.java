package webServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Map;

import user.User;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import forumSystemCore.ForumSystem;

public class WebServer implements Runnable {
	
	private final String BASE_PATH;
	private final int PORT;
	private HttpServer server;
	private ForumSystem system;
	
	public WebServer(int port, String baseUrl, ForumSystem system) throws IOException {
		this.BASE_PATH = baseUrl;
		this.PORT = port;
		this.system = system;
		this.server = HttpServer.create(new InetSocketAddress(this.PORT), 0);
		this.server.createContext(this.BASE_PATH, new WebHandler(this.BASE_PATH, this.system));
		this.server.setExecutor(null); // creates a default executor
	}

	@Override
	public void run() {
		this.server.start();
	}
	
	public static void main(String[] args) throws IOException {
		ForumSystem forumSystem = new ForumSystem();
		System.out.println("Starting system...");
		User admin= forumSystem.startSystem("halevm@em.walla.com", "firstname", "admin", "1234");
		String f1 = forumSystem.createForum("yaquierrrr", admin);
		forumSystem.createForum("lahan-el", admin);
		forumSystem.createForum("shem-nahash!", admin);
		String s= forumSystem.createSubForum(admin, admin, "subforum1", f1);
		s= forumSystem.createMessage("1", "1", admin, "new title", "new content");
		boolean bool = forumSystem.addReply("1", "1", "1", admin, "tguva", "content tguva");
		 bool = forumSystem.addReply("1", "1", "1", admin, "tguva2", "content tguva23234234");
		 bool = forumSystem.addReply("1", "1", "1", admin, "tguva44", "content tguva4124");
		WebServer server = new WebServer(8080, "/forum", forumSystem);
		new Thread(server).run();
	}

}
