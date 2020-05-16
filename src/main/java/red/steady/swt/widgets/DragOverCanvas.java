package red.steady.swt.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

import red.steady.richWidgets.utils.RichUtils;
import red.steady.richWidgets.utils.UnsupportedEnumException;

public class DragOverCanvas extends Canvas {

	private final static int MARGIN = 4;
	private final static int LINE_WIDTH = 2;

	private Image dropImage;
	private DropSection dropSection;

	public DragOverCanvas(Composite parent) {
		super(parent, SWT.DOUBLE_BUFFERED);

		addPaintListener(new PaintListener() {
			@Override
			public void paintControl(PaintEvent e) {
				GC gc = e.gc;

				if (dropImage != null) {
					gc.drawImage(dropImage, getClientArea().x, getClientArea().y);
				}

				if (dropSection != null) {
					gc.setForeground(new Color(getDisplay(), 128, 128, 128));
					gc.setLineWidth(LINE_WIDTH);
					switch (dropSection) {
					case Left:
					case Right: {
						gc.drawRectangle(LINE_WIDTH, LINE_WIDTH, //
								(getBounds().width - MARGIN - 2 * LINE_WIDTH) / 2, getBounds().height - 2 * LINE_WIDTH);
						gc.drawRectangle(LINE_WIDTH + (getBounds().width - MARGIN - 2 * LINE_WIDTH) / 2 + MARGIN,
								LINE_WIDTH, //
								(getBounds().width - MARGIN - 2 * LINE_WIDTH) / 2, getBounds().height - 2 * LINE_WIDTH);
						break;
					}
					case Top:
					case Bottom: {
						gc.drawRectangle(LINE_WIDTH, LINE_WIDTH, //
								getBounds().width - 2 * LINE_WIDTH, (getBounds().height - MARGIN - 2 * LINE_WIDTH) / 2);
						gc.drawRectangle(LINE_WIDTH,
								LINE_WIDTH + (getBounds().height - MARGIN - 2 * LINE_WIDTH) / 2 + MARGIN, //
								getBounds().width - 2 * LINE_WIDTH, (getBounds().height - MARGIN - 2 * LINE_WIDTH) / 2);
						break;
					}
					default:
						throw new UnsupportedEnumException(dropSection);
					}
				}
			}
		});
	}

	public void updateDropImage(Composite c1) {
		if (dropImage != null) {
			dropImage.dispose();
		}

		GC gc = new GC(c1);
		Point c1size = c1.getSize();
		dropImage = new Image(c1.getDisplay(), c1size.x, c1size.y);
		gc.copyArea(dropImage, 0, 0);
		gc.dispose();

		System.out.println(dropImage.getBounds());
	}

	public DropSection getDropSection() {
		return dropSection;
	}

	public void setDropSection(DropSection dropSection) {
		RichUtils.checkNotNullParameter(dropSection, "dropSection");

		this.dropSection = dropSection;
	}

	public void setDropSection(//
			DropSection defaultDropSection, //
			Composite composite, //
			DropTargetEvent event) {
		Rectangle bounds = composite.getBounds();

		Point point = getDisplay().map(null, composite, event.x, event.y);

		if (point.y < bounds.height / 4) {
			setDropSection(DropSection.Top);

		} else if (point.y >= 3 * bounds.height / 4) {
			setDropSection(DropSection.Bottom);

		} else if (point.x <= bounds.width / 4) {
			setDropSection(DropSection.Left);

		} else if (point.x >= 3 * bounds.width / 4) {
			setDropSection(DropSection.Right);

		} else {
			setDropSection(defaultDropSection);
		}
	}

}
