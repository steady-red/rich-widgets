package red.steady.swt.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class SpStackedForm extends SpComposite {

	private StackLayout stackLayout;

	public SpStackedForm(Composite parent) {
		this(parent, SWT.NULL);
	}

	public SpStackedForm(Composite parent, int style) {
		super(parent, style);

		stackLayout = new StackLayout();

		setLayout(stackLayout);
	}

	public void setTopControl(Control control) {
		if (stackLayout.topControl != control) {
			stackLayout.topControl = control;

			layout();
		}
	}
}
