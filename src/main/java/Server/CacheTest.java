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

/**
 * Created by Kwak on 10/12/16.
 */
public class CacheTest {
	public static Set<String> firstSet;

	// dummy function
	public static Set<String> sendQuery(String keywords, String bitvector) {
		Set<String> s = new HashSet<>();

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
				// System.out.println(line);
			}

			JsonParser parser = new JsonParser();
			JsonElement element = parser.parse(responseSb.toString());
			if(element.isJsonObject()) {
				// print out single response by key
				JsonObject response = element.getAsJsonObject();

				JsonArray TFIDFResults = response.getAsJsonArray("TFIDFResults");

				int size = TFIDFResults.size();
				for(int i = 0; i < size; i++) {
					s.add(TFIDFResults.get(i).getAsString());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return s;
	}

	public static void main(String[] args) {
		String bitVector = "11111111111111111";

		for(int i = 0; i < 11; i++) {
			if(i == 0) {
				// store in set
				firstSet = sendQuery("database+OR+connection+OR+manager", bitVector);
			}
			else {
				Set<String> testSet = sendQuery("database+OR+connection+OR+manager", bitVector);

				if(testSet.size() != firstSet.size() || !testSet.containsAll(firstSet)) {
					System.out.println("not a complete match");
					System.exit(-1);
				}
				else {
					System.out.println("complete match");
				}
			}
		}

		System.out.println("made it!");
	}
}
