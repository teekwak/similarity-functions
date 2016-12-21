package DataAnalysis;

import Utilities.Statistics;
import Utilities.UsefulThings;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;

/**
 * Created by Kwak
 */

class Bin {
	Map<String, Integer> mapping;

	Bin() {
		this.mapping = new LinkedHashMap<>();
	}

	Map<String, Integer> getMapping() {
		return this.mapping;
	}

	int size() {
		int size = 0;

		for(int value : this.mapping.values()) {
			size += value;
		}

		return size;
	}

	void add(String key, int value) {
		mapping.put(key, value);
	}
}

class BoundedBin extends Bin {
	double lowerBound;
	double upperBound;

	BoundedBin(double l, double u) {
		this.lowerBound = l;
		this.upperBound = u;
		this.mapping = new LinkedHashMap<>();
	}
}

public class SampleURLGenerator {
	private static final int SAMPLE_SIZE = 8600;
	private static Random randomObject;
	private static Set<String> savedURLs;

	private static int getNumFound(String server, String key, String value) {
		// query the server once to get the total number
		int numFound = 0;
		try {
			URI uri = new URI("http://" + server + ":9551/solr/MoreLikeThisIndex/select?q=parent%3Atrue+AND+" + key + "%3A" + URLEncoder.encode(value, "UTF-8") + "&rows=0&wt=json&indent=true");
			URL url = uri.toURL();

			StringBuilder responseSb = new StringBuilder();
			BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
			for (String line; (line = br.readLine()) != null;) {
				responseSb.append(line);
			}
			br.close();

			JsonParser parser = new JsonParser();
			JsonElement element = parser.parse(responseSb.toString());

			if(element.isJsonObject()) {
				JsonObject totalResponse = element.getAsJsonObject();
				JsonObject response = totalResponse.getAsJsonObject("response");
				numFound = response.get("numFound").getAsInt();
			}
		} catch (IOException|URISyntaxException e) {
			e.printStackTrace();
		}

		if(numFound == 0) {
			try {
				System.out.println("http://" + server + ":9551/solr/MoreLikeThisIndex/select?q=parent%3Atrue+AND+" + key + "%3A" + URLEncoder.encode(value, "UTF-8") + "&rows=0&wt=json&indent=true");
				throw new IllegalArgumentException("[ERROR]: the number found cannot be zero!");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return numFound;
	}

	private static String queryForURL(String server, String key, String value, int rowNumber) {
		try {
			URI uri = new URI("http://" + server + ":9551/solr/MoreLikeThisIndex/select?q=parent%3Atrue+AND+" + key + "%3A" + URLEncoder.encode(value, "UTF-8") + "&start=" + rowNumber + "&rows=1&fl=id&wt=json&indent=true");
			URL url = uri.toURL();

			StringBuilder responseSb = new StringBuilder();
			BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
			for (String line; (line = br.readLine()) != null; ) {
				responseSb.append(line);
			}
			br.close();

			JsonParser parser = new JsonParser();
			JsonElement element = parser.parse(responseSb.toString());
			if(element.isJsonObject()) {
				return element.getAsJsonObject().getAsJsonObject("response").getAsJsonArray("docs").get(0).getAsJsonObject().get("id").getAsString();
			}
		} catch (IOException|URISyntaxException e) {
			e.printStackTrace();
		}

		throw new IllegalArgumentException("You should not be here!");
	}

	// sort bins by fractional part, which is calculated as bin size / total size
	// round up those closest to 1 until targetValue is met
	// randomly select X elements from each bin
	// return that list and merge with the master list
	// VERY IMPORTANT THAT SAMPLE SIZE IS DIVISIBLE BY 100 (makes things nice at the end)
	private static void stratifiedSample(String schemaKey, List<Bin> bins) {
		int populationSize = 0;
		for(Bin b : bins) {
			for(int value : b.getMapping().values()) {
				populationSize += value;
			}
		}

		final int finalPopulationSize = populationSize; // damn you lambda expressions

		// sort by highest decimal to lowest decimal
		PriorityQueue<Integer> binPercentagesByDecimal = new PriorityQueue<>(
			(a, b) -> {
				double firstDecimal = (double)bins.get(a).size() / finalPopulationSize;
				double secondDecimal = (double)bins.get(b).size() / finalPopulationSize;
				return Double.compare((secondDecimal * 100) % 100, (firstDecimal * 100) % 100);
			}
		);

		int difference = 100;
		List<Integer> percentageFromEachBin = new ArrayList<>();

		for(int i = 0; i < bins.size(); i++) {
			int flooredPercentage = (int)Math.floor((double)bins.get(i).size() / finalPopulationSize * 100);
			percentageFromEachBin.add(flooredPercentage);

			if(bins.get(i).size() != 0) {
				binPercentagesByDecimal.add(i);
			}

			difference -= flooredPercentage;
		}

		while(difference > 0 && !binPercentagesByDecimal.isEmpty()) {
			int index = binPercentagesByDecimal.poll();
			int newValue = percentageFromEachBin.get(index) + 1;
			percentageFromEachBin.set(index, newValue);
			difference -= 1;
		}

		// simple random sample from each bin
		for(int i = 0; i < bins.size(); i++) {
			if(percentageFromEachBin.get(i) != 0) {
				addUniqueURLsFromBin(schemaKey, bins.get(i), percentageFromEachBin.get(i) * SAMPLE_SIZE / 100);
			}
		}
	}

	// simple random sampling
	private static void addUniqueURLsFromBin(String schemaKey, Bin bin, int amount) {
		int counter = 0;
		String server = "grok.ics.uci.edu";
		while(counter < amount) {
			int rand = randomObject.nextInt(bin.size());
			int index = 0;

			for(Map.Entry<String, Integer> entry : bin.getMapping().entrySet()) {
				if(rand < index) {
					String classURL;
					if(schemaKey.equals("snippet_author_name") || schemaKey.equals("snippet_project_owner")) {
						String newKey = "\"" + entry.getKey() + "\"";
						int numFound = getNumFound(server, schemaKey, newKey);
						classURL = queryForURL(server, schemaKey, newKey, randomObject.nextInt(numFound));
					}
					else {
						int numFound = getNumFound(server, schemaKey, entry.getKey());
						classURL = queryForURL(server, schemaKey, entry.getKey(), randomObject.nextInt(numFound));
					}

					if(!savedURLs.contains(classURL)) {
						savedURLs.add(classURL);
						counter++;

						UsefulThings.generateProgressBar(counter, amount);
					}

					break;
				}

				index += entry.getValue();
			}
		}

		UsefulThings.generateProgressBar(counter, amount);
	}

	private static Map<String, Integer> populateWithCSVFile(String fileName) {
		Map<String, Integer> map = new LinkedHashMap<>();

		String directory = "java_output";
		File file = new File(directory + "/" + fileName + ".csv");

		try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"))) {
			int counter = 0;
			for(String line; (line = br.readLine()) != null; ) {
				if(counter != 0) {
					String[] splitAtComma = line.split(",");
					map.put(splitAtComma[0], Integer.parseInt(splitAtComma[1]));
				}

				counter++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return map;
	}

	private static List<Bin> createEmptyBoundedBins(int n, int numberOfBins) {
		List<Bin> listOfBins = new ArrayList<>();

		int binWidth = (int)Math.ceil((double)n / numberOfBins);

		for(int i = 0; i < numberOfBins; i++) {
			listOfBins.add(new BoundedBin(binWidth * i, binWidth * (i + 1)));
		}

		return listOfBins;
	}

	private static String addPunctuationMarks(String input) {
		return input.replace("_(comma)_", ",").replace("_(doublequote)_", "\"").replace("_(singlequote)_", "'").replace("_(tab)_", "\t").replace("_(empty)_", "");
	}

	private static String escapeCharactersForSolr(String input) {
		return input
						.replace("\\", "\\\\")
						.replace("\"", "\\\"")
						.replace("[", "\\[")
						.replace("]", "\\]")
						.replace("(", "\\(")
						.replace(")", "\\)")
						.replace("{", "\\{")
						.replace("}", "\\}")
						.replace("*", "\\*")
						.replace("+", "\\+")
						.replace("-", "\\-")
						.replace("&", "\\&")
						.replace("!", "\\!")
						.replace("|", "\\|")
						.replace("~", "\\~")
						.replace("?", "\\?")
						.replace(";", "\\;")
						.replace(":", "\\:")
						.replace(" ", "\\ ")
						.replace("^", "\\^");
	}

	// put data into bins
	// number of bins equals number of keys in map
	private static void populateBins(Map<String, Integer> data, List<Bin> bins) {
		int counter = 0;
		for(Map.Entry<String, Integer> entry : data.entrySet()) {
			bins.get(counter).add(entry.getKey(), entry.getValue());
			counter++;
		}
	}

	// put data into bins
	// number of bins calculated using FD
	private static void populateBoundedBins(Map<String, Integer> data, List<Bin> bins) {
		for(Map.Entry<String, Integer> entry : data.entrySet()) {
			for(Bin bin : bins) {
				BoundedBin b = (BoundedBin)bin;
				if(entry.getValue() >= b.lowerBound && entry.getValue() < b.upperBound) {
					bin.add(escapeCharactersForSolr(addPunctuationMarks(entry.getKey())), entry.getValue());
					break;
				}
			}
		}
	}

	// start
	private static void init() {
		Map<String, Boolean> schemaKeys = new HashMap<>();
		{
//			schemaKeys.put("snippet_imports", true);
//			schemaKeys.put("snippet_variable_names", true);
//			schemaKeys.put("snippet_class_name", true);
//			schemaKeys.put("snippet_author_name", true);
//			schemaKeys.put("snippet_project_name", true);
			schemaKeys.put("snippet_method_invocation_names", true);
//			schemaKeys.put("snippet_method_dec_names", true);
//			schemaKeys.put("snippet_size", true);
//			schemaKeys.put("snippet_imports_count", true);
//			schemaKeys.put("snippet_complexity_density", true);
//			schemaKeys.put("snippet_extends", true);
//			schemaKeys.put("snippet_package", true);
//			schemaKeys.put("snippet_number_of_fields", true);
//			schemaKeys.put("snippet_is_generic", false); // fails because of FD calculation
//			schemaKeys.put("snippet_is_abstract", false); // fails because of FD calculation
//			schemaKeys.put("snippet_is_wildcard", false); // fails because of FD calculation
//			schemaKeys.put("snippet_project_owner", true);
		}

		for(Map.Entry<String, Boolean> entry : schemaKeys.entrySet()) {
			String schemaKey = entry.getKey();
			System.out.println("Running " + schemaKey);

			Map<String, Integer> fieldToOccurrenceMap = Statistics.sortByValue(populateWithCSVFile(schemaKey));

			List<Bin> bins = new ArrayList<>();

			if(entry.getValue()) {
				List<Integer> valuesList = new ArrayList<>(fieldToOccurrenceMap.values());
				bins = createEmptyBoundedBins(valuesList.size(), Statistics.calculateFreedmanDiaconisChoice(Statistics.calculateIQR(valuesList), valuesList.size()));
				populateBoundedBins(fieldToOccurrenceMap, bins);
			}
			else {
				for(int i = 0; i < fieldToOccurrenceMap.size(); i++) {
					bins.add(new Bin());
				}

				populateBins(fieldToOccurrenceMap, bins);
			}

			stratifiedSample(schemaKey, bins);
		}

		UsefulThings.printDataStructureToFile(savedURLs, "java_output/final_urls.txt");
	}

	public static void main(String[] args) {
		randomObject = new Random();
		savedURLs = new HashSet<>();
		init();
	}
}
