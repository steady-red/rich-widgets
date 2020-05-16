package red.steady.swt.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Shell;

public class SPortalsDropShadowShell extends Shell {

	public SPortalsDropShadowShell(Shell parent) {
		super(parent, SWT.NO_TRIM);

		setAlpha(64);

		addPaintListener(new PaintListener() {
			@Override
			public void paintControl(PaintEvent e) {
				System.out.println("Drawing dropshadow");
				e.gc.setBackground(getDisplay().getSystemColor(SWT.COLOR_BLACK));
				Rectangle bounds = getBounds();
				bounds.x = 0;
				bounds.y = 0;
				e.gc.fillRectangle(bounds);
			}
		});
	}

	@Override
	protected void checkSubclass() {
	}

}
