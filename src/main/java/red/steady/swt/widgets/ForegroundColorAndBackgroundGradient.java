package red.steady.swt.widgets;

import org.eclipse.swt.graphics.Color;

import red.steady.richWidgets.utils.RichUtils;

public class ForegroundColorAndBackgroundGradient {

	public final Color foregroundColor;

	public final Color[] backgroundColors;
	public final int[] percents;
	public final boolean vertical;

	public static ForegroundColorAndBackgroundGradient createTopEdgeGradient(//
			Color foregroundColor, //
			Color[] backgroundColors) {
		RichUtils.checkIsEqual(backgroundColors.length, 4);
		// FirstUtils.is(backgroundColors).hasSize(4);

		return new ForegroundColorAndBackgroundGradient(foregroundColor, backgroundColors, new int[] { 20, 80, 100 },
				true);
	}

	public ForegroundColorAndBackgroundGradient(Color foregroundColor, Color[] backgroundColors) {
		this(foregroundColor, backgroundColors, getEvenlySpacedPercentages(backgroundColors), false);
	}

	public ForegroundColorAndBackgroundGradient(Color foregroundColor, Color[] backgroundColors, int[] percents) {
		this(foregroundColor, backgroundColors, percents, false);
	}

	public ForegroundColorAndBackgroundGradient(Color foregroundColor, Color[] backgroundColors, int[] percents,
			boolean vertical) {
		super();

		this.foregroundColor = foregroundColor;

		this.backgroundColors = backgroundColors;
		this.percents = percents;
		this.vertical = vertical;
	}

	private static int[] getEvenlySpacedPercentages(Color[] backgroundColors) {
		if (backgroundColors.length <= 1) {
			return new int[] { 100 };
		}

		int[] resultIntArray = new int[backgroundColors.length - 1];

		for (int index = 0; index < resultIntArray.length; index++) {
			resultIntArray[index] = (index + 2) * 100 / backgroundColors.length;
		}

		return resultIntArray;
	}

}
