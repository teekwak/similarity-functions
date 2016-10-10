package RaspberryPi;

import java.io.PrintWriter;

// generates 32 files with 2913 numbers and 13 files with 2912 ==> 131072
public class BinaryNumberGenerator {
	public static void printPaddedBinaryNumbers() {
		try {
			int counter = 0;

			for(int i = 1; i <= 42; i++) {
				PrintWriter pw = new PrintWriter("Output/pi" + i + "_binaryNumbersList.txt", "UTF-8");

				if(i <= 32) {
					for(int j = 1; j <= 3121; j++) {
						String unpadded = Integer.toBinaryString(counter);
						String padded = "00000000000000000".substring(unpadded.length()) + unpadded;
						pw.println(padded);
						counter++;
					}
				}
				else {
					for(int j = 1; j <= 3120; j++) {
						String unpadded = Integer.toBinaryString(counter);
						String padded = "00000000000000000".substring(unpadded.length()) + unpadded;
						pw.println(padded);
						counter++;
					}
				}

				pw.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}

	public static void main(String[] args) {
		printPaddedBinaryNumbers();
	}
}