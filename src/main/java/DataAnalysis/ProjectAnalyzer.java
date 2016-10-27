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
						pw.write(schemaKeyResponseArray.get(i).getAsString());
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

	/*

	snippet_class_name
	snippet_class_name_delimited
	snippet_granularity
	parent
	snippet_address_lower_bound
	snippet_address_upper_bound
	snippet_has_java_comments
	snippet_is_abstract
	snippet_is_anonymous
	snippet_is_generic
	snippet_is_innerClass
	snippet_is_wildcard
	snippet_imports
	snippet_imports_short
	snippet_imports_count
	snippet_extends
	snippet_extends_short
	snippet_path_complexity_class_sum
	snippet_complexity_density
	snippet_number_of_fields
	snippet_number_of_functions
	snippet_method_dec_names
	snippet_method_invocation_names
	snippet_all_authors
	snippet_author_count
	snippet_all_author_avatars
	snippet_all_author_emails
	snippet_author_name
	snippet_author_email
	snippet_author_avatar
	snippet_author_site_admin
	snippet_author_type
	snippet_project_address
	snippet_project_name
	snippet_project_owner
	snippet_project_owner_avatar
	snippet_project_is_fork
	snippet_project_description
	snippet_all_version_comments
	snippet_all_dates
	snippet_all_versions
	month
	day
	year
	snippet_size
	snippet_number_of_lines
	snippet_number_of_insertions
	snippet_number_of_deletions
	snippet_total_insertions
	snippet_total_deletions
	snippet_insertion_code_churn
	snippet_deleted_code_churn
	snippet_insertion_deletion_code_churn
	snippet_this_version
	snippet_version_comment
	snippet_last_updated
	snippet_address
	id
	expand_id
	snippet_containing_class_id
	snippet_containing_class_complexity_sum
	snippet_variable_types
	snippet_variable_types_short
	snippet_variable_names
	snippet_variable_names_delimited
	_version_

	 */

	public static void main(String[] args) {
		schemaKeys = new ArrayList<>(Arrays.asList(new String[] {
						"snippet_project_name",
						"snippet_size"
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
