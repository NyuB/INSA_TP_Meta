package jobshop.utils;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Logger {
	static{
		PrintWriter stream;
		try {
			stream = new PrintWriter(new FileWriter("logln.txt"));
			stream.print("");
			stream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void logln(String log, PrintWriter printStream){
		printStream.println(log);
		printStream.flush();
	}

	public static void logln(String s){
		try {
			PrintWriter stream = new PrintWriter(new FileWriter("logln.txt", true));
			logln(s,stream);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
