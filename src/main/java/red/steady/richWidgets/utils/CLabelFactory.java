package red.steady.richWidgets.utils;

import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import red.steady.swt.widgets.ForegroundColorAndBackgroundGradient;

public class CLabelFactory {

	/**
	 * @see CLabel
	 *
	 * @param form
	 * @param style
	 * @return
	 */
	public static CLabel createTopCLabel(Composite composite, int style, Color foregroundColor,
			Color[] backgroundColors) {
		ForegroundColorAndBackgroundGradient foregroundColorAndBackgroundGradient = new ForegroundColorAndBackgroundGradient(
				foregroundColor, backgroundColors);

		return createTopCLabel(composite, style, foregroundColorAndBackgroundGradient);
	}

	/**
	 * @see CLabel
	 *
	 * @param form
	 * @param style
	 * @return
	 */
	public static CLabel createTopCLabel(Composite composite, int style) {
		return createTopCLabel(composite, style, null);
	}

	/**
	 * @see CLabel
	 *
	 * @param form
	 * @param style
	 * @return
	 */
	public static CLabel createTopCLabel(Composite composite, Control topControl, int style) {
		return createTopCLabel(composite, topControl, style, 0, null);
	}

	/**
	 * @see CLabel
	 *
	 * @param form
	 * @param style
	 * @return
	 */
	public static CLabel createTopCLabel(Composite composite, Control topControl, int style, int margin) {
		return createTopCLabel(composite, topControl, style, margin, null);
	}

	/**
	 * @see CLabel
	 *
	 * @param form
	 * @param style
	 * @return
	 */
	public static CLabel createTopCLabel(Composite composite, int style,
			ForegroundColorAndBackgroundGradient foregroundColorAndBackgroundGradient) {
		return createTopCLabel(composite, (Control) null, style, 0, foregroundColorAndBackgroundGradient);
	}

	/**
	 * @see CLabel
	 *
	 * @param form
	 * @param style
	 * @return
	 */
	public static CLabel createTopCLabel(Composite composite, int style, int margin,
			ForegroundColorAndBackgroundGradient foregroundColorAndBackgroundGradient) {
		return createTopCLabel(composite, (Control) null, style, margin, foregroundColorAndBackgroundGradient);
	}

	/**
	 * @see CLabel
	 *
	 * @param form
	 * @param style
	 * @return
	 */
	public static CLabel createTopCLabel(Composite composite, int style, int margin) {
		return createTopCLabel(composite, (Control) null, style, margin, null);
	}

	/**
	 * @see CLabel
	 *
	 * @param form
	 * @param style
	 * @return
	 */
	public static CLabel createTopCLabel(Composite composite, Control topControl, int style, int margin,
			ForegroundColorAndBackgroundGradient foregroundColorAndBackgroundGradient) {
		CLabel clabel = new CLabel(composite, style);

		// TODO: composite.getRapApplication().getRapTheme().scaleFont(clabel);

		if (foregroundColorAndBackgroundGradient != null) {
			clabel.setBackground(foregroundColorAndBackgroundGradient.backgroundColors,
					foregroundColorAndBackgroundGradient.percents, foregroundColorAndBackgroundGradient.vertical);

			clabel.setForeground(foregroundColorAndBackgroundGradient.foregroundColor);
		}

		if (topControl == null) {
			FormData data = new FormData();
			data.left = new FormAttachment(0, margin);
			data.right = new FormAttachment(100, -1 * margin);
			data.top = new FormAttachment(0, margin);
			clabel.setLayoutData(data);
		} else {
			FormData data = new FormData();
			data.left = new FormAttachment(0, margin);
			data.right = new FormAttachment(100, -1 * margin);
			data.top = new FormAttachment(topControl, margin);
			clabel.setLayoutData(data);
		}

		return clabel;
	}
}
