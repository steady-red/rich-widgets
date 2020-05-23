package red.steady.richWidgets;

import java.io.File;
import java.util.function.Consumer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;

import red.steady.richWidgets.utils.FormLayoutDataFactory;
import red.steady.richWidgets.utils.SwtUtils;

public class FilePickerPanel extends RichForm {

	private final Consumer<String[]> filesConsumer;

	public FilePickerPanel(Composite parent, Consumer<String[]> filesConsumer) {
		super(parent);

		this.filesConsumer = filesConsumer;

		createControls();
	}

	private void createControls() {
		CLabel cLabel = new CLabel(this, SWT.CENTER);
		cLabel.setText("Drop Files Here");
		cLabel.setBackground(
				SwtUtils.createImage(getDisplay(), "/red/steady/richWidgets/images/blue-repeating-background.jpg"));
		cLabel.setLayout(new FormLayout());

		Button button = new Button(this, SWT.PUSH);
		button.setText("Select Files");
		button.addSelectionListener(new SelectionAdapter() {
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
					filesConsumer.accept(filenames);
				}
			}
		});

		FormLayoutDataFactory.builder()//
				.toTop(0)//
				.fillHorizontal(0)//
				.bottomControl(button).bottomOffset(2)//
				.build().apply(cLabel);

		FormLayoutDataFactory.builder()//
				.toBottom(2)//
				.toRight(0)//
				.build().apply(button);

		DropTarget droptarget = new DropTarget(cLabel, DND.DROP_COPY | DND.DROP_DEFAULT | DND.DROP_LINK);

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

					filesConsumer.accept(filenames);
				}
			}
		});
	}

	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {
		Point point = super.computeSize(wHint, hHint, changed);

		if (wHint == SWT.DEFAULT) {
			point.x = 200;
		}

		if (hHint == SWT.DEFAULT) {
			point.y = 100;
		}

		return point;
	}

}
