package red.steady.richWidgets;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.RichyRichText;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.RichImageButton;

import red.steady.richWidgets.application.RichApplication;
import red.steady.richWidgets.utils.FormLayoutDataFactory;

public class RichChatPanel extends RichForm {

	private RichyRichText richyRichText;
	private RichForm inputForm;
	private RichyRichText inputRichyRichText;
	private RichForm inputControlsForm;

	public RichChatPanel(RichComposite parent) {
		this(parent.getRichApplication(), parent, SWT.NULL);
	}

	public RichChatPanel(RichComposite parent, int style) {
		this(parent.getRichApplication(), parent, style);
	}

	public RichChatPanel(RichApplication richApplication, Composite parent) {
		this(richApplication, parent, SWT.NULL);
	}

	public RichChatPanel(RichApplication richApplication, Composite parent, int style) {
		super(richApplication, parent, style);

		createControls();
	}

	private void createControls() {
		RichDualSashForm richDualSashForm = new RichDualSashForm(this, SWT.VERTICAL, 80, 20);

		richyRichText = new RichyRichText(richDualSashForm.getTopOrLeftForm(), SWT.V_SCROLL);

		inputForm = new RichForm(richDualSashForm.getBottomOrRightForm());
		createInputFormControls();
	}

	private void handleFiles(String[] filenames) {
		System.out.println("Files: " + String.join("\n", filenames));
	}

	private void createInputFormControls() {
		inputRichyRichText = new RichyRichText(inputForm, SWT.V_SCROLL);

		DropTarget droptarget = new DropTarget(inputRichyRichText, DND.DROP_COPY | DND.DROP_DEFAULT | DND.DROP_LINK);

		droptarget.setTransfer(new Transfer[] { TextTransfer.getInstance(), FileTransfer.getInstance() });

		droptarget.addDropListener(new DropTargetListener() {
			@Override
			public void dragEnter(DropTargetEvent e) {
				e.detail = DND.DROP_COPY;
			}

			@Override
			public void dragOver(DropTargetEvent e) {
			}

			@Override
			public void dragOperationChanged(DropTargetEvent e) {
			}

			@Override
			public void dragLeave(DropTargetEvent event) {
			}

			@Override
			public void dropAccept(DropTargetEvent event) {
			}

			@Override
			public void drop(DropTargetEvent dropTargetEvent) {
				if (dropTargetEvent.data instanceof String[]) {
					String[] filenames = (String[]) dropTargetEvent.data;

					handleFiles(filenames);
				}
			}
		});

		inputControlsForm = new RichForm(inputForm);

		FormLayoutDataFactory.builder()//
				.toRight(0)//
				.toBottom(0)//
				.build().apply(inputControlsForm);

		List<Image> emojisButtonImages = new ArrayList<Image>();
		RichImageButton emojisButton = new RichImageButton(inputControlsForm, emojisButtonImages);
		FormLayoutDataFactory.builder()//
				.toLeft(0)//
				.toTop(0)//
				.toBottom(0)//
				.build().apply(emojisButton);

		List<Image> filePickerButtonImages = new ArrayList<Image>();
		RichImageButton filePickerButton = new RichImageButton(inputControlsForm, filePickerButtonImages);
		FormLayoutDataFactory.builder()//
				.leftControl(emojisButton)//
				.toTop(0)//
				.toBottom(0)//
				.build().apply(filePickerButton);
		filePickerButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fileDialog = new FileDialog(getShell(), SWT.MULTI);
				String fullpath = fileDialog.open();
				File directory = new File(fullpath).getParentFile();

				String[] filenames = fileDialog.getFileNames();

				for (int index = 0; index < filenames.length; index++) {
					filenames[index] = directory.getAbsolutePath() + File.separatorChar + filenames[index];
				}

				if (filenames.length > 0) {
					handleFiles(filenames);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		List<Image> sendButtonImages = new ArrayList<Image>();
		RichImageButton sendButton = new RichImageButton(inputControlsForm, sendButtonImages);
		FormLayoutDataFactory.builder()//
				.leftControl(filePickerButton)//
				.toRight(0)//
				.toTop(0)//
				.toBottom(0)//
				.build().apply(sendButton);
	}

	public static class EmojiPanel extends RichForm {

		public EmojiPanel(RichComposite parent) {
			super(parent.getRichApplication(), parent);
		}

		public EmojiPanel(RichApplication richApplication, Composite parent) {
			super(richApplication, parent);

			createControls();
		}

		private void createControls() {

		}
	}

}
