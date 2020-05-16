package red.steady.richWidgets.utils;

import static org.joor.Reflect.onClass;

import java.io.IOException;

public class WindowsUtils {

	private WindowsUtils() {
	}

	public static boolean isAdmin() {
		// TODO: vary on system

		String groups[] //
				= onClass("com.sun.security.auth.module.NTSystem") // Like Class.forName()
						.create() // Call most specific matching constructor
						.call("getGroupIDs") // Call toString()
						.get();

//		String groups[] = (new com.sun.security.auth.module.NTSystem()).getGroupIDs();
		for (String group : groups) {
			if (group.equals("S-1-5-32-544"))
				return true;
		}
		return false;
	}

	public static void main(String[] args) throws IOException {
		System.out.println("Current user is admin ? " + WindowsUtils.isAdmin());
	}
}
