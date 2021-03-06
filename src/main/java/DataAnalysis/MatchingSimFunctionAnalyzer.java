package DataAnalysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;

class Counter {
	private int totalCount;
	private final int[] propertyMatches;

	public Counter() {
		this.propertyMatches = new int[17];
	}

	public void setTotalCount(int n) {
		totalCount = n;
	}

	public void addToPropertyMatches(String bitvector) {
		char[] charArray = bitvector.toCharArray();

		for(int i = 0; i < charArray.length; i++) {
			if(charArray[i] == '1') {
				this.propertyMatches[i]++;
			}
		}
	}

	public void printPropertyMatches() {
		DecimalFormat df = new DecimalFormat("#.###");
		df.setRoundingMode(RoundingMode.CEILING);

		System.out.println("Number of similarity functions with a 1 at each position");
		for(int i = 0; i < propertyMatches.length; i++) {
			double percentage = (double)propertyMatches[i] / this.totalCount * 100;
			System.out.println("Position " + i + ": " + propertyMatches[i] + " / " + this.totalCount + " [ " + df.format(percentage) + "% ]");
		}
	}
}

public class MatchingSimFunctionAnalyzer {
	@SuppressWarnings("Duplicates")
	public static void main(String[] args) {
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

		File outputFile = new File("saved/overlapScores_" + latestTime + ".txt");
		System.out.println("Most recent overlapScore file: " + outputFile.getName());

		Counter counterObject = new Counter();

		try (BufferedReader br = new BufferedReader(new FileReader(outputFile))) {
			counterObject.setTotalCount(MultipleFileReader.checkNumberOfPerfectScores(outputFile));

			for(String line; (line = br.readLine()) != null; ) {
				String[] parts = line.split("_");
				if(parts[1].equals("1.0")) {
					counterObject.addToPropertyMatches(parts[0]);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		counterObject.printPropertyMatches();
	}
}
