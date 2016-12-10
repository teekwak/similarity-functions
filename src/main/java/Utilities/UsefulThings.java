package Utilities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;

public class UsefulThings {
	public static <T> void printSetToFile(Set<T> set, String fileName) {
		try(PrintWriter pw = new PrintWriter(new FileOutputStream(fileName, true))) {
			set.forEach(obj -> pw.write(obj.toString() + "\n"));
			pw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static int getNumberOfLinesInFile(File file) {
		try {
			return (int) Files.lines(Paths.get(file.getAbsolutePath())).count();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return -1;
	}

	public static void generateProgressBar(int count, int maxCount) {
		int numberOfBars = 30;
		int numberOfFilledBars = (int)Math.floor((double)count / maxCount * numberOfBars);

		StringBuilder sb = new StringBuilder();
		sb.append("\rCompletion: [");
		for(int i = 0; i < numberOfFilledBars; i++) {
			sb.append("=");
		}
		sb.append("                              ".substring(numberOfFilledBars));
		sb.append("] ");
		sb.append(count);
		sb.append(" / ");
		sb.append(maxCount);

		System.out.print(sb.toString());
	}
}
