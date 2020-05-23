package red.steady.swt.widgets.gallery;

import java.util.function.Consumer;
import java.util.function.Function;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.MessageBox;

import red.steady.richWidgets.FilePickerPanel;
import red.steady.richWidgets.RichForm;
import red.steady.richWidgets.RichScrolledComposite;
import red.steady.richWidgets.RichSlider;
import red.steady.richWidgets.utils.FormLayoutDataFactory;
import red.steady.richWidgets.utils.RichUtils;

public class ControlGalleryInfo {

	public static ControlInfo[] controlInfosList//
			= { //
					ControlInfo.builder()//
							.controlClass(RichSlider.class) //
							.displayName(RichSlider.class.getSimpleName())//
							.createControlFunction(new Function<Composite, Control>() {
								@Override
								public Control apply(Composite parent) {
									return new RichSlider(parent, SWT.NULL);
								}
							})//
							.createExampleControlsConsumer(new Consumer<Composite>() {
								@Override
								public void accept(Composite parent) {
									RichSlider nebulaSlider = new RichSlider(parent, SWT.NULL);

									FormLayoutDataFactory.fill(nebulaSlider);
								}
							})//
							.build(), //

					ControlInfo.builder()//
							.controlClass(FilePickerPanel.class) //
							.displayName(FilePickerPanel.class.getSimpleName())//
							.createControlFunction(new Function<Composite, Control>() {
								@Override
								public Control apply(Composite parent) {
									Consumer<String[]> fileConsumer //
									= new Consumer<String[]>() {
										@Override
										public void accept(String[] files) {
											parent.getDisplay().asyncExec(new Runnable() {
												@Override
												public void run() {
													MessageBox messageBox = new MessageBox(parent.getShell());
													messageBox.setText("Picked files...");
													messageBox.setMessage("Files: " + RichUtils.join("\n", files));
													messageBox.open();
												}
											});
										}
									};

									return new FilePickerPanel(parent, fileConsumer);
								}
							})//
							.createExampleControlsConsumer(new Consumer<Composite>() {
								@Override
								public void accept(Composite parent) {
									Consumer<String[]> fileConsumer //
									= new Consumer<String[]>() {
										@Override
										public void accept(String[] files) {
											parent.getDisplay().asyncExec(new Runnable() {
												@Override
												public void run() {
													MessageBox messageBox = new MessageBox(parent.getShell());
													messageBox.setText("Picked files...");
													messageBox.setMessage("Files: " + RichUtils.join("\n", files));
													messageBox.open();
												}
											});
										}
									};

									FilePickerPanel filePickerPanel = new FilePickerPanel(parent, fileConsumer);

									FormLayoutDataFactory.fill(filePickerPanel);
								}
							})//
							.build(), //

					ControlInfo.builder()//
							.controlClass(RichScrolledComposite.class) //
							.displayName(RichScrolledComposite.class.getSimpleName())//
							.createControlFunction(new Function<Composite, Control>() {
								@Override
								public Control apply(Composite parent) {
									RichForm exampleParent = new RichForm(parent, SWT.BORDER) {
										@Override
										public Point computeSize(int wHint, int hHint, boolean changed) {
											return new Point(80, 80);
										}
									};

									FormLayoutDataFactory.fill(exampleParent);

									RichForm colorRichForm = new RichForm(exampleParent, SWT.BORDER) {
										@Override
										public Point computeSize(int wHint, int hHint, boolean changed) {
											return new Point(100, 100);
										}
									};
									colorRichForm.setBackground(0, 0, 255);

									RichScrolledComposite richScrolledComposite //
									= new RichScrolledComposite(exampleParent, colorRichForm);

									FormLayoutDataFactory.fill(richScrolledComposite);

									return exampleParent;
								}
							})//
							.createExampleControlsConsumer(new Consumer<Composite>() {
								@Override
								public void accept(Composite parent) {
									RichForm exampleParent = new RichForm(parent, SWT.BORDER) {
										@Override
										public Point computeSize(int wHint, int hHint, boolean changed) {
											return new Point(80, 80);
										}
									};

									FormLayoutDataFactory.fill(exampleParent);

									RichForm colorRichForm = new RichForm(exampleParent, SWT.BORDER) {
										@Override
										public Point computeSize(int wHint, int hHint, boolean changed) {
											return new Point(100, 100);
										}
									};
									colorRichForm.setBackground(0, 0, 255);

									RichScrolledComposite richScrolledComposite //
									= new RichScrolledComposite(exampleParent, colorRichForm);

									FormLayoutDataFactory.fill(richScrolledComposite);
								}
							})//
							.build(), //

			};

}
