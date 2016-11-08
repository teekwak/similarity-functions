package DataAnalysis;

import java.io.*;

public class BinGenerator {
	private static int[] bins;
	private static int minField = Integer.MAX_VALUE;
	private static int maxField = Integer.MIN_VALUE;
	private static final int NUMBER_OF_BINS = 3291;

	public static void binPair(int field) {
		int upperBound = (maxField - minField) / NUMBER_OF_BINS;
		for(int i = 0; i < bins.length; i++) {
			if(field <= (i + 1) * upperBound) {
				bins[i] += 1;
				return;
			}
		}

		bins[NUMBER_OF_BINS - 1] += 1;
	}


	public static void main(String[] args) {
		File inputFile = new File("MLT_CSV/snippet_imports_count_one_column.csv");
		bins = new int[NUMBER_OF_BINS];

		int counter = 0;
		try (BufferedReader br = new BufferedReader(new FileReader(inputFile))){
			for(String line; (line = br.readLine()) != null; ) {
				if(counter == 0) {
					// skip first line of CSV file (Field, Count,)
					counter++;
				}
				else {
					String[] parts = line.split(",");
					if(Integer.parseInt(parts[0]) < minField) {
						minField = Integer.parseInt(parts[0]);
					}
					else if(Integer.parseInt(parts[0]) > maxField) {
						maxField = Integer.parseInt(parts[0]);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		// 3, 81474
		// 81471
		// divide by 10
		// 8147

		// 8147, 16294, 24441, 32588, 40735, 48882, 57029, 65176, 73323, 81470 (upper bounds?)
		// should not be for last one due to rounding error
		// last one should be 73323+

		counter = 0;
		try (BufferedReader br = new BufferedReader(new FileReader(inputFile))){
			for(String line; (line = br.readLine()) != null; ) {
				if(counter == 0) {
					// skip first line of CSV file (Field, Count,)
					counter++;
				}
				else {
					String[] parts = line.split(",");
					binPair(Integer.parseInt(parts[0]));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		int upperBound = (maxField - minField) / NUMBER_OF_BINS;
		int k = 1;
		File outputFile = new File("MLT_CSV/snippet_imports_count_FD.csv");
		try(PrintWriter pw = new PrintWriter(outputFile)) {
			pw.write("UpperBound,Count,\n");
		} catch (IOException e) {
			e.printStackTrace();
		}

		for(int b : bins) {
			try(PrintWriter pw = new PrintWriter(new FileOutputStream(outputFile, true))) {
				pw.write(upperBound * k + "," + b  + ",\n");
			} catch (IOException e) {
				e.printStackTrace();
			}

			k++;
		}
	}
}
