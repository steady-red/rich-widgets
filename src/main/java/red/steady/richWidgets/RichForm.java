package red.steady.richWidgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;

import red.steady.richWidgets.utils.FormLayoutDataFactory;

public class RichForm extends RichComposite {

	public RichForm(Composite parent) {
		this(parent, SWT.NULL);
	}

	public RichForm(Composite parent, int style) {
		this(parent, style, 0);
	}

	public RichForm(Composite parent, int style, int margin) {
		super(parent, style);

		FormLayout formLayout = new FormLayout();
		formLayout.marginWidth = margin;
		formLayout.marginHeight = margin;

		setLayout(formLayout);

		FormLayoutDataFactory.fill(this);
	}

}
