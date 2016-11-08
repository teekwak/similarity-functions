package DataAnalysis;

import Utilities.UsefulThings;
import java.io.*;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class HashMapMultipleFileReader {
	private static Map<String, Double> bitvectorMap;

	private static void printPerfectScores() {
		int perfectCounter = 0;

		for(Map.Entry<String, Double> entry : bitvectorMap.entrySet()) {
			if(entry.getValue().equals(1.0)) {
				perfectCounter++;
			}
		}

		System.out.println("\nNumber of perfect scores: " + perfectCounter);
	}

	private static void printToFile(long timestamp) {
		int count = 0;

		for(Map.Entry<String, Double> entry : bitvectorMap.entrySet()) {
			try (
				FileWriter fw = new FileWriter("saved/overlapScores_" + timestamp + ".txt", true);
				BufferedWriter bw = new BufferedWriter(fw);
				PrintWriter pw = new PrintWriter(bw)
			) {
				pw.println(entry.getKey() + "_" + entry.getValue());
			} catch (IOException e) {
				e.printStackTrace();
			}
			count++;
			UsefulThings.generateProgressBar(count, 131072);
		}
	}

	private static void normalizeScores(int fileCounter) {
		if(fileCounter == 0) {
			throw new IllegalArgumentException("[ERROR]: fileCounter cannot equal zero!");
		}

		for(Map.Entry<String, Double> entry : bitvectorMap.entrySet()) {
			bitvectorMap.put(entry.getKey(), entry.getValue() / (fileCounter * 10));
		}
	}

	private static void readScoreFiles(String directoryPath) {
		File dir = new File(directoryPath);

		if(!dir.isDirectory()) {
			System.out.println(directoryPath + " is not a directory!");
			return;
		}

		File[] directoryListing = dir.listFiles();
		int fileCounter = 0;

		if(directoryListing != null) {
			for(File file : directoryListing) {
				if(file.getName().startsWith("pi")) {
					try (BufferedReader br = new BufferedReader(new FileReader(file))) {
						for(String line; (line = br.readLine()) != null; ) {
							String[] parts = line.split("_");
							Double mapValue = bitvectorMap.get(parts[0]);

							bitvectorMap.put(parts[0], mapValue + Double.parseDouble(parts[1]));
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
					fileCounter++;
				}
			}
		}

		normalizeScores(fileCounter);
	}

	public static void main(String[] args) {
		String binaryFilePath = "saved/allBinaryNumbers.txt";
		String directoryPath = "saved";
		bitvectorMap = new HashMap<>();

		try (BufferedReader br = new BufferedReader(new FileReader(new File(binaryFilePath)))){
			for(String line; (line = br.readLine()) != null; ) {
				bitvectorMap.put(line, 0.0);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		readScoreFiles(directoryPath);
		printToFile(Instant.now().getEpochSecond() );
		printPerfectScores();
	}
}
