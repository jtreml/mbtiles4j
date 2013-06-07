package mbtiles4j;

import java.io.IOException;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

@SuppressWarnings("serial")
public class TileServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String path = request.getPathInfo();
		if (StringUtils.isEmpty(path)) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		path = path.substring(1);
		String[] split = path.split(Pattern.quote("/"));

		if (split.length != 3) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		if (!split[2].toLowerCase().endsWith(".png")) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		split[2] = split[2].replace(".png", "");

		int z;
		int y;
		int x;

		try {
			z = Integer.parseInt(split[0]);
			y = Integer.parseInt(split[1]);
			x = Integer.parseInt(split[2]);
		} catch (NumberFormatException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		byte[] tile = MBTilesUtils.getInstance().getTiles(x, y, z);
		if (tile == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		response.setContentType("image/png");
		response.setContentLength(tile.length);

		ServletOutputStream oStream = response.getOutputStream();
		IOUtils.write(tile, oStream);
		oStream.flush();
		oStream.close();
	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}
