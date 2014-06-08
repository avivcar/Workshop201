package webServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Map;
import java.util.ArrayList;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class httpRequest {
	private ArrayList<DataFragment> post; //holds all the dat of post
	private ArrayList<DataFragment> get; //hlds all the data of get
	private String requestPath; //holds the path, without the base path
	
	public httpRequest(HttpExchange exchange, String BASE_PATH) throws IOException {
		post = parsePostParameters(exchange);
		get = parseGetParameters(exchange);
		requestPath = getPath(exchange, BASE_PATH);
	}
	
	private ArrayList<DataFragment> parsePostParameters(HttpExchange exchange) throws IOException {
        if (!("post".equalsIgnoreCase(exchange.getRequestMethod()))) return new ArrayList<DataFragment>();
        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
        BufferedReader br = new BufferedReader(isr);
        String query = br.readLine();
        return parseRequestData(query);
    }
	
	private ArrayList<DataFragment> parseGetParameters(HttpExchange exchange) throws IOException {
        //if (!("get".equalsIgnoreCase(exchange.getRequestMethod()))) return new ArrayList<DataFragment>();
        String path = exchange.getRequestURI() + ""; //got the uri here
        if (path.indexOf("?") == -1) return new ArrayList<DataFragment>(); //no parameters
        
        path = path.substring(path.indexOf("?")+1); //now we got the data "id=3&hi=4"...
        return parseRequestData(path);
    }

	private ArrayList<DataFragment> parseRequestData(String query) {
		ArrayList<DataFragment> parsedData = new ArrayList<DataFragment>();
		String[] listOfCouples = query.split("&");
		for (int i=0; i < listOfCouples.length; i++) {
			String[] temp = listOfCouples[i].split("=");
			parsedData.add(new DataFragment(temp[0], temp[1]));
		}
		return parsedData;
	}
	
	private String getPath(HttpExchange request, String BASE_PATH) {
		String path = request.getRequestURI() + "";
		if (path.substring(0, BASE_PATH.length()).equals(BASE_PATH)) path = path.substring(BASE_PATH.length());
		if (path.indexOf("?") != -1) path = path.substring(0, path.indexOf("?"));
		if (path.charAt(path.length() - 1) == '/') path = path.substring(0, path.length() - 1);
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
}
