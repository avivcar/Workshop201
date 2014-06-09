package webServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class httpRequest {
	private ArrayList<DataFragment> post; //holds all the dat of post
	private ArrayList<DataFragment> get; //hlds all the data of get
	private String requestPath; //holds the path, without the base path
	private String cookies;
	
	private HttpExchange raw;
	
	
	public httpRequest(HttpExchange exchange, String BASE_PATH) throws IOException {
		this.raw = exchange;
		post = parsePostParameters(exchange);
		get = parseGetParameters(exchange);
		requestPath = getPath(exchange, BASE_PATH);
		Headers reqHeaders = exchange.getRequestHeaders();
		List<String> cookies = reqHeaders.get("Cookie");
		this.cookies = "";
		if (cookies != null) {
			for (String cookie : cookies) this.cookies += cookie + ";";
			if (this.cookies.charAt(this.cookies.length() - 1) == ';') this.cookies = this.cookies.substring(0, this.cookies.length() - 1);
		}
	}
	
	private ArrayList<DataFragment> parsePostParameters(HttpExchange exchange) throws IOException {
        if (!("post".equalsIgnoreCase(exchange.getRequestMethod()))) return new ArrayList<DataFragment>();
        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
        BufferedReader br = new BufferedReader(isr);
        String query = br.readLine();
        return parseRequestData(java.net.URLDecoder.decode(query, "UTF-8"));
    }
	
	private ArrayList<DataFragment> parseGetParameters(HttpExchange exchange) throws IOException {
        //if (!("get".equalsIgnoreCase(exchange.getRequestMethod()))) return new ArrayList<DataFragment>();
        String path = exchange.getRequestURI() + ""; //got the uri here
        if (path.indexOf("?") == -1) return new ArrayList<DataFragment>(); //no parameters
        
        path = path.substring(path.indexOf("?")+1); //now we got the data "id=3&hi=4"...
        return parseRequestData(java.net.URLDecoder.decode(path, "UTF-8"));
    }

	private ArrayList<DataFragment> parseRequestData(String query) {
		ArrayList<DataFragment> parsedData = new ArrayList<DataFragment>();
		String[] listOfCouples = query.split("&");
		for (int i=0; i < listOfCouples.length; i++) {
			String[] temp = listOfCouples[i].split("=");
			parsedData.add(new DataFragment(temp[0], temp.length > 1 ? temp[1] : ""));
		}
		return parsedData;
	}
	
	private String getPath(HttpExchange request, String BASE_PATH) {
		String path = request.getRequestURI() + "";
		if (path.substring(0, BASE_PATH.length()).equals(BASE_PATH)) path = path.substring(BASE_PATH.length());
		if (path.indexOf("?") != -1) path = path.substring(0, path.indexOf("?"));
		if (path.length() > 0 && path.charAt(path.length() - 1) == '/') path = path.substring(0, path.length() - 1);
		if (path.length() > 0 && path.charAt(0) == '/') path = path.substring(1);
		return path;
	}	
	
	public boolean hasGet(String key) {
		for (int i=0; i<this.get.size(); i++) if (this.get.get(i).getKey().equals(key)) return true;
		return false;
	}
	
	public boolean hasPost(String key) {
		for (int i=0; i<this.post.size(); i++) if (this.post.get(i).getKey().equals(key)) return true;
		return false;
	}
	
	public String getPath() {
		return this.requestPath;
	}
	
	public String getGet(String key) {
		if (hasGet(key) == false) return null;
		for (int i = 0; i < this.get.size(); i++)
			if (this.get.get(i).getKey().equals(key)) return this.get.get(i).getValue();
		return null;
	}
	
	public String getPost(String key) {
		if (hasPost(key) == false) return null;
		for (int i = 0; i < this.post.size(); i++)
			if (this.post.get(i).getKey().equals(key)) return this.post.get(i).getValue();
		return null;
	}
	
	//this method returns the String of the cookie that holds the ForumSystemSesssionID substring, if it exists
	//otherwise returns null
	public String getSessionId() {
		String[] cookies = this.splitCookies();
		for (int i=0; i < cookies.length; i++) {
			String cur = cookies[i];
			if (cur.contains("ForumSystemSessionID")) 
				return cur.split("=")[1];
		}
		return null;
	}
	
	public void setSessionId(String value) {
		Headers respHeaders = this.raw.getResponseHeaders();
		List<String> values = new ArrayList<>();
		values.add("ForumSystemSessionID=" + value);
		respHeaders.put("Set-Cookie", values);
	}
	
	//this method splits the cookies string by ";", and returns an array of them
	public String[] splitCookies(){
		return this.cookies.split(";");
	}
}
