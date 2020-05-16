package red.steady.richWidgets.utils;

public interface GroupCompletionMessageHandler {
	String getGroupCompletionMessage(int groupNumber, int totalProcessed, long durationInMilliSeconds);
}