package red.steady.richWidgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class RichScrolledComposite extends ScrolledComposite {

	private final Control control;

	public RichScrolledComposite(Composite parent, Control control) {
		this(parent, control, SWT.H_SCROLL | SWT.V_SCROLL);
	}

	public RichScrolledComposite(Composite parent, Control control, int style) {
		super(parent, style);

		this.control = control;

		createControls();

		parent.addControlListener(new ControlListener() {
			@Override
			public void controlResized(ControlEvent e) {
				setControlSize();
			}

			@Override
			public void controlMoved(ControlEvent e) {
			}
		});

	}

	private void createControls() {
		control.setParent(this);

		setContent(control);

		setControlSize();
	}

	private void setControlSize() {
		Point newSize = control.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		System.out.println("newSize: " + newSize);
		control.setSize(newSize);
	}

}
