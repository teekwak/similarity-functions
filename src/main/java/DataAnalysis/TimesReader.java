package DataAnalysis;

import java.io.*;
import java.util.HashSet;
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
//				else if(line.startsWith("Started technical")) {
//
//				}
//				else if(line.startsWith("Started process::")) {
//
//				}
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


		for(ProjectTime pt : projectTimes) {
			System.out.println(pt.getProcessTotalTime() / 1000);
		}
	}
}
