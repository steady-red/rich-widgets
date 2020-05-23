package red.steady.richWidgets;

import java.io.InputStream;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class RichWindow extends Shell {

	private int returnCode = Window.CANCEL;

	public RichWindow() {
		super();

		setLayout(new FormLayout());
	}

	public RichWindow(Display display, int style) {
		super(display, style);

		setLayout(new FormLayout());
	}

	public RichWindow(Display display) {
		super(display);

		setLayout(new FormLayout());
	}

	public RichWindow(int style) {
		super(style);

		setLayout(new FormLayout());
	}

	public RichWindow(Shell parent, int style) {
		super(parent, style);

		setLayout(new FormLayout());
	}

	public RichWindow(Shell parent) {
		super(parent);

		setLayout(new FormLayout());
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

	public int openModal() {
		open();

		try {
			while (!this.isDisposed()) {
				if (!getDisplay().readAndDispatch()) {
					getDisplay().sleep();
				}
			}
		} catch (RuntimeException e) {
			System.out.println("Modal event loop had a RuntimeException");

			e.printStackTrace();

			throw e;
		}

		return getResultCode();
	}

	public int getResultCode() {
		switch (returnCode) {
		case Window.OK:
			return SWT.OK;
		case Window.CANCEL:
			return SWT.CANCEL;
		default:
			throw new RuntimeException(
					getClass().getName() + ".open() returned with unsupported return code, " + returnCode);
		}
	}

	public void setReturnCode(int returnCode) {
		this.returnCode = returnCode;
	}

	public void centerOnParent() {
		if (getParent() == null) {
			centerOnMonitor(getShell(), 50);
		} else {
			centerOnWindow((Shell) getParent(), this);
		}
	}

//	public void centerOnWindow(Shell parent) {
//		centerOnWindow(parent, (Shell) getNativeComponent());
//	}

	// TODO sort out the following..
	public static void centerOnPrimaryMonitor(Shell shell) {
		Rectangle shell_bounds = shell.getBounds();

		centerOnPrimaryMonitor(shell, shell_bounds.width, shell_bounds.height);
	}

	public static void centerOnMonitor(Shell shell) {
		Rectangle shell_bounds = shell.getBounds();

		centerOnMonitor(shell, shell_bounds.width, shell_bounds.height);
	}

	public static void centerOnPrimaryMonitor(Shell shell, int size_percentage_of_parent) {
		Rectangle display_bounds = shell.getDisplay().getPrimaryMonitor().getBounds();

		int width = size_percentage_of_parent * display_bounds.width / 100;

		int height = size_percentage_of_parent * display_bounds.height / 100;

		centerOnPrimaryMonitor(shell, width, height);
	}

	public static void centerOnMonitor(Shell shell, int size_percentage_of_parent) {
		Rectangle display_bounds = shell.getMonitor().getBounds();

		int width = size_percentage_of_parent * display_bounds.width / 100;

		int height = size_percentage_of_parent * display_bounds.height / 100;

		centerOnMonitor(shell, width, height);
	}

	public static void centerOnPrimaryMonitor(Shell shell, int width, int height) {
		Rectangle display_bounds = shell.getDisplay().getPrimaryMonitor().getBounds();

		int x = (display_bounds.width - width) / 2;

		int y = (display_bounds.height - height) / 2;

		shell.setBounds(x, y, width, height);
	}

	public static void centerOnMonitor(Shell shell, int width, int height) {
		Rectangle display_bounds = shell.getMonitor().getBounds();

		int x = (display_bounds.width - width) / 2;

		int y = (display_bounds.height - height) / 2;

		shell.setBounds(x, y, width, height);
	}

	public static void centerOnParent(Shell shell) {
		Composite parent = shell.getParent();

		if (parent == null) {
			centerOnMonitor(shell);
		} else {
			centerOnWindow(parent.getShell(), shell);
		}
	}

	public static void centerOnParent(Shell shell, int size_percentage_of_parent) {
		Composite parent = shell.getParent();

		if (parent == null) {
			centerOnMonitor(shell, size_percentage_of_parent);
		} else {
			centerOnWindow(parent.getShell(), shell, size_percentage_of_parent);
		}
	}

	public static void centerOnParent(Shell shell, int width, int height) {
		Composite parent = shell.getParent();

		if (parent == null) {
			centerOnMonitor(shell, width, height);
		} else {
			centerOnWindow(parent.getShell(), shell, width, height);
		}
	}

	public static void centerOnWindow(Shell parent, Shell shell) {
		Rectangle this_shell_bounds = shell.getBounds();

		centerOnWindow(parent, shell, this_shell_bounds.width, this_shell_bounds.height);
	}

	public static void centerOnWindow(Shell parent, Shell shell, int size_percentage_of_parent) {
		Rectangle parent_bounds = parent.getBounds();

		int width = size_percentage_of_parent * parent_bounds.width / 100;

		int height = size_percentage_of_parent * parent_bounds.height / 100;

		centerOnWindow(parent, shell, width, height);
	}

	public static void centerOnWindow(Shell parent, Shell shell, int width, int height) {
		Rectangle parent_bounds = parent.getBounds();

		int x = parent_bounds.x + (parent_bounds.width - width) / 2;

		if (x < 0) {
			x = 0;
		}

		int y = parent_bounds.y + (parent_bounds.height - height) / 2;

		if (y < 0) {
			y = 0;
		}

		// shell.setLocation(x, y);
		shell.setBounds(x, y, width, height);
	}

}
