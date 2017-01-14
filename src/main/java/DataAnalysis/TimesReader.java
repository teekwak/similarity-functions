package DataAnalysis;

import java.io.*;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

class SimilarityFunctionTime {
	private String bitvector;
	private long technicalStartTime;
	private long technicalEndTime;
	private long socialStartTime;
	private long socialEndTime;
	private long uploadStartTime;
	private long uploadEndTime;

	SimilarityFunctionTime(String bv) {
		this.bitvector = bv;
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
}

class ProjectTime {
	private String url;
	private long processStartTime;
	private long processEndTime;
	private long cloningStartTime;
	private long cloningEndTime;
	private Map<String, SimilarityFunctionTime> similarityFunctionTimes;

	ProjectTime(String u) {
		this.url = u;
		this.similarityFunctionTimes = new HashMap<>();
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

	void addSimilarityFunctionTime(SimilarityFunctionTime sft) {
		similarityFunctionTimes.put(sft.getBitvector(), sft);
	}

	long getProcessTotalTime() {
		return this.processEndTime - this.processStartTime;
	}

	long getCloningTotalTime() {
		return this.cloningEndTime - this.cloningStartTime;
	}
}

public class TimesReader {
	private static Set<ProjectTime> projectTimes;
	private static ProjectTime projectTime;
	private static SimilarityFunctionTime similarityFunctionTime;

	private static void parseTimesFromFile(File file) {
		try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"))) {
			for(String line; (line = br.readLine()) != null; ) {
				if(line.startsWith("URL")) {
					projectTime = new ProjectTime(line.split("::")[1]);
				}
				else if(line.startsWith("Started process")) {
					projectTime.setProcessStartTime(Long.parseLong(line.split("::")[1]));
				}
				else if(line.startsWith("Finished process")) {
					projectTime.setProcessEndTime(Long.parseLong(line.split("::")[1]));
					projectTimes.add(projectTime);
				}
				else if(line.startsWith("Started cloning")) {
					projectTime.setCloningStartTime(Long.parseLong(line.split("::")[1]));
				}
				else if(line.startsWith("Finished cloning")) {
					projectTime.setCloningEndTime(Long.parseLong(line.split("::")[1]));
				}
				else if(line.startsWith("Started technical")) {
					similarityFunctionTime = new SimilarityFunctionTime(line.split("::|\\$\\$")[1]);
					similarityFunctionTime.setTechnicalStartTime(Long.parseLong(line.split("::|\\$\\$")[2]));
				}
				else if(line.startsWith("Finished technical")) {
					similarityFunctionTime.setTechnicalEndTime(Long.parseLong(line.split("::|\\$\\$")[2]));
				}
				else if(line.startsWith("Started social")) {
					similarityFunctionTime.setSocialStartTime(Long.parseLong(line.split("::|\\$\\$")[2]));
				}
				else if(line.startsWith("Finished social")) {
					similarityFunctionTime.setSocialEndTime(Long.parseLong(line.split("::|\\$\\$")[2]));
				}
				else if(line.startsWith("Started uploading")) {
					similarityFunctionTime.setUploadStartTime(Long.parseLong(line.split("::|\\$\\$")[2]));
				}
				else if(line.startsWith("Finished uploading")) {
					similarityFunctionTime.setUploadEndTime(Long.parseLong(line.split("::|\\$\\$")[2]));
					projectTime.addSimilarityFunctionTime(similarityFunctionTime);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		projectTimes = new HashSet<>();

		File topDirectory = new File("times/");

		File[] files = topDirectory.listFiles();
		if(files != null) {
			for(File file : files) {
				if(file.getName().endsWith("_times.txt")) {
					parseTimesFromFile(file);
				}
			}
		}


		// random printing times
		long totalProcessTime = 0;
		long totalCloningTime = 0;
		for(ProjectTime pt : projectTimes) {
			totalProcessTime += pt.getProcessTotalTime();
			totalCloningTime += pt.getCloningTotalTime();
		}

		System.out.println("Total number of observations: " + projectTimes.size());
		System.out.println("Average time to process: " + (totalProcessTime / projectTimes.size() / 1000) + "s");
		System.out.println("Average time to clone: " + (totalCloningTime / projectTimes.size() / 1000) + "s");
	}
}
