package red.steady.swt.widgets;

import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import red.steady.richWidgets.utils.FormLayoutDataFactory;

public class VolumeControlButtonPanelTestMain extends ApplicationWindow {

	public VolumeControlButtonPanelTestMain(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);

		shell.setBounds(200, 200, 500, 400);
	}

	@Override
	protected Control createContents(Composite parent) {
		SpForm spForm = new SpForm(parent);

		VolumeControlButtonPanel buttonPanel = new VolumeControlButtonPanel(spForm, new String[] { "One", "Two" });

		FormLayoutDataFactory.builder()//
				.toRight(2)//
				.toBottom(2)//
				.build().apply(buttonPanel);

		return spForm;
	}

	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);

		VolumeControlButtonPanelTestMain testApplicationWindow = new VolumeControlButtonPanelTestMain(shell);

		testApplicationWindow.setBlockOnOpen(true);

		testApplicationWindow.open();
	}
}
