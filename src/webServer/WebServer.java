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


public class WebServer {
	
	private final String BASE_PATH;
	private final int PORT;
	
	public WebServer(int port, String baseUrl) throws IOException {
		this.BASE_PATH = baseUrl;
		this.PORT = port;
		HttpServer server = HttpServer.create(new InetSocketAddress(this.PORT), 0);
        server.createContext(this.BASE_PATH, new WebHandler(this.BASE_PATH));
        server.setExecutor(null); // creates a default executor
        server.start();
	}
	
	public static void main(String[] args) throws IOException {
		new WebServer(8080, "/forum");
	}

}
