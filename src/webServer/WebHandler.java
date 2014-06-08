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
    	parsePostParameters(request);
        String response = "fdsfsd";
        request.sendResponseHeaders(200, response.length());
        OutputStream os = request.getResponseBody();
        os.write(response.getBytes());
        os.close();
        
	}
	
	private void parsePostParameters(HttpExchange exchange) throws IOException {
        if ("post".equalsIgnoreCase(exchange.getRequestMethod())) {
            @SuppressWarnings("unchecked")
            Map<String, Object> parameters =
                (Map<String, Object>)exchange.getAttribute("parameters");
            InputStreamReader isr =
                new InputStreamReader(exchange.getRequestBody(),"utf-8");
            BufferedReader br = new BufferedReader(isr);
            String query = br.readLine();
            //query holds the list
            System.out.println(parseRequestData(query));
        }
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
	
}
