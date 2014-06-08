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
	
	public httpRequest(HttpExchange exchange) {
		post = parsePostParameters(exchange);
		get = parsePostParameters(exchange);
		
	}
}
