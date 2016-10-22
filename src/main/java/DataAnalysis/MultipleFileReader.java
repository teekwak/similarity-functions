package DataAnalysis;

import RaspberryPi.PiQuerySender;

import java.io.*;

public class MultipleFileReader {
	private static int findNeedleInFile(File file, String needle) {
		try(BufferedReader br = new BufferedReader(new FileReader(file))) {
			for(String line; (line = br.readLine()) != null; ) {
				if(line.contains(needle)) {
					String[] parts = line.split("_");
					br.close();
					return Integer.parseInt(parts[1]);
				}
			}	
		} catch (IOException e) {
			e.printStackTrace();
		}

		return -1;
	}

	private static void searchFilesInDirectory(String directoryPath, String extension, String needle) {
		File dir = new File(directoryPath);

		if(!dir.isDirectory()) {
			System.out.println(directoryPath + " is not a directory!");
			return;
		}

		File[] directoryListing = dir.listFiles();

		String prefix;
		int needleNumber = Integer.parseInt(needle, 2);
		if(needleNumber < 43691) {
			prefix = "pi1";
		}
		else if(needleNumber >= 43691 && needleNumber <= 87381) {
			prefix = "pi2";
		}
		else {
			prefix = "pi3";
		}

		int overlapTotal = 0;
		int occurrences = 0;
		if(directoryListing != null) {
			for(File file : directoryListing) {
				String fileName = file.getName();

				if(fileName.endsWith(extension) && fileName.startsWith(prefix)) {
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

		try (
			FileWriter fw = new FileWriter("saved/overlapScores.txt", true);
			BufferedWriter bw = new BufferedWriter(fw);
			PrintWriter pw = new PrintWriter(bw)
		) {
			pw.println(needle + "_" + (double)overlapTotal / (occurrences * 10));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static int checkNumberOfPerfectScores(File scoresFile) {
		int perfectScoreCounter = 0;

		try(BufferedReader br = new BufferedReader(new FileReader(scoresFile))) {

			for(String line; (line = br.readLine()) != null; ) {
				String[] parts = line.split("_");

				if(parts[1].equals("1.0")) {
					perfectScoreCounter++;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return perfectScoreCounter;
	}

	public static void main(String[] args) {
		String binaryFilePath = "saved/allBinaryNumbers.txt";
		String directoryPath = "saved";
		String extension = ".txt";

		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(binaryFilePath)));

			int counter = 0;
			for(String line; (line = br.readLine()) != null; ) {
				searchFilesInDirectory(directoryPath, extension, line);
				counter++;
				PiQuerySender.generateProgressBar(counter, 131072);
			}

			System.out.println(" Number of perfect scores: " + checkNumberOfPerfectScores(new File("saved/overlapScores.txt")));

			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}