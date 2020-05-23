package red.steady.richWidgets;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Control;

import lombok.Builder;
import lombok.Data;
import red.steady.richWidgets.utils.RichUtils;
import red.steady.richWidgets.utils.SwtUtils;

@Data
public class RichSliderColorScheme {
	private final Color barInsideColor;
	private final Color barBorderColor;
	private final Color barSelectionColor;

	private final Color selectorColor;
	private final Color selectorColorBorder;
	private final Color selectorTextColor;

	@Builder
	private RichSliderColorScheme(Control control, //
			Color barInsideColor, Color barBorderColor, Color barSelectionColor, //
			Color selectorColor, Color selectorColorBorder, Color selectorTextColor) {
		super();

		RichUtils.checkNotNullParameter(control, "control");

		this.barInsideColor = ((barInsideColor != null) ? barInsideColor
				: SwtUtils.getAndDisposeColor(control, 225, 225, 225));
		this.barBorderColor = ((barBorderColor != null) ? barBorderColor
				: SwtUtils.getAndDisposeColor(control, 211, 211, 211));
		this.barSelectionColor = ((barSelectionColor != null) ? barSelectionColor
				: SwtUtils.getAndDisposeColor(control, 41, 128, 185));

		this.selectorColor = ((selectorColor != null) ? selectorColor
				: SwtUtils.getAndDisposeColor(control, 52, 152, 219));
		this.selectorColorBorder = ((selectorColorBorder != null) ? selectorColorBorder
				: SwtUtils.getAndDisposeColor(control, 224, 237, 245));
		this.selectorTextColor = ((selectorTextColor != null) ? selectorTextColor
				: SwtUtils.getAndDisposeColor(control, 255, 255, 255));
	}
}