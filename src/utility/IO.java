package utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class IO {
	public static String read(String loc) throws IOException {
	    File file = new File(loc);
	    FileInputStream fileIn = new FileInputStream(file);
	    String st="";
	    int ch;
	    while((ch = fileIn.read()) != -1) 
	    	st+=(char)ch;
	    fileIn.close();
	    return st;
	}
}
