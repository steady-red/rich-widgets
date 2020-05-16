package red.steady.richWidgets.utils;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.GroupPrincipal;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.UserPrincipal;
import java.security.InvalidParameterException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Formatter;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import com.google.common.util.concurrent.SimpleTimeLimiter;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigObject;
import com.typesafe.config.ConfigValue;

public class RichUtils {

	public static <T> T evaluate(Supplier<T> supplier) {
		return supplier.get();
	}

	public static <T> List<T> getItemsInThreads(//
			String executionName, //
			ExecutorService executorService, //
			int total, //
			int groupCount, //
			Function<Integer, T> createItemFunction, //
			GroupCompletionMessageHandler groupCompletionMessageHandler) {
		List<T> resultList = Collections.synchronizedList(new ArrayList<T>());

		int groupNumber = 0;
		int totalProcessed = 0;
		do { // groupNumber < total / groupCount; groupNumber++) {
			final int finalGroupNumber = groupNumber;

			long startTime = System.currentTimeMillis();

			int processCount = Math.min(groupCount, (total - totalProcessed));

			CompletableFuture<?>[] completableFutures = new CompletableFuture<?>[processCount];

			// Start all
			for (int index = 0; index < processCount; index++) {
				final int finalIndex = index;

				completableFutures[index] //
						= CompletableFuture.supplyAsync(() -> {
							return createItemFunction.apply(finalGroupNumber * groupCount + finalIndex);
						}, executorService)//
								.whenComplete((item, exception) -> {
									if (exception != null) {
										throw new RuntimeException("Failed to create item", exception);
									} else {
										resultList.add(item);
									}
								});
			}

			CompletableFuture<?> allCompletableFuture = CompletableFuture.allOf(completableFutures);

			try {
				allCompletableFuture.get();
			} catch (InterruptedException e) {
				throw new RuntimeException("getItemsInThreads() failed to complete thread executions", e);
			} catch (ExecutionException e) {
				throw new RuntimeException("getItemsInThreads() failed to complete thread executions", e);
			}

			totalProcessed += processCount;

			if (groupCompletionMessageHandler != null) {
				long durationInMilliSeconds = System.currentTimeMillis() - startTime;

				System.out.println(groupCompletionMessageHandler.getGroupCompletionMessage(groupNumber, totalProcessed,
						durationInMilliSeconds));
			}
			groupNumber++;
		} while (totalProcessed < total);

		return resultList;
	}

	public static Function<Integer, String> integerZeroPadder(int numberOfDigits) {
		return new Function<Integer, String>() {
			@Override
			public String apply(Integer value) {
				return String.format("%0" + numberOfDigits + "d", value);
			}
		};
	}

	public static Date date(int year, int month, int dayOfMonth) {
		return new GregorianCalendar(year, month - 1, dayOfMonth).getTime();
	}

	public static Date addDays(Date date, int numberOfDays) {
		GregorianCalendar gregorianCalendar = new GregorianCalendar();

		gregorianCalendar.setTime(date);

		gregorianCalendar.set(Calendar.DAY_OF_YEAR, gregorianCalendar.get(Calendar.DAY_OF_YEAR) + numberOfDays);

		return gregorianCalendar.getTime();
	}

	public static Date addDay(Date date) {
		GregorianCalendar gregorianCalendar = new GregorianCalendar();

		gregorianCalendar.setTime(date);

		gregorianCalendar.set(Calendar.DAY_OF_YEAR, gregorianCalendar.get(Calendar.DAY_OF_YEAR) + 1);

		return addDays(date, 1);
	}

	public static void setSystemProperty(String key, String value) {
		Properties properties = System.getProperties();

		properties.setProperty(key, value);
	}

	public static Properties readProperties(File propertiesFile) {
		try {
			String fileContents = readFileToString(propertiesFile);

			Properties properties = new Properties();

			properties.load(new StringReader(fileContents));

			return properties;
		} catch (IOException e) {
			throw new RuntimeException("Failed to load properties from " + quoted(propertiesFile.getAbsolutePath()));
		}
	}

	public static boolean notNull(Object... objects) {
		for (Object object : objects) {
			if (object == null) {
				return false;
			}
		}

		return true;
	}

