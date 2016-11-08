package DataAnalysis;

import java.io.*;

public class CSVToSingleColumn {


	public static void main(String[] args) {
		File inputFile = new File("MLT_CSV/snippet_number_of_fields.csv");
		File outputFile = new File("MLT_CSV/snippet_number_of_fields_one_column.csv");

		try(PrintWriter pw = new PrintWriter(new FileOutputStream(outputFile, true))) {
			pw.write("Field,\n");
		} catch (IOException e) {
			e.printStackTrace();
		}

		int counter = 0;
		try (BufferedReader br = new BufferedReader(new FileReader(inputFile))){
			for(String line; (line = br.readLine()) != null; ) {
				if(counter == 0) {
					// skip first line of CSV file (Field, Count,)
					counter++;
				}
				else {
					String[] parts = line.split(",");

					try(PrintWriter pw = new PrintWriter(new FileOutputStream(outputFile, true))) {
						for(int i = 0; i < Integer.parseInt(parts[1]); i++) {
							pw.write(parts[0]);
							pw.write(",\n");
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
