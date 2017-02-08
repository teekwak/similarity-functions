package DataAnalysis;

import java.io.*;
import java.util.*;

class DataPoint {
	private double accuracy;
	private double time;

	DataPoint(double a, double t) {
		this.accuracy = a;
		this.time = t;
	}

	double getAccuracy() {
		return this.accuracy;
	}

	double getTime() {
		return this.time;
	}
}

class SimilarityFunctionRecord {
	private String bitvector;
	private long technicalStartTime;
	private long technicalEndTime;
	private long socialStartTime;
	private long socialEndTime;
	private long uploadStartTime;
	private long uploadEndTime;

	SimilarityFunctionRecord(String bv) {
		this.bitvector = bv;
		this.technicalStartTime = -1;
		this.technicalEndTime = -1;
		this.socialStartTime = -1;
		this.socialEndTime = -1;
		this.uploadStartTime = -1;
		this.uploadEndTime = -1;
	}

	String getBitvector() {
		return this.bitvector;
	}

	void setTechnicalStartTime(long time) {
		this.technicalStartTime = time;
	}

	void setTechnicalEndTime(long time) {
		this.technicalEndTime = time;
	}

	void setSocialStartTime(long time) {
		this.socialStartTime = time;
	}

	void setSocialEndTime(long time) {
		this.socialEndTime = time;
	}

	void setUploadStartTime(long time) {
		this.uploadStartTime = time;
	}

	void setUploadEndTime(long time) {
		this.uploadEndTime = time;
	}

	long getProcessTotalTime() {
		return this.uploadEndTime - this.technicalStartTime;
	}

	boolean isValid() {
		if(this.bitvector.length() < 17) return false;
		else if(this.technicalStartTime == -1) return false;
		else if(this.technicalEndTime == -1) return false;
		else if(this.socialStartTime == -1) return false;
		else if(this.socialEndTime == -1) return false;
		else if(this.uploadStartTime == -1) return false;
		else if(this.uploadEndTime == -1) return false;

		return true;
	}
}

class ProjectTime {
	private String url;
	private long processStartTime;
	private long processEndTime;
	private long cloningStartTime;
	private long cloningEndTime;
	private Map<String, SimilarityFunctionRecord> similarityFunctionRecords;

	ProjectTime(String u) {
		this.url = u;
		this.processStartTime = -1;
		this.processEndTime = -1;
		this.cloningStartTime = -1;
		this.cloningEndTime = -1;
		this.similarityFunctionRecords = new HashMap<>();
	}

	String getURL() {
		return this.url;
	}

	void setProcessStartTime(long time) {
		this.processStartTime = time;
	}

	void setProcessEndTime(long time) {
		this.processEndTime = time;
	}

	void setCloningStartTime(long time) {
		this.cloningStartTime = time;
	}

	void setCloningEndTime(long time) {
		this.cloningEndTime = time;
	}

	void addSimilarityFunctionTime(SimilarityFunctionRecord sfr) {
		similarityFunctionRecords.put(sfr.getBitvector(), sfr);
	}

	SimilarityFunctionRecord getSimilarityFunctionRecord(String bitvector) {
		return this.similarityFunctionRecords.get(bitvector);
	}
	
	long getProcessTotalTime() {
		return this.processEndTime - this.processStartTime;
	}

	long getCloningTotalTime() {
		return this.cloningEndTime - this.cloningStartTime;
	}

	long getProcessEndTime() {
		return this.processEndTime;
	}

	Map<String, SimilarityFunctionRecord> getSimilarityFunctionRecords() {
		return this.similarityFunctionRecords;
	}

	boolean isValid() {
		if(this.processStartTime == -1) return false;
		if(this.processEndTime == -1) return false;
		if(this.cloningStartTime == -1) return false;
		if(this.cloningEndTime == -1) return false;

		for(SimilarityFunctionRecord sfr : this.similarityFunctionRecords.values()) {
			if(!sfr.isValid()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public int hashCode() {
		int hash = 7;

		int sum = 0;
		for(char c : this.url.toCharArray()) {
			sum += c;
		}

		return 31 * hash + sum;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == null) return false;
		else if(!(obj instanceof ProjectTime)) return false;

		return ((ProjectTime) obj).getURL().equals(this.url);
	}
}

public class TimesReader {
	private static Set<ProjectTime> projectTimes;
	private static Set<String> invalidURLs;
	private static ProjectTime projectTime;
	private static SimilarityFunctionRecord similarityFunctionRecord;

	private static void parseTimesFromFile(File file) {
		try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"))) {
			for(String line; (line = br.readLine()) != null; ) {
				if(line.startsWith("URL")) {
					try {
						if(projectTime != null) {
							invalidURLs.add(projectTime.getURL());
							projectTime = null;
						}

						projectTime = new ProjectTime(line.split("::")[1]);
					} catch (ArrayIndexOutOfBoundsException e) {
						invalidURLs.add(line);
					}
				}
				else if(line.startsWith("Started process")) {
					try {
						projectTime.setProcessStartTime(Long.parseLong(line.split("::")[1]));
					} catch (ArrayIndexOutOfBoundsException e) {
						invalidURLs.add(projectTime.getURL());
					}
				}
				else if(line.startsWith("Finished process")) {
					try {
						projectTime.setProcessEndTime(Long.parseLong(line.split("::")[1]));

						if(projectTime.isValid()) {
							projectTimes.add(projectTime);
							projectTime = null;
						}
						else {
							invalidURLs.add(projectTime.getURL());
						}
					} catch (ArrayIndexOutOfBoundsException e) {
						invalidURLs.add(projectTime.getURL());
					}
				}
				else if(line.startsWith("Started cloning")) {
					try {
						projectTime.setCloningStartTime(Long.parseLong(line.split("::")[1]));
					} catch (ArrayIndexOutOfBoundsException e) {
						invalidURLs.add(projectTime.getURL());
					}
				}
				else if(line.startsWith("Finished cloning")) {
					try {
						projectTime.setCloningEndTime(Long.parseLong(line.split("::")[1]));
					} catch (ArrayIndexOutOfBoundsException e) {
						invalidURLs.add(projectTime.getURL());
					}
				}
				else if(line.startsWith("Started technical")) {
					try {
						similarityFunctionRecord = new SimilarityFunctionRecord(line.split("::|\\$\\$")[1]);
						similarityFunctionRecord.setTechnicalStartTime(Long.parseLong(line.split("::|\\$\\$")[2]));
					} catch (ArrayIndexOutOfBoundsException e) {
						invalidURLs.add(projectTime.getURL());
					}
				}
				else if(line.startsWith("Finished technical")) {
					try {
						similarityFunctionRecord.setTechnicalEndTime(Long.parseLong(line.split("::|\\$\\$")[2]));
					} catch (ArrayIndexOutOfBoundsException e) {
						invalidURLs.add(projectTime.getURL());
					}
				}
				else if(line.startsWith("Started social")) {
					try {
						similarityFunctionRecord.setSocialStartTime(Long.parseLong(line.split("::|\\$\\$")[2]));
					} catch (ArrayIndexOutOfBoundsException e) {
						invalidURLs.add(projectTime.getURL());
					}
				}
				else if(line.startsWith("Finished social")) {
					try {
						similarityFunctionRecord.setSocialEndTime(Long.parseLong(line.split("::|\\$\\$")[2]));
					} catch (ArrayIndexOutOfBoundsException e) {
						invalidURLs.add(projectTime.getURL());
					}
				}
				else if(line.startsWith("Started uploading")) {
					try {
						similarityFunctionRecord.setUploadStartTime(Long.parseLong(line.split("::|\\$\\$")[2]));
					} catch (ArrayIndexOutOfBoundsException e) {
						invalidURLs.add(projectTime.getURL());
					}
				}
				else if(line.startsWith("Finished uploading")) {
					try {
						similarityFunctionRecord.setUploadEndTime(Long.parseLong(line.split("::|\\$\\$")[2]));
						projectTime.addSimilarityFunctionTime(similarityFunctionRecord);
					} catch (ArrayIndexOutOfBoundsException e) {
						invalidURLs.add(projectTime.getURL());
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static Map<Integer, Integer> projectsPerUnitOfTime(String unit, boolean cumulative, long startTime, int numberOfUnits) {
		int millisecondsInUnitOfTime;
		switch(unit) {
			case "day":
				millisecondsInUnitOfTime = 1000 * 60 * 60 * 24;
				break;
			case "hour":
				millisecondsInUnitOfTime = 1000 * 60 * 60;
				break;
			case "minute":
				millisecondsInUnitOfTime = 1000 * 60;
				break;
			default:
				throw new IllegalArgumentException("[ERROR]: unit of time given was not \"day\", \"hour\", or \"minute\"");
		}

		Map<Integer, Integer> projectsCompletedPerUnitOfTime = new HashMap<>();
		for(int i = 1; i <= numberOfUnits; i++) {
			projectsCompletedPerUnitOfTime.put(i, 0);
		}

		for(ProjectTime pt : projectTimes) {
			int currentUnit = 1;
			while(currentUnit <= numberOfUnits) {
				if(pt.getProcessEndTime() <= startTime + (currentUnit * millisecondsInUnitOfTime)) {
					int currentValue = projectsCompletedPerUnitOfTime.get(currentUnit);
					projectsCompletedPerUnitOfTime.put(currentUnit, currentValue + 1);
					break;
				}
				currentUnit++;
			}
		}

		if(cumulative) {
			for(int i = 2; i <= numberOfUnits; i++) {
				if(i > projectsCompletedPerUnitOfTime.size()) break;


				int currentValue = projectsCompletedPerUnitOfTime.get(i);
				projectsCompletedPerUnitOfTime.put(i, projectsCompletedPerUnitOfTime.get(i - 1) + currentValue);
			}
		}

		// projectsCompletedPerUnitOfTime.forEach((k, v) -> System.out.println(k + " -> " + v));

		return projectsCompletedPerUnitOfTime;
	}

	private static void processFiles(String fileStartName) {
		int fileCounter = 0;
		File topDirectory = new File("times/");
		File[] files = topDirectory.listFiles();
		if(files != null) {
			for(File file : files) {
				if(file.getName().startsWith(fileStartName) && file.getName().endsWith("_times.txt")) {
					fileCounter++;
					parseTimesFromFile(file);
					System.out.print("\rProcessed " + fileCounter + " files");
				}
			}
		}

		System.out.print("\rFinished processing all " + fileStartName + " files\n\n");

		// random printing times
		long totalProcessTime = 0;
		long totalCloningTime = 0;
		for(ProjectTime pt : projectTimes) {
			totalProcessTime += pt.getProcessTotalTime();
			totalCloningTime += pt.getCloningTotalTime();
		}

//		System.out.println("Number of files read: " + fileCounter);
//		System.out.println("Total number of observations: " + projectTimes.size());
//		System.out.println("Total number of invalid URLS: " + invalidURLs.size());
//		System.out.println("Total time to clone: " + (totalCloningTime / 1000) + "s");
//		System.out.println("Total time to process: " + (totalProcessTime / 1000) + "s");
//		System.out.println();
	}

	private static void removeInvalidProjectTimes() {
		Set<ProjectTime> validSet = new HashSet<>();

		for(ProjectTime pt : projectTimes) {
			if(pt.getSimilarityFunctionRecords().size() == 17) {
				validSet.add(pt);
			}
		}

		projectTimes = validSet;
	}

	private static void removeDuplicateProjectTimes() {
		Set<String> projectURLs = new HashSet<>();
		Set<ProjectTime> validSet = new HashSet<>();

		for(ProjectTime pt : projectTimes) {
			if(!projectURLs.contains(pt.getURL())) {
				projectURLs.add(pt.getURL());
				validSet.add(pt);
			}
		}

		projectTimes = validSet;
	}

	private static void accuracyVsTime() {
		List<String> singleBitvectors = new ArrayList<>(
						Arrays.asList(
										"10000000000000000",
										"01000000000000000",
										"00100000000000000",
										"00010000000000000",
										"00001000000000000",
										"00000100000000000",
										"00000010000000000",
										"00000001000000000",
										"00000000100000000",
										"00000000010000000",
										"00000000001000000",
										"00000000000100000",
										"00000000000010000",
										"00000000000001000",
										"00000000000000100",
										"00000000000000010",
										"00000000000000001"
						)
		);

		Map<String, DataPoint> accuracyVsTimeMap = new HashMap<>();

		// generate all 131072 mutations
		for(int i = 0; i < 131072; i++) {
			try {
				String unpadded = Integer.toBinaryString(i);
				String padded = "00000000000000000".substring(unpadded.length()) + unpadded;
				accuracyVsTimeMap.put(padded, null);
			} catch (StringIndexOutOfBoundsException e) {
				System.out.println(i);
				e.printStackTrace();
			}
		}

		Map<String, Double> overlapScores = new HashMap<>();
		// store all overlap scores
		try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("saved/overlapScores_1478630814.txt"), "UTF-8"))) {
			for(String line; (line = br.readLine()) != null; ) {
				String[] splitLine = line.split("_");

				overlapScores.put(splitLine[0], Double.parseDouble(splitLine[1]));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		// get the cumulative time for each mutation and print to file
		final int millisecondsPerDay = 1000 * 60 * 60 * 24;
		int counter = 1;
		for(String bv : accuracyVsTimeMap.keySet()) {
			double cumulativeTime = 0;

			for(ProjectTime project : projectTimes) {
				for(int i = 0; i < bv.length(); i++) {
					if(bv.charAt(i) == '1') {
						cumulativeTime += (double)project.getSimilarityFunctionRecord(singleBitvectors.get(i)).getProcessTotalTime() / millisecondsPerDay;
					}
				}
			}

			accuracyVsTimeMap.put(bv, new DataPoint(overlapScores.get(bv), cumulativeTime));

			System.out.print("\r Finished " + counter + " bitvector(s)");
			counter++;
		}

		System.out.print("\r Finished all bitvectors. Printing...\n");

		// print sim func to total process time
		try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("simFuncToCumulativeTime.csv", true)))) {
			for(Map.Entry<String, DataPoint> entry : accuracyVsTimeMap.entrySet()) {
				bw.write(entry.getKey() + "," + entry.getValue().getTime() + ",\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		// write data point to file
		try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("accuracyVsTime.csv", true)))) {
			for(DataPoint dp : accuracyVsTimeMap.values()) {
				bw.write(dp.getAccuracy() + "," + dp.getTime() + ",\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Map<String, String> simFuncToName = new HashMap<>();
		{
			simFuncToName.put("10000000000000000", "import names");
			simFuncToName.put("01000000000000000", "variable names");
			simFuncToName.put("00100000000000000", "class names");
			simFuncToName.put("00010000000000000", "author name");
			simFuncToName.put("00001000000000000", "project name");
			simFuncToName.put("00000100000000000", "method invocation names");
			simFuncToName.put("00000010000000000", "method declaration names");
			simFuncToName.put("00000001000000000", "size");
			simFuncToName.put("00000000100000000", "number of import");
			simFuncToName.put("00000000010000000", "cyclomatic complexity");
			simFuncToName.put("00000000001000000", "extends names");
			simFuncToName.put("00000000000100000", "package names");
			simFuncToName.put("00000000000010000", "field names");
			simFuncToName.put("00000000000001000", "is generic");
			simFuncToName.put("00000000000000100", "is abstract");
			simFuncToName.put("00000000000000010", "is wild card");
			simFuncToName.put("00000000000000001", "project owner name");
		}

		Map<String, Long> processTimeForSimFunc = new LinkedHashMap<>();
		{
			processTimeForSimFunc.put("10000000000000000", 0L);
			processTimeForSimFunc.put("01000000000000000", 0L);
			processTimeForSimFunc.put("00100000000000000", 0L);
			processTimeForSimFunc.put("00010000000000000", 0L);
			processTimeForSimFunc.put("00001000000000000", 0L);
			processTimeForSimFunc.put("00000100000000000", 0L);
			processTimeForSimFunc.put("00000010000000000", 0L);
			processTimeForSimFunc.put("00000001000000000", 0L);
			processTimeForSimFunc.put("00000000100000000", 0L);
			processTimeForSimFunc.put("00000000010000000", 0L);
			processTimeForSimFunc.put("00000000001000000", 0L);
			processTimeForSimFunc.put("00000000000100000", 0L);
			processTimeForSimFunc.put("00000000000010000", 0L);
			processTimeForSimFunc.put("00000000000001000", 0L);
			processTimeForSimFunc.put("00000000000000100", 0L);
			processTimeForSimFunc.put("00000000000000010", 0L);
			processTimeForSimFunc.put("00000000000000001", 0L);
		}

		projectTimes = new HashSet<>();
		invalidURLs = new HashSet<>();

		// pi processing
		// todo: make the processFiles method have its own data structures so we can separate pi from ce
		processFiles("pi");

		removeInvalidProjectTimes();
		System.out.println("Projects after removing invalid: " + projectTimes.size());

		removeDuplicateProjectTimes();
		System.out.println("Projects after removing duplicates: " + projectTimes.size());

		Map<Integer, Integer> piProjectsPerHour = projectsPerUnitOfTime("hour", true, 1485569280000L, 120);

		try(BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("times/pi_projects_per_hour.csv", true), "UTF-8"))) {
			bw.write("Time (hours),Number of Projects,\n");
			bw.write("0,0,\n");

			for(Map.Entry<Integer, Integer> entry : piProjectsPerHour.entrySet()) {
				bw.write(entry.getKey() + "," + entry.getValue() + ",\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		for(ProjectTime pt : projectTimes) {
			for(Map.Entry<String, SimilarityFunctionRecord> entry : pt.getSimilarityFunctionRecords().entrySet()) {
				long currentValue = processTimeForSimFunc.get(entry.getKey());
				processTimeForSimFunc.put(entry.getKey(), currentValue + entry.getValue().getProcessTotalTime());
			}
		}

//		processTimeForSimFunc.forEach((k, v) -> System.out.println("Total time for " + simFuncToName.get(k) + ": " + v / 1000 + "s"));

		// CE processing
//		processFiles("codeExchange");
//		if(!duplicateCheck()) throw new IllegalArgumentException("There are duplicates in the CE files!");
//
//		Map<Integer, Integer> ceProjectsPerHour = projectsPerUnitOfTime("hour", true, 1485569280000L, 120);
//
//		try(BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("times/ce_projects_per_hour.csv", true), "UTF-8"))) {
//			bw.write("Time (hours),Number of Projects,\n");
//			bw.write("0,0,\n");
//
//			for(Map.Entry<Integer, Integer> entry : ceProjectsPerHour.entrySet()) {
//				bw.write(entry.getKey() + "," + entry.getValue() + ",\n");
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//		for(ProjectTime pt : projectTimes) {
//			for(Map.Entry<String, SimilarityFunctionRecord> entry : pt.getSimilarityFunctionRecords().entrySet()) {
//				long currentValue = processTimeForSimFunc.get(entry.getKey());
//				processTimeForSimFunc.put(entry.getKey(), currentValue + entry.getValue().getProcessTotalTime());
//			}
//		}
//
//		processTimeForSimFunc.forEach((k, v) -> System.out.println("Total time for " + simFuncToName.get(k) + ": " + v / 1000 + "s"));

		// refactor below






//		System.out.println();
//		System.out.println("Invalid URLs");
//		invalidURLs.forEach(System.out::println);

		// these functions show how many projects were uploaded in X time units
//		System.out.println();
//		projectsPerUnitOfTime("day", false, 1485569280000L, 5);
//		System.out.println();
//		projectsPerUnitOfTime("hour", false, 1485569280000L, 120);

		// maybe create functions that are cumulative
//		System.out.println();
//		Map<Integer, Integer> projectsPerDay = projectsPerUnitOfTime("day", true, 1485569280000L, 5);
//		System.out.println();






		// csv format
		// project number, cumulativeTime


		List<String> singleBitvectors = new ArrayList<>(
			Arrays.asList(
				"10000000000000000",
				"01000000000000000",
				"00100000000000000",
				"00010000000000000",
				"00001000000000000",
				"00000100000000000",
				"00000010000000000",
				"00000001000000000",
				"00000000100000000",
				"00000000010000000",
				"00000000001000000",
				"00000000000100000",
				"00000000000010000",
				"00000000000001000",
				"00000000000000100",
				"00000000000000010",
				"00000000000000001"
			)
		);


		Set<String> bitvectors = new HashSet<>(
//			Arrays.asList(
//				"10000000000000000",
//				"01000000000000000",
//				"00100000000000000",
//				"00010000000000000",
//				"00001000000000000",
//				"00000100000000000",
//				"00000010000000000",
//				"00000001000000000",
//				"00000000100000000",
//				"00000000010000000",
//				"00000000001000000",
//				"00000000000100000",
//				"00000000000010000",
//				"00000000000001000",
//				"00000000000000100",
//				"00000000000000010",
//				"00000000000000001"
//			)
				Arrays.asList(
								"00000000110010000",
								"00000000100010000",
								"00000000000010000"
				)
		);



		accuracyVsTime();

//		final int millisecondsPerDay = 1000 * 60 * 60 * 24;
//
//		for(String bv : bitvectors) {
//			try(BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("times/" + bv + ".csv", true)))) {
//				double cumulativeTime = 0;
//
//				bw.write(bv + ",\n");
//				for(ProjectTime project : projectTimes) {
//					for(int i = 0; i < bv.length(); i++) {
//						if(bv.charAt(i) == '1') {
//							cumulativeTime += (double)project.getSimilarityFunctionRecord(singleBitvectors.get(i)).getProcessTotalTime() / millisecondsPerDay;
//						}
//					}
//
//					bw.write(cumulativeTime + ",\n");
//				}
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}

//		for(String bv : bitvectors) {
//			double cumulativeTime = 0;
//
//			for(ProjectTime project : projectTimes) {
//				for(int i = 0; i < bv.length(); i++) {
//					if(bv.charAt(i) == '1') {
//						cumulativeTime += (double)project.getSimilarityFunctionRecord(singleBitvectors.get(i)).getProcessTotalTime() / 1000 / 60;
//					}
//				}
//			}
//
//			System.out.println(bv + " -> " + cumulativeTime);
//		}
	}
}
