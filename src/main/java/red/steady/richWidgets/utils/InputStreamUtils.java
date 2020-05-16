package red.steady.richWidgets.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class InputStreamUtils {

	public static InputStream getInputStream(File file) {
		try {
			return new FileInputStream(file);
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Input file, " + file.getAbsolutePath() + ", was not found", e);
		}
	}

	public static InputStream getInputStream(Class<?> aClass, String resource) {
		return aClass.getResourceAsStream(resource);
	}
}
