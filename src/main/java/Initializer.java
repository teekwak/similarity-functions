// import Server.*;
import RaspberryPi.*;
import Server.*;
import DataAnalysis.*;
import Extra.*;

import java.util.Scanner;

public class Initializer {
	public static void main(String[] args) {
		System.out.println("Type in the letter of the class you want to run\nTyping in any other letter will exit the program\n");

		System.out.println("a) MultipleFileReader");
		System.out.println("b) BinaryNumberGenerator");
		System.out.println("c) PiQuerySender");
		System.out.println("d) GroundTruthGenerator");
		System.out.println("e) CacheTest");
		System.out.println("f) FileValidator");
		System.out.println("g) MatchingSimFunctionAnalyzer");
		System.out.println("y) Clean Project");
		System.out.println("z) Exit\n");
		
		System.out.print("Response: ");
		Scanner sc = new Scanner(System.in);
		String ans = sc.nextLine();
		switch(ans) {
			case "a": 
				MultipleFileReader.main(null);
				break;
			case "b": 
				BinaryNumberGenerator.main(null);
				break;
			case "c":
				PiQuerySender.main(null);
				break;
			case "d":
				GroundTruthGenerator.main(null);
				break;
			case "e":
				CacheTest.main(null);
				break;
			case "f":
				FileValidator.main(null);
				break;
			case "g":
				MatchingSimFunctionAnalyzer.main(null);
				break;
			case "y":
				Cleaner.main(null);
				break;
			default:
				System.out.println("Exiting program...");
				break;
		}
	}
}