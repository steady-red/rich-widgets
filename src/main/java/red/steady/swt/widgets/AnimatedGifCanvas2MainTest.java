package red.steady.swt.widgets;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import red.steady.richWidgets.utils.FormLayoutDataFactory;
import red.steady.richWidgets.utils.InputStreamUtils;

public class AnimatedGifCanvas2MainTest {

	public static void main(String[] args) {
		Display display = new Display();

		final Shell shell = new Shell(display, SWT.SHELL_TRIM);
		shell.setLayout(new FormLayout());

		File diceImageFile = new File("misc/images/animated-dice-image-0098.gif");

		AnimatedGifCanvas2 animatedGifCanvas = null;

		try (InputStream inputStream = InputStreamUtils.getInputStream(diceImageFile);) {
			animatedGifCanvas //
					= AnimatedGifCanvas2.builder()//
							.parent(shell)//
							.animatedGifInputStream(inputStream)//
							.slowDown(0)//
							.build();

			FormLayoutDataFactory.fill(animatedGifCanvas);
		} catch (IOException e) {
			e.printStackTrace();
		}

		shell.setSize(animatedGifCanvas.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}
}
