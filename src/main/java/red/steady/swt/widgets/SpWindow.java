package red.steady.swt.widgets;

import java.io.InputStream;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class SpWindow extends Shell {

	public SpWindow() {
		super();
	}

	public SpWindow(Display display, int style) {
		super(display, style);
	}

	public SpWindow(Display display) {
		super(display);
	}

	public SpWindow(int style) {
		super(style);
	}

	public SpWindow(Shell parent, int style) {
		super(parent, style);
	}

	public SpWindow(Shell parent) {
		super(parent);
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

	@Override
	protected void checkSubclass() {
	}

}
