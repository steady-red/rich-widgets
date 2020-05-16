package red.steady.swt.widgets;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;

public class SPortalsTableSingleColumnLayout extends org.eclipse.jface.layout.TableColumnLayout {

	@Override
	protected Point computeSize(Composite composite, int wHint, int hHint, boolean flushCache) {
		return new Point(wHint, hHint);
	}
}
