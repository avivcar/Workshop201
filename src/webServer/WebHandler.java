package webServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Map;
import java.util.ArrayList;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class WebHandler implements HttpHandler {
	
	private final String BASE_PATH;
	
	public WebHandler(String basePath) {
		this.BASE_PATH = basePath;
	}

	public void handle(HttpExchange request) throws IOException {

    	
    	//System.out.println(request.getRequestURI());
    	parsePostParameters(request, this.BASE_PATH);
        String response = "fdsfsd";
        request.sendResponseHeaders(200, response.length());
        OutputStream os = request.getResponseBody();
        os.write(response.getBytes());
        os.close();
        
	}
	

	
}
