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
		httpRequest httpReq = new httpRequest(request, this.BASE_PATH);
		User user = validateUser(httpReq); //checking if the user is logged in
		String response = "Not logged in";
		
		//not logged in
		if (user == null) {
			if (httpReq.hasPost("username") && httpReq.hasPost("password") && httpReq.hasPost("forumId")) {
				user = system.login(httpReq.getPost("username"),httpReq.getPost("password"), httpReq.getPost("forumId"));
				String sessId = generateSessionId();
				this.sessions.put(sessId, user);
				httpReq.setSessionId(sessId);
			}	
		}
		
		//if user exists and logged in, get response. 
		//if the user just logged in in this request, need to return sub forums.
		if (user != null) response = WebProtocol.getResponse(httpReq, user, system);
		
		//headers and cookies
		Headers respHeaders = request.getResponseHeaders();
        request.sendResponseHeaders(200, response.length());
        OutputStream os = request.getResponseBody();
        os.write(response.getBytes());
        os.close();
	}
	
	/*public String generateResponse(httpRequest request) {
		
		
		String ans = WebProtocol.getResponse(request);
		// login request
		if (!(request.hasPost("username") && request.hasPost("password") && request.hasPost("forumId"))) return "error";
		user = new User(); //system.login(request.getPost("username"),request.getPost("password"), request.getPost("forumId"));
		if (user == null) return "Not logged in";
		String sessId = generateSessionId();
		this.sessions.put(sessId, user);
		request.setSessionId(sessId);
		
		return "cool";
	}*/
	
	public User validateUser(httpRequest request) {
		String sessId = request.getSessionId();
		if (sessId == null || !this.sessions.containsKey(sessId)) return null;
		return this.sessions.get(sessId);
	}
	
	public static String generateSessionId() {
		return new BigInteger(130, new SecureRandom()).toString(32);
	}
	
	
	

	
}
