package DataAnalysis;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

class Row {
	double overlapScore;
	String binaryNumberString;

	Row(double o, String s) {
		this.overlapScore = o;
		this.binaryNumberString = s;
	}

	public double getOverlapScore() {
		return this.overlapScore;
	}

	public String getBinaryNumberString() {
		return this.binaryNumberString;
	}

	public int getSum() {
		int sum = 0;
		for(int i = 0; i < 17; i++) {
			if(this.binaryNumberString.charAt(i) == '1') {
				sum++;
			}
		}
		return sum;
	}
}

public class TableGenerator {
	public static List<Row> rowList;

	public static void printToCSV() {
		String csvFile = "saved/table.csv";

		// write header
		try(PrintWriter pw = new PrintWriter(new File(csvFile))) {
			StringBuilder sb = new StringBuilder();
			sb.append("ROW NUMBER,");
			sb.append("OVERLAP SCORE,");
			sb.append("Import Names,");
			sb.append("Variable Names,");
			sb.append("Class Name,");
			sb.append("Author Name,");
			sb.append("Project Name,");
			sb.append("Method Call,");
			sb.append("Method Declaration,");
			sb.append("Size,");
			sb.append("Import Number,");
			sb.append("Complexity,");
			sb.append("Parent Class,");
			sb.append("Package,");
			sb.append("Fields,");
			sb.append("Is Generic,");
			sb.append("Is Abstract,");
			sb.append("Is Wildcard,");
			sb.append("Owner,");
			sb.append("HORIZONTAL SUM");
			sb.append("\n");

			pw.write(sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}

		// sort and write rows
		Collections.sort(rowList, new Comparator<Row>() {
			public int compare(Row row1, Row row2) {
				// compare scores
				if(row1.getOverlapScore() < row2.getOverlapScore()) return 1;
				if(row1.getOverlapScore() > row2.getOverlapScore()) return -1;

				// compare horizontal sums
				if(row1.getSum() < row2.getSum()) return 1;
				if(row1.getSum() > row2.getSum()) return -1;

				// we should never hit here
				return 0;
			}
		});

		int counter = 1;
		for(Row r : rowList) {
			try(PrintWriter pw = new PrintWriter(new FileOutputStream(new File(csvFile), true))) {
				StringBuilder sb = new StringBuilder();
				sb.append(counter);
				sb.append(",");
				sb.append(r.getOverlapScore());
				sb.append(",");

				char[] binaryNumberString = r.getBinaryNumberString().toCharArray();
				for(char digit : binaryNumberString) {
					sb.append(digit);
					sb.append(",");
				}
				sb.append(r.getSum());
				sb.append(",");
				sb.append("\n");

				pw.write(sb.toString());
				counter++;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void createRows(File outputFile) {

		try (BufferedReader br = new BufferedReader(new FileReader(outputFile))){
			for(String line; (line = br.readLine()) != null; ) {
				String[] parts = line.split("_");

				Row child = new Row(Double.parseDouble(parts[1]), parts[0]);

				rowList.add(child);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		rowList = new ArrayList<>();

		File outputFile = new File("saved/overlapScores10252016.txt");

		System.out.println("Creating rows");
		createRows(outputFile);

		System.out.println("Printing to CSV");
		printToCSV();
	}
}
