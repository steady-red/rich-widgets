package org.eclipse.swt.custom;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import red.steady.richWidgets.RichComposite;
import red.steady.richWidgets.application.RichApplication;
import red.steady.richWidgets.utils.RichUtils;

public class RichyRichText extends StyledText {

	private final RichApplication richApplication;

	public RichyRichText(RichComposite parent) {
		this(parent.getRichApplication(), parent, SWT.V_SCROLL | SWT.H_SCROLL);
	}

	public RichyRichText(RichComposite parent, int style) {
		this(parent.getRichApplication(), parent, style);
	}

	public RichyRichText(RichApplication richApplication, Composite parent) {
		this(richApplication, parent, SWT.V_SCROLL | SWT.H_SCROLL);
	}

	public RichyRichText(RichApplication richApplication, Composite parent, int style) {
		super(parent, style);

		RichUtils.checkNotNullParameter(richApplication, "richApplication");

		this.richApplication = richApplication;
	}
}
