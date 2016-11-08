package DataAnalysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

public class SingleBitVectorAnalyzer {
	private static void inputValidation(String input) {
		if(input.length() != 17) {
			throw new IllegalArgumentException("[ERROR]: The input provided was not 17 digits long.");
		}

		for(int i = 0; i < input.length(); i++) {
			char current = input.charAt(i);

			if(!Character.isDigit(current)) {
				throw new IllegalArgumentException("[ERROR]: The bit vector must be only digits.");
			}

			if(current != '0' && current != '1') {
				throw new IllegalArgumentException("[ERROR]: The bit vector must be only 0's or 1's.");
			}
		}
	}

	private static Map<String, Integer> getScorePerFile(String bitvector) {
		String directoryPath = "saved";
		Map<String, Integer> returnMap = new LinkedHashMap<>();

		// read file
		File dir = new File(directoryPath);

		if(!dir.isDirectory()) {
			throw new IllegalArgumentException("[ERROR]: " + directoryPath + " is not a directory!");
		}

		File[] directoryListing = dir.listFiles();
		if(directoryListing != null) {
			for(File file : directoryListing) {
				if(file.getName().startsWith("pi")) {
					try (BufferedReader br = new BufferedReader(new FileReader(file))) {
						for(String line; (line = br.readLine()) != null; ) {
							if(line.startsWith(bitvector)) {
								returnMap.put(file.getName().split("_")[1], Integer.parseInt(line.split("_")[1]));
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

		return returnMap;
	}

	public static void main(String[] args) {
		System.out.print("Enter the 17-digit bit vector you want to analyze: ");
		Scanner sc = new Scanner(System.in);
		String input = sc.nextLine();

		// do input validation
		inputValidation(input);

		// print out scores
		getScorePerFile(input).forEach((key, value) -> System.out.println(key + ": " + value));
	}
}
