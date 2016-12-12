package DataAnalysis;

import java.util.*;

/**
 * Created by Kwak
 */
public class Statistics {
	public static int calculateFreedmanDiaconisChoice(double iqr, int n) {
		return (int)Math.ceil(2 * iqr / Math.pow(n, 1/3));
	}

	// calculate median from map
	public static <K, V extends Number> double calculateIQR(Map<K, V> map) {
		map = sortByValue(map);
		List<V> entryList = new ArrayList<>(map.values());
		return calculateIQR(entryList);
	}

	// calculate median from list of numbers
	public static <V extends Number> double calculateIQR(List<V> list) {
		int middle = list.size() / 2;
		double firstQuartile, thirdQuartile;
		if(list.size() % 2 == 0) {
			firstQuartile = calculateMedian(list.subList(0, middle));
			thirdQuartile = calculateMedian(list.subList(middle, list.size()));
		}
		else {
			firstQuartile = calculateMedian(list.subList(0, middle));
			thirdQuartile = calculateMedian(list.subList(middle + 1, list.size()));
		}

		return thirdQuartile - firstQuartile;
	}

	// calculate median from list of numbers
	public static <V extends Number> double calculateMedian(List<V> list) {
		list = sortByValue(list);

		int middle = list.size() / 2;

		if(list.size() % 2 == 0) {
			int left = middle - 1;
			return (list.get(left).doubleValue() + list.get(middle).doubleValue()) / 2;
		}
		else {
			return list.get(middle).doubleValue();
		}
	}

	// calculate median from map
	public static <K, V extends Number> double calculateMedian(Map<K, V> map) {
		map = sortByValue(map);
		List<V> entryList = new ArrayList<>(map.values());
		return calculateMedian(entryList);
	}

	// works!
	// calculate mean from list of numbers
	public static <V extends Number> double calculateMean(List<V> list) {
		double totalSum = 0.0;

		for(V value : list) {
			totalSum += value.doubleValue();
		}

		return totalSum / list.size();
	}

	// works!
	// calculate mean from map
	public static <K, V extends Number> double calculateMean(Map<K, V> map) {
		double totalSum = 0.0;

		for(Map.Entry<K, V> entry : map.entrySet()) {
			totalSum += entry.getValue().doubleValue();
		}

		return totalSum / map.size();
	}

	// works!
	// sort list by value (lowest to highest)
	public static <V extends Number> List<V> sortByValue(List<V> list) {
		List<V> tempList = new ArrayList<>(list);
		tempList.sort(Comparator.comparingDouble(Number::doubleValue));
		return tempList;
	}

	// works!
	// sort map by value (lowest to highest)
	public static <K, V extends Number> Map<K, V> sortByValue(Map<K, V> map) {
		List<Map.Entry<K, V>> entryList = new ArrayList<>(map.entrySet());

		entryList.sort((a, b) -> Double.compare(a.getValue().doubleValue(), b.getValue().doubleValue()));

		map = new LinkedHashMap<>();
		for(Map.Entry<K, V> e : entryList) {
			map.put(e.getKey(), e.getValue());
		}

		return map;
	}

	public static void main(String[] args) {
		Map<String, Integer> map = new HashMap<>();
		map.put("a", 4);
		map.put("b", 3);
		map.put("c", 2);
		map.put("d", 6);
		map.put("e", 7);

		sortByValue(map).forEach((k, v) -> System.out.println(k + " " + v));

//		List<Integer> list = new ArrayList<>(Arrays.asList(4, 6, 3, 7, 2));
//		sortByValue(list).forEach(System.out::println);

		//System.out.println(calculateMean(map));
	}
}
