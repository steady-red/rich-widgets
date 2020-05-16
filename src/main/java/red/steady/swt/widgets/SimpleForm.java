package red.steady.swt.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;

import red.steady.richWidgets.utils.FormLayoutDataFactory;

public abstract class SimpleForm extends Composite {

	public SimpleForm(Composite parent, boolean callCreateControls) {
		this(parent, SWT.NONE, callCreateControls, 0);
	}

	public SimpleForm(Composite parent, int style, boolean callCreateControls) {
		this(parent, style, callCreateControls, 0);
	}

	public SimpleForm(Composite parent, int style, int margin) {
		this(parent, style, true, margin);
	}

	public SimpleForm(Composite parent, int style) {
		this(parent, style, true, 0);
	}

	public SimpleForm(Composite parent) {
		this(parent, SWT.NONE, true, 0);
	}

	public SimpleForm(Composite parent, int style, boolean initialize, int margin) {
		super(parent, style);

		FormLayout formLayout = new FormLayout();

		formLayout.marginWidth = margin;
		formLayout.marginHeight = margin;

		setLayout(formLayout);

		if (initialize == true) {
			initialize();
		}

		FormLayoutDataFactory.fill(this);
	}

	protected abstract void createControls(Composite rapComposite);

	protected void initialize() {
		createControls(this);
	}

}
