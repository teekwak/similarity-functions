package RaspberryPi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.BufferedWriter;
import com.google.gson.*;

import java.net.*;
import java.io.*;
import java.util.InputMismatchException;
import java.util.Scanner;

public class PiQuerySender {
	public static String[] keywordsArray = new String[] {
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

	// writes results to file in bitVector_keyword_overlapTotal format
	public static void storeResults(String bitVector, String keyword, int overlapTotal, int piNumber) {
		StringBuilder sb = new StringBuilder();
		sb.append(bitVector);
		sb.append("_");
		sb.append(keyword);
		sb.append("_");
		sb.append(overlapTotal);

		try (
			FileWriter fw = new FileWriter("pi" + piNumber + "_OutputFile.txt", true);
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
	public static int sendQuery(String keywords, String bitvector) {
		try {
			StringBuilder querySb = new StringBuilder();
			querySb.append("http://grok.ics.uci.edu:9551/solr/MoreLikeThisIndex/sim/?q=snippet_code:(");
			querySb.append(keywords);
			querySb.append(")+AND+snippet_number_of_functions:[1+TO+*]+AND+parent:true+AND+snippet_is_innerClass:false+AND+snippet_is_anonymous:false&start=0&fl=id&indent=on&wt=json&rows=1000&started=false&test=false&conciseCount=100&bitvector=");
			querySb.append(bitvector);

			// System.out.println(querySb.toString());

			// REALLY GOTTA CHANGE THE URL TO THE CORRECT ONE
			URL url = new URL(querySb.toString());

			StringBuilder responseSb = new StringBuilder();
			BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
			for (String line; (line = br.readLine()) != null;) {
				responseSb.append(line);
			}

			// System.out.println("************");
			JsonParser parser = new JsonParser();
			JsonElement element = parser.parse(responseSb.toString());
			if(element.isJsonObject()) {
				// print out single response by key
				JsonObject response = element.getAsJsonObject();
				// System.out.println("OverlapNumber: " + response.get("OverlapNumber").getAsString());


				// iterate over map (not required)
//				JsonArray hybridExemplarsContents = response.getAsJsonArray("HybridExemplarsContents");
//				for(int i = 0; i < hybridExemplarsContents.size(); i++) {
//					JsonObject dataset = hybridExemplarsContents.get(i).getAsJsonObject();
//					System.out.println(dataset.get("id").getAsString());
//				}

				return response.get("OverlapNumber").getAsInt();
			}

			} catch (IOException e) {
				e.printStackTrace();
			}

		return -1;
	}

	public static void generateProgressBar(int count, int maxCount) {
		int numberOfBars = maxCount / 10;
		int numberOfFilledBars = count / numberOfBars;

		StringBuilder sb = new StringBuilder();
		sb.append("\rCompletion: [");
		for(int i = 0; i < numberOfFilledBars; i++) {
			sb.append("=");
		}
		sb.append("          ".substring(numberOfFilledBars));
		sb.append("] ");
		sb.append(count);
		sb.append("/");
		sb.append(maxCount);

		System.out.print(sb.toString());
	}

	public static void main(String[] args) {

		System.out.print("Enter the pi # you are working on: ");
		Scanner sc = new Scanner(System.in);
		try {
			int piNumber = sc.nextInt();

			// if binaryNumbersList does not exist for input, throw error
			if(!new File("java_output/pi" + piNumber + "_binaryNumbersList.txt").exists()) {
				throw new InputMismatchException("[ERROR]: You entered a number which has no matching file!");
			}

			File bitVectorFile = new File("java_output/pi" + piNumber + "_binaryNumbersList.txt");

			long a = System.currentTimeMillis();

			// read bitVectorFile
//		try {
//			BufferedReader br = new BufferedReader(new FileReader(bitVectorFile));
//
//			for(String keyword : PiQuerySender.keywordsArray) {
//			  for(String line; (line = br.readLine()) != null; ) {
//					storeResults(line, keyword, sendQuery(keyword, line));
//				}
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}

			// test
			int counter = 0;

			try {
				BufferedReader br = new BufferedReader(new FileReader(bitVectorFile));

				for(String line; (line = br.readLine()) != null; ) {
					if(counter < 100) {
						storeResults(line, keywordsArray[1], sendQuery(keywordsArray[1], line), piNumber);
						generateProgressBar(counter, 100);
						counter++;
					}
					generateProgressBar(counter, 100);
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