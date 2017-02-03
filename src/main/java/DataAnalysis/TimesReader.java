package DataAnalysis;

import java.io.*;
import java.util.*;

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
	private static ProjectTime projectTime;
	private static SimilarityFunctionRecord similarityFunctionRecord;

	private static void parseTimesFromFile(File file) {
		try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"))) {
			for(String line; (line = br.readLine()) != null; ) {
				if(line.startsWith("URL")) {
					projectTime = new ProjectTime(line.split("::")[1]);
				}
				else if(line.startsWith("Started process")) {
					try {
						projectTime.setProcessStartTime(Long.parseLong(line.split("::")[1]));
					} catch (ArrayIndexOutOfBoundsException e) {
						// do nothing
					}
				}
				else if(line.startsWith("Finished process")) {
					try {
						projectTime.setProcessEndTime(Long.parseLong(line.split("::")[1]));

						if(projectTime.isValid()) {
							projectTimes.add(projectTime);
						}
					} catch (ArrayIndexOutOfBoundsException e) {
						// do nothing
					}
				}
				else if(line.startsWith("Started cloning")) {
					projectTime.setCloningStartTime(Long.parseLong(line.split("::")[1]));
				}
				else if(line.startsWith("Finished cloning")) {
					projectTime.setCloningEndTime(Long.parseLong(line.split("::")[1]));
				}
				else if(line.startsWith("Started technical")) {
					similarityFunctionRecord = new SimilarityFunctionRecord(line.split("::|\\$\\$")[1]);
					similarityFunctionRecord.setTechnicalStartTime(Long.parseLong(line.split("::|\\$\\$")[2]));
				}
				else if(line.startsWith("Finished technical")) {
					try {
						similarityFunctionRecord.setTechnicalEndTime(Long.parseLong(line.split("::|\\$\\$")[2]));
					} catch (ArrayIndexOutOfBoundsException e) {
						// do nothing
					}
				}
				else if(line.startsWith("Started social")) {
					try {
						similarityFunctionRecord.setSocialStartTime(Long.parseLong(line.split("::|\\$\\$")[2]));
					} catch (ArrayIndexOutOfBoundsException e) {
						// do nothing
					}
				}
				else if(line.startsWith("Finished social")) {
					try {
						similarityFunctionRecord.setSocialEndTime(Long.parseLong(line.split("::|\\$\\$")[2]));
					} catch (ArrayIndexOutOfBoundsException e) {
						// do nothing
					}
				}
				else if(line.startsWith("Started uploading")) {
					try {
						similarityFunctionRecord.setUploadStartTime(Long.parseLong(line.split("::|\\$\\$")[2]));
					} catch (ArrayIndexOutOfBoundsException e) {
						// do nothing
					}
				}
				else if(line.startsWith("Finished uploading")) {
					similarityFunctionRecord.setUploadEndTime(Long.parseLong(line.split("::|\\$\\$")[2]));
					projectTime.addSimilarityFunctionTime(similarityFunctionRecord);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		projectTimes = new HashSet<>();

		int fileCounter = 0;
		File topDirectory = new File("times/");
		File[] files = topDirectory.listFiles();
		if(files != null) {
			for(File file : files) {
				if(file.getName().endsWith("_times.txt")) {
					fileCounter++;
					parseTimesFromFile(file);
					System.out.print("\rProcessed " + fileCounter + " files");
				}
			}
		}

		System.out.print("\rFinished processing all files\n\n");

		// duplicate project check
		Set<String> urls = new HashSet<>();
		for(ProjectTime pt : projectTimes) {
			if(urls.contains(pt.getURL())) {
				System.out.println("Duplicate found! " + pt.getURL());
			}
			else {
				urls.add(pt.getURL());
			}
		}

		// random printing times
		long totalProcessTime = 0;
		long totalCloningTime = 0;
		for(ProjectTime pt : projectTimes) {
			totalProcessTime += pt.getProcessTotalTime();
			totalCloningTime += pt.getCloningTotalTime();
		}

		System.out.println("Number of files read: " + fileCounter);
		System.out.println("Total number of observations: " + projectTimes.size());
		System.out.println("Total time to clone: " + (totalCloningTime / 1000) + "s");
		System.out.println("Total time to process: " + (totalProcessTime / 1000) + "s");
		System.out.println();

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
		for(ProjectTime pt : projectTimes) {
			for(Map.Entry<String, SimilarityFunctionRecord> entry : pt.getSimilarityFunctionRecords().entrySet()) {
				long currentValue = processTimeForSimFunc.get(entry.getKey());
				processTimeForSimFunc.put(entry.getKey(), currentValue + entry.getValue().getProcessTotalTime());
			}
		}

		processTimeForSimFunc.forEach((k, v) -> System.out.println("Total time for " + simFuncToName.get(k) + ": " + v / 1000 + "s"));




		// csv format
		// project number, cumulativeTime


//		List<String> singleBitvectors = new ArrayList<>(
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
//		);


//		Set<String> bitvectors = new HashSet<>(
////			Arrays.asList(
////				"10000000000000000",
////				"01000000000000000",
////				"00100000000000000",
////				"00010000000000000",
////				"00001000000000000",
////				"00000100000000000",
////				"00000010000000000",
////				"00000001000000000",
////				"00000000100000000",
////				"00000000010000000",
////				"00000000001000000",
////				"00000000000100000",
////				"00000000000010000",
////				"00000000000001000",
////				"00000000000000100",
////				"00000000000000010",
////				"00000000000000001"
////			)
//				Arrays.asList(
//								"00001000110010001"
//				)
//		);




//		for(String bv : bitvectors) {
//			try(BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("times/" +bv + ".csv", true)))) {
//				double cumulativeTime = 0;
//
//				bw.write(bv + ",\n");
//				for(ProjectTime project : projectTimes) {
//					for(int i = 0; i < bv.length(); i++) {
//						if(bv.charAt(i) == '1') {
//							cumulativeTime += (double)project.getSimilarityFunctionRecord(singleBitvectors.get(i)).getProcessTotalTime() / 1000 / 60;
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
