// import Server.*;
import RaspberryPi.*;
import DataAnalysis.*;

import java.util.Scanner;

public class Initializer {
	public static void main(String[] args) {
		System.out.println("Type in the letter of the class you want to run\nTyping in any other letter will exit the program\n");

		System.out.println("a) MultipleFileReader");
		System.out.println("b) BinaryNumberGenerator");
		System.out.println("c) ConfiguredMiner");
		System.out.println("d) PiQuerySender");
		System.out.println("z) Exit\n");
		
		System.out.print("Response: ");
		Scanner sc = new Scanner(System.in);
		String ans = sc.nextLine();
		switch(ans) {
			case "a": {
				MultipleFileReader.main(null);
			}
			case "b": {
				BinaryNumberGenerator.main(null);
			}
			case "c": {
				ConfiguredMiner.main(null);
			}
			case "d": {
				PiQuerySender.main(null);
			}
			default: {
				System.out.println("Exiting program...");
				break;
			}
		}
	}
}