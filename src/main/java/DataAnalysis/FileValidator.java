package DataAnalysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

// purpose of this class is to make sure each binary number only occurs once in a file
// also makes sure the overlap total is not greater than 10 or less than 0
public class FileValidator {
	private static boolean validateFile(File file) {
		Set<String> binaryNumberSet = new HashSet<>();
		boolean valid = true;

		try {
			BufferedReader br = new BufferedReader(new FileReader(file));

			for(String line; (line = br.readLine()) != null; ) {
				String[] parts = line.split("_");

				if(binaryNumberSet.contains(parts[0])) {
					System.out.println("[DUPLICATE]: " + parts[0] + " --> " + file.getName());
					valid = false;
				}
				else if(Integer.parseInt(parts[1]) > 10 || Integer.parseInt(parts[1]) < 0) {
					System.out.println("[INVALID VALUE]: " + parts[0] + ": " + parts[1] + " --> " + file.getName());
					valid = false;
				}
				else {
					binaryNumberSet.add(parts[0]);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return valid;
	}

	public static void main(String[] args) {
		Map<String, Boolean> validationMap = new HashMap<>();

		String directoryPath = "saved";
		String extension = ".txt";

		File dir = new File(directoryPath);

		if(!dir.isDirectory()) {
			System.out.println(directoryPath + " is not a directory!");
			return;
		}

		File[] directoryListing = dir.listFiles();

		if(directoryListing != null) {
			for(File file : directoryListing) {
				String fileName = file.getName();
				if(fileName.endsWith(extension) && fileName.startsWith("pi")) {
					validationMap.put(file.getName(), validateFile(file));
				}
			}
		} else {
			System.out.println(directoryPath + " is empty!");
		}

		validationMap.forEach((key, value) -> System.out.println(key + ": " + value));
	}
}
