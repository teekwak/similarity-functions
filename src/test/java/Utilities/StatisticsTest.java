package Utilities;

import org.junit.Test;

import java.util.*;

import static Utilities.Statistics.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;

/**
 * Created by Kwak
 */
public class StatisticsTest {
	private static final double DELTA = 1e-15;

	@Test
	public void sortByValueWorksForMapsWithZeroElements() {
		Map<String, Integer> map = new LinkedHashMap<>();
		assertThat(sortByValue(map), is(new LinkedHashMap<>()));
	}

	@Test
	public void sortByValueWorksForMaps() {
		Map<String, Integer> map = new LinkedHashMap<>();
		map.put("a", 1);
		map.put("b", 4);
		map.put("c", 5);
		map.put("d", 3);
		map.put("e", 2);

		Map<String, Integer> expected = new LinkedHashMap<>();
		expected.put("a", 1);
		expected.put("e", 2);
		expected.put("d", 3);
		expected.put("b", 4);
		expected.put("c", 5);

		assertThat(sortByValue(map), is(expected));
	}

	@Test
	public void sortByValueWorksForListsWithZeroElements() {
		List<Integer> list = new ArrayList<>();
		assertThat(sortByValue(list), is(new ArrayList<>()));
	}

	@Test
	public void sortByValueWorksForLists() {
		List<Integer> list = new ArrayList<>(Arrays.asList(3, 1, 4, 5, 2));
		List<Integer> expected = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5));

		assertThat(sortByValue(list), is(expected));
	}

	@Test(expected=IllegalArgumentException.class)
	public void calculateMeanThrowsExceptionWhenMapSizeIsZero() {
		calculateMean(new HashMap<String, Integer>());
	}

	@Test(expected=IllegalArgumentException.class)
	public void calculateMeanThrowsExceptionWhenListSizeIsZero() {
		calculateMean(new ArrayList<Integer>());
	}

	@Test
	public void calculateMeanForMap() {
		Map<String, Integer> map = new HashMap<>();
		map.put("a", 1);
		map.put("b", 4);
		map.put("c", 5);
		map.put("d", 3);
		map.put("e", 2);

		assertEquals(calculateMean(map), 3.0, DELTA);
	}

	@Test
	public void calculateMeanForMapWithOneElement() {
		Map<String, Integer> map = new HashMap<>();
		map.put("a", 1);

		assertEquals(calculateMean(map), 1.0, DELTA);
	}

	@Test
	public void calculateMeanForList() {
		List<Integer> list = new ArrayList<>();
		list.add(1);
		list.add(4);
		list.add(5);
		list.add(3);
		list.add(2);

		assertEquals(calculateMean(list), 3.0, DELTA);
	}

	@Test
	public void calculateMeanForListWithOneElement() {
		List<Integer> list = new ArrayList<>();
		list.add(1);

		assertEquals(calculateMean(list), 1.0, DELTA);
	}
}