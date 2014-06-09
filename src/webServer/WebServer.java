package webServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Map;

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
		WebServer s = new WebServer(8080, "/forum", new ForumSystem());
		new Thread(s).run();
	}

}
