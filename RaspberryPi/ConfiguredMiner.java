package RaspberryPi;

import com.google.gson.*;

import java.util.Map;
import java.util.HashMap;
import java.net.*;
import java.io.*;

public class ConfiguredMiner {
	public static Map<String, Boolean> flagMap;

	public static void mineWithConfiguration(String keywords, String bitvector) {
		try {
			StringBuilder querySb = new StringBuilder();
			querySb.append("http://grok.ics.uci.edu:9551/solr/MoreLikeThisIndex/sim/?q=snippet_code:(");
			querySb.append(keywords);
			querySb.append(")+AND+snippet_number_of_functions:[1+TO+*]+AND+parent:true+AND+snippet_is_innerClass:false+AND+snippet_is_anonymous:false&start=0&fl=id&rows=300&indent=on&wt=json&started=false&test=false&conciseCount=100&bitvector=");
			querySb.append(bitvector);

			// REALLY GOTTA CHANGE THE URL TO THE CORRECT ONE
			URL url = new URL(querySb.toString());

			StringBuilder responseSb = new StringBuilder();
			BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
			for (String line; (line = br.readLine()) != null;) {
				responseSb.append(line);
			}

			System.out.println("************");
			JsonParser parser = new JsonParser();
			JsonElement element = parser.parse(responseSb.toString());
			if(element.isJsonObject()) {
				// print out single response by key
				JsonObject response = element.getAsJsonObject();
				System.out.println(response.get("OverlapNumber").getAsString());

				// iterate over map (not required)
				JsonArray hybridExemplarsContents = response.getAsJsonArray("HybridExemplarsContents");
				for(int i = 0; i < hybridExemplarsContents.size(); i++) {
					JsonObject dataset = hybridExemplarsContents.get(i).getAsJsonObject();
					System.out.println(dataset.get("id").getAsString());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		// take in a config file
		// need to separate the URLs across pis

		// given a bit vector
		String bitvector = "11111111111101111";
		String[] keywordsArray = new String[] {
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

		long a = System.currentTimeMillis();

		for(String keywords : keywordsArray) {
			// lessen load for testing
			if(keywords.equals("binary+OR+search+OR+tree")) {
				mineWithConfiguration(keywords, bitvector);
			}

			// mineWithConfiguration(keywords, bitvector);
		}

		long b = System.currentTimeMillis();

		System.out.println("Process time: " + (b-a) + "ms");
	}
}