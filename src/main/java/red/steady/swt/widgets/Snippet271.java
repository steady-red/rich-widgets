package red.steady.swt.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

public class Snippet271 {

	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setText("Snippet 271");
		shell.setBounds(10, 10, 200, 250);
		final Table table = new Table(shell, SWT.NONE);
		table.setBounds(10, 10, 150, 200);
		table.setLinesVisible(true);
		for (int i = 0; i < 5; i++) {
			new TableItem(table, SWT.NONE).setText("item " + i);
		}

		/*
		 * NOTE: MeasureItem is called repeatedly. Therefore it is critical for
		 * performance that this method be as efficient as possible.
		 */
		table.addListener(SWT.MeasureItem, event -> {
			int clientWidth = table.getClientArea().width;
			event.height = event.gc.getFontMetrics().getHeight() * 2;
			event.width = clientWidth * 2;
		});

		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}
}
