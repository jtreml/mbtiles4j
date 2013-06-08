package mbtiles4j;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class MBTiles4jContextListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		MBTilesUtils.connect();
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		MBTilesUtils.disconnect();
	}
}
