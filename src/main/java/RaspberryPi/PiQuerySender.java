package RaspberryPi;

import Utilities.UsefulThings;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.Set;

public class PiQuerySender {
	private static Set<String> sim2Set;
	private static String port;

	private static final String[] keywordsArray = new String[] {
		"database+OR+connection+OR+manager",
		"ftp+OR+client",
		"quick+OR+sort",
		"depth+OR+first+OR+search",
		"tic+OR+tac+OR+toe",
		"api+OR+amazon",
		"mail+OR+sender",
		"array+OR+multiplication",
		"algorithm+OR+for+OR+parsing+OR+string+OR+integer",
		"binary+OR+search+OR+tree",
		"file+OR+writer",
		"regular+OR+expressions",
		"concatenating+OR+strings",
		"awt+OR+events",
		"date+OR+arithmetic",
		"JSpinner",
		"prime+OR+factors",
		"fibonacci",
		"combinations+OR+n+OR+per+OR+k",
		"input+OR+stream+OR+to+OR+byte+OR+array",
		"spring+OR+rest+OR+template"
	};

	// writes results to file in bitVector_overlapTotal format
	private static void storeResults(String bitVector, String keyword, int overlapTotal, int piNumber) {
		while(overlapTotal == -1) {
			overlapTotal = sendQuery(keyword, bitVector);
		}

		StringBuilder sb = new StringBuilder();
		sb.append(bitVector);
		sb.append("_");
		sb.append(overlapTotal);

		try (
			FileWriter fw = new FileWriter("query_output/pi" + piNumber + "_" + keyword + "_output.txt", true);
			BufferedWriter bw = new BufferedWriter(fw);
			PrintWriter pw = new PrintWriter(bw)
		) {
			pw.println(sb.toString());
			pw.close();
			bw.close();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// send query and calculate differences
	public static int sendQuery(String keywords, String bitvector) {
		try {
			URL url = new URL(port + "/solr/MoreLikeThisIndex/sim/?q=snippet_code:(" + keywords + ")+AND+snippet_number_of_functions:[1+TO+*]+AND+parent:true+AND+snippet_is_innerClass:false+AND+snippet_is_anonymous:false&start=0&fl=id&indent=on&wt=json&rows=1000&started=false&test=false&conciseCount=100&bitvector=" + bitvector);

			StringBuilder responseSb = new StringBuilder();
			BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
			for (String line; (line = br.readLine()) != null;) {
				responseSb.append(line);
			}
			br.close();

			JsonParser parser = new JsonParser();
			JsonElement element = parser.parse(responseSb.toString());
			if(element.isJsonObject()) {
				Set<String> generatedSet = new HashSet<>();

				JsonObject response = element.getAsJsonObject();
				JsonArray hybridExemplars = response.getAsJsonArray("HybridExemplars");
				for(int i = 0; i < hybridExemplars.size(); i++) {
					generatedSet.add(hybridExemplars.get(i).getAsString());
				}

				// do set comparison here
				int similarities = 0;
				for(String s : generatedSet) {
					if(sim2Set.contains(s)) {
						similarities++;
					}
				}

				return similarities;
			}

			} catch (IOException e) {
				// e.printStackTrace();
			}

		return -1;
	}

	public static Set<String> generateSim2Set(String keyword) {
		try {
			URL url = new URL(port + "/solr/MoreLikeThisIndex/sim/?q=snippet_code:(" + keyword + ")+AND+snippet_number_of_functions:[1+TO+*]+AND+parent:true+AND+snippet_is_innerClass:false+AND+snippet_is_anonymous:false&start=0&fl=id&indent=on&wt=json&rows=1000&started=false&test=false&conciseCount=100&bitvector=11111111111111111");

			StringBuilder responseSb = new StringBuilder();
			BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
			for (String line; (line = br.readLine()) != null;) {
				responseSb.append(line);
			}
			br.close();

			JsonParser parser = new JsonParser();
			JsonElement element = parser.parse(responseSb.toString());
			if(element.isJsonObject()) {
				Set<String> generatedSet = new HashSet<>();

				JsonObject response = element.getAsJsonObject();
				JsonArray hybridExemplars = response.getAsJsonArray("HybridExemplars");
				for(int i = 0; i < hybridExemplars.size(); i++) {
					generatedSet.add(hybridExemplars.get(i).getAsString());
				}

				return generatedSet;
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static void main(String[] args) {
		if(!new File("query_output").exists()) {
			boolean createdDirectory = new File("query_output").mkdir();

			if(!createdDirectory) {
				System.out.println("[ERROR]: failed to create query_output directory");
				return;
			}
		}

		// need to read config file here
		File configFile = new File("config.txt");
		try(BufferedReader br = new BufferedReader(new FileReader(configFile))) {
			for(String line; (line = br.readLine()) != null; ) {
				if(line.startsWith("port")) {
					String[] parts = line.split("=");
					port = parts[1];
				}
			}

			if(port.length() == 0) {
				throw new InputMismatchException("[ERROR]: port is not correct");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.print("Enter the pi # you are working on: ");
		Scanner sc = new Scanner(System.in, "UTF-8");
		try {
			int piNumber = sc.nextInt();

			// if binaryNumbersList does not exist for input, throw error
			if(!new File("java_output/pi" + piNumber + "_binaryNumbersList.txt").exists()) {
				throw new InputMismatchException("[ERROR]: You entered a number which has no matching file!");
			}

			System.out.print("Enter the keyword you want to use (0-20): ");
			int keywordNumber;
			keywordNumber = sc.nextInt();

			if(keywordNumber < 0 || keywordNumber > 20) {
				throw new InputMismatchException("[ERROR]: You entered a number outside of the bounds of 0 to 20");
			}

			File bitVectorFile = new File("java_output/pi" + piNumber + "_binaryNumbersList.txt");

			long a = System.currentTimeMillis();

			// generate sim2Set
			sim2Set = generateSim2Set(keywordsArray[keywordNumber]);

			if(sim2Set != null && sim2Set.size() == 0) {
				System.out.println("[ERROR]: sim2Set is empty");
				return;
			}

			int counter = 0;
			int maxCounter = UsefulThings.getNumberOfLinesInFile(bitVectorFile);

			UsefulThings.generateProgressBar(counter, maxCounter);
			try(BufferedReader br = new BufferedReader(new FileReader(bitVectorFile))) {
				for(String line; (line = br.readLine()) != null; ) {
					storeResults(line, keywordsArray[keywordNumber], sendQuery(keywordsArray[keywordNumber], line), piNumber);
					counter++;
					UsefulThings.generateProgressBar(counter, maxCounter);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

			long b = System.currentTimeMillis();

			System.out.println("\nProcess time: " + (b-a) + "ms");

		} catch (InputMismatchException e) {
			System.out.println("[ERROR]: You entered something that was not a number...");
		}
	}	
}