package RaspberryPi;

import com.google.gson.*;

import java.util.Map;
import java.util.HashMap;
import java.net.*;
import java.io.*;

public class ConfiguredMiner {
	public static Map<String, Boolean> flagMap;

	public static void mineWithConfiguration() {
		// this is where the mining will start
		// need file reader here to read in the URLs

//		for(Map.Entry<String, Boolean> entry : flagMap.entrySet()) {
//			System.out.println(entry.getKey() + " -> " + entry.getValue());
//		}

		try {
			StringBuilder sb = new StringBuilder();

			URL url = new URL("http://grok.ics.uci.edu:9551/solr/MoreLikeThisIndex/sim?q=snippet_code%3A(quick+OR+sort)&fl=id&wt=json&indent=true&bitvector=" + "11111111111111111");

			BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
			for (String line; (line = br.readLine()) != null;) {
				System.out.println(line);
				sb.append(line);
			}

			System.out.println("************");
			JsonParser parser = new JsonParser();
			JsonElement element = parser.parse(sb.toString());
			if(element.isJsonObject()) {
				// print out single respones by key
				JsonObject response = element.getAsJsonObject();
				System.out.println(response.get("OverlapNumber").getAsString());

				// iterate over map (not required)
				JsonArray hybridExemplarsContents = response.getAsJsonArray("HybridExemplarsContents");
				for(int i = 0; i < hybridExemplarsContents.size(); i++) {
					JsonObject dataset = hybridExemplarsContents.get(i).getAsJsonObject();
					System.out.println(dataset.get("id").getAsString());
				}
			}


		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		// take in a config file
		// need to separate the URLs across pis

		// given a bit vector
		String bitVector = "11111111111111111";
		flagMap = new HashMap<>();

		flagMap.put("authorName", bitVector.charAt(0) == '1');
		flagMap.put("className", bitVector.charAt(1) == '1');
		flagMap.put("complexity", bitVector.charAt(2) == '1');
		flagMap.put("fields", bitVector.charAt(3) == '1');
		flagMap.put("hasWildCard", bitVector.charAt(4) == '1');
		flagMap.put("isAbstract", bitVector.charAt(5) == '1');
		flagMap.put("isGeneric", bitVector.charAt(6) == '1');
		flagMap.put("imports", bitVector.charAt(7) == '1');
		flagMap.put("inverseImports", bitVector.charAt(8) == '1');			// check with Lee on this one
		flagMap.put("methodCallNames", bitVector.charAt(9) == '1');
		flagMap.put("methodDecNames", bitVector.charAt(10) == '1');
		flagMap.put("ownerName", bitVector.charAt(11) == '1');
		flagMap.put("package", bitVector.charAt(12) == '1');
		flagMap.put("parentClass", bitVector.charAt(13) == '1');
		flagMap.put("projectName", bitVector.charAt(14) == '1');
		flagMap.put("size", bitVector.charAt(15) == '1');
		flagMap.put("variableWords", bitVector.charAt(16) == '1');

		mineWithConfiguration();
	}
}