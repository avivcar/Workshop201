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
		User user = validateUser(httpReq); //checking if the user is logged in, requests log in or requests signup
		String response = WebProtocol.getResponse(httpReq, user, system);
        request.sendResponseHeaders(200, response.length());
        OutputStream os = request.getResponseBody();
        os.write(response.getBytes());
        os.close();
	}
	
	public User validateUser(httpRequest request) {
		if (request.hasPost("sideEffect") && request.getPost("sideEffect").equals("login")) { // login request
			if (!request.hasPost("username") || !request.hasPost("password") || !request.hasPost("forumId")) return null;
			User user = this.system.login(request.getPost("username"), request.getPost("password"), request.getPost("forumId"));
			String sessId = generateSessionId();
			this.sessions.put(sessId, user);
			request.setSessionId(sessId);
			return user;
		}
		if (request.hasPost("sideEffect") && request.getPost("sideEffect").equals("signup")) { // signup request
			if (!request.hasPost("mail") || !request.hasPost("name") || !request.hasPost("username") || !request.hasPost("password") || !request.hasPost("forumId")) return null;
			User user = this.system.signup(request.getPost("mail"), request.getPost("name"), request.getPost("username"), request.getPost("password"), request.getPost("forumId"));
			String sessId = generateSessionId();
			this.sessions.put(sessId, user);
			request.setSessionId(sessId);
			return user;
		}
		String sessId = request.getSessionId();
		if (sessId == null || !this.sessions.containsKey(sessId)) return null;
		return this.sessions.get(sessId);
	}
	
	public static String generateSessionId() {
		return new BigInteger(130, new SecureRandom()).toString(32);
	}
	
	
	

	
}
