package DataAnalysis;

import java.io.*;
import java.util.*;

public class OverlapScoreCutoff {
	public static Map<String, Double> passingSimilarityFunctions;

	public static void printPassingSimilarityFunctions() {
		passingSimilarityFunctions.forEach((key, value) -> System.out.println(key + " -> " + value));

		System.out.println("Size of set: " + passingSimilarityFunctions.size());
	}

	public static void removeSimilarityFunctionsByString(String functionToMatch) {
		passingSimilarityFunctions.entrySet().removeIf(entry -> {
			// true means 'yes, remove this entry'
			for(int i = 0; i < 17; i++) {
				if(functionToMatch.charAt(i) == '1' && entry.getKey().charAt(i) != '1') {
					return true;
				}
			}
			return false;
		});
	}

	public static void filterSimilarityFunctionsByString(String functionToMatch, File overlapScoreFile) {
		try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(overlapScoreFile), "UTF-8"))) {
			for(String line; (line = br.readLine()) != null; ) {
				String[] splitLine = line.split("_");

				if(splitLine[0].length() != 17) {
					throw new IllegalArgumentException("[ERROR]: similarity function has less than 17 digits!");
				}

				boolean match = true;
				for(int i = 0; i < 17; i++) {
					if(splitLine[0].charAt(i) != functionToMatch.charAt(i)) {
						match = false;
						break;
					}
				}

				if(match) {
					passingSimilarityFunctions.put(splitLine[0], Double.parseDouble(splitLine[1]));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void filterSimilarityFunctionsByCutoff(double lowerBound, double upperBound, File overlapScoreFile) {
		try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(overlapScoreFile), "UTF-8"))) {
			for(String line; (line = br.readLine()) != null; ) {
				String[] splitLine = line.split("_");

				if(splitLine[0].length() != 17) {
					throw new IllegalArgumentException("[ERROR]: similarity function has less than 17 digits!");
				}

				// if number is equal to or greater than lower bound
				if(Double.compare(Double.parseDouble(splitLine[1]), lowerBound) != -1 && Double.compare(Double.parseDouble(splitLine[1]), upperBound) == -1) {
					passingSimilarityFunctions.put(splitLine[0], Double.parseDouble(splitLine[1]));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String printPrompt() {
		System.out.println("Choose the type of search");
		System.out.println("a) Lower Bound");
		System.out.println("b) Specific Properties");
		System.out.println("c) Lower Bound + Specific Properties");
		System.out.println("d) Lower Bound + Least Number of Properties");
		System.out.println("e) Lower Bound + Upper Bound");
		System.out.print("Response: ");

		Scanner sc = new Scanner(System.in);
		String ans = sc.nextLine();

		while(!ans.equals("a") && !ans.equals("b") && !ans.equals("c") && !ans.equals("d") && !ans.equals("e")) {
			System.out.println("[ERROR]: Invalid choice\n");
			System.out.println("Choose the type of search");
			System.out.println("a) Lower Bound");
			System.out.println("b) Specific Properties");
			System.out.println("c) Lower Bound + Specific Properties");
			System.out.println("d) Lower Bound + Least Number of Properties");
			System.out.println("e) Lower Bound + Upper Bound");
			System.out.print("Response: ");
			ans = sc.nextLine();
		}

		return ans;
	}

	@SuppressWarnings("Duplicates")
	public static void main(String[] args) {
		// find the latest overlap score file in saved
		// find all similarity functions with a cutoff
		// you could also find all similarity functions with a cutoff AND number of properties

		// find the most recent file
		long latestTime = -1;
		File[] allFiles = new File("saved").listFiles();
		if(allFiles != null) {
			for(File f : allFiles) {
				if(f.getName().startsWith("overlapScores")) {
					String[] parts = f.getName().split("_|\\.");
					long fileTime = Long.parseLong(parts[1]);
					if(fileTime > latestTime) {
						latestTime = fileTime;
					}
				}
			}
		}

		if(latestTime == -1) {
			throw new IllegalArgumentException("[ERROR]: no overlap scores file exists!");
		}

		passingSimilarityFunctions = new TreeMap<>();
		File overlapScoreFile = new File("saved/overlapScores_" + latestTime + ".txt");
		System.out.println("Most recent overlapScore file: " + overlapScoreFile.getName());

		// find by cutoff, find by number of properties, find by cutoff and number of properties
		String choice = printPrompt();
		switch(choice) {
			case "a": {
				System.out.print("Enter a lower bound for overlap score: ");
				Scanner sc = new Scanner(System.in);
				double input = Double.parseDouble(sc.nextLine());
				filterSimilarityFunctionsByCutoff(input, 1.1, overlapScoreFile);
				printPassingSimilarityFunctions();
				break;
			}
			case "b": {
				System.out.println("Type a similarity function with 1's and X's");
				System.out.println("1 denotes that we will search for that property");
				System.out.println("X denotes that we will not consider that property");
				System.out.print("Enter a similarity function to match: ");
				Scanner sc = new Scanner(System.in);
				String input = sc.nextLine();

				// need to do some serious error checking here

				filterSimilarityFunctionsByString(input, overlapScoreFile);
				printPassingSimilarityFunctions();

				break;
			}
			case "c": {
				System.out.print("Enter a lower bound for overlap score: ");
				Scanner sc = new Scanner(System.in);
				double input = Double.parseDouble(sc.nextLine());
				filterSimilarityFunctionsByCutoff(input, 1.1, overlapScoreFile);

				System.out.println("\nType a similarity function with 1's and X's");
				System.out.println("1 denotes that we will search for that property");
				System.out.println("X denotes that we will not consider that property");
				System.out.print("Enter a similarity function to match: ");
				sc = new Scanner(System.in);
				String inputFunction = sc.nextLine();

				// do some checking of input here

				removeSimilarityFunctionsByString(inputFunction);
				printPassingSimilarityFunctions();

				break;
			}
			case "d": {
				System.out.print("Enter a lower bound for overlap score: ");
				Scanner sc = new Scanner(System.in);
				double input = Double.parseDouble(sc.nextLine());
				filterSimilarityFunctionsByCutoff(input, 1.1, overlapScoreFile);

				Map<String, Double> smallestSimilarityFunctions = new HashMap<>();
				int minSize = 17;

				for(Map.Entry<String, Double> entry : passingSimilarityFunctions.entrySet()) {
					int size = 0;
					for(int i = 0; i < 17; i++) {
						if(entry.getKey().charAt(i) == '1') {
							size++;
						}
					}

					if(size == minSize) {
						smallestSimilarityFunctions.put(entry.getKey(), entry.getValue());
					}
					else if(size < minSize) {
						smallestSimilarityFunctions.clear();
						smallestSimilarityFunctions.put(entry.getKey(), entry.getValue());
						minSize = size;
					}
				}

				passingSimilarityFunctions = smallestSimilarityFunctions;
				printPassingSimilarityFunctions();

				break;
			}
			case "e": {
				System.out.print("Enter a lower bound for overlap score: ");
				Scanner sc = new Scanner(System.in);
				double lowerBound = Double.parseDouble(sc.nextLine());

				System.out.print("Enter an upper bound for overlap score: ");
				sc = new Scanner(System.in);
				double upperBound = Double.parseDouble(sc.nextLine());
				filterSimilarityFunctionsByCutoff(lowerBound, upperBound, overlapScoreFile);
				printPassingSimilarityFunctions();
				break;
			}
			default:
				throw new IllegalArgumentException("[ERROR]: Your invalid choice was somehow not caught!");
		}

		// could implement different sorting techniques, which would affect insertion to map and printing
		// could implement range search
		// maybe allow sort by score in the future

	}
}
