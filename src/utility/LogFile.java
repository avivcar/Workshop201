package utility;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;


public class LogFile {
	private String filePath;
	
	public LogFile(String filePath){
		this.filePath = filePath;
	}
	
	/**
	 * writes a new line to the log file in the log's filepath
	 * @param s the text added to the log
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	public void log(String s) throws FileNotFoundException, UnsupportedEncodingException{
		PrintWriter writer = new PrintWriter(this.filePath, "UTF-8");
    	writer.println(s);
    	writer.close();	
	}

}
