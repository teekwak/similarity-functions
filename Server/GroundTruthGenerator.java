package Server;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class GroundTruthGenerator {
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

	public static Set<String> getGroundTruthForKeyword(String keywords) {
		Set<String> set = new HashSet<>();

		for(int i = 0; i < 100; i++) {
			try {
				StringBuilder querySb = new StringBuilder();
				querySb.append("http://grok.ics.uci.edu:9551/solr/MoreLikeThisIndex/sim/?q=snippet_code:(");
				querySb.append(keywords);
				querySb.append(")+AND+snippet_number_of_functions:[1+TO+*]+AND+parent:true+AND+snippet_is_innerClass:false+AND+snippet_is_anonymous:false&start=0&fl=id&indent=on&wt=json&rows=1000&started=false&test=false&conciseCount=100&bitvector=11111111111111111");

				// REALLY GOTTA CHANGE THE URL TO THE CORRECT ONE
				URL url = new URL(querySb.toString());

				StringBuilder responseSb = new StringBuilder();
				BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
				for (String line; (line = br.readLine()) != null; ) {
					responseSb.append(line);
				}

				JsonParser parser = new JsonParser();
				JsonElement element = parser.parse(responseSb.toString());
				if (element.isJsonObject()) {
					JsonObject response = element.getAsJsonObject();
					JsonArray hybridExemplarsContents = response.getAsJsonArray("HybridExemplarsContents");
					for (int j = 0; j < hybridExemplarsContents.size(); j++) {
						JsonObject dataset = hybridExemplarsContents.get(j).getAsJsonObject();
						set.add(dataset.get("id").getAsString());
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(-1);
			}
		}

		return set;
	}

	public static void main(String[] args) {
		for(String keywords : keywordsArray) {
			Set<String> set = getGroundTruthForKeyword(keywords);

			// LOOK OUT! does the server cache results?

			System.out.println("Ground truth for " + keywords);
			for(String s : set) {
				System.out.println(s);
			}
			System.out.println("**********");
		}
	}
}
