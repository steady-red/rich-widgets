package red.steady.swt.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.win32.OS;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public class SPortalsShellWithDropShadow extends Shell {

	private static final int DROPDOWN_BORDER_WIDTH = 1;

	private static final int DROP_OFFSET = 3;

	private final Shell dropShadowShell;

	private SimpleForm innerForm;

	public SPortalsShellWithDropShadow(Shell parent) {
		super(createParentDropShadowShell(parent), SWT.NO_TRIM);

		dropShadowShell = (Shell) getParent();

		setBackground(getDisplay().getSystemColor(SWT.COLOR_BLACK));
		FormLayout formLayout = new FormLayout();
		formLayout.marginWidth = 1;
		formLayout.marginHeight = 1;
		setLayout(formLayout);

		innerForm = new SimpleForm(this, SWT.NULL) {
			@Override
			protected void createControls(Composite rapComposite) {
			}
		};
	}

	@Override
	public void setBounds(Rectangle bounds) {
		this.setBounds(bounds.x, bounds.y, bounds.width, bounds.height);
	}

	@Override
	public void setBounds(int x, int y, int width, int height) {
		dropShadowShell.setBounds(x + DROP_OFFSET, y + DROP_OFFSET, //
				width, height);

		super.setBounds(x, y, //
				width, height);

//		dropShadowShell.setBounds(x + DROP_OFFSET, y + DROP_OFFSET, //
//				width + 2 * DROPDOWN_BORDER_WIDTH, height + 2 * DROPDOWN_BORDER_WIDTH);
//
//		super.setBounds(x, y, //
//				width + 2 * DROPDOWN_BORDER_WIDTH, height + 2 * DROPDOWN_BORDER_WIDTH);
	}

	@Override
	public void setSize(int width, int height) {
		dropShadowShell.setSize(width, height);

		super.setSize(width, height);

//		dropShadowShell.setSize(width + 2 * DROPDOWN_BORDER_WIDTH, height + 2 * DROPDOWN_BORDER_WIDTH);
//
//		super.setSize(width + 2 * DROPDOWN_BORDER_WIDTH, height + 2 * DROPDOWN_BORDER_WIDTH);
	}

	@Override
	public void close() {
		if (isDisposed() == false) {
			super.close();
		}
		if (dropShadowShell.isDisposed() == false) {
			dropShadowShell.close();
		}
	}

	@Override
	public void open() {
		dropShadowShell.open();
		super.open();

		OS.BringWindowToTop(dropShadowShell.handle);
		OS.BringWindowToTop(this.handle);
	}

	private static Shell createParentDropShadowShell(Shell parent) {
		Shell shell = new Shell(parent, SWT.NO_TRIM) {

			{
				setAlpha(64);

				addPaintListener(new PaintListener() {
					@Override
					public void paintControl(PaintEvent e) {
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

		};

		return shell;
	}

	@Override
	protected void checkSubclass() {
	}

	public SimpleForm getInnerForm() {
		return innerForm;
	}

}
