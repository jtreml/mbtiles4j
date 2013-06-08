package mbtiles4j;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class MBTilesUtils {

	private static final ConcurrentHashMap<String, MBTilesUtils> INSTANCES = new ConcurrentHashMap<String, MBTilesUtils>();

	private final Connection conn;

	private final PreparedStatement ps;

	private MBTilesUtils(String db) {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}

		if (db == null || !new File(db).exists()) {
			throw new RuntimeException("No database");
		}

		try {
			conn = DriverManager.getConnection("jdbc:sqlite:" + db);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		try {
			ps = conn.prepareStatement("SELECT tile_data FROM tiles "
					+ "WHERE zoom_level = ? AND tile_column = ? "
					+ "AND tile_row = ?;");
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	static Map<String, String> getDatabases() {
		Properties configuration = new Properties();
		try {
			configuration.load(MBTilesUtils.class.getClassLoader()
					.getResourceAsStream("mbtiles4j.properties"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		HashMap<String, String> result = new HashMap<String, String>();

		String dbs = configuration.getProperty("tile-dbs");
		if (StringUtils.isEmpty(dbs)) {
			return result;
		}

		String[] split = dbs.split(Pattern.quote(","));
		for (String entry : split) {
			String path = configuration.getProperty(entry + ".path");
			if (!StringUtils.isEmpty(path)) {
				result.put(entry, path);
			}
		}

		return result;
	}

	public static MBTilesUtils getInstance(String db) {
		return INSTANCES.get(db);
	}

	public synchronized byte[] getTiles(int x, int y, int z) {
		int index = 1;

		ResultSet rs = null;
		try {
			ps.setInt(index++, z);
			ps.setInt(index++, x);
			ps.setInt(index++, y);

			rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getBytes(1);
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	private synchronized void close() {
		if (ps != null) {
			try {
				ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static void connect() {
		Map<String, String> dbs = getDatabases();
		for (String db : dbs.keySet()) {
			if (!INSTANCES.containsKey(db)) {
				INSTANCES.put(db, new MBTilesUtils(dbs.get(db)));
			}
		}
	}

	public static void disconnect() {
		for (MBTilesUtils entry : INSTANCES.values()) {
			entry.close();
		}
	}
}
