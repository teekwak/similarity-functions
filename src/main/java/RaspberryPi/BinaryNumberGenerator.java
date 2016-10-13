package RaspberryPi;

import java.io.File;
import java.io.PrintWriter;

// generates 32 files with 2913 numbers and 13 files with 2912 ==> 131072
public class BinaryNumberGenerator {
	public static void printPaddedBinaryNumbers(int n) {
		if(n == 2) {
			 try {
				 PrintWriter pw = new PrintWriter("java_output/pi1_binaryNumbersList.txt", "UTF-8");

				 for(int i = 1; i < 65536; i++) {
					 String unpadded = Integer.toBinaryString(i);
					 String padded = "00000000000000000".substring(unpadded.length()) + unpadded;
					 pw.println(padded);
				 }

				 pw.close();

				 pw = new PrintWriter("java_output/pi2_binaryNumbersList.txt", "UTF-8");

				 for(int i = 65536; i < 131072; i++) {
					 String unpadded = Integer.toBinaryString(i);
					 String padded = "00000000000000000".substring(unpadded.length()) + unpadded;
					 pw.println(padded);
				 }

				 pw.close();
			 } catch (Exception e) {
				 e.printStackTrace();
			 }
		}
		else if(n == 42){
			try {
				int counter = 0;

				for(int i = 1; i <= 42; i++) {
					PrintWriter pw = new PrintWriter("java_output/pi" + i + "_binaryNumbersList.txt", "UTF-8");

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
		else {
			System.out.println("Need a number (2, 42)");
		}
	}

	public static void main(String[] args) {
		if(!new File("java_output").exists()) {
			new File("java_output").mkdir();
		}

		printPaddedBinaryNumbers(2);
	}
}