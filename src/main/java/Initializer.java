import RaspberryPi.*;
import DataAnalysis.*;

import java.util.Scanner;

public class Initializer {
	public static void main(String[] args) {
		System.out.println("Type in the letter of the class you want to run\nTyping in any other letter will exit the program\n");

		System.out.println("a) BinaryNumberGenerator");
		System.out.println("b) PiQuerySender");
		System.out.println("c) FileValidator");
		System.out.println("d) MatchingSimFunctionAnalyzer");
		System.out.println("e) HashMapMultipleFileReader");
		System.out.println("f) TableGenerator");
		System.out.println("z) Exit\n");
		
		System.out.print("Response: ");
		Scanner sc = new Scanner(System.in);
		String ans = sc.nextLine();
		switch(ans) {
			case "a":
				BinaryNumberGenerator.main(null);
				break;
			case "b":
				PiQuerySender.main(null);
				break;
			case "c":
				FileValidator.main(null);
				break;
			case "d":
				MatchingSimFunctionAnalyzer.main(null);
				break;
			case "e":
				HashMapMultipleFileReader.main(null);
				break;
			case "f":
				TableGenerator.main(null);
				break;
			default:
				System.out.println("Exiting program...");
				break;
		}
	}
}