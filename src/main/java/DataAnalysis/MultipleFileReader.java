package DataAnalysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class MultipleFileReader {
	public static void findNeedleInFile(File file, String needle) {
		int overlapTotal = 0;
		int numberOfMatches = 0;

		try {
			BufferedReader br = new BufferedReader(new FileReader(file));

			for(String line; (line = br.readLine()) != null; ) {
				if(line.contains(needle)) {
					String[] parts = line.split("_");
					overlapTotal += Integer.parseInt(parts[2]);
					numberOfMatches++;
				}
			}	
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("Overlap score for " + needle + ": " + overlapTotal / numberOfMatches);
	}

	public static void searchFilesInDirectory(String directoryPath, String extension, String needle) {
		System.out.println("* Searching for \"" + needle + "\"");
		File dir = new File(directoryPath);

		if(!dir.isDirectory()) {
			System.out.println(directoryPath + " is not a directory!");
			return;
		}

		File[] directoryListing = dir.listFiles();

		if(directoryListing != null) {
			for(File file : directoryListing) {
				if(file.getName().endsWith(extension)) {
					findNeedleInFile(file, needle);
				}
			}
		} else {
			System.out.println(directoryPath + " is empty!");
			return;
		}

		System.out.println("* End search");
	}

	public static void main(String[] args) {
		searchFilesInDirectory("/Users/Kwak/Desktop/SimilarityFunctions", ".txt", "00000100101110110");
	}
}