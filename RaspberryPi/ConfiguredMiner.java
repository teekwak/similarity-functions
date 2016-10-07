package RaspberryPi;

import java.util.Map;
import java.util.HashMap;

public class ConfiguredMiner {
	public static Map<String, Boolean> flagMap;

	public static void mineWithConfiguration() {
		// this is where the mining will start
		// need file reader here to read in the URLs

		for(Map.Entry<String, Boolean> entry : flagMap.entrySet()) {
			System.out.println(entry.getKey() + " -> " + entry.getValue());
		}
	}

	public static void main(String[] args) {
		// take in a config file
		// need to separate the URLs across pis

		String bitVector = "10000000000000001";
		flagMap = new HashMap<>();

		flagMap.put("authorName", bitVector.charAt(0) == '1' ? true : false);
		flagMap.put("className", bitVector.charAt(1) == '1' ? true : false);
		flagMap.put("complexity", bitVector.charAt(2) == '1' ? true : false);
		flagMap.put("fields", bitVector.charAt(3) == '1' ? true : false);
		flagMap.put("hasWildCard", bitVector.charAt(4) == '1' ? true : false);
		flagMap.put("isAbstract", bitVector.charAt(5) == '1' ? true : false);
		flagMap.put("isGeneric", bitVector.charAt(6) == '1' ? true : false);
		flagMap.put("imports", bitVector.charAt(7) == '1' ? true : false);
		flagMap.put("inverseImports", bitVector.charAt(8) == '1' ? true : false);			// check with Lee on this one
		flagMap.put("methodCallNames", bitVector.charAt(9) == '1' ? true : false);
		flagMap.put("methodDecNames", bitVector.charAt(10) == '1' ? true : false);
		flagMap.put("ownerName", bitVector.charAt(11) == '1' ? true : false);
		flagMap.put("package", bitVector.charAt(12) == '1' ? true : false);
		flagMap.put("parentClass", bitVector.charAt(13) == '1' ? true : false);
		flagMap.put("projectName", bitVector.charAt(14) == '1' ? true : false);
		flagMap.put("size", bitVector.charAt(15) == '1' ? true : false);
		flagMap.put("variableWords", bitVector.charAt(16) == '1' ? true : false);

		mineWithConfiguration();
	}
}