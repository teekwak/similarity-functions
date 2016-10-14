package DataAnalysis;

import java.io.*;

public class MultipleFileReader {
	public static int findNeedleInFile(File file, String needle) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));

			for(String line; (line = br.readLine()) != null; ) {
				if(line.contains(needle)) {
					String[] parts = line.split("_");
					return Integer.parseInt(parts[1]);
				}
			}	
		} catch (IOException e) {
			e.printStackTrace();
		}

		return -1;
	}

	public static void searchFilesInDirectory(String directoryPath, String extension, String needle) {
		System.out.println("* Searching for \"" + needle + "\"");
		File dir = new File(directoryPath);

		if(!dir.isDirectory()) {
			System.out.println(directoryPath + " is not a directory!");
			return;
		}

		File[] directoryListing = dir.listFiles();

		int overlapTotal = 0;
		int occurrences = 0;
		if(directoryListing != null) {
			for(File file : directoryListing) {
				if(file.getName().endsWith(extension)) {
					int temp = findNeedleInFile(file, needle);

					if(temp != -1) {
						overlapTotal += temp;
						occurrences += 1;
					}
				}
			}
		} else {
			System.out.println(directoryPath + " is empty!");
		}

		// write average to file if we find 21
		if(occurrences == 21) {
			try (
				FileWriter fw = new FileWriter("java_output/overlapScores.txt", true);
				BufferedWriter bw = new BufferedWriter(fw);
				PrintWriter pw = new PrintWriter(bw))
			{
				pw.println(needle + "_" + (double)overlapTotal / occurrences);
				pw.close();
				bw.close();
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		String binaryFilePath = "java_output/allBinaryNumbers.txt";
		String directoryPath = "java_output";
		String extension = ".txt";

		try {
			// file = file with all binary numbers
			BufferedReader br = new BufferedReader(new FileReader(new File(binaryFilePath)));

			for(String line; (line = br.readLine()) != null; ) {
				searchFilesInDirectory(directoryPath, extension, line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}