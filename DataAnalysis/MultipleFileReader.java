package DataAnalysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class MultipleFileReader {
	public static void readFile(File file, String needle) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));

			for(String line; (line = br.readLine()) != null; ) {
				if(line.contains(needle)) {
					System.out.println(file.getName() + " -> " + line);
				}
			}	
		} catch (IOException e) {
			e.printStackTrace();
		}
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
					readFile(file, needle);
				}
			}
		} else {
			System.out.println(directoryPath + " is empty!");
			return;
		}

		System.out.println("* End search");
	}

	public static void main(String[] args) {
		searchFilesInDirectory("/Users/Kwak/Desktop/concurrent/output", ".txt", "01010101010101010");
	}
}