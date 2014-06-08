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
	
}
