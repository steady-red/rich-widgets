package red.steady.richWidgets.utils;

@SuppressWarnings("serial")
public class UnsupportedEnumException extends RuntimeException {
	public UnsupportedEnumException(Enum<?> enumValue) {
		super("Unsuported enum value: " + enumValue);
	}
}