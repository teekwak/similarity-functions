package RaspberryPi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.util.Set;

public class PiQuerySender {
	public static String[] keywordsArray = new String[] {
		"database connection manager",
		"ftp client",
		"quick sort",
		"depth first search",
		"tic tac toe",
		"api amazon",
		"mail sender",
		"array multiplication",
		"algorithm for parsing string integer",
		"binary search tree",
		"file writer",
		"regular expressions",
		"concatenating strings",
		"awt events",
		"date arithmetic",
		"JSpinner",
		"prime factors",
		"fibonacci",
		"combinations n per k",
		"input stream to byte array",
		"spring rest template"
	};

	// writes results to file in bitVector_keyword_overlapTotal format
	public static void storeResults(String bitVector, String keyword, int overlapTotal) {
		StringBuilder sb = new StringBuilder();
		sb.append(bitVector);
		sb.append("_");
		sb.append(keyword);
		sb.append("_");
		sb.append(overlapTotal);

		try (
			FileWriter fw = new FileWriter("piOutputFile.txt", true);
			BufferedWriter bw = new BufferedWriter(fw);
			PrintWriter pw = new PrintWriter(bw)) 
		{
			pw.println(sb.toString());
			pw.close();
			bw.close();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// dummy function
	public static int sendQuery(String bitVector, String keyword) {
		// System.out.println("Sending query: " + bitVector + " + " + keyword);
		// System.out.println("Received result: " + 1);

		return 1;
	}

	public static void main(String[] args) {
		File bitVectorFile = new File("Output/testfile_1.txt");

		// read bitVectorFile
		try {
			BufferedReader br = new BufferedReader(new FileReader(bitVectorFile));

			for(String line; (line = br.readLine()) != null; ) {
				for(String keyword : PiQuerySender.keywordsArray) {
					storeResults(line, keyword, sendQuery(line, keyword));
				}
			}	
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	
}