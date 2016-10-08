package Extra;

import java.io.File;

public class Cleaner {
	public static void deleteAllFilesInDirectory(String directory) {
		File[] allFiles = new File(directory).listFiles();

		for(File file : allFiles) {
			file.delete();
		}
	}

	public static void main(String[] args) {
		Cleaner.deleteAllFilesInDirectory("./Output");
	}
}
