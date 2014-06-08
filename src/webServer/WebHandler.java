package webServer;

import java.util.HashMap;
import java.util.Map;
import java.security.SecureRandom;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.List;
import java.util.ArrayList;

import user.User;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import forumSystemCore.ForumSystem;

public class WebHandler implements HttpHandler {
	
	private final String BASE_PATH;
	private ForumSystem system;
	private HashMap<String, User> sessions;
	
	public WebHandler(String basePath, ForumSystem system) {
		this.sessions = new HashMap<String, User>();
		this.BASE_PATH = basePath;
		this.system = system;
	}

	public void handle(HttpExchange request) throws IOException {
        String response = this.generateResponse(new httpRequest(request, this.BASE_PATH));
        Headers respHeaders = request.getResponseHeaders();
        request.sendResponseHeaders(200, response.length());
        OutputStream os = request.getResponseBody();
        os.write(response.getBytes());
        os.close();
	}
	
	public String generateResponse(httpRequest request) {
		User user = validateUser(request);
		
		
		// login request
		if (!(request.hasPost("username") && request.hasPost("password") && request.hasPost("forumId"))) return "error";
		user = new User(); //system.login(request.getPost("username"),request.getPost("password"), request.getPost("forumId"));
		if (user == null) return "error";
		String sessId = generateSessionId();
		this.sessions.put(sessId, user);
		request.setSessionId(sessId);
		
		return "cool";
	}
	
	public User validateUser(httpRequest request) {
		String sessId = request.getSessionId();
		if (sessId == null || !this.sessions.containsKey(sessId)) return null;
		return this.sessions.get(sessId);
	}
	
	public static String generateSessionId() {
		return new BigInteger(130, new SecureRandom()).toString(32);
	}
	
	
	

	
}
