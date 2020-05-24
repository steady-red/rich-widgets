package red.steady.swt.widgets.gallery;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import red.steady.richWidgets.RichForm;
import red.steady.richWidgets.application.RichApplication;
import red.steady.richWidgets.utils.FormLayoutDataFactory;

public class CreateGalleryImagesMain implements RichGallery {

	public static void main(String[] args) {
		Display display = new Display();

		final Shell shell = new Shell(display, SWT.SHELL_TRIM);
		shell.setLayout(new FormLayout());

		shell.setSize(500, 400);
		shell.open();

		File resourceDirectory = new File("src/main/resources" + GALLERY_IMAGES_RESOURCE_BASE_PATH);
		resourceDirectory.mkdirs();

		Control lastControl = null;

		RichApplication richApplication = new RichApplication("CreateGalleryImages");
		RichForm richForm = new RichForm(richApplication, shell);

		Button button = new Button(richForm, SWT.PUSH);
		button.setText("Copy");
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				for (ControlInfo widgetInfo : ControlGalleryInfo.controlInfosList) {
					Control control = widgetInfo.getCreatedControl();

					Point controlSize = control.getSize();

					GC gc = new GC(control);
					final Image image = new Image(display, controlSize.x, controlSize.y);
					gc.copyArea(image, 0, 0);
					gc.dispose();

					ImageLoader saver = new ImageLoader();
					saver.data = new ImageData[] { image.getImageData() };
					saver//
							.save(new File(resourceDirectory, widgetInfo.getImageName()).getAbsolutePath(), //
									SWT.IMAGE_PNG);

					image.dispose();
				}
			}
		});

		lastControl = button;

		for (ControlInfo widgetInfo : ControlGalleryInfo.controlInfosList) {
			Control control = widgetInfo.getCreateControlFunction().apply(richForm);
			widgetInfo.setCreatedControl(control);

			FormLayoutDataFactory.builder()//
					.toLeft(0)//
					.topControl(lastControl).topOffset(2)//
					.build().apply(control);
			shell.layout();
			richForm.layout();

			Point controlSize = control.getSize();
//					control.redraw(0, 0, controlSize.x, controlSize.y, true);

			GC gc = new GC(control);
			final Image image = new Image(display, controlSize.x, controlSize.y);
			gc.copyArea(image, 0, 0);
			gc.dispose();

			ImageLoader saver = new ImageLoader();
			saver.data = new ImageData[] { image.getImageData() };
			saver//
					.save(new File(resourceDirectory, widgetInfo.getImageName()).getAbsolutePath(), //
							SWT.IMAGE_PNG);

			image.dispose();

			lastControl = control;
		}

//		new Thread() {
//			@Override
//			public void run() {
//				RichUtils.sleep(1000);
//
//				for (WidgetInfo widgetInfo : WidgetGalleryInfo.widgetInfosList) {
//					display.syncExec(new Runnable() {
//						@Override
//						public void run() {
//							control = widgetInfo.getCreateControlFunction().apply(shell);
//							control.setBackground(new Color(shell.getDisplay(), 0, 0, 0));
//							FormLayoutDataFactory.toTopLeft(control);
//							shell.layout();
//							Point controlSize = control.getSize();
////							control.redraw(0, 0, controlSize.x, controlSize.y, true);
//
//							GC gc = new GC(control);
//							final Image image = new Image(display, controlSize.x, controlSize.y);
//							gc.copyArea(image, 0, 0);
//							gc.dispose();
//
//							ImageLoader saver = new ImageLoader();
//							saver.data = new ImageData[] { image.getImageData() };
//							saver//
//									.save(new File(resourceDirectory, widgetInfo.getImageName()).getAbsolutePath(), //
//											SWT.IMAGE_PNG);
//
//							image.dispose();
//						}
//					});
//				}
//			}
//		}.start();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}

		display.dispose();
	}
}
