package red.steady.swt.widgets;

import java.io.InputStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

import red.steady.richWidgets.utils.FormLayoutDataFactory;

public class SpComposite extends Composite {

	public SpComposite(Composite parent) {
		this(parent, SWT.NULL);
	}

	public SpComposite(Composite parent, int style) {
		super(parent, style);

		FormLayoutDataFactory.fill(this);
	}

	public void setBackground(int red, int green, int blue) {
		setBackground(new Color(getDisplay(), red, green, blue));
	}

	public Color createColor(int red, int green, int blue) {
		return new Color(getDisplay(), red, green, blue);
	}

	protected Image createImageFromClasspath(String imageInClassPath) {
		InputStream image_input_stream = getClass().getClassLoader().getResourceAsStream(imageInClassPath);

		return new Image(getDisplay(), image_input_stream);
	}

}
