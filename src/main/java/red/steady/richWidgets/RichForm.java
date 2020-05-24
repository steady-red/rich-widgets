package red.steady.richWidgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;

import red.steady.richWidgets.application.RichApplication;
import red.steady.richWidgets.utils.FormLayoutDataFactory;

public class RichForm extends RichComposite {

	public RichForm(RichComposite parent) {
		this(parent.getRichApplication(), parent, SWT.NULL);
	}

	public RichForm(RichComposite parent, int style) {
		this(parent.getRichApplication(), parent, style);
	}

	public RichForm(RichApplication richApplication, Composite parent) {
		this(richApplication, parent, SWT.NULL);
	}

	public RichForm(RichApplication richApplication, Composite parent, int style) {
		this(richApplication, parent, style, 0);
	}

	public RichForm(RichApplication richApplication, Composite parent, int style, int margin) {
		super(richApplication, parent, style);

		FormLayout formLayout = new FormLayout();
		formLayout.marginWidth = margin;
		formLayout.marginHeight = margin;

		setLayout(formLayout);

		FormLayoutDataFactory.fill(this);
	}

}
