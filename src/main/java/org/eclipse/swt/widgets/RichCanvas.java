package org.eclipse.swt.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FormLayout;

import red.steady.richWidgets.RichComposite;
import red.steady.richWidgets.RichWidget;
import red.steady.richWidgets.application.RichApplication;
import red.steady.richWidgets.utils.FormLayoutDataFactory;

public class RichCanvas extends Canvas implements RichWidget {

	private final RichApplication richApplication;

	public RichCanvas(RichComposite parent) {
		this(parent, SWT.NULL);
	}

	public RichCanvas(RichComposite parent, int style) {
		this(parent.getRichApplication(), parent, style);
	}

	public RichCanvas(RichApplication richApplication, Composite parent) {
		this(richApplication, parent, SWT.NULL);
	}

	public RichCanvas(RichApplication richApplication, Composite parent, int style) {
		super(parent, style);

		this.richApplication = richApplication;

		FormLayoutDataFactory.fill(this);
	}

	@Override
	public void setBackground(int red, int green, int blue) {
		setBackground(new Color(getDisplay(), red, green, blue));
	}

	@Override
	public Color createColor(int red, int green, int blue) {
		return new Color(getDisplay(), red, green, blue);
	}

	@Override
	public void empathizeBorderForTesting(int width, int red, int green, int blue) {
		FormLayout formLayout = new FormLayout();
		formLayout.marginWidth = width;
		formLayout.marginHeight = width;
		setLayout(formLayout);

		setBackground(red, green, blue);
	}

	@Override
	public RichApplication getRichApplication() {
		return richApplication;
	}

}
