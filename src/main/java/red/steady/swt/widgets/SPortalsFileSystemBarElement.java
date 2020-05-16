package red.steady.swt.widgets;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public class SPortalsFileSystemBarElement extends Canvas implements PaintListener, ToggleTweenChangeListener {

	private static final int DOWN_BUTTON_SPACER = 1;

	private static final int MARGIN = 1;

	private static final int BORDER_WIDTH = 1;

	private final static int DOWN_BUTTON_WIDTH = 15;

	private Image downButtonImage;
	private String displayText;

	private final SPortalsBreadcrumbItemListener breadcrumbItemListener;
	private final SPortalsFileSystemBar fileSystemBarParent;
	private final Color defaultBackgroundColor;
	private final int defaultBackgroundRed;
	private final int defaultBackgroundGreen;
	private final int defaultBackgroundBlue;

	private Object syncObject = new Object();

	private final SPortalsToggleTween sPortalsToggleTween;

	public SPortalsFileSystemBarElement(SPortalsFileSystemBar fileSystemBarParent, //
			String displayText, //
			SPortalsBreadcrumbItemListener breadcrumbItemListener) {
		super(fileSystemBarParent, SWT.NULL);

		this.fileSystemBarParent = fileSystemBarParent;
		this.displayText = displayText;
		this.breadcrumbItemListener = breadcrumbItemListener;

		downButtonImage = getImage("com/redmindset/images/arrow-minimal-down-16x16.png");

		this.sPortalsToggleTween = new SPortalsToggleTween(this);

		createControlActions();

		this.fileSystemBarParent.addSPortalsBreadcrumbItem(this);

		this.defaultBackgroundColor = getBackground();
		defaultBackgroundRed = defaultBackgroundColor.getRed();
		defaultBackgroundGreen = defaultBackgroundColor.getGreen();
		defaultBackgroundBlue = defaultBackgroundColor.getBlue();

		addPaintListener(this);

	}

	@Override
	public void dispose() {
		sPortalsToggleTween.stop();
	}

	private Color getActualBackgroundColor() {
		return new Color(getDisplay(), //
				sPortalsToggleTween.getTween(defaultBackgroundRed, 192), //
				sPortalsToggleTween.getTween(defaultBackgroundGreen, 229), //
				sPortalsToggleTween.getTween(defaultBackgroundBlue, 255));
	}

	private Color getActualBorderColor() {
		return new Color(getDisplay(), //
				sPortalsToggleTween.getTween(defaultBackgroundRed, 157), //
				sPortalsToggleTween.getTween(defaultBackgroundGreen, 197), //
				sPortalsToggleTween.getTween(defaultBackgroundBlue, 228));
	}

	private void createControlActions() {
		addListener(SWT.MouseEnter, new Listener() {
			@Override
			public void handleEvent(Event e) {
				sPortalsToggleTween.addToTweenQueue(true);
			}
		});

		addListener(SWT.MouseExit, new Listener() {
			@Override
			public void handleEvent(Event e) {
				sPortalsToggleTween.addToTweenQueue(false);
			}
		});

		addListener(SWT.MouseDown, new Listener() {
			@Override
			public void handleEvent(Event event) {
				if (event.x < getBounds().width - DOWN_BUTTON_WIDTH) {
					breadcrumbItemListener.selected();
				} else {
					String[] dropdownValues //
					= breadcrumbItemListener.dropdownSelected(SPortalsFileSystemBarElement.this);

//					Table 
				}
			}
		});

	}

	private Image getImage(String imageName) {
		try (InputStream inputStream = SPortalsFileSystemBarElement.class.getClassLoader()
				.getResourceAsStream(imageName);) {

			ImageData imageData = new ImageData(inputStream);
			Image image = new Image(getDisplay(), imageData);

			return image;
		} catch (IOException e) {
			throw new RuntimeException("Could not load image: " + imageName, e);
		}
	}

	@Override
	public void paintControl(PaintEvent e) {
		GC gc = e.gc;
		Rectangle bounds = getBounds();
		bounds.x = 0;
		bounds.y = 0;

		int buttonLeft = bounds.width - DOWN_BUTTON_WIDTH;

		gc.setBackground(getActualBackgroundColor());
		gc.fillRectangle(bounds);

		gc.setForeground(getActualBorderColor());
		gc.drawRectangle(buttonLeft, bounds.y, bounds.width - buttonLeft - 1, bounds.height - 1);
		gc.drawRectangle(bounds.x, bounds.y, buttonLeft, bounds.height - 1);

		Point extent = gc.textExtent(displayText);

		gc.setForeground(new Color(getDisplay(), 0, 0, 0));
		gc.drawText(//
				displayText, //
				(buttonLeft + DOWN_BUTTON_SPACER - extent.x) / 2, //
				(bounds.height - extent.y) / 2, //
				true);

		gc.drawImage(downButtonImage, //
				buttonLeft + (DOWN_BUTTON_WIDTH - downButtonImage.getBounds().width) / 2 - 1, //
				bounds.y + (bounds.height - downButtonImage.getBounds().width) / 2);
	}

	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {
		GC gc = new GC(this);
		Point extent = gc.textExtent(displayText);
		gc.dispose();

		int width = DOWN_BUTTON_WIDTH + DOWN_BUTTON_SPACER + extent.x + 2 * BORDER_WIDTH + 2 * MARGIN;
		int height = extent.y + 2 * BORDER_WIDTH + 2 * MARGIN;

		return new Point(width, height);
	}

	public String getDisplayText() {
		return displayText;
	}

	@Override
	public void tweenIndexChanged(int tweenIndex) {
		if (isDisposed() == false) {
			getDisplay().syncExec(new Runnable() {
				@Override
				public void run() {
					if (SPortalsFileSystemBarElement.this.isDisposed() == false) {
						SPortalsFileSystemBarElement.this.redraw();
					}
				}
			});
		}
	}

	@Override
	public void completed(boolean lastToggleValue) {
		// TODO Auto-generated method stub

	}

}