	public static void waitForInterrupt(Class<?> aClass) {
		try {
			synchronized (aClass) {
				aClass.wait();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * from http://stackoverflow.com/users/336422/abduliam-rehmanius
	 *
	 * @param dateTime
	 * @return
	 */
	public static String timeSince(Date dateTime) {
		StringBuffer sb = new StringBuffer();
		Date current = Calendar.getInstance().getTime();
		long diffInSeconds = (current.getTime() - dateTime.getTime()) / 1000;

		/*
		 * long diff[] = new long[]{0, 0, 0, 0}; /* sec * diff[3] = (diffInSeconds >= 60
		 * ? diffInSeconds % 60 : diffInSeconds); /* min * diff[2] = (diffInSeconds =
		 * (diffInSeconds / 60)) >= 60 ? diffInSeconds % 60 : diffInSeconds; /* hours *
		 * diff[1] = (diffInSeconds = (diffInSeconds / 60)) >= 24 ? diffInSeconds % 24 :
		 * diffInSeconds; /* days * diff[0] = (diffInSeconds = (diffInSeconds / 24));
		 */
		long sec = (diffInSeconds >= 60 ? diffInSeconds % 60 : diffInSeconds);
		long min = (diffInSeconds = (diffInSeconds / 60)) >= 60 ? diffInSeconds % 60 : diffInSeconds;
		long hrs = (diffInSeconds = (diffInSeconds / 60)) >= 24 ? diffInSeconds % 24 : diffInSeconds;
		long days = (diffInSeconds = (diffInSeconds / 24)) >= 30 ? diffInSeconds % 30 : diffInSeconds;
		long months = (diffInSeconds = (diffInSeconds / 30)) >= 12 ? diffInSeconds % 12 : diffInSeconds;
		long years = (diffInSeconds = (diffInSeconds / 12));

		if (years > 0) {
			if (years == 1) {
				sb.append("1 year");
			} else {
				sb.append(years + " years");
			}
			if (years <= 6 && months > 0) {
				if (months == 1) {
					sb.append(" and a month");
				} else {
					sb.append(" and " + months + " months");
				}
			}
		} else if (months > 0) {
			if (months == 1) {
				sb.append("1 month");
			} else {
				sb.append(months + " months");
			}
			if (months <= 6 && days > 0) {
				if (days == 1) {
					sb.append(" and a day");
				} else {
					sb.append(" and " + days + " days");
				}
			}
		} else if (days > 0) {
			if (days == 1) {
				sb.append("1 day");
			} else {
				sb.append(days + " days");
			}
			if (days <= 3 && hrs > 0) {
				if (hrs == 1) {
					sb.append(" and an hour");
				} else {
					sb.append(" and " + hrs + " hours");
				}
			}
		} else if (hrs > 0) {
			if (hrs == 1) {
				sb.append("1 hour");
			} else {
				sb.append(hrs + " hours");
			}
			if (min > 1) {
				sb.append(" and " + min + " minutes");
			}
		} else if (min > 0) {
			if (min == 1) {
				sb.append("1 minute");
			} else {
				sb.append(min + " minutes");
			}
			if (sec > 1) {
				sb.append(" and " + sec + " seconds");
			}
		} else {
			if (sec <= 1) {
				sb.append("1 second");
			} else {
				sb.append(sec + " seconds");
			}
		}

		sb.append(" ago");

		/*
		 * String result = new String(String.format(
		 * "%d day%s, %d hour%s, %d minute%s, %d second%s ago", diff[0], diff[0] > 1 ?
		 * "s" : "", diff[1], diff[1] > 1 ? "s" : "", diff[2], diff[2] > 1 ? "s" : "",
		 * diff[3], diff[3] > 1 ? "s" : ""));
		 */

		return sb.toString();
	}

	// /////////////////////////////////////////////////////////////////////
	// Parameter Checking
	// /////////////////////////////////////////////////////////////////////

	public static File checkAndCanonicalizeFileParameter(File file, String fileName) {
		if (file == null) {
			throw new NullPointerException("File " + quoted(fileName) + " must not be null");
		}

		try {
			file = file.getCanonicalFile().getAbsoluteFile();
		} catch (IOException e) {
			throw new RuntimeException("Could not canonicalize file: " + quoted(file.getAbsolutePath()), e);
		}

		return file;
	}

	public static File createPersistenceDirectory(File directory, String subDirectoryName) {
		try {
			File newDirectory = new File(directory, subDirectoryName).getCanonicalFile().getAbsoluteFile();
			checkPersistenceDirectory(newDirectory);
			return newDirectory;
		} catch (IOException e) {
			throw new RuntimeException("Failed to create directory " + quoted(subDirectoryName) + " in "
					+ quoted(directory.getAbsolutePath()), e);
		}
	}

	public static void checkPersistenceDirectory(File persistenceDirectory) {

		persistenceDirectory.mkdirs();

		checkDirectoriesExist(persistenceDirectory);

		checkFileWritable(persistenceDirectory);
	}

	public static File checkPerisenceDirectoryParameter(File persistenceDirectory, String directoryFilePath) {
		File canonicalPersistenceDirectory = checkAndCanonicalizeFileParameter(persistenceDirectory, directoryFilePath);

		canonicalPersistenceDirectory.mkdirs();

		checkDirectoriesExist(canonicalPersistenceDirectory);

		checkFileWritable(canonicalPersistenceDirectory);

		return canonicalPersistenceDirectory;
	}

	public static File checkPerisenceFileParameter(File persistenceFile, String fileName) {
		File canonicalPersistenceFile = checkAndCanonicalizeFileParameter(persistenceFile, fileName);

		canonicalPersistenceFile.getParentFile().mkdirs();

		checkDirectoriesExist(canonicalPersistenceFile.getParent());

		if (canonicalPersistenceFile.exists() == true) {
			checkFileWritable(canonicalPersistenceFile);
		} else {
			try {
				canonicalPersistenceFile.createNewFile();
			} catch (IOException e) {
				throw new RuntimeException(
						"Could not create persistence file: " + quoted(canonicalPersistenceFile.getAbsolutePath()), e);
			}
		}

		if (canonicalPersistenceFile.exists() == false) {
			throw new RuntimeException(
					"Could not create persistence file: " + quoted(canonicalPersistenceFile.getAbsolutePath()));
		}

		return canonicalPersistenceFile;
	}

	public static <T> T checkIsNull(T reference) {
		if (reference != null) {
			throw new RuntimeException("Should be null");
		}

		return reference;
	}

	/**
	 * Ensures that an object reference passed as a parameter to the calling method
	 * is not null.
	 *
	 * from Guava Preconditions
	 *
	 * @param reference an object reference
	 * @return the non-null reference that was validated
	 * @throws NullPointerException if {@code reference} is null
	 */
	public static <T> T checkNotNull(T reference) {
		if (reference == null) {
			throw new NullPointerException();
		}
		return reference;
	}

	@SuppressWarnings("serial")
	public static class InvalidCondition extends RuntimeException {
		public InvalidCondition() {
			super();
		}

		public InvalidCondition(String message, Throwable cause, boolean enableSuppression,
				boolean writableStackTrace) {
			super(message, cause, enableSuppression, writableStackTrace);
		}

		public InvalidCondition(String message, Throwable cause) {
			super(message, cause);
		}

		public InvalidCondition(String message) {
			super(message);
		}

		public InvalidCondition(Throwable cause) {
			super(cause);
		}
	}

	public static <T> boolean isEqual(byte[] bytes1, byte[] bytes2) {
		if (((bytes1 == null) || (bytes2 == null)) //
				&& !((bytes1 == null) && (bytes2 == null))) {
			return false;
		}

		if (bytes1.length != bytes2.length) {
			return false;
		}

		for (int index = 0; index < bytes2.length; index++) {
			if (bytes1[index] != bytes2[index]) {
				return false;
			}
		}

		return true;
	}

	public static <T> void checkIsEqual(byte[] bytes1, byte[] bytes2) {
		if (((bytes1 == null) || (bytes2 == null)) //
				&& !((bytes1 == null) && (bytes2 == null))) {
			throw new InvalidCondition();
		}

		checkTrue(bytes1.length == bytes2.length);

		for (int index = 0; index < bytes2.length; index++) {
			checkTrue(bytes1[index] == bytes2[index]);
		}
	}

	public static <T> void checkIsEqual(T o1, T o2) {
		if (((o1 == null) || (o2 == null)) //
				&& !((o1 == null) && (o2 == null))) {
			throw new InvalidCondition();
		}

		checkTrue(o1.equals(o2));
	}

	public static void checkTrue(boolean condition) {
		if (condition == false) {
			throw new InvalidCondition();
		}
	}

	public static void checkFalse(boolean condition) {
		if (condition == true) {
			throw new InvalidCondition();
		}
	}

	/**
	 * Ensures that an object reference passed as a parameter to the calling method
	 * is not null.
	 *
	 * from Guava Preconditions
	 *
	 * @param reference    an object reference
	 * @param errorMessage the exception message to use if the check fails; will be
	 *                     converted to a string using
	 *                     {@link String#valueOf(Object)}
	 * @return the non-null reference that was validated
	 * @throws NullPointerException if {@code reference} is null
	 */
	public static <T> T checkNotNull(T reference, /* @Nullable */Object errorMessage) {
		if (reference == null) {
			throw new NullPointerException(String.valueOf(errorMessage));
		}
		return reference;
	}

	public static void writeSystemJsonMessage(String message, Object... objects) {
		System.out.println(getJsonMessage("System", message, objects));
	}

	public static String getSystemJsonMessage(String message, Object... objects) {
		return getJsonMessage("System", message, objects);
	}

	public static void writeJsonMessage(String messageType, String message, Object... objects) {
		System.out.println(getJsonMessage(messageType, message, objects));
	}

	public static String getJsonMessage(String messageType, String message, Object... objects) {
		String formattedMessage = message;

		if (objects.length != 0) {
			formattedMessage = String.format(message, objects);
		}

		return "{ \"messageType\", \"" + messageType + "\", \"message\", \""
				+ StringEscapeUtils.escapeEcmaScript(formattedMessage) + "\" }";
	}

	public static String defaultOnEmpty(String string, String defaultString) {
		if (isNonEmpty(string) == true) {
			return string;
		} else {
			return defaultString;
		}
	}

	public static int checkArrayBoundsParameter(int arraySize, int anInt, String parameterName) {
		if (anInt < 0) {
			throw new InvalidParameterException("Parameter " + parameterName + " must be greater than 0");
		}

		if (anInt > arraySize - 1) {
			throw new InvalidParameterException("Parameter " + parameterName + " must be less than " + (arraySize - 1));
		}

		return anInt;
	}

	public static int checkNonNegativeInt(int anInt) {
		if (anInt <= -1) {
			throw new InvalidParameterException("Must be greater than -1");
		}

		return anInt;
	}

	public static int checkPositiveInt(int anInt) {
		if (anInt <= 0) {
			throw new InvalidParameterException("Must be greater than 0");
		}

		return anInt;
	}

	public static int checkPositiveIntParameter(int anInt, String parameterName) {
		if (anInt <= 0) {
			throw new InvalidParameterException("Parameter " + parameterName + " must be greater than 0");
		}

		return anInt;
	}

	public static int checkZeroOrPositiveIntParameter(int anInt, String parameterName) {
		if (anInt < 0) {
			throw new InvalidParameterException("Parameter " + parameterName + " must be greater than or equal to 0");
		}

		return anInt;
	}

	public static int checkSameValue(int value1, int value2) {
		if (value1 != value2) {
			throw new InvalidParameterException(value1 + " = value1 != value2 = " + value2);
		}

		return value1;
	}

	public static String checkNotEmpty(String string) {
		if (isNonEmpty(string) == false) {
			throw new InvalidParameterException("Must not be empty");
		}

		return string;
	}

	public static String checkNotEmptyParameter(String string, String parameterName) {
		if (isNonEmpty(string) == false) {
			throw new InvalidParameterException("Parameter " + quoted(parameterName) + " must not be empty");
		}

		return string;
	}

	public static <T> void checkNotEmptyArrayParameter(T[] array, String parameterName) {
		if ((array == null) || (array.length == 0)) {
			throw new InvalidParameterException(
					"Parameter " + quoted(parameterName) + " must not be NOT null and NOT lenght zero");
		}
	}

	public static <T> T checkParameterType(T reference, String parameterName, Class<?>... classes) {
		if (classes.length == 0) {
			throw new NullPointerException(
					"Parameter " + quoted(parameterName) + " not classes were specefied in test.");
		}

		List<String> classStringsList //
				= toList(classes).stream().map(new Function<Class<?>, String>() {
					@Override
					public String apply(Class<?> aClass) {
						return quoted(aClass.getName());
					}
				}).collect(Collectors.toList());

		String classsesNamesString = String.join(", ", classStringsList);

		for (Class<?> class1 : classes) {
			if (class1.isInstance(reference) == true) {
				return reference;
			}
		}

		throw new NullPointerException(
				"Parameter " + quoted(parameterName) + " was not an instance of " + classsesNamesString);
	}

	public static <T> T checkInValueSetParameter(T reference, String parameterName, Set<T> valuesSet) {
		if (valuesSet.contains(reference) == false) {
			throw new NullPointerException(
					"Parameter " + quoted(parameterName) + " must not be one of " + join(valuesSet, ","));
		}

		return reference;
	}

	public static void checkNonNegativeIntParameter(int anInt, String parameterName) {
		if (anInt <= -1) {
			throw new InvalidParameterException("Parameter " + quoted(parameterName) + " must be greater than -1");
		}
	}

	public static void checkKeyParameter(Object key, Map<?, ?> map, String keyName, String mapName) {
		if (map.containsKey(key) == false) {
			throw new InvalidParameterException(
					"Parameter key" + quoted("" + key) + " must be in map " + quoted(mapName));
		}
	}

	public static <T> T checkNotNullParameter(T reference, String parameterName) {
		if (reference == null) {
			throw new NullPointerException("Parameter " + quoted(parameterName) + " must not be null");
		}
		return reference;
	}

	public static <T> List<T> checkNotNullsInListParameter(List<T> aList, String parameterName) {
		checkNotNullParameter(aList, parameterName);

		for (int index = 0; index < aList.size(); index++) {
			checkNotNullParameter(aList.get(index), "parameterName[" + index + "]");
		}

		return aList;
	}

	public static <T> List<T> checkNotEmptyList(List<T> aList) {
		if (aList == null) {
			throw new NullPointerException();
		}

		if (aList.size() == 0) {
			throw new InvalidParameterException("List must not be empty");
		}

		return aList;
	}

	public static <T> List<T> checkNotEmptyListParameter(List<T> aList, String parameterName) {
		if (aList == null) {
			throw new NullPointerException("Parameter " + parameterName + " must not be null");
		}

		if (aList.size() == 0) {
			throw new InvalidParameterException("Parameter " + parameterName + " must not be an empty list");
		}

		return aList;
	}

	public static <T> TreeSet<T> checkNotEmptyCollection(TreeSet<T> treeSet) {
		if (treeSet == null) {
			throw new NullPointerException();
		}

		if (treeSet.size() == 0) {
			throw new InvalidParameterException("List must not be empty");
		}

		return treeSet;
	}

	public static <T> T checkNotNullAndConditionForParameter(T object, String parameterName,
			Function<T, Boolean> condition, String errorMessageFormat) {
		if (object == null) {
			throw new NullPointerException("Parameter " + parameterName + " must not be null");
		}

		if (condition.apply(object) == false) {
			throw new InvalidParameterException(MessageFormat.format(errorMessageFormat, parameterName, object));
		}

		return object;
	}

	public static <T, R> Map<T, R> checkMapForKeysParameter(Map<T, R> map, List<T> keys, String parameterName) {
		if (map == null) {
			throw new NullPointerException("Parameter " + parameterName + " must not be null");
		}

		List<String> missingKeys = new ArrayList<String>();

		for (T key : keys) {
			if (map.containsKey(key) == false) {
				missingKeys.add(key.toString());
			}
		}

		if (missingKeys.isEmpty() == false) {
			throw new InvalidParameterException("Parameter " + parameterName
					+ ": Map must contain values for each key - missing values for the following keys: "
					+ String.join(",", missingKeys));
		}

		return map;
	}

	// /////////////////////////////////////////////////////////////////////
	// Browser Support
	// /////////////////////////////////////////////////////////////////////

	public static String getHost(SocketAddress socketAddress) {
		if (socketAddress instanceof InetSocketAddress) {
			InetSocketAddress inetSocketAddress = (InetSocketAddress) socketAddress;

			return inetSocketAddress.getHostName();
		} else {
			throw new RuntimeException(
					"Found unsupported SocketAddress, " + quoted(socketAddress.getClass().getName()));
		}
	}

	public static int getPort(SocketAddress socketAddress) {
		if (socketAddress instanceof InetSocketAddress) {
			InetSocketAddress inetSocketAddress = (InetSocketAddress) socketAddress;

			return inetSocketAddress.getPort();
		} else {
			throw new RuntimeException(
					"Found unsupported SocketAddress, " + quoted(socketAddress.getClass().getName()));
		}
	}

	public static void openBrowser(String url) {
		openUrl(url);
	}

	public static void openUrl(String url) {

		if (Desktop.isDesktopSupported()) {
			Desktop desktop = Desktop.getDesktop();

			try {
				desktop.browse(new URI(url));
			} catch (IOException | URISyntaxException e) {
				throw new RuntimeException("Failed to open browser", e);
			}
		} else {
			Runtime runtime = Runtime.getRuntime();

			try {
				runtime.exec("xdg-open " + url);
			} catch (IOException e) {
				throw new RuntimeException("Failed to open browser", e);
			}
		}
	}

	// /////////////////////////////////////////////////////////////////////
	// Misc
	// /////////////////////////////////////////////////////////////////////

	// // TODO rework...
	// public static String getFileExtension(String filename) {
	// if (filename == null) {
	// return null;
	// }
	// int extensionPos = filename.lastIndexOf('.');
	//
	// int lastUnixPos = filename.lastIndexOf('/');
	// int lastWindowsPos = filename.lastIndexOf('\\');
	// int lastSeparator = Math.max(lastUnixPos, lastWindowsPos);
	//
	// int index = lastSeparator > extensionPos ? -1 : extensionPos;
	//
	// if (index == -1) {
	// return "";
	// } else {
	// return filename.substring(index + 1);
	// }
	// }

	public static String getCwd() {
		return getCwdFile().getAbsolutePath();
	}

	public static File getCwdFile() {
		return new File("").getAbsoluteFile();
	}

	public static boolean changeDirectory(String directoryName) {
		return changeDirectory(new File(directoryName));
	}

	public static boolean changeDirectory(File directory) {
		directory = directory.getAbsoluteFile(); // Needs to be Absolute File

		if (directory.exists() == false) {
			directory.mkdirs();
		}

		if (directory.exists() == false) {
			return false;
		}

		try {
			System.setProperty("user.dir", directory.getAbsolutePath());

			return true;
		} catch (Exception e) {
			System.err.println("Change directory failed: " + e.getMessage());

			return false;
		}
	}

	public static String parentheses(String value) {
		return "(" + value + ")";
	}

	public static String quoted(String value) {
		return "\"" + value + "\"";
	}

	public static List<String> quoted(String[] values) {
		return quoted(toList(values));
	}

	public static List<String> quoted(List<String> values) {
		return values.stream().map(new Function<String, String>() {
			@Override
			public String apply(String string) {
				return quoted(string);
			}
		}).collect(Collectors.toList());
	}

	public static Set<String> quoted(Set<String> values) {
		return values.stream().map(new Function<String, String>() {
			@Override
			public String apply(String string) {
				return quoted(string);
			}
		}).collect(Collectors.toSet());
	}

	public static Set<String> quoted(Collection<String> values) {
		return values.stream().map(new Function<String, String>() {
			@Override
			public String apply(String string) {
				return quoted(string);
			}
		}).collect(Collectors.toSet());
	}

	public static String singleQuoted(String value) {
		return "'" + value + "'";
	}

	public static boolean isEmpty(String value) {
		return !isNonEmpty(value);
	}

	public static boolean isNonEmptyFile(File file) {
		return isNonEmpty(readFileToString(file));
	}

	public static boolean isNonEmpty(String value) {
		return ((value != null) && (value.trim().isEmpty() == false));
	}

	public static boolean isNonEmpty(String... values) {
		boolean result = true;

		for (String string : values) {
			result = result && isNonEmpty(string);
		}

		return result;
	}

	public static Object getPrivateField(Object object, String fieldName) {
		try {
			Field channelsField = object.getClass().getDeclaredField("channels");
			channelsField.setAccessible(true);

			return channelsField.get(object);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException("Failed to get access to field, " + quoted(fieldName), e);
		} catch (SecurityException e) {
			throw new RuntimeException("Failed to get access to field, " + quoted(fieldName), e);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException("Failed to get access to field, " + quoted(fieldName), e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Failed to get access to field, " + quoted(fieldName), e);
		}
	}

	// /////////////////////////////////////////////////////////////////////
	// Read Resource Files
	// /////////////////////////////////////////////////////////////////////

	public static String readResourceFileToString(Class<?> aClass, String resourceName) {
		final Properties properties = new Properties();

		try (final InputStream stream = aClass.getResourceAsStream(resourceName)) {
			String resourceAsString = IOUtils.toString(stream);

			return resourceAsString;
		} catch (IOException e) {
			e.printStackTrace();

			throw new RuntimeException("Failed to read resource, " + quoted(resourceName) + ", to String", e);
		}
	}

	// /////////////////////////////////////////////////////////////////////
	// Read and Write Files
	// /////////////////////////////////////////////////////////////////////

	public static File writeLinesToFile(String fileName, List<String> linesList) {
		File file = new File(fileName);

		writeLinesToFile(file, linesList);

		return file;
	}

	public static void writeLinesToFile(File file, List<String> linesList) {
		String linesString = String.join("\n", linesList);

		writeFile(file, linesString);
	}

	public static void writeFile(String fileName, CharSequence data) {
		File file = new File(fileName);

		writeFile(file, data);
	}

	public static void writeFile(File file, CharSequence data) {

		if (file.exists() == true) {
			if (file.canWrite() == false) {
				throw new RuntimeException("writeFile(): Do not have permissions to write to existing file, \""
						+ file.getAbsolutePath() + "\"");
			}
		}

		try {
			FileUtils.write(file.getAbsoluteFile(), data);
		} catch (IOException e) {
			throw new RuntimeException("Could not write file, \"" + file.getAbsolutePath() + "\"", e);
		}
	}

	public static byte[] readFileToBytes(String fileName) {
		File file = new File(fileName);

		return readFileToBytes(file);
	}

	public static byte[] readFileToBytes(File file) {

		commonFileReadAssertions(file);

		try {
			return FileUtils.readFileToByteArray(file.getAbsoluteFile());
		} catch (IOException e) {
			throw new RuntimeException("readFileToBytes(): Could not read file, \"" + file.getAbsolutePath() + "\"", e);
		}
	}

	public static String readFileToString(String fileName) {
		File file = new File(fileName);

		return readFileToString(file);
	}

	private static void commonFileReadAssertions(File file) {
		if (file.getAbsoluteFile().exists() == false) {
			throw new RuntimeException("readFileToString(): File does not exist, \"" + file.getAbsolutePath() + "\"");
		}

		if (file.getAbsoluteFile().canRead() == false) {
			throw new RuntimeException(
					"readFileToString(): Do not have permissions to read file, \"" + file.getAbsolutePath() + "\"");
		}
	}

	public static String readFileToString(File file) {

		commonFileReadAssertions(file);

		try {
			return FileUtils.readFileToString(file.getAbsoluteFile());
		} catch (IOException e) {
			throw new RuntimeException("readFileToString(): Could not read file, \"" + file.getAbsolutePath() + "\"",
					e);
		}
	}

	public static String readFilesToString(List<File> files) {
		return readFilesToString("", files);
	}

	public static String readFilesToString(File... files) {
		return readFilesToString("", files);
	}

	public static String readFilesToString(String separator, File... files) {
		return readFilesToString(separator, toList(files));
	}

	public static String readFilesToString(String separator, List<File> files) {
		List<String> fileContentsList = new ArrayList<String>();

		for (File file : files) {
			fileContentsList.add(readFileToString(file));
		}

		return String.join(separator, fileContentsList);
	}

	public static List<String> readFileToLines(String fileName) {
		File file = new File(fileName);

		return readFileToLines(file);
	}

	public static List<String> readFileToLines(File file) {

		if (file.exists() == false) {
			throw new RuntimeException("readFileToLines(): File does not exist, \"" + file.getAbsolutePath() + "\"");
		}

		if (file.canRead() == false) {
			throw new RuntimeException(
					"readFileToLines(): Do not have permissions to read file, \"" + file.getAbsolutePath() + "\"");
		}

		try {
			return FileUtils.readLines(file.getAbsoluteFile());
		} catch (IOException e) {
			throw new RuntimeException("readFileToLines(): Could not read file, \"" + file.getAbsolutePath() + "\"", e);
		}
	}

	// /////////////////////////////////////////////////////////////////////
	// Conversions
	// /////////////////////////////////////////////////////////////////////

	@SuppressWarnings("unchecked")
	public static <T> T[] toArray(Class<T> aClass, List<T> aList) {
		T[] resultArray = (T[]) Array.newInstance(aClass, aList.size());

		for (int index = 0; index < resultArray.length; index++) {
			resultArray[index] = aList.get(index);
		}

		return resultArray;
	}

	public static <T> T[] toArray(Set<T> aSet) {
		if (aSet.size() == 0) {
			throw new RuntimeException("Set was empty - unsupported");
		}

		return toArray(aSet.stream().collect(Collectors.toList()));
	}

	@SuppressWarnings("unchecked")
	public static <T> T[] toArray(List<T> aList) {
		if (aList.size() == 0) {
			throw new RuntimeException("List was empty - unsupported");
		}

		return toArray((Class<T>) aList.get(0).getClass(), aList);
	}

	public static int[] toIntArray(List<Integer> aList) {
		int[] result = new int[aList.size()];

		for (int index = 0; index < result.length; index++) {
			result[index] = aList.get(index);
		}

		return result;
	}

	public static long[] toLongArray(List<Long> aList) {
		long[] result = new long[aList.size()];

		for (int index = 0; index < result.length; index++) {
			result[index] = aList.get(index);
		}

		return result;
	}

	public static double[] toDoubleArray(List<Double> aList) {
		double[] result = new double[aList.size()];

		for (int index = 0; index < result.length; index++) {
			result[index] = aList.get(index);
		}

		return result;
	}

	public static boolean[] toBooleanArray(List<Boolean> aList) {
		boolean[] result = new boolean[aList.size()];

		for (int index = 0; index < result.length; index++) {
			result[index] = aList.get(index);
		}

		return result;
	}

	public static List<String> toStringList(Set<Object> set) {
		List<String> resultList = new ArrayList<String>();

		for (Object object : set) {
			resultList.add(object.toString());
		}

		return resultList;
	}

	public static List<Integer> toIntegerList(int[] values) {
		List<Integer> resultList = new ArrayList<Integer>();

		for (int index = 0; index < values.length; index++) {
			resultList.add(values[index]);
		}

		return resultList;
	}

	public static Integer[] toIntegerArray(int[] values) {
		Integer[] result = new Integer[values.length];

		for (int index = 0; index < values.length; index++) {
			result[index] = values[index];
		}

		return result;
	}

	public static List<Double> toDoubleList(double[] values) {
		List<Double> resultList = new ArrayList<Double>();

		for (int index = 0; index < values.length; index++) {
			resultList.add(values[index]);
		}

		return resultList;
	}

	public static Double[] toDoubleArray(double[] values) {
		Double[] result = new Double[values.length];

		for (int index = 0; index < values.length; index++) {
			result[index] = values[index];
		}

		return result;
	}

	public static List<Long> toLongList(long[] values) {
		List<Long> resultList = new ArrayList<Long>();

		for (int index = 0; index < values.length; index++) {
			resultList.add(values[index]);
		}

		return resultList;
	}

	public static Long[] toLongArray(long[] values) {
		Long[] result = new Long[values.length];

		for (int index = 0; index < values.length; index++) {
			result[index] = values[index];
		}

		return result;
	}

	public static List<Boolean> toBooleanList(boolean[] values) {
		List<Boolean> resultList = new ArrayList<Boolean>();

		for (int index = 0; index < values.length; index++) {
			resultList.add(values[index]);
		}

		return resultList;
	}

	public static Boolean[] toBooleanArray(boolean[] values) {
		Boolean[] result = new Boolean[values.length];

		for (int index = 0; index < values.length; index++) {
			result[index] = values[index];
		}

		return result;
	}

	public static List<String> toStringList(Object... objects) {
		List<String> resultList = new ArrayList<String>();

		for (Object object : objects) {
			resultList.add(object.toString());
		}

		return resultList;
	}

	public static <T> List<T> toList(Set<T> set) {
		return new ArrayList<T>(set);
	}

	public static <T> List<T> toList(T... objects) {
		return new ArrayList<T>(Arrays.asList(objects));
	}

	public static <T> Set<T> toSet(T... objects) {
		return new HashSet<T>(Arrays.asList(objects));
	}

	// /////////////////////////////////////////////////////////////////////
	// Build Version and Build Date
	// /////////////////////////////////////////////////////////////////////

	public static String getVersionString(Class<?> aClass) {
		final Properties properties = new Properties();

		try (final InputStream stream = aClass.getResourceAsStream("buildInfo.properties")) {
			properties.load(stream);

			return properties.getProperty("version");
		} catch (IOException | NullPointerException e) {
			System.err.println("buildInfo.properties not found in resources path: " + aClass.getPackage().getName());
		}

		return "Unknown";
	}

	public static String getBuildDateString(Class<?> aClass) {
		final Properties properties = new Properties();

		try (final InputStream stream = aClass.getResourceAsStream("buildInfo.properties")) {
			properties.load(stream);

			String dateTimeString = properties.getProperty("buildDate");

			try {
				long dateTime = Long.parseLong(dateTimeString);

				return new SimpleDateFormat("MMM/d/yyyy h:mm a").format(new Date(dateTime));
			} catch (NumberFormatException e) {
				return dateTimeString;
			}
		} catch (IOException | NullPointerException e) {
			System.err.println("buildInfo.properties not found in resources path: " + aClass.getPackage().getName());
		}

		return "Unknown Time";
	}

	public static String getDateTimeString(long miniSecondsSinceUnixStart, int dateFormat, Locale aLocale) {
		try {
			DateFormat formatter //
					= DateFormat.getDateTimeInstance(dateFormat, dateFormat, aLocale);

			return formatter.format(new Date(miniSecondsSinceUnixStart));
		} catch (NumberFormatException e) {
			return String.valueOf(miniSecondsSinceUnixStart + "miliseconds");
		}
	}

	//

	public static final String EMPTY = "";

	public static <T> String join(T[] array, final String separator, String finalSeparator) {
		if (array.length == 0) {
			return "";
		}

		return join(toList(array), separator, finalSeparator);
	}

	public static String join(final Iterable<?> iterable, final String separator, String finalSeparator) {
		if (iterable == null) {
			return null;
		}
		return join(iterable.iterator(), separator, finalSeparator);
	}

	/**
	 * <p>
	 * Joins the elements of the provided {@code Iterable} into a single String
	 * containing the provided elements.
	 * </p>
	 *
	 * <p>
	 * No delimiter is added before or after the list. A {@code null} separator is
	 * the same as an empty String ("").
	 * </p>
	 *
	 * <p>
	 * See the examples here: {@link #join(Object[],String)}.
	 * </p>
	 *
	 * @param iterable  the {@code Iterable} providing the values to join together,
	 *                  may be null
	 * @param separator the separator character to use, null treated as ""
	 * @return the joined String, {@code null} if null iterator input
	 * @since 2.3
	 */
	public static String join(final Iterable<?> iterable, final String separator) {
		if (iterable == null) {
			return null;
		}
		return join(iterable.iterator(), separator, null);
	}

	/**
	 * <p>
	 * Joins the elements of the provided {@code Iterator} into a single String
	 * containing the provided elements.
	 * </p>
	 *
	 * <p>
	 * No delimiter is added before or after the list. A {@code null} separator is
	 * the same as an empty String ("").
	 * </p>
	 *
	 * <p>
	 * See the examples here: {@link #join(Object[],String)}.
	 * </p>
	 *
	 * @param iterator  the {@code Iterator} of values to join together, may be null
	 * @param separator the separator character to use, null treated as ""
	 * @return the joined String, {@code null} if null iterator input
	 */
	public static String join(final Iterator<?> iterator, final String separator, String finalSeparator) {

		// handle null, zero and one elements before building a buffer
		if (iterator == null) {
			return null;
		}
		if (!iterator.hasNext()) {
			return EMPTY;
		}
		final Object first = iterator.next();
		if (!iterator.hasNext()) {
			@SuppressWarnings("deprecation")
			// ObjectUtils.toString(Object) has been deprecated in 3.2
			final String result = toString(first);
			return result;
		}

		// two or more elements
		final StringBuilder buf = new StringBuilder(256); // Java default is 16,
															// probably too
															// small
		if (first != null) {
			buf.append(first);
		}

		if (finalSeparator == null) {
			while (iterator.hasNext()) {
				if (separator != null) {
					buf.append(separator);
				}
				final Object obj = iterator.next();
				if (obj != null) {
					buf.append(obj);
				}
			}
		} else {
			while (iterator.hasNext()) {
				final Object obj = iterator.next();

				if (iterator.hasNext()) {
					if (separator != null) {
						buf.append(separator);
					}
				} else {
					buf.append(finalSeparator);
				}

				if (obj != null) {
					buf.append(obj);
				}
			}
		}

		return buf.toString();
	}

	public static String toString(Throwable aThrowable) {
		Writer result = new StringWriter();

		PrintWriter printWriter = new PrintWriter(result);

		aThrowable.printStackTrace(printWriter);

		return result.toString();
	}

	private static String colorize(String color, String text) {
		return "<span style='color: " + color + ";'>" + text + "</span>";
	}

	private static String formatFirstStackTraceLine(String line) {
		int colonIndex = line.indexOf(":");

		if (colonIndex < 0) {
			return colorize("blue", line);
		} else {
			return colorize("blue", line.substring(0, colonIndex)) + colorize("red", line.substring(colonIndex));
		}
	}

	private static String formatNotFirstStackTraceLine(String line) {
		if (line.startsWith("Caused by:") == true) {
			return colorize("red", "Caused by:") + formatFirstStackTraceLine(line.substring(line.indexOf(":") + 1));
		} else if (line.trim().equals("nested exception is:") == true) {
			return colorize("red", line);
		} else if (line.contains("(") == true) {
			int openParamIndex = line.indexOf("(");

			return colorize("red", line.substring(0, openParamIndex + 1))
					+ colorize("blue", line.substring(openParamIndex + 1, line.length() - 1))
					+ colorize("red", line.substring(line.length() - 1));
		} else {
			return colorize("red", line);
		}
	}

	public static String toHtmlString(Throwable aThrowable) {
		Writer result = new StringWriter();

		PrintWriter printWriter = new PrintWriter(result);

		aThrowable.printStackTrace(printWriter);

		String stackTrace = result.toString();

		List<String> lines = toList(getLines(stackTrace));

		String finalStackTrace = formatFirstStackTraceLine(lines.get(0)) + "<br/>\n";

		lines.remove(0);

		for (String line : lines) {
			finalStackTrace += formatNotFirstStackTraceLine(line) + "<br/>\n";
		}

		return finalStackTrace;
	}

	public static String toString(final Object obj) {
		return obj == null ? "" : obj.toString();
	}

	//

	private static final int TEMP_DIR_ATTEMPTS = 10000;

	public static File createTempDirectory() {
		File baseDir = new File(System.getProperty("java.io.tmpdir"));
		String baseName = System.currentTimeMillis() + "-";

		for (int counter = 0; counter < TEMP_DIR_ATTEMPTS; counter++) {
			File tempDir = new File(baseDir, baseName + counter);
			if (tempDir.mkdir()) {
				return tempDir;
			}
		}

		throw new IllegalStateException("Failed to create directory within " + TEMP_DIR_ATTEMPTS + " attempts (tried "
				+ baseName + "0 to " + baseName + (TEMP_DIR_ATTEMPTS - 1) + ')');
	}

	// //////////////////////////////////////////////////////////////////////////
	// File/Directory Checks
	// //////////////////////////////////////////////////////////////////////////

	public static File checkFileWritable(File file) {
		if (file == null) {
			throw new NullPointerException();
		}

		if (file.getAbsoluteFile().canWrite() == false) {
			if (file.getAbsoluteFile().isDirectory()) {
				throw new RuntimeException("Can write to directory: " + quoted(file.getAbsolutePath()));
			} else {
				throw new RuntimeException("Can write to file: " + quoted(file.getAbsolutePath()));
			}
		}

		return file;
	}

	public static File checkFileReadable(File file) {
		if (file == null) {
			throw new NullPointerException();
		}

		if (file.getAbsoluteFile().canRead() == false) {
			if (file.getAbsoluteFile().isDirectory()) {
				throw new RuntimeException("Can read a directory: " + quoted(file.getAbsolutePath()));
			} else {
				throw new RuntimeException("Can read file: " + quoted(file.getAbsolutePath()));
			}
		}

		return file;
	}

	public static List<File> checkFilesExist(List<File> files) {
		for (File file : files) {
			checkFileWritable(file);
		}

		return files;
	}

	public static File[] checkFilesExist(File... files) {
		for (File file : files) {
			checkFileWritable(file);
		}

		return files;
	}

	public static File checkFileDoesNotExist(File file) {
		if (file == null) {
			throw new NullPointerException();
		}

		if (file.getAbsoluteFile().exists() == true) {
			throw new RuntimeException("File DOES exist: " + quoted(file.getAbsolutePath()));
		}

		return file;
	}

	public static File checkFileExists(File file) {
		if (file == null) {
			throw new NullPointerException();
		}

		if (file.getAbsoluteFile().exists() == false) {
			throw new RuntimeException("File does not exist: " + quoted(file.getAbsolutePath()));
		}

		if (file.getAbsoluteFile().isFile() == false) {
			throw new RuntimeException("File is not normal file: " + quoted(file.getAbsolutePath()));
		}

		return file;
	}

	public static File checkDirectoryExists(File directory) {
		if (directory == null) {
			throw new NullPointerException();
		}

		if (directory.getAbsoluteFile().exists() == false) {
			throw new RuntimeException("Directory does not exist: " + quoted(directory.getAbsolutePath()));
		}

		if (directory.getAbsoluteFile().isDirectory() == false) {
			throw new RuntimeException("File is not directory: " + quoted(directory.getAbsolutePath()));
		}

		return directory;
	}

	public static List<File> checkDirectoriesExist(List<File> directories) {
		for (File file : directories) {
			checkDirectoryExists(file);
		}

		return directories;
	}

	public static File[] checkDirectoriesExist(File... directories) {
		for (File file : directories) {
			checkDirectoryExists(file);
		}

		return directories;
	}

	public static List<String> checkDirectoriesAsStringsExist(List<String> directories) {
		for (String file : directories) {
			checkDirectoryExists(new File(file));
		}

		return directories;
	}

	public static String[] checkDirectoriesExist(String... directories) {
		for (String file : directories) {
			checkDirectoryExists(new File(file));
		}

		return directories;
	}

	public static void deleteDirectory(String directoryFilePath) {
		deleteDirectory(directoryFilePath);
	}

	public static void deleteDirectory(File directoryFile) {
		try {
			FileUtils.deleteDirectory(directoryFile.getAbsoluteFile());
		} catch (IOException e) {
			throw new RuntimeException("Failed to delete directory, " + quoted(directoryFile.getAbsolutePath()), e);
		}
	}

	//

	private static final Pattern newlinePattern = Pattern.compile("\\r?\\n");

	public static boolean containsFullLine(String string) {

		Matcher matcher = newlinePattern.matcher(string);

		return matcher.find();
	}

	public static String removeEmptyLines(String string) {
		String[] lines = getLines(string);

		List<String> nonEmptyLines = new ArrayList<String>();

		for (String line : lines) {
			if (line.trim().equals("") == false) {
				nonEmptyLines.add(line);
			}
		}

		return String.join("\n", nonEmptyLines);
	}

	public static String[] toLines(String string) {
		return getLines(string);
	}

	public static String[] getLines(String string) {
		return string.split("\\r?\\n", -1);
	}

	public static String[] splitParts(String string, String separator) {
		return string.split(separator, -1);
	}

	public static String capitalize(String string) {
		return StringUtils.capitalize(string);
	}

	public static String getDisplayStringSplitingByCamelCase(String string) {
		return String.join(" ", StringUtils.splitByCharacterTypeCamelCase(string));
	}

	//

	public static String localDevelopmentEnvironmentIndicatorFile = "c:/work/localDevelopmentEnvironmentIndicatorFile";

	private static Boolean isLocalDev;

	public static boolean isLocalDevelopmentEnvironment() {
		if (isLocalDev == null) {
			isLocalDev = new File(localDevelopmentEnvironmentIndicatorFile).exists();
		}

		return isLocalDev;
	}

	public static String getPrettyDateString() {
		return getPrettyDateString(new Date());
	}

	public static String getPrettyDateString(Date date) {
		return new SimpleDateFormat("MMM/dd/yyy HH:mm:ss").format(date);
	}

	public static String fileTimeStampString() {
		return new DateTime().toString("yyyy.MM.dd-HH~mm~ssa-SSS");
	}

	public static String getDateString() {
		return new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss-SSS").format(new Date());
	}

	public static String getFileDateSuffix() {
		return new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss-SSS").format(new Date());
	}

	private static SimpleDateFormat parsableSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss-SSS");

	public static String getParsableDateString(Date date) {
		return parsableSimpleDateFormat.format(date);
	}

	public static Date parsableDateString(String dateString) {
		try {
			return parsableSimpleDateFormat.parse(dateString);
		} catch (ParseException e) {
			throw new RuntimeException("Failed to ParsableDateString, " + quoted(dateString), e);
		}
	}

	// For Maps
	public static List<String> getSortedKeys(Map<String, ?> map) {
		List<String> keys = new ArrayList<String>(map.keySet());

		Collections.sort(keys);

		return keys;
	}

	public static <T> List<T> toReverse(List<T> aList) {
		List<T> resultList = new ArrayList<T>(aList);

		Collections.reverse(resultList);

		return resultList;
	}

	public static byte[] getUtfBytes(String string) {
		return string.getBytes(StandardCharsets.UTF_8);
	}

	// ////////////////////////////////////////////////////////////////////////////
	// TypeSafe Config support
	// ////////////////////////////////////////////////////////////////////////////

	public static Map<String, String> readConfigMap(Config config, String configPath) {
		Map<String, String> resultMap = new HashMap<String, String>();

		List<? extends ConfigObject> mapEntries = config.getObjectList("remoteCommandsDeployer.scriptsMap");

		for (ConfigObject configObject : mapEntries) {
			Set<Entry<String, ConfigValue>> entrySet = configObject.entrySet();

			if (entrySet.size() != 1) {
				throw new RuntimeException("Invalid map in configuration: remoteCommandsDeployer.scriptsMap");
			}

			Entry<String, ConfigValue> entry = entrySet.iterator().next();
			String key = entry.getKey();
			String value = entry.getValue().render();

			resultMap.put(key, value);
		}

		return resultMap;
	}

	public static String getKey(String property) {
		return property.split("=", 2)[0];
	}

	public static String getValue(String property) {
		return property.split("=", 2)[1];
	}

	public static Map<String, Object> propertiesToParameters(List<String> properties) {
		Map<String, Object> resultMap = properties.stream().collect(Collectors.toMap(p -> getKey(p), p -> getValue(p)));

		return resultMap;
	}

	public static Map<String, String> propertiesToPasswordParameters(List<String> properties) {
		Map<String, String> resultMap = properties.stream().collect(Collectors.toMap(p -> getKey(p), p -> getValue(p)));

		return resultMap;
	}

	public static Map<String, Object> configToKeyObjectMap(Config config) {
		Map<String, Object> resultMap = config.entrySet().stream()
				.collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue().unwrapped()));

		return resultMap;
	}

	public static Map<String, String> configToKeyStringMap(Config config) {
		Map<String, String> resultMap = config.entrySet().stream()
				.collect(Collectors.toMap(p -> p.getKey(), p -> (String) p.getValue().unwrapped()));

		return resultMap;
	}

	// //////////////////////////////

	public static void shutdownExecutor(ExecutorService executorService, int secondsToWaitBeforeForcedShutdown,
			java.util.logging.Logger logger) {
		try {
			logger.info("shutdownExecutor(): attempting to shutdown executor gracefully");

			executorService.shutdown();

			executorService.awaitTermination(secondsToWaitBeforeForcedShutdown, TimeUnit.SECONDS);

		} catch (InterruptedException e) {
			logger.severe("shutdownExecutor() was not able to gracefully shutdown within "
					+ secondsToWaitBeforeForcedShutdown + " seconds");

		} finally {
			if (!executorService.isTerminated()) {
				logger.severe("shutdownExecutor(): will now cancel all non-finished tasks");
			}

			executorService.shutdownNow();

			logger.info("shutdownExecutor(): shutdown finished");
		}
	}

	public static void sleep(long miliseconds) {
		try {
			Thread.sleep(miliseconds);
		} catch (InterruptedException e) {
			throw new RuntimeException("Sleeping failed", e);
		}
	}

	public static String removeOuterSingleQuotes(String value) {
		if ((value.startsWith("'") == true) && (value.endsWith("'") == true)) {
			return value.substring(1, value.length() - 1);
		} else {
			return value;
		}
	}

	// /

	public static <T> void copyRange(List<T> sourceList, T[] targetArray, int startIndex, int length) {
		for (int index = startIndex; index < startIndex + length; index++) {
			targetArray[index - startIndex] = sourceList.get(index);
		}
	}

	public static double nanoToMilliseconds(long nanoseconds) {
		return nanoseconds / 1000000.0;
	}

	public static double nanoToSeconds(long nanoseconds) {
		return nanoseconds / 1000000000.0;
	}

	public static void runInThread(Runnable runnable) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					runnable.run();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	public static void runNoFail(Runnable runnable) {
		try {
			runnable.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String cleanFilePath(String filePath) {
		try {
			return new File(filePath).getCanonicalFile().getAbsoluteFile().getAbsolutePath();
		} catch (IOException e) {
			throw new RuntimeException("Failed to get clean file path", e);
		}
	}

	public static String getHostNameFromIp(String ipAddress) {
		try {
			InetAddress inetAddress = InetAddress.getByName(ipAddress);

			return inetAddress.getCanonicalHostName();
		} catch (UnknownHostException e) {
			e.printStackTrace();

			throw new RuntimeException("Could not determine IP reverse lookup", e);
		}
	}

	public static String getHostNameFromIp(String ipAddress, String fileNameOfCache) {
		if (isNonEmpty(fileNameOfCache) == true) {
			String hostName = getHostNameFromIpUsingCache(ipAddress, fileNameOfCache);

			if (isNonEmpty(hostName) == true) {
				return hostName;
			}
		}

		String hostName = getHostNameFromIp(ipAddress);

		if (isNonEmpty(fileNameOfCache) == true) {
			putHostNameAndIpIntoCache(ipAddress, hostName, fileNameOfCache);
		}

		return hostName;
	}

	// /////////////////////////////////////////////////////////////////////////////////////////////////
	// Host Name To Ip Address Caching
	// /////////////////////////////////////////////////////////////////////////////////////////////////

	private static String getHostNameFromIpUsingCache(String ipAddress, String fileNameOfCache) {
		ipAddress = trimNlower(ipAddress);

		return getPropertiesCache(fileNameOfCache).getProperty(ipAddress, null);
	}

	private static Map<String, Properties> fileNameToPropertiesMap = new HashMap<String, Properties>();

	private static String trimNlower(String string) {
		return string.trim().toLowerCase();
	}

	private static Properties getPropertiesCache(String fileNameOfCache) {
		try {
			fileNameOfCache = cleanFilePath(fileNameOfCache);

			if (fileNameToPropertiesMap.containsKey(fileNameOfCache) == false) {
				File file = new File(fileNameOfCache);

				Properties properties = new Properties();

				if (file.exists() == true) {
					try (FileInputStream fileinputStream = new FileInputStream(file);) {

						properties.load(fileinputStream);
					}
				} else {
					file.getParentFile().mkdirs();

					try (FileOutputStream fileOutputStream = new FileOutputStream(file);) {

						properties.store(fileOutputStream, "Initial Save");
					}
				}

				fileNameToPropertiesMap.put(fileNameOfCache, properties);
			}

			return fileNameToPropertiesMap.get(fileNameOfCache);
		} catch (IOException e) {
			throw new RuntimeException("Failed to get Properties Cache", e);
		}
	}

	private static void savePropertiesCacheToDisk(String fileNameOfCache) {
		try {
			File file = new File(fileNameOfCache);
			try (FileOutputStream fileOutputStream = new FileOutputStream(file);) {

				getPropertiesCache(fileNameOfCache).save(fileOutputStream, "Updating cache");
			}
		} catch (IOException e) {
			throw new RuntimeException("Failed to save Properties Cache", e);
		}
	}

	private static Object syncObject = new Object();

	private static void putHostNameAndIpIntoCache(String ipAddress, String hostName, String fileNameOfCache) {
		ipAddress = trimNlower(ipAddress);
		hostName = trimNlower(hostName);

		synchronized (syncObject) {
			getPropertiesCache(fileNameOfCache).put(ipAddress, hostName);

			savePropertiesCacheToDisk(fileNameOfCache);
		}
	}

	// /////////////////////////////////////////////////////////////////////////////////////////////////
	// End of Host Name To Ip Address Caching
	// /////////////////////////////////////////////////////////////////////////////////////////////////

	public static boolean isHostNameNotIpAddress(String hostName) {
		if ((hostName.contains(":") == true) && (hostName.contains(".") == false)) {
			return true; // IPv6
		} else if (Pattern.matches("^.[0-9]{1,3}/..[0-9]{1,3}/..[0-9]{1,3}/..[0-9]{1,3}", hostName) == true) {
			return true; // IPv4
		} else if (hostName.contains(".") == true) {
			return true; // FQDN host name
		} else {
			return true; // short name
		}
	}

	public static boolean hasValidDnsEntry(String hostName) {
		try {
			InetAddress inetAddress = InetAddress.getByName(hostName);

			if (isNonEmpty(inetAddress.getHostAddress()) == true) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}

	// public static void addConfigWatcher(String filePath, String path,
	// ConfigChangeListener configChangeListener) {
	// CarefulConfigFileWatcher configFileWatcher = new
	// CarefulConfigFileWatcher(filePath);
	//
	// configFileWatcher.addWatch(path, configChangeListener);
	//
	// configFileWatcher.start();
	// }

	private static Map<String, Map> searchCacheMap = new HashMap<String, Map>();

	// TODO
	// public static <T,S> T findWithCache(String searchObject,
	// List<T> listToSearch,
	// BiPredicate<S,T> comparison,
	// String cacheName) {
	// Function<String,String> keyGenerator = (String name) -> {
	// return name;
	// };
	//
	// return findWithCache(searchObject,
	// listToSearch,
	// comparison,
	// keyGenerator,
	// cacheName);
	// }

	public static <T, S, K> T findWithCache(S searchObject, List<T> listToSearch, BiPredicate<S, T> comparison,
			Function<S, K> keyGenerator, String cacheName) {
		if (searchCacheMap.containsKey(cacheName) == false) {
			searchCacheMap.put(cacheName, new HashMap<String, T>());
		}

		if (searchCacheMap.get(cacheName).containsKey(keyGenerator.apply(searchObject)) == true) {
			return ((Map<K, T>) searchCacheMap.get(cacheName)).get(keyGenerator.apply(searchObject));
		}

		for (T currentObject : listToSearch) {
			if (comparison.test(searchObject, currentObject) == true) {
				((Map<K, T>) searchCacheMap.get(cacheName)).put(keyGenerator.apply(searchObject), currentObject);

				return currentObject;
			}
		}

		throw new RuntimeException(
				"Object with key, " + quoted(keyGenerator.apply(searchObject).toString()) + ", was not found.");
	}

	public static void shutdownRapUtils() {
		// if (_timedExecutor != null) {
		// _timedExecutor.shutdown();
		// }
	}

	// private static TimedExecutor _timedExecutor;
	//
	// private static TimedExecutor getTimedExecutor() {
	// if (_timedExecutor == null) {
	// _timedExecutor = new TimedExecutor();
	// }
	//
	// return _timedExecutor;
	// }

	// public static <T> TimedExecutorResult<T> executeWithTimeOut(long
	// timeLimitInMilliSeconds, Callable<T> callable) {
	// return getTimedExecutor().execute(timeLimitInMilliSeconds, callable);
	// }

	private static Map<String, SimpleTimeLimiter> poolNameToSimpleTimeLimiterMap = new HashMap<String, SimpleTimeLimiter>();

	private static SimpleTimeLimiter getSimpleTimeLimiter(String threadPoolName) {
		if (poolNameToSimpleTimeLimiterMap.containsKey(threadPoolName) == false) {
			poolNameToSimpleTimeLimiterMap.put(threadPoolName,
					new SimpleTimeLimiter(Executors.newCachedThreadPool(getNamedThreadFactory(threadPoolName))));
		}

		SimpleTimeLimiter simpleTimeLimiter = poolNameToSimpleTimeLimiterMap.get(threadPoolName);

		return checkNotNullParameter(simpleTimeLimiter,
				"Return value: poolNameToSimpleTimeLimiterMap.get(threadPoolName)");
	}

	public static <T> T executeWithExceptions(String threadPoolName, long timeLimitInMilliSeconds, Callable<T> callable)
			throws Exception {
		boolean amInterruptible = true;

		return getSimpleTimeLimiter(threadPoolName).callWithTimeout(callable, timeLimitInMilliSeconds,
				TimeUnit.MILLISECONDS, amInterruptible);
	}

	// public static <T> T executeWithExceptions(long timeLimitInMilliSeconds,
	// Callable<T> callable) throws TimeoutException, InterruptedException,
	// ExecutionException {
	// return getTimedExecutor().executeWithExceptions(timeLimitInMilliSeconds,
	// callable).resultObject;
	// }

	// //////////////////

	public static ElapsedTime startElapsedTime(String startMessage) {
		ElapsedTime elapsedTime = new ElapsedTime();

		elapsedTime.printTimeSoFar(startMessage);

		return elapsedTime;
	}

	public static class ElapsedTime {
		public final long startTime;

		private int counter;

		public ElapsedTime() {
			super();

			this.startTime = System.currentTimeMillis();
		}

		public void printTimeSoFar(String message) {
			elapsedTimePrintln(startTime, String.valueOf(counter++) + ":" + message);
		}

		public void printTimeSoFarInFloat(String message) {
			elapsedTimePrintlnInFloat(startTime, String.valueOf(counter++) + ":" + message);
		}
	}

	public static void elapsedTimePrintln(long startTime, String message) {
		System.out.println(
				"Elapsed Time: " + ((System.currentTimeMillis() - startTime) / 1000) + " seconds - " + message);
	}

	public static void elapsedTimePrintlnInFloat(long startTime, String message) {
		System.out.println(
				"Elapsed Time: " + ((System.currentTimeMillis() - startTime) / 1000.0) + " seconds - " + message);
	}

	//
	/**
	 * Explicit argument indices may be used to re-order output. format("%4$2s %3$2s
	 * %2$2s %1$2s", "a", "b", "c", "d") returns " d c b a"
	 *
	 * @param templateString
	 * @param objects
	 * @return
	 */
	public static String format(String templateString, Object... objects) {
		StringBuilder sb = new StringBuilder();

		try (Formatter formatter = new Formatter(sb, Locale.US)) {
			return formatter.format(templateString, objects).toString();
		}
	}

	public static void updateServiceFileAttributes(File file) {
		if (System.getProperty("os.name").toLowerCase().contains("windows") == true) {
			System.out.println("updateServiceFileAttributes() does nothing on Windows");
			return;
		}

		try {
			Path directoryPath = file.getParentFile().toPath();

			Files.setPosixFilePermissions(file.toPath(),
					toSet(PosixFilePermission.GROUP_READ, PosixFilePermission.GROUP_WRITE,
							PosixFilePermission.GROUP_EXECUTE, PosixFilePermission.OWNER_READ,
							PosixFilePermission.OWNER_WRITE, PosixFilePermission.OWNER_EXECUTE));

			UserPrincipal directoryOwner = Files.getOwner(directoryPath);

			System.out.println("directoryOwner: " + directoryOwner.getName());

			GroupPrincipal directoryGroup = Files
					.readAttributes(directoryPath, PosixFileAttributes.class, LinkOption.NOFOLLOW_LINKS).group();

			Files.getFileAttributeView(file.toPath(), PosixFileAttributeView.class, LinkOption.NOFOLLOW_LINKS)
					.setGroup(directoryGroup);
		} catch (IOException e) {
			throw new RuntimeException("Failed to updateServiceFileAttributes", e);
		}
	}

	private static String generateCheckSum(File file, String digestName, int digestLength) {
		try (FileInputStream fis = new FileInputStream(file.getAbsoluteFile());) {
			MessageDigest messageDigest = MessageDigest.getInstance(digestName);

			byte[] dataBytes = new byte[1024];

			int nread = 0;
			while ((nread = fis.read(dataBytes)) != -1) {
				messageDigest.update(dataBytes, 0, nread);
			}
			;

			byte[] mdbytes = messageDigest.digest();

			String md5Hex = new BigInteger(1, mdbytes).toString(16);

			md5Hex = padLeft(md5Hex, digestLength, "0");

			return md5Hex;
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Failed to find algorithm for " + digestName + " Sum", e);

		} catch (FileNotFoundException e) {
			throw new RuntimeException("Failed to determine " + digestName + " Sum because file is missing: "
					+ quoted(file.getAbsolutePath()), e);

		} catch (IOException e) {
			throw new RuntimeException("Failed to determine " + digestName + " Sum", e);
		}
	}

	public static String getMd5Sum(File file) {
		return generateCheckSum(file, "MD5", 32);
	}

	public static String getSha1Sum(File file) {
		return generateCheckSum(file, "SHA1", 40);
	}

	//
	/**
	 * Pad a String to a minimum length specified by <tt>numberOfChars</tt>, adding
	 * the supplied padding String as many times as needed to the left.
	 *
	 * If the String is already the same size or bigger than the target
	 * <tt>numberOfChars</tt>, then the original String is returned. An example:
	 *
	 * <pre>
	 * println 'Numbers:'
	 * [1, 10, 100, 1000].each{ println it.toString().padLeft(5, '*') }
	 * [2, 20, 200, 2000].each{ println it.toString().padLeft(5, '*_') }
	 * </pre>
	 *
	 * will produce output like:
	 *
	 * <pre>
	 * Numbers:
	 * ****1
	 * ***10
	 * **100
	 * *1000
	 * *_*_2
	 * *_*20
	 * *_200
	 * *2000
	 * </pre>
	 *
	 * @param self          a String object
	 * @param numberOfChars the total minimum number of characters of the resulting
	 *                      string
	 * @param padding       the characters used for padding
	 * @return the String padded to the left
	 * @since 1.0
	 */
	public static String padLeft(String string, int numChars, String padding) {
		if (numChars <= string.length()) {
			return string;
		} else {
			return getPadding(padding, numChars - string.length()) + string;
		}
	}

	private static String getPadding(String padding, int length) {
		if (padding.length() < length) {
			return multiply(padding, length / padding.length() + 1).substring(0, length);
		} else {
			return padding.substring(0, length);
		}
	}

	/**
	 * Repeat a String a certain number of times.
	 *
	 * @param self   a String to be repeated
	 * @param factor the number of times the String should be repeated
	 * @return a String composed of a repetition
	 * @throws IllegalArgumentException if the number of repetitions is &lt; 0
	 * @since 1.0
	 */
	public static String multiply(String self, int size) {
		if (size == 0)
			return "";
		else if (size < 0) {
			throw new IllegalArgumentException(
					"multiply() should be called with a number of 0 or greater not: " + size);
		}
		StringBuilder answer = new StringBuilder(self);
		for (int i = 1; i < size; i++) {
			answer.append(self);
		}
		return answer.toString();
	}

	//

	public static String getFileExtension(File file) {
		return getFileExtension(file.getAbsolutePath());
	}

	public static String getFileExtension(String filePath) {
		return FilenameUtils.getExtension(filePath);
	}

	/**
	 * Same as String.join() in Java 8
	 *
	 * @param separator
	 * @param collection
	 * @return
	 */
	public static String join(String separator, Collection<?> collection) {
		return StringUtils.join(collection, separator);
	}

	public static String join(String separator, String... strings) {
		return StringUtils.join(Arrays.asList(strings), separator);
	}

	public static String join(String separator, Object[] objects) {
		return StringUtils.join(Arrays.asList(objects), separator);
	}

	public static String addTrailingSlash(String string) {
		if (string.endsWith("/") == true) {
			return string;
		} else {
			return string + "/";
		}
	}

	public static int compareStringsOrIntegers(String value1, String value2) {
		if ((isNumeric(value1) == true) && (isNumeric(value2) == true)) {
			return Integer.compare(Integer.parseInt(value1), Integer.parseInt(value2));
		} else {
			return value1.compareToIgnoreCase(value2);
		}
	}

	public static boolean isNumeric(String str) {
		if (isEmpty(str) == true) {
			return false;
		}

		for (char c : str.toCharArray()) {
			if (!Character.isDigit(c))
				return false;
		}

		return true;
	}

	public static byte[] readClassFileToBytes(Class aClass, String fileName) {
		try {
			InputStream fileInputStream = aClass.getResourceAsStream(fileName);

			if (fileInputStream == null) {
				throw new FileNotFoundException("class://" + fileName + " relative to " + aClass.getName());
			}

			return IOUtils.toByteArray(fileInputStream);
		} catch (IOException e) {
			throw new RuntimeException("Failed to read file " + quoted(fileName)
					+ " from class path starting at class, " + quoted(aClass.getName()), e);
		}
	}

	public static String readClassFileToString(Class aClass, String fileName) {
		try {
			InputStream fileInputStream = aClass.getResourceAsStream(fileName);

			if (fileInputStream == null) {
				throw new FileNotFoundException("class://" + fileName + " relative to " + aClass.getName());
			}

			return IOUtils.toString(fileInputStream);
		} catch (IOException e) {
			throw new RuntimeException("Failed to read file " + quoted(fileName)
					+ " from class path starting at class, " + quoted(aClass.getName()), e);
		}
	}

	public static void writeFile(File file, byte[] data) {

		if (file.exists() == true) {
			if (file.canWrite() == false) {
				throw new RuntimeException("writeFile(): Do not have permissions to write to existing file, \""
						+ file.getAbsolutePath() + "\"");
			}
		}

		try {
			FileUtils.writeByteArrayToFile(file.getAbsoluteFile(), data);
		} catch (IOException e) {
			throw new RuntimeException("Could not write file, \"" + file.getAbsolutePath() + "\"", e);
		}
	}

	public static <T> T defaultOnNull(T reference, T defaultValue) {
		if (reference == null) {
			return defaultValue;
		} else {
			return reference;
		}
	}

	public static int defaultOnZero(int value, int defaultValue) {
		if (value != 0) {
			return value;
		} else {
			return defaultValue;
		}
	}

	public static <S, T> Map<S, T> checkNotEmptyMapParameter(Map<S, T> aMap, String parameterName) {
		if (aMap == null) {
			throw new NullPointerException("Parameter " + parameterName + " must not be null");
		}

		if (aMap.size() == 0) {
			throw new InvalidParameterException("Parameter " + parameterName + " must not be an empty map");
		}

		return aMap;
	}

	public static String prettyPrintDateAsInterval(Date date) {
		return prettyPrintDateAsInterval(date.getTime());
	}

	public static String prettyPrintDateAsInterval(long timeInMilliSeconds) {
		Duration duration = new Duration(timeInMilliSeconds); // in milliseconds

		PeriodFormatter formatter = new PeriodFormatterBuilder().appendDays().appendSuffix(" days ").appendHours()
				.appendSuffix(" hours ").appendMinutes().appendSuffix(" minutes ").appendSeconds()
				.appendSuffix(" seconds").toFormatter();

		String formatted = formatter.print(duration.toPeriod());

		return formatted;
	}

	public static <T> String getNextNameWithSuffix(List<T> objectsList, String prefix,
			Function<T, String> mapObjectToStringFunction) {
		List<String> names //
				= objectsList.stream().map(mapObjectToStringFunction).collect(Collectors.toList());

		String result = getNextNameWithSuffix(names, prefix);

		return result;
	}

	public static String getNextNameWithSuffix(List<String> names, String prefix) {
		int counter = 1;

		String newName = prefix + "-" + counter++;

		while (names.contains(newName) == true) {
			newName = prefix + "-" + counter++;
		}

		return newName;
	}

	public static String getRapUtilsVersionString() {
		return getVersionString(RichUtils.class);
	}

	/**
	 * @see <a href=
	 *      "https://howtodoinjava.com/regex/java-regex-validate-email-address/">Java
	 *      email validation using regex</a>
	 * @param email
	 * @return
	 */
	public static boolean validateEmailFormat(String email) {
		String regex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";

		Pattern pattern = Pattern.compile(regex);

		Matcher matcher = pattern.matcher(email);

		return matcher.matches();
	}

	/**
	 * @see <a href=
	 *      "https://howtodoinjava.com/regex/java-regex-validate-and-format-north-american-phone-numbers/">Java
	 *      Regex for North American Phone Numbers</a>
	 * 
	 *      <p>
	 *      Examples of valid phone numbers:
	 *      <ul>
	 *      <li>"1234567890"</li>
	 *      <li>"123-456-7890"</li>
	 *      <li>"123.456.7890"</li>
	 *      <li>"123 456 7890"</li>
	 *      <li>"(123) 456 7890"</li>
	 *      </ul>
	 *      <p>
	 * 
	 * @param phoneNumber
	 * @return
	 */
	public static boolean validateNorthAmericanPhoneFormat(String phoneNumber) {
		String regex = "^\\(?([0-9]{3})\\)?[-.\\s]?([0-9]{3})[-.\\s]?([0-9]{4})$";

		Pattern pattern = Pattern.compile(regex);

		Matcher matcher = pattern.matcher(phoneNumber);

		return matcher.matches();
	}

	public static String reformatNorthAmericanPhone(String phoneNumber) {
		String regex = "^\\(?([0-9]{3})\\)?[-.\\s]?([0-9]{3})[-.\\s]?([0-9]{4})$";

		Pattern pattern = Pattern.compile(regex);

		Matcher matcher = pattern.matcher(phoneNumber);

		if (matcher.matches() == true) {
			return matcher.replaceFirst("($1) $2-$3");
		} else {
			return phoneNumber;
		}
	}

	public static void main(String[] args) {

		System.out.println("Phone 919.362.5303: " + validateNorthAmericanPhoneFormat("919.362.5303"));

		System.out.println(integerZeroPadder(2).apply(5));

		System.out.println(getJsonMessage("System", "Jack says \"%1$s\"", "hello, world"));

		System.out.println(join(",", getListMinusLast(toList("one", "two", "three"))));
		System.out.println(join(",", getListMinusLast(toList("one", "two"))));
		System.out.println(join(",", getListMinusLast(toList("one"))));
		System.out.println(join(",", getListMinusLast(new ArrayList<String>()))); // Should
																					// this
																					// be
																					// an
																					// error?

		System.out.println(byteCountToDisplaySize(4L * Integer.MAX_VALUE));

		System.out.println(compareStringsOrIntegers("0104", "1208"));

		System.out.println(isNonEmpty("1", "2"));

		System.out.println(isNonEmpty("1", "", "2"));

		System.out.println(getVersionString(RichUtils.class));

		// // addConfigWatcher("c:/work/tmp/watch-test/my.config",
		// // new ConfigChangeListener() {
		// // @Override
		// // public void configChanged(Config config) {
		// // System.out.println("Contents have changed:");
		// //
		// // if (config.hasPath("testValue") == true) {
		// // String testValue = config.getString("testValue");
		// //
		// // System.out.println("testValue: " + testValue);
		// // } else {
		// //
		// // System.out.println("testValue is not defined");
		// // }
		// // }
		// // });
		//
		// Config config = ConfigFactory.parseFile(new
		// File("c:/work/tmp/watch-test/my.config"));
		//
		// //
		// System.out.println(config.root().render(ConfigRenderOptions.defaults()));
		//
		// ConfigRenderOptions configRenderOptions =
		// ConfigRenderOptions.defaults();
		// // configRenderOptions.setOriginComments(false);
		// // configRenderOptions.setComments(false);
		// // configRenderOptions.setFormatted(false);
		// // configRenderOptions.setJson(false);
		//
		// System.out.println(config.root().render(configRenderOptions));
		//
		// // sleep(5000);
		// //
		// // runInThread(() -> {
		// // System.out.println(join(toList("one"), ", " , " and "));
		// // System.out.println(join(toList("one","two"), ", " , " and "));
		// // System.out.println(join(toList("one","two","three"), ", " ,
		// // " and "));
		// // });
	}

	public static ThreadFactory getNamedThreadFactory(String namePrefix) {
		return NamedThreadFactory.getNamedThreadFactory(namePrefix);
	}

	public static ThreadFactory getNamedThreadFactory(Class<?> aClass, String namePrefix) {
		return getNamedThreadFactory(aClass.getSimpleName() + "-" + namePrefix);
	}

	public static int getAvailableProcessors() {
		return Runtime.getRuntime().availableProcessors();
	}

	/**
	 * The number of bytes in a kilobyte.
	 */
	public static final BigInteger ONE_KB = BigInteger.valueOf(1024);

	/**
	 * The number of bytes in a megabyte.
	 */
	public static final BigInteger ONE_MB = ONE_KB.multiply(ONE_KB);

	/**
	 * The number of bytes in a gigabyte.
	 */
	public static final BigInteger ONE_GB = ONE_KB.multiply(ONE_MB);

	/**
	 * The number of bytes in a terabyte.
	 */
	public static final BigInteger ONE_TB = ONE_KB.multiply(ONE_GB);

	/**
	 * The number of bytes in a petabyte.
	 */
	public static final BigInteger ONE_PB = ONE_KB.multiply(ONE_TB);

	/**
	 * The number of bytes in an exabyte.
	 */
	public static final BigInteger ONE_EB = ONE_KB.multiply(ONE_PB);

	/**
	 * The number of bytes in a zettabyte.
	 */
	public static final BigInteger ONE_ZB = ONE_KB.multiply(ONE_EB);

	/**
	 * The number of bytes in a yottabyte.
	 */
	public static final BigInteger ONE_YB = ONE_KB.multiply(ONE_ZB);

	/**
	 * Returns a human-readable version of the file size, where the input represents
	 * a specific number of bytes.
	 *
	 * @param size the number of bytes
	 * @return a human-readable display value (includes units - YB, ZB, EB, PB, TB,
	 *         GB, MB, KB or bytes)
	 */
	public static String byteCountToDisplaySize(long size) {
		return byteCountToDisplaySize(BigInteger.valueOf(size));
	}

	private static String getThreeSigFigs(BigDecimal displaySize) {
		String number = String.valueOf(displaySize);

		StringBuffer trimmedNumber = new StringBuffer();

		int cnt = 0;
		for (char digit : number.toCharArray()) {
			if (cnt < 3) {
				trimmedNumber.append(digit);
			}
			if (digit != '.') {
				cnt++;
			}
		}
		return trimmedNumber.toString();
	}

	public static String byteCountToDisplaySize(BigInteger size) {
		String displaySize;
		BigDecimal decimalSize = new BigDecimal(size);

		if (size.divide(ONE_YB).compareTo(BigInteger.ZERO) > 0) {
			displaySize = String.valueOf(size.divide(ONE_YB)) + " YB";
		} else if (size.divide(ONE_ZB).compareTo(BigInteger.ZERO) > 0) {
			displaySize = getThreeSigFigs(decimalSize.divide(new BigDecimal(ONE_ZB))) + " ZB";
		} else if (size.divide(ONE_EB).compareTo(BigInteger.ZERO) > 0) {
			displaySize = getThreeSigFigs(decimalSize.divide(new BigDecimal(ONE_EB))) + " EB";
		} else if (size.divide(ONE_PB).compareTo(BigInteger.ZERO) > 0) {
			displaySize = getThreeSigFigs(decimalSize.divide(new BigDecimal(ONE_PB))) + " PB";
		} else if (size.divide(ONE_TB).compareTo(BigInteger.ZERO) > 0) {
			displaySize = getThreeSigFigs(decimalSize.divide(new BigDecimal(ONE_TB))) + " TB";
		} else if (size.divide(ONE_GB).compareTo(BigInteger.ZERO) > 0) {
			displaySize = getThreeSigFigs(decimalSize.divide(new BigDecimal(ONE_GB))) + " GB";
		} else if (size.divide(ONE_MB).compareTo(BigInteger.ZERO) > 0) {
			displaySize = getThreeSigFigs(decimalSize.divide(new BigDecimal(ONE_MB))) + " MB";
		} else if (size.divide(ONE_KB).compareTo(BigInteger.ZERO) > 0) {
			displaySize = getThreeSigFigs(decimalSize.divide(new BigDecimal(ONE_KB))) + " KB";
		} else {
			displaySize = String.valueOf(size) + " bytes";
		}
		return displaySize;
	}

	public static String replaceAllEach(String startString, String[][] replacementPairs) {
		String result = startString;

		for (String[] strings : replacementPairs) {
			String startValue = strings[0];
			String endValue = strings[1];

			result = result.replaceAll(startValue, endValue);
		}

		return result;
	}

	public static String replaceEach(String startString, String[][] replacementPairs) {
		String result = startString;

		for (String[] strings : replacementPairs) {
			String startValue = strings[0];
			String endValue = strings[1];

			result = result.replace(startValue, endValue);
		}

		return result;
	}

	public static void printlnWithStars(String message) {
		System.out.println(
				"********************************************************************************************************");
		System.out.println(message);
		System.out.println(
				"********************************************************************************************************");
	}

	public static <T> List<List<T>> getBuckets(List<T> values, int size) {
		List<List<T>> resultListOfBuckets = new ArrayList<List<T>>();

		List<T> bucket = null;

		for (int index = 0; index < values.size(); index++) {
			if ((index % size) == 0) {
				bucket = new ArrayList<T>();
				resultListOfBuckets.add(bucket);
			}
			T value = values.get(index);

			bucket.add(value);
		}

		return resultListOfBuckets;
	}

	public static <T> FutureTask<T> createSimpleFutureTask(Supplier<T> supplier) {
		return new FutureTask<T>(new Callable<T>() {
			@Override
			public T call() throws Exception {
				return supplier.get();
			}
		});
	}

	public static <T> List<String> getNames(List<T> values, Function<T, String> getNameFunction) {
		return values.stream().map(getNameFunction).collect(Collectors.toList());
	}

	public static <Z> List<Z> getListMinusLast(List<Z> aList) {
		return aList.stream().filter(new Predicate<Z>() {
			private int counter = 0;

			@Override
			public boolean test(Z t) {
				return (counter++ < aList.size() - 1);
			}
		}).collect(Collectors.toList());
	}

	public static String uncamelCaseWithDashes(String string) {
		return uncamelCaseWithDelimiter(string, "-");
	}

	public static String uncamelCaseWithDelimiter(String string, String delimiter) {
		return String.join(delimiter, string.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])"));
	}

	public static String getSimpleName(Class<?> aClass) {
		if (aClass.getName().contains("$") == true) {
			return aClass.getSuperclass().getSimpleName();
		} else {
			return aClass.getSimpleName();
		}
	}

	public static String getClassName(Class<?> aClass) {
		if (aClass.getName().contains("$") == true) {
			return aClass.getSuperclass().getName();
		} else {
			return aClass.getName();
		}
	}

	public static String lowercaseFirstLetter(String simpleName) {
		String firstLetter = simpleName.substring(0, 1);
		String rest = simpleName.substring(1);
		return firstLetter.toLowerCase() + rest;
	}

	public static <T> T getLast(T[] list) {
		return list[list.length - 1];
	}

	public static <T> T getLast(List<T> list) {
		return list.get(list.size() - 1);
	}

	public static void throwUnsupportedEnumException(Enum<?> enumValue) {
		throw new RuntimeException("Unsuported enum value: " + enumValue);
	}

	public static boolean isEven(int size) {
		return ((size % 2) == 0);
	}

	public static <T> List<T> getReversedList(T[] array) {
		List<T> list = Arrays.asList(array);

		Collections.reverse(list);

		return list;
	}

	public static <T> T removeLast(List<T> list) {
		return list.remove(list.size() - 1);
	}

	public static String ints2DToString(int[][] intArray) {
		String[] strings = new String[intArray.length];

		for (int index = 0; index < strings.length; index++) {
			strings[index] = intsToString(intArray[index]);
		}

		return join(":", strings);
	}

	public static int[][] stringToInts2D(String string) {
		if (isEmpty(string) == true) {
			return new int[][] {};
		}

		String[] strings = string.split(":", -1);
		int[][] ints = new int[strings.length][];

		for (int index = 0; index < strings.length; index++) {
			ints[index] = stringToInts(strings[index]);
		}

		return ints;
	}

	public static String intsToString(int[] intArray) {
		String[] strings = new String[intArray.length];

		for (int index = 0; index < strings.length; index++) {
			strings[index] = String.valueOf(intArray[index]);
		}

		return join(",", strings);
	}

	public static int[] stringToInts(String string) {
		if (isEmpty(string) == true) {
			return new int[] {};
		}

		String[] strings = string.split(",", -1);
		int[] ints = new int[strings.length];

		for (int index = 0; index < strings.length; index++) {
			ints[index] = Integer.parseInt(strings[index]);
		}

		return ints;
	}

	public static String getColorizedSimpleClassName(Class<?> aClass) {
		return "\033[0;44m\033[1;37m" + aClass.getSimpleName() + "\033[0m";
	}

	public static String getColorizedClassName(Class<?> aClass) {
		return "\033[0;44m\033[1;37m" + aClass.getName() + "\033[0m"; // \033[0;35m
	}

}
