package red.steady.swt.widgets.gallery;

import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import red.steady.richWidgets.RichComposite;
import red.steady.richWidgets.RichForm;
import red.steady.richWidgets.RichScrolledComposite;
import red.steady.richWidgets.RichWindow;
import red.steady.richWidgets.utils.CLabelFactory;
import red.steady.richWidgets.utils.FormLayoutDataFactory;
import red.steady.richWidgets.utils.SwtUtils;

public class RichWidgetGalleryMain extends ApplicationWindow implements RichGallery {

	private RichComposite mainRichComposite;

	public RichWidgetGalleryMain(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);

		shell.setBounds(200, 200, 600, 400);

		shell.setText("Rich Widget Gallery");
	}

	@Override
	protected Control createContents(Composite parent) {
		mainRichComposite = new RichComposite(parent);
		mainRichComposite.setLayout(new GridLayout(2, true));

		RichScrolledComposite richScrolledComposite = new RichScrolledComposite(parent, mainRichComposite);

		for (ControlInfo widgetInfo : ControlGalleryInfo.controlInfosList) {
			RichForm richForm = new RichForm(mainRichComposite);
			richForm.setLayoutData(null);

			CLabel cLabel = CLabelFactory.createTopCLabel(richForm, SWT.CENTER);
			cLabel.setText(widgetInfo.getDisplayName());

			Button button = new Button(richForm, SWT.PUSH);
			button.setImage(SwtUtils//
					.createImage(mainRichComposite.getDisplay(),
							GALLERY_IMAGES_RESOURCE_BASE_PATH + widgetInfo.getImageName()));
			button.setToolTipText(widgetInfo.getDisplayName());
			button.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					System.out.println("Selected " + widgetInfo.getDisplayName());

					parent.getDisplay().asyncExec(new Runnable() {
						@Override
						public void run() {
							RichWindow richWindow = new RichWindow(getShell(), SWT.SHELL_TRIM);

							RichForm contentForm = new RichForm(richWindow);
							widgetInfo.getCreateExampleControlsConsumer().accept(contentForm);

							richWindow.pack();

							richWindow.centerOnParent();
							richWindow.openModal();
						}
					});
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
				}
			});
			FormLayoutDataFactory.builder()//
					.topControl(cLabel).topOffset(2)//
					.fill(0)//
					.build().apply(button);
		}

		return richScrolledComposite;
	}

	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);

		RichWidgetGalleryMain richWidgetGalleryMain = new RichWidgetGalleryMain(shell);

		richWidgetGalleryMain.setBlockOnOpen(true);

		richWidgetGalleryMain.open();
	}
}
