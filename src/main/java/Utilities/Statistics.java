package Utilities;

import java.util.*;

/**
 * Created by Kwak
 */
public class Statistics {

	/**
	 * Calculates the number of bins using the Freedman Diaconis Choice formula
	 *
	 * @param iqr the IQR of the population
	 * @param n the number of unique values
	 * @return number of bins
	 */
	public static int calculateFreedmanDiaconisChoice(double iqr, int n) {
		return (int)Math.ceil(2 * iqr / Math.pow(n, 1/3));
	}

	// calculate median from map
	public static <K, V extends Number> double calculateIQR(Map<K, V> map) {
		if(map == null || map.size() == 0) {
			throw new IllegalArgumentException("[ERROR]: map size cannot be zero!");
		}

		map = sortByValue(map);
		List<V> entryList = new ArrayList<>(map.values());
		return calculateIQR(entryList);
	}

	// calculate median from list of numbers
	public static <V extends Number> double calculateIQR(List<V> list) {
		if(list == null || list.size() == 0) {
			throw new IllegalArgumentException("[ERROR]: list size cannot be zero!");
		}

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

	// untested
	public static <K, V extends Number> double calculateVariance(Map<K, V> map) {
		return calculateVariance(new ArrayList<>(map.values()));
	}

	// untested
	public static <V extends Number> double calculateVariance(List<V> list) {
		return Math.pow(calculateStandardDeviation(list), 2);
	}

	// untested
	public static <K, V extends Number> double calculateStandardDeviation(Map<K, V> map) {
		return calculateStandardDeviation(new ArrayList<>(map.values()));
	}

	// untested
	public static <V extends Number> double calculateStandardDeviation(List<V> list) {
		double mean = calculateMean(list);
		double standardDeviation = 0.0;

		for(V value : list) {
			standardDeviation += Math.pow((value.doubleValue() - mean), 2);
		}

		return standardDeviation;
	}

	// calculate median from list of numbers
	public static <V extends Number> double calculateMedian(List<V> list) {
		if(list == null || list.size() == 0) {
			throw new IllegalArgumentException("[ERROR]: list size cannot be zero!");
		}

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
		if(map == null || map.size() == 0) {
			throw new IllegalArgumentException("[ERROR]: map size cannot be zero!");
		}

		map = sortByValue(map);
		List<V> entryList = new ArrayList<>(map.values());
		return calculateMedian(entryList);
	}

	// calculate mean from list of numbers
	public static <V extends Number> double calculateMean(List<V> list) {
		if(list == null || list.size() == 0) {
			throw new IllegalArgumentException("[ERROR]: list size cannot be zero!");
		}

		double totalSum = 0.0;

		for(V value : list) {
			totalSum += value.doubleValue();
		}

		return totalSum / list.size();
	}

	// calculate mean from map
	public static <K, V extends Number> double calculateMean(Map<K, V> map) {
		if(map == null || map.size() == 0) {
			throw new IllegalArgumentException("[ERROR]: map size cannot be zero!");
		}

		double totalSum = 0.0;

		for(Map.Entry<K, V> entry : map.entrySet()) {
			totalSum += entry.getValue().doubleValue();
		}

		return totalSum / map.size();
	}

	// sort list by value (lowest to highest)
	public static <V extends Number> List<V> sortByValue(List<V> list) {
		List<V> tempList = new ArrayList<>(list);
		tempList.sort(Comparator.comparingDouble(Number::doubleValue));
		return tempList;
	}

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
		System.out.println(calculateMedian(new ArrayList<Integer>()));
	}
}
