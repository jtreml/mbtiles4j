package mbtiles4j;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class MBTilesUtilsTest {

	private static MBTilesUtils mbtu;

	private static String path;

	@BeforeClass
	public static void extractTestDatabase() throws IOException {
		Map<String, String> dbs = MBTilesUtils.getDatabases();
		Assert.assertTrue(dbs.size() == 1);

		String db = dbs.keySet().iterator().next();
		path = dbs.get(db);

		InputStream is = null;
		OutputStream os = null;
		try {
			is = MBTilesUtilsTest.class.getResourceAsStream(path);
			os = new FileOutputStream(path);
			IOUtils.copy(is, os);
			os.flush();
		} finally {
			if (os != null) {
				os.close();
			}
			if (is != null) {
				is.close();
			}
		}

		MBTilesUtils.connect();
		mbtu = MBTilesUtils.getInstance(db);
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
		MBTilesUtils.disconnect();
		new File(path).delete();
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
