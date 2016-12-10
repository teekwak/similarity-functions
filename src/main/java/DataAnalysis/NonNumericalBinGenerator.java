package DataAnalysis;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.net.URL;
import java.util.*;
import static java.util.Arrays.asList;

class Bin {
	double lowerBound;
	double upperBound;
	private Map<String, Integer> entries;

	Bin(double l, double u) {
		this.lowerBound = l;
		this.upperBound = u;
		this.entries = new LinkedHashMap<>();
	}

	void add(String key, int value) {
		entries.put(key, value);
	}

	int size() {
		return this.entries.size();
	}

	Map<String, Integer> getEntries() {
		return this.entries;
	}
}

public class NonNumericalBinGenerator {
	private static List<String> schemaKeys;
	private static List<Bin> bins;

	private static Set<String> simpleRandomSample(int sampleSize, int populationSize) {
		Set<String> sampleSet = new HashSet<>();
		Set<Integer> randomNumbers = new HashSet<>();

		while(randomNumbers.size() != sampleSize) {
			randomNumbers.add((int)(Math.random() * populationSize));
		}

		// TODO
		// how do i check that this is working properly?
		for(int randomNumber : randomNumbers) {
			int sumOfBinSizes = 0;
			for(Bin bin : bins) {
				if(randomNumber >= sumOfBinSizes && randomNumber <= sumOfBinSizes + bin.size()) {
					int currentIndex = sumOfBinSizes;
					for(Map.Entry<String, Integer> entry : bin.getEntries().entrySet()) {
						if(currentIndex == randomNumber) {
							sampleSet.add(entry.getKey());
							break;
						}

						currentIndex++;
					}
				}

				sumOfBinSizes += bin.size();
			}
		}

		return sampleSet;
	}

	// create bins based on n and number of bins
	public static List<Bin> generateBins(int n, int numberOfBins) {
		List<Bin> listOfBins = new ArrayList<>();

		int binWidth = (int)Math.ceil((double)n / numberOfBins);

		for(int i = 0; i < numberOfBins; i++) {
			listOfBins.add(new Bin(binWidth * i, binWidth * (i + 1)));
		}

		return listOfBins;
	}

	// put data into bins
	public static void populateBins(Map<String, Integer> data, List<Bin> bins) {
		data.forEach((key, value) -> {
			bins.forEach(bin -> {
				if(value > bin.lowerBound && value <= bin.upperBound) {
					bin.add(key, value);
				}
			});
		});
	}

	// calculate number of bins based on Freedman-Diaconis choice
	public static int freedmanDiaconisChoice(double iqr, int n) {
		return (int)Math.ceil(2 * iqr / Math.pow(n, 1/3));
	}

	// find the median of a list of integers
	public static double findMedian(List<Integer> list) {
		int middle = list.size() / 2;

		if(list.size() % 2 == 0) {
			int left = middle - 1;
			return (double)(list.get(left) + list.get(middle)) / 2;
		}
		else {
			return list.get(middle);
		}
	}

	// find the IQR of an entire list
	private static double calculateIQR(List<Integer> list) {
		int middle = list.size() / 2;
		double firstQuartile, thirdQuartile;
		if(list.size() % 2 == 0) {
			firstQuartile = findMedian(list.subList(0, middle));
			thirdQuartile = findMedian(list.subList(middle, list.size()));
		}
		else {
			firstQuartile = findMedian(list.subList(0, middle));
			thirdQuartile = findMedian(list.subList(middle + 1, list.size()));
		}

		return thirdQuartile - firstQuartile;
	}

	// fancy comparator notation to sort the map by value (lowest to highest)
	private static Map<String, Integer> sortMapByValue(Map<String, Integer> map) {
		List<Map.Entry<String, Integer>> entryList = new ArrayList<>(map.entrySet());
		entryList.sort(Comparator.comparing(Map.Entry::getValue));

		map = new LinkedHashMap<>();
		for(Map.Entry<String, Integer> e : entryList) {
			map.put(e.getKey(), e.getValue());
		}

		return map;
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

	private static String queryForURL(String server, String key, String value, int rowNumber) {
		try {
			URL url = new URL("http://" + server + ":9551/solr/MoreLikeThisIndex/select?q=parent%3Atrue+AND+" + key + ":" + value + "&start=" + rowNumber + "&rows=1&fl=id&wt=json&indent=true&");
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

	public static void main(String[] args) {
//		Map<String, Integer> fieldToOccurrenceMap = sortMapByValue(populateWithCSVFile("snippet_author_name.csv"));
//		List<Integer> valuesList = new ArrayList<>(fieldToOccurrenceMap.values());
//		// System.out.println(calculateIQR(valuesList));
//		// System.out.println(freedmanDiaconisChoice(calculateIQR(valuesList), valuesList.size()));
//
//		bins = generateBins(valuesList.size(), freedmanDiaconisChoice(calculateIQR(valuesList), valuesList.size()));
//		populateBins(fieldToOccurrenceMap, bins);
//
//		// bins.forEach(bin -> System.out.println(bin.size()));
//
//		simpleRandomSample(10, fieldToOccurrenceMap.size()).forEach(System.out::println);

		// what happens after this?
		// we remove the sample from the population
		// we then do SRS again on the new bucket based on a new "populateWithCSVFile"
		// shit, so this needs to be super extra modular
		// or we need to keep track of the ones we already picked
		// how do we get project names just from the author name and count?





		// outline the entire program
		//
		// for each file in set
		// take in a file name
		// stores all values of the file into a map
		// remove all values that are in the save data structure
		// generate bins from that map
		// put the values into the bins
		// do SRS on the bins
		// put those values into a save data structure

		schemaKeys = new ArrayList<>(asList(
//						"snippet_imports",
//						"snippet_variable_names",
//						"snippet_class_name",
//						"snippet_author_name",
//						"snippet_project_name",
//						"snippet_method_invocation_names",
//						"snippet_method_dec_names",
//						// "snippet_size",
//						// "snippet_imports_count",
//						// "snippet_complexity_density",
//						"snippet_extends",
//						"snippet_package",
//						// "snippet_number_of_fields",
//						// "snippet_is_generic", // fails because of median?
//						// "snippet_is_abstract", // fails because of median?
//						// "snippet_is_wildcard", // fails because of median?
						"snippet_project_owner"
		));

		Map<String, Set<String>> saved = new HashMap<>(); // this will be used to save the values from the stratified sampling + SRS
		for(String fileName : schemaKeys) {
			System.out.println("Running " + fileName);

			Map<String, Integer> fieldToOccurrenceMap = sortMapByValue(populateWithCSVFile(fileName));

			List<Integer> valuesList = new ArrayList<>(fieldToOccurrenceMap.values());
			bins = generateBins(valuesList.size(), freedmanDiaconisChoice(calculateIQR(valuesList), valuesList.size()));
			populateBins(fieldToOccurrenceMap, bins);

			// i should have an add to saved function?
			simpleRandomSample(10, fieldToOccurrenceMap.size()).forEach(value -> {
				saved.putIfAbsent(fileName, new HashSet<>());
				saved.get(fileName).add(value);
			});
		}

		System.out.println(saved.size());

		// port=http://grok.ics.uci.edu:9551 // just make sure to run MoreLikeThisSearchEngineFull and not MoreLikeThisSearchEngine2. check the screens!

		String server = "grok.ics.uci.edu";

		Set<String> savedURLs = new HashSet<>(); // this will be used to get values using SRS
		saved.forEach((key, valueSet) -> {
			valueSet.forEach(value -> {

				// query the server once to get the total number
				int numFound = 0;
				try {
					URL url = new URL("http://" + server + ":9551/solr/MoreLikeThisIndex/select?q=parent%3Atrue+AND+" + key + ":" + value + "&rows=0&wt=json&indent=true&");
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

						System.out.println(numFound);
					}

				} catch (IOException e) {
					e.printStackTrace();
				}

				if(numFound == 0) {
					throw new IllegalArgumentException("[ERROR]: the number found cannot be zero!");
				}

				// query until you can add something to the set
				while(true) {
					String classURL = queryForURL(server, key, value, (int)(Math.random() * numFound));
					if(!savedURLs.contains(classURL)) {
						savedURLs.add(classURL);
						break;
					}
				}
			});
		});

		savedURLs.forEach(System.out::println);
	}
}
