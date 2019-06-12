package bg.bulsi.eforms.jsf.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class Util {

	public static String getFileNameFromUrl(String url) {
		if (url != null) {

			try {
				url = URLDecoder.decode(url, "UTF-8");
			} catch (UnsupportedEncodingException ignored) {
			}

			int beginIndex = url.lastIndexOf("/") + 1;
			int endIndex = url.length();
			int paramIndex = url.indexOf("?");
			if (paramIndex > 0) {
				endIndex = paramIndex;
			}
			return url.substring(beginIndex, endIndex);
		}
		return null;
	}
}
