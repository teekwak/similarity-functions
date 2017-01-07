package Utilities;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class UsefulThings {
	public static <T> void printDataStructureToFile(Set<T> set, String fileName) {
		try(PrintWriter pw = new PrintWriter(new FileOutputStream(fileName, true))) {
			set.forEach(obj -> pw.write(obj.toString() + "\n"));
			pw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static <T> void printDataStructureToFile(List<T> list, String fileName) {
		try(PrintWriter pw = new PrintWriter(new FileOutputStream(fileName, true))) {
			list.forEach(obj -> pw.write(obj.toString() + "\n"));
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

	public static <K, V> Map<K, V> parseValuesFromCSVFileToMap() {
		Map<K, V> map = new LinkedHashMap<K, V>();


		return map;
	}

	public static boolean uniqueLines(File file) {
		try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
			Set<String> lines = new HashSet<>();

			for(String line; (line = br.readLine()) != null;) {
				if(lines.contains(line)) {
					return false;
				}

				lines.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return true;
	}

	public static void findValidURLs(String fileName) {
		File file = new File(fileName);

		int nonvalidURLCount = 0;
		int counter = 0;
		int maxCount = 146200;

		try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"))) {
			for(String line; (line = br.readLine()) != null; ) {
				try {
					counter++;
					generateProgressBar(counter, maxCount);

					URL url = new URL(line);
					BufferedReader gbr = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));

					int fileCounter = 0;
					for(String javaLine; (javaLine = gbr.readLine()) != null; ) {
						if(fileCounter > 0) {
							break;
						}
						fileCounter++;
					}
				} catch (IOException e) {
					nonvalidURLCount++;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("Nonvalid url count: " + nonvalidURLCount);
	}
}
