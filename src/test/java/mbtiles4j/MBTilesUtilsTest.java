package mbtiles4j;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class MBTilesUtilsTest {

	private static MBTilesUtils mbtu;

	@BeforeClass
	public static void extractTestDatabase() throws IOException {
		String db = MBTilesUtils.getDatabaseLocation();

		InputStream is = MBTilesUtilsTest.class.getResourceAsStream(db);
		OutputStream os = new FileOutputStream(db);
		IOUtils.copy(is, os);
		os.flush();
		os.close();
		is.close();

		mbtu = MBTilesUtils.getInstance();
	}

	@Test
	public void retrieveTiles() {
		check(0, 0, 0);
		checkNot(0, 1, 0);
		check(0, 1, 1);
		checkNot(0, 0, 3);
	}

	@AfterClass
	public static void deleteTestDatabase() {
		mbtu.close();

		String db = MBTilesUtils.getDatabaseLocation();
		new File(db).delete();
	}

	private void check(int x, int y, int z) {
		byte[] tile = mbtu.getTiles(x, y, z);
		Assert.assertTrue(tile != null && tile.length > 0);
	}

	private void checkNot(int x, int y, int z) {
		byte[] tile = mbtu.getTiles(x, y, z);
		Assert.assertTrue(tile == null);
	}
}
