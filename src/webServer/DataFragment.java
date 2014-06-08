package webServer;

import com.sun.net.httpserver.HttpExchange;

public class DataFragment {
	
	private String key;
	private String value;
	
	public DataFragment(String key, String value) {
		this.key = key;
		this.value = value;
	}
	
	public String getKey() {
		return this.key;
	}
	
	public String getValue() {
		return this.value;
	}
	
	private String getPath(HttpExchange request) {
		String path = request.getRequestURI();
		if (path.substring(0, this.BASE_URL.length).equals(this.BASE_URL)) path = path.substring(this.BASE_URL.length);
		if (path.indexOf("?") != -1) path = path.substring(0, path.indexOf("?"));
		return path;
	}
	
}
