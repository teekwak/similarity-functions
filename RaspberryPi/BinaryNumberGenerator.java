package RaspberryPi;

import java.io.PrintWriter;

public class BinaryNumberGenerator {
	public static void printPaddedBinaryNumbers() {
		try {
			PrintWriter pw = new PrintWriter("Output/testfile.txt", "UTF-8");
			for(int i = 1; i < 131072; i++)	{
				String unpadded = Integer.toBinaryString(i);
				String padded = "00000000000000000".substring(unpadded.length()) + unpadded;
				pw.println(padded);
			}
			pw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}

	public static void main(String[] args) {
		printPaddedBinaryNumbers();
	}
}