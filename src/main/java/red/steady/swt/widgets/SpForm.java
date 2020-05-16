package red.steady.swt.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;

public class SpForm extends SpComposite {

	public SpForm(Composite parent) {
		this(parent, SWT.NULL);
	}

	public SpForm(Composite parent, int style) {
		this(parent, style, 0, 0);
	}

	public SpForm(Composite parent, int style, int marginWidth, int marginHeight) {
		super(parent, style);

		FormLayout formLayout = new FormLayout();
		formLayout.marginWidth = marginWidth;
		formLayout.marginHeight = marginHeight;

		setLayout(formLayout);
	}

}
