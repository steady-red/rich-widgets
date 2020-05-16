/*
 * Created on Dec 19, 2003
 */
package red.steady.swt.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Paul S Davenport III
 * @copyright Steady Red Fred 2011-2012
 * @svnid SVNID
 */

public class ImageCanvas extends Canvas {

	private Image image = null;

	public ImageCanvas(Composite parent, Image image) {
		this(parent, image, SWT.NULL);
	}

	public ImageCanvas(Composite parent, Image image, int style) {
		super(parent, SWT.NO_REDRAW_RESIZE | style);

		setImage(image);

		addPaintListener(new PaintListener() {
			@Override
			public void paintControl(PaintEvent event) {
				if (ImageCanvas.this.image == null) {
					return;
				}

//				event.gc//
//				.drawImage(ImageCanvas.this.image, //
//						0, 0, ImageCanvas.this.image.getBounds().width,
//						ImageCanvas.this.image.getBounds().height, //
//						0, 0, ImageCanvas.this.image.getBounds().width,
//						ImageCanvas.this.image.getBounds().height);

				// new: pauld3
				Rectangle bounds = getBounds();

				event.gc//
						.drawImage(ImageCanvas.this.image, //
								0, 0, ImageCanvas.this.image.getBounds().width,
								ImageCanvas.this.image.getBounds().height, //
								0, 0, bounds.width, bounds.height);
			}
		});
	}

	public ImageCanvas(Composite parent) {
		this(parent, null);
	}

	public void setImage(Image image) {
		this.image = image;
	}

	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {
		Point size = super.computeSize(wHint, hHint, changed);

		if (wHint == SWT.DEFAULT) {
			size.x = 20;
		}

		if (hHint == SWT.DEFAULT) {
			size.y = 20;
		}

		return size;
	}

}
