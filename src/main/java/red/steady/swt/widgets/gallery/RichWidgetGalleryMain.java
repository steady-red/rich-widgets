package red.steady.swt.widgets.gallery;

import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import red.steady.richWidgets.RichComposite;
import red.steady.richWidgets.RichForm;
import red.steady.richWidgets.RichWindow;
import red.steady.richWidgets.utils.CLabelFactory;
import red.steady.richWidgets.utils.FormLayoutDataFactory;
import red.steady.richWidgets.utils.SwtUtils;

public class RichWidgetGalleryMain extends ApplicationWindow implements RichGallery {

	private RichComposite mainRichComposite;
	private ScrolledComposite scrolledComposite;

	public RichWidgetGalleryMain(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);

		shell.setBounds(200, 200, 600, 400);

		shell.addControlListener(new ControlListener() {
			@Override
			public void controlResized(ControlEvent e) {
				Point newSize = mainRichComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT);
				System.out.println("newSize: " + newSize);
				mainRichComposite.setSize(newSize);
			}

			@Override
			public void controlMoved(ControlEvent e) {
			}
		});

		shell.setText("Rich Widget Gallery");
	}

	@Override
	protected Control createContents(Composite parent) {
		scrolledComposite = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);

		mainRichComposite = new RichComposite(scrolledComposite);
		mainRichComposite.setLayout(new GridLayout(2, true));

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
							RichWindow richWindow = new RichWindow(getShell());

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

		scrolledComposite.setContent(mainRichComposite);
		Point mainFormSize = mainRichComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		System.out.println("mainFormSize: " + mainFormSize);
		mainRichComposite.setSize(mainFormSize);

		return scrolledComposite;
	}

	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);

		RichWidgetGalleryMain richWidgetGalleryMain = new RichWidgetGalleryMain(shell);

		richWidgetGalleryMain.setBlockOnOpen(true);

		richWidgetGalleryMain.open();
	}
}
