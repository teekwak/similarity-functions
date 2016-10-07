package RaspberryPi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.BufferedWriter;

public class PiQuerySender {
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
		if(keyword.equals("firstKeyWord")) {
			return 3;
		}
		else if(keyword.equals("secondKeyWord")) {
			return 2;
		}

		return 1;
	}

	public static void main(String[] args) {
		File bitVectorFile = new File("Output/testfile_1.txt");

		// read bitVectorFile
		try {
			BufferedReader br = new BufferedReader(new FileReader(bitVectorFile));

			for(String line; (line = br.readLine()) != null; ) {
				storeResults(line, "firstKeyWord", sendQuery(line, "firstKeyWord"));
				storeResults(line, "secondKeyWord", sendQuery(line, "secondKeyWord"));
				storeResults(line, "thirdKeyWord", sendQuery(line, "thirdKeyWord"));
			}	
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	
}