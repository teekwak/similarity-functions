import DataAnalysis.*;

import java.util.Scanner;

public class Initializer {
	public static void main(String[] args) {
		System.out.println("Type in the letter of the class you want to run\nTyping in any other letter will exit the program\n");

		System.out.println("a) SampleURLGenerator");
		System.out.println("z) Exit\n");
		
		System.out.print("Response: ");
		Scanner sc = new Scanner(System.in);
		String ans = sc.nextLine();
		switch(ans) {
			case "a":
				SampleURLGenerator.main(null);
				break;
			default:
				System.out.println("Exiting program...");
				break;
		}
	}
}