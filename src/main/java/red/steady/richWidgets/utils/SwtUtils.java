package red.steady.richWidgets.utils;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class SwtUtils {

	public static void runDialogEventLoop(Shell loop_shell) {
		RichUtils.checkTrue((loop_shell.getStyle() & SWT.APPLICATION_MODAL) != 0);
		RichUtils.checkNotNullParameter(loop_shell, "loop_shell");

		try {
			// Use the display provided by the shell if possible
			Display display;
//			if (loop_shell != null) {
			display = loop_shell.getDisplay();
//			} else {
//				display = Display.getCurrent();
//
//				if (display == null) {
//					display = Display.getDefault();
//				}
//			}

			while ((loop_shell != null) && (loop_shell.isDisposed() == false)) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}

			display.update();

			System.out.println(SwtUtils.class.getName() + ".runDialogEventLoop() is ending");
		} catch (Throwable e) {
			e.printStackTrace();

			showException(e, "runDialogEventLoop Aborting");
		}
	}

	public static int showException(Throwable e, String caption) {
		String message = ThrowableUtils.getStackTrace(e);
		message = message.substring(0, Math.min(message.length(), 600));

		return showDialog(getParentShell(), message, caption, SWT.ICON_ERROR | SWT.OK);
	}

	public static int showDialog(final Shell shell, final String message, final String caption, final int style) {
		class MessageBoxRunnable implements Runnable {

			public int result = 0;

			@Override
			public void run() {
				MessageBox message_box = new MessageBox(shell, style);

				message_box.setText(caption);

				message_box.setMessage(message);

				result = message_box.open();
			}

		}
		;

		MessageBoxRunnable messagebox_runnable = new MessageBoxRunnable();

		shell.getDisplay().syncExec(messagebox_runnable);

		return messagebox_runnable.result;
	}

	public static Shell getParentShell() {
		final Display a_display;

		if (Display.getCurrent() != null) {
			a_display = Display.getCurrent(); // getCurrent() is with respect to current thread.
		} else {
			a_display = Display.getDefault();
		}

		class GetShellRunnable implements Runnable {
			public Shell parent = null;

			@Override
			public void run() {
				try {
					parent = a_display.getActiveShell();
				} catch (RuntimeException e) {
				}

				if (parent == null) {
					if (a_display.getShells().length > 0) {
						parent = a_display.getShells()[0];
					} else {
						parent = new Shell();
					}
				}
			}
		}
		;

		GetShellRunnable a_runnable = new GetShellRunnable();

		a_display.syncExec(a_runnable);

		return a_runnable.parent;
	}

	public static boolean readAndDispatch(final Display display) {
		try {
			return display.readAndDispatch();
		} catch (Throwable e) {
			e.printStackTrace(); // TODO log error

			try {
				display.asyncExec(new Runnable() {
					@Override
					public void run() {
						showException(e, "readAndDispatch() Internal Error");
					}
				});
			} catch (Throwable e1) {
				e1.printStackTrace();
			}

//    		return true; // New - try to keep going even with errors.
			return false;
		}
	}

	public static Color getAndDisposeColor(Control control, int r, int g, int b) {
		final Color color = new Color(control.getDisplay(), r, g, b);
		control.addDisposeListener(e -> {
			if (!color.isDisposed()) {
				color.dispose();
			}
		});
		return color;
	}

	public static Color getSystemColor(Display display, final int id) {
		return display.getSystemColor(id);
	}

	public static Image createImage(Device device, String imageName) {
		return createImage(device, SwtUtils.class, imageName);
	}

	public static Image createImage(Device device, Class<?> aClass, String imageName) {
		try (InputStream inputStream = InputStreamUtils.getInputStream(aClass, imageName);) {

			return new Image(device, inputStream);
		} catch (IOException e) {
			e.printStackTrace();

			throw new RuntimeException("Failed to create image", e);
		}
	}

}
