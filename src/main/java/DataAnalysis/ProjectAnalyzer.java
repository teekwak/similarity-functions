package DataAnalysis;

import Utilities.UsefulThings;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.net.URL;
import java.util.*;

public class ProjectAnalyzer {
	public static List<String> schemaKeys;

	public static void sendOutputToCSV(String schemaKey) {
		if(schemaKey == null) {
			throw new IllegalArgumentException("[ERROR]: schemaKey is null!");
		}

		try {
			URL url = new URL("http://grok.ics.uci.edu:9551/solr/MoreLikeThisIndex/select?q=parent%3Atrue&rows=0&wt=json&indent=true&facet=true&facet.field=" + schemaKey + "&facet.limit=-1");

			StringBuilder responseSb = new StringBuilder();
			BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
			for (String line; (line = br.readLine()) != null;) {
				responseSb.append(line);
			}
			br.close();

			JsonParser parser = new JsonParser();
			JsonElement element = parser.parse(responseSb.toString());
			if(element.isJsonObject()) {
				JsonObject response = element.getAsJsonObject();
				JsonObject facetCounts = response.getAsJsonObject("facet_counts");
				JsonObject facetFields = facetCounts.getAsJsonObject("facet_fields");
				JsonArray schemaKeyResponseArray = facetFields.getAsJsonArray(schemaKey);

				if(schemaKeyResponseArray == null) {
					throw new IllegalArgumentException("[ERROR]: You gave the server an incorrect schema key!");
				}

				File csvFile = new File("java_output/" + schemaKey + ".csv");
				if(csvFile.exists()) {
					boolean deleted = csvFile.delete();
					if(!deleted) {
						throw new IllegalStateException("[ERROR]: " + csvFile.getName() + " was unable to be deleted");
					}
				}

				try(PrintWriter pw = new PrintWriter(new FileOutputStream(csvFile, true))) {
					pw.write("Field,Count,\n");

					for(int i = 0; i < schemaKeyResponseArray.size(); i += 2) {
						String key = schemaKeyResponseArray.get(i).getAsString().replaceAll(",", "_(comma)_").replaceAll("\"", "_(doublequote)_").replaceAll("'", "_(singlequote)_");
						if(key.length() == 0) {
							key = "_(empty)_";
						}

						pw.write(key);
						pw.write(",");
						pw.write(schemaKeyResponseArray.get(i + 1).getAsString());
						pw.write(",\n");
					}

					pw.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		schemaKeys = new ArrayList<>(Arrays.asList(new String[] {
						"snippet_imports",
						"snippet_variable_names",
						"snippet_class_name",
						"snippet_author_name",
						"snippet_project_name",
						"snippet_method_invocation_names",
						"snippet_method_dec_names",
						"snippet_size",
						"snippet_imports_count",
						"snippet_complexity_density",
						"snippet_extends",
						"snippet_package",
						"snippet_number_of_fields",
						"snippet_is_generic",
						"snippet_is_abstract",
						"snippet_is_wildcard",
						"snippet_project_owner"
		}));

		int count = 0;
		int size = schemaKeys.size();

		UsefulThings.generateProgressBar(count, size);

		for(String s : schemaKeys) {
			sendOutputToCSV(s);
			count++;
			UsefulThings.generateProgressBar(count, size);
		}
	}
}
