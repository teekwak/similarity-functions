package DataAnalysis;

import Utilities.Statistics;
import Utilities.UsefulThings;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;

/**
 * Created by Kwak
 */

class BoundedBin {
	double lowerBound;
	double upperBound;
	private Map<String, Integer> mapping;

	BoundedBin(double l, double u) {
		this.lowerBound = l;
		this.upperBound = u;
		this.mapping = new LinkedHashMap<>();
	}

	void add(String key, int value) {
		mapping.put(key, value);
	}

	int size() {
		int size = 0;

		for(int value : this.mapping.values()) {
			size += value;
		}

		return size;
	}

	Map<String, Integer> getMapping() {
		return this.mapping;
	}
}

class TrueFalseBin {
	private String key;
	private int size;

	TrueFalseBin(String k, int s) {
		this.key = k;
		this.size = s;
	}

	String getKey() {
		return this.key;
	}

	int size() {
		return this.size;
	}
}

public class SampleURLGenerator {
	private static final int SAMPLE_SIZE = 8600;
	private static final int POPULATION_SIZE = 10829321;
	private static Random randomObject;

	private static int getNumFound(String server, String key, String value) {
		// query the server once to get the total number
		int numFound = 0;
		try {
			URL url = new URL("http://" + server + ":9551/solr/MoreLikeThisIndex/select?q=parent%3Atrue+AND+" + key + "%3A\"" + URLEncoder.encode(value, "UTF-8") + "\"&rows=0&wt=json&indent=true");
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
		} catch (IOException e) {
			e.printStackTrace();
		}

		if(numFound == 0) {
			try {
				System.out.println("http://" + server + ":9551/solr/MoreLikeThisIndex/select?q=parent%3Atrue+AND+" + key + "%3A\"" + URLEncoder.encode(value, "UTF-8") + "\"&rows=0&wt=json&indent=true");
				throw new IllegalArgumentException("[ERROR]: the number found cannot be zero!");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return numFound;
	}

	private static String queryForURL(String server, String key, String value, int rowNumber) {
		try {
			URL url = new URL("http://" + server + ":9551/solr/MoreLikeThisIndex/select?q=parent%3Atrue+AND+" + key + "%3A\"" + URLEncoder.encode(value, "UTF-8") + "\"&start=" + rowNumber + "&rows=1&fl=id&wt=json&indent=true");
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
		} catch (IOException e) {
			e.printStackTrace();
		}

		throw new IllegalArgumentException("You should not be here!");
	}

	private static Set<Integer> generateRandomNumberSet(int size, int upperBound) {
		Set<Integer> randomNumbers = new TreeSet<>();

		while(randomNumbers.size() != size) {
			randomNumbers.add(randomObject.nextInt(upperBound));
		}

		return randomNumbers;
	}

	// sort bins by fractional part, which is calculated as bin size / total size
	// round up those closest to 1 until targetValue is met
	// randomly select X elements from each bin
	// return that list and merge with the master list
	// VERY IMPORTANT THAT SAMPLE SIZE IS DIVISIBLE BY 100 (makes things nice at the end)
	private static List<String> stratifiedSample(List<BoundedBin> bins) {
		List<String> sampleValues = new ArrayList<>();

		int populationSize = 0;
		for(BoundedBin b : bins) {
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
				sampleValues.addAll(simpleRandomSampleFromBin(bins.get(i), percentageFromEachBin.get(i) * SAMPLE_SIZE / 100));
			}
		}

		return sampleValues;
	}

	private static List<String> simpleRandomSampleFromBin(BoundedBin bin, int amount) {
		List<String> sampleValues = new ArrayList<>();
		List<Integer> randomNumbers = new ArrayList<>(generateRandomNumberSet(amount, bin.size()));

		int currentIndex = 0;
		int randomNumberCounter = 0;

		for(Map.Entry<String, Integer> entry : bin.getMapping().entrySet()) {
			currentIndex += entry.getValue();

			while(randomNumberCounter < randomNumbers.size() && currentIndex > randomNumbers.get(randomNumberCounter)) {
				sampleValues.add(entry.getKey());
				randomNumberCounter++;
			}
		}

		return sampleValues;
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

	private static List<String> createSampleValues(List<TrueFalseBin> bins) {
		List<String> sampleValues = new ArrayList<>();
		Set<Integer> randomNumbers = generateRandomNumberSet(SAMPLE_SIZE, POPULATION_SIZE);

		for(int randomNumber : randomNumbers) {
			int sumOfBinSizes = 0;
			for(TrueFalseBin bin : bins) {
				if(randomNumber < sumOfBinSizes + bin.size()) {
					sampleValues.add(bin.getKey());
					break;
				}
				sumOfBinSizes += bin.size();
			}
		}

		return sampleValues;
	}

	private static List<BoundedBin> createEmptyBins(int n, int numberOfBins) {
		List<BoundedBin> listOfBins = new ArrayList<>();

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
		return input.replace("\\", "\\\\");
	}

	// put data into bins
	// this fails for true/false
	private static void populateBins(Map<String, Integer> data, List<BoundedBin> bins) {
		for(Map.Entry<String, Integer> entry : data.entrySet()) {
			for(BoundedBin bin : bins) {
				if(entry.getValue() >= bin.lowerBound && entry.getValue() < bin.upperBound) {
					bin.add(escapeCharactersForSolr(addPunctuationMarks(entry.getKey())), entry.getValue());
					break;
				}
			}
		}
	}

	public static void main(String[] args) {
		randomObject = new Random();

		Map<String, Boolean> schemaKeys = new HashMap<>();
		{
			schemaKeys.put("snippet_imports", true);
			schemaKeys.put("snippet_variable_names", true);
			schemaKeys.put("snippet_class_name", true);
			schemaKeys.put("snippet_author_name", true);
			schemaKeys.put("snippet_project_name", true);
			schemaKeys.put("snippet_method_invocation_names", true);
			schemaKeys.put("snippet_method_dec_names", true);
			schemaKeys.put("snippet_size", true);
			schemaKeys.put("snippet_imports_count", true);
			schemaKeys.put("snippet_complexity_density", true);
			schemaKeys.put("snippet_extends", true);
			schemaKeys.put("snippet_package", true);
			schemaKeys.put("snippet_number_of_fields", true);
			schemaKeys.put("snippet_is_generic", false); // fails because of FD calculation
			schemaKeys.put("snippet_is_abstract", false); // fails because of FD calculation
			schemaKeys.put("snippet_is_wildcard", false); // fails because of FD calculation
			schemaKeys.put("snippet_project_owner", true);
		}

		Map<String, List<String>> saved = new HashMap<>(); // this will be used to save the values from the stratified sampling + SRS

		for(Map.Entry<String, Boolean> entry : schemaKeys.entrySet()) {
			String schemaKey = entry.getKey();
			System.out.println("Running " + schemaKey);

			Map<String, Integer> fieldToOccurrenceMap = Statistics.sortByValue(populateWithCSVFile(schemaKey));

			if(entry.getValue()) {
				List<Integer> valuesList = new ArrayList<>(fieldToOccurrenceMap.values());
				List<BoundedBin> bins = createEmptyBins(valuesList.size(), Statistics.calculateFreedmanDiaconisChoice(Statistics.calculateIQR(valuesList), valuesList.size()));
				populateBins(fieldToOccurrenceMap, bins);

				stratifiedSample(bins).forEach(value -> {
					saved.putIfAbsent(schemaKey, new ArrayList<>());
					saved.get(schemaKey).add(value);
				});
			}
			else {
				List<TrueFalseBin> bins = new ArrayList<>();
				fieldToOccurrenceMap.forEach((field, occurrence) -> bins.add(new TrueFalseBin(field, occurrence)));
				saved.put(schemaKey, createSampleValues(bins));
			}
		}

		String server = "grok.ics.uci.edu";

		for(Map.Entry<String, List<String>> entry : saved.entrySet()) {
			Set<String> savedURLs = new HashSet<>(); // this will be used to get values using SRS

			int counter = 0;

			for(String value : entry.getValue()) {
				int numFound = getNumFound(server, entry.getKey(), value);

				UsefulThings.generateProgressBar(counter, SAMPLE_SIZE);

				// query until you can add something to the set
				while(true) {
					String classURL = queryForURL(server, entry.getKey(), value, randomObject.nextInt(numFound));
					if(!savedURLs.contains(classURL)) {
						savedURLs.add(classURL);
						counter++;
						break;
					}
				}
			}

			UsefulThings.generateProgressBar(counter, SAMPLE_SIZE);

			UsefulThings.printDataStructureToFile(savedURLs, "java_output/final_URL_list.txt");
		}
	}
}
