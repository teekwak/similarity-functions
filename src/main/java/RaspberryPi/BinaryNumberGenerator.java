package RaspberryPi;

import Extra.Cleaner;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.InputMismatchException;
import java.util.Scanner;

public class BinaryNumberGenerator {
	// print binary numbers from 0 to 131072 in a certain number of files as evenly distributed as possible
	private static void printPaddedBinaryNumbers(int numberOfFilesToCreate) {
		PrintWriter pw;

		// calculate number of lines per file
		int linesPerFileRoundedDown = 131072 / numberOfFilesToCreate;
		// calculate how many lines are left
		int remainder = 131072 - (linesPerFileRoundedDown * numberOfFilesToCreate);

		// counter ranges from 0 to 131071
		int counter = 0;

		for(int i = 1; i < numberOfFilesToCreate + 1; i++) {
			try {
				int upperBound = counter + linesPerFileRoundedDown - 1;

				if(remainder > 0) {
					upperBound++;
					remainder--;
				}

				pw = new PrintWriter("java_output/pi" + i + "_binaryNumbersList.txt", "UTF-8");

				while(counter < upperBound + 1) {
					String unpadded = Integer.toBinaryString(counter);
					String padded = "00000000000000000".substring(unpadded.length()) + unpadded;
					pw.println(padded);
					counter++;
				}

				pw.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// checks the number of lines in every file for a certain directory
	private static void checkNumberOfLinesInFile(String directory) {
		try {
			File[] allFiles = new File(directory).listFiles();

			if(allFiles != null) {
				for(File file : allFiles) {
					long count = Files.lines(Paths.get(file.getAbsolutePath())).count();
					System.out.println(file.getName() + " -> " + count + " lines");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		String output_directory = "java_output";

		if(!new File(output_directory).exists()) {
			boolean createdDirectory = new File(output_directory).mkdir();

			if(!createdDirectory) {
				System.out.println("[ERROR]: failed to create java_output directory");
				return;
			}
		}
		Cleaner.deleteAllFilesInDirectory(output_directory);

		System.out.print("Enter the number of binary number files you want to create: ");
		Scanner sc = new Scanner(System.in);
		try {
			int input = sc.nextInt();

			if(input > 131072) {
				throw new InputMismatchException("[ERROR]: You entered a number too high!");
			}

			printPaddedBinaryNumbers(input);
		} catch (InputMismatchException e) {
			System.out.println("[ERROR]: You entered something that was not a number...");
		}

		checkNumberOfLinesInFile(output_directory);
	}
}