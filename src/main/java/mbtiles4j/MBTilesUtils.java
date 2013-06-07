package mbtiles4j;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class MBTilesUtils {

	private static MBTilesUtils instance;

	private final Connection conn;

	private final PreparedStatement ps;

	private MBTilesUtils() {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}

		String db = getDatabaseLocation();
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

	static String getDatabaseLocation() {
		Properties configuration = new Properties();
		try {
			configuration.load(MBTilesUtils.class.getClassLoader()
					.getResourceAsStream("mbtiles4j.properties"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return configuration.getProperty("tile-db");
	}

	public static MBTilesUtils getInstance() {
		if (instance == null) {
			instance = new MBTilesUtils();
		}

		return instance;
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

	public synchronized void close() {
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
}
