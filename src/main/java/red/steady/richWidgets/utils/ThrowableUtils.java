/*
 * Created on Mar 31, 2004
 */

package red.steady.richWidgets.utils;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * @author Paul S Davenport III
 * @copyright Steady Red Fred 2011-2012
 * @svnid SVNID
 */

public class ThrowableUtils {

	public static String getStackTrace(Throwable throwable) {

		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			PrintStream ps = new PrintStream(baos);

			throwable.printStackTrace(ps);

			String result = baos.toString(); // + "\n";

			return result;
		} catch (Throwable e) {
			return "Stack Trace Failed" + "\n" + e.getMessage() + "\n";
		}
	}

}
