package Tests.Extra;

import Extra.Cleaner;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;
import java.io.IOException;

public class CleanerTest {
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Before
	public void initialize() {
			File dir = new File("./TestDirectory");
			dir.mkdir();
			File f = new File("./TestDirectory/TestFile.txt");
	}

	@After
	public void destroy() {
		new File("./testDirectory").delete();
	}

	// deleteAllFilesInDirectory

	@Test
	public void correctDirectoryThrowsNothing() {
		Cleaner.deleteAllFilesInDirectory("./testDirectory");
	}

	@Test
	public void wrongDirectoryThrowsNullPointerException() {
		thrown.expect(NullPointerException.class);
		Cleaner.deleteAllFilesInDirectory("./Does/Not/Exist");
	}

	// deleteCertainFileInDirectory

	// empty
}