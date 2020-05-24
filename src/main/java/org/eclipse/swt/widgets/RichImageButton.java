package org.eclipse.swt.widgets;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;

import red.steady.richWidgets.RichComposite;
import red.steady.richWidgets.SPortalsToggleTween;
import red.steady.richWidgets.ToggleTweenChangeListener;
import red.steady.richWidgets.application.RichApplication;
import red.steady.richWidgets.utils.RichUtils;

public class RichImageButton extends RichCanvas implements PaintListener {

	private static final int MARGIN = 1;

	private static final int BORDER_WIDTH = 1;

//	private final static int DOWN_BUTTON_WIDTH = 15;
	private int imageWidth;
	private int imageHeight;

	private boolean setActivated;

	private List<Image> images;

	private final Color defaultBackgroundColor;
	private final int defaultBackgroundRed;
	private final int defaultBackgroundGreen;
	private final int defaultBackgroundBlue;

	private final SPortalsToggleTween sPortalsToggleTween;
	private final ToggleTweenChangeListener hoverToggleTweenChangeListener;

	private final boolean alwaysShowBorder;

	public RichImageButton(RichComposite parent, List<Image> images) {
		this(parent.getRichApplication(), parent, images, true);
	}

	public RichImageButton(RichComposite parent, List<Image> images, boolean alwaysShowBorder) {
		this(parent.getRichApplication(), parent, images, alwaysShowBorder);
	}

	public RichImageButton(RichApplication richApplication, Composite parent, List<Image> images) {
		this(richApplication, parent, images, true);
	}

	public RichImageButton(RichApplication richApplication, Composite parent, List<Image> images,
			boolean alwaysShowBorder) {
		super(richApplication, parent);

		this.alwaysShowBorder = alwaysShowBorder;

		this.images //
				= new ArrayList<Image>(images);

		imageWidth = 5;
		imageHeight = 5;

		for (Image image : images) {
			Rectangle bounds = image.getBounds();

			imageWidth = Math.max(imageWidth, bounds.width);
			imageHeight = Math.max(imageHeight, bounds.height);
		}

		this.hoverToggleTweenChangeListener //
				= new ToggleTweenChangeListener() {
					@Override
					public void tweenIndexChanged(int tweenIndex) {
						if (isDisposed() == false) {
							getDisplay().syncExec(new Runnable() {
								@Override
								public void run() {
									if (RichImageButton.this.isDisposed() == false) {
										RichImageButton.this.redraw();
									}
								}
							});
						}
					}

					@Override
					public void completed(boolean lastToggleValue) {
					}
				};
		this.sPortalsToggleTween = new SPortalsToggleTween(hoverToggleTweenChangeListener, 20, 100);

		createControlActions();

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
		if (setActivated == true) {
			return new Color(getDisplay(), 192, 229, 255);
		}

		return new Color(getDisplay(), //
				sPortalsToggleTween.getTween(defaultBackgroundRed, 192), //
				sPortalsToggleTween.getTween(defaultBackgroundGreen, 229), //
				sPortalsToggleTween.getTween(defaultBackgroundBlue, 255));
	}

	private RGB defaultBackgroundRGB = new RGB(157, 197, 228);

	private Color _defaultBackgroundSwtColor;

	private Color getDefaultBorderSwtColor() {
		if (_defaultBackgroundSwtColor == null) {
			_defaultBackgroundSwtColor = new Color(getDisplay(), defaultBackgroundRGB.red, defaultBackgroundRGB.green,
					defaultBackgroundRGB.blue);
			;
		}

		return _defaultBackgroundSwtColor;
	}

	private Color getActualBorderColor() {
		if (setActivated == true) {
			return getDefaultBorderSwtColor();
		}

		return new Color(getDisplay(), //
				sPortalsToggleTween.getTween(defaultBackgroundRed, defaultBackgroundRGB.red), //
				sPortalsToggleTween.getTween(defaultBackgroundGreen, defaultBackgroundRGB.green), //
				sPortalsToggleTween.getTween(defaultBackgroundBlue, defaultBackgroundRGB.blue));
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
				if (isDisposed() == false) {
					sPortalsToggleTween.addToTweenQueue(false);
				}
			}
		});

		addMouseListener(new MouseListener() {
			@Override
			public void mouseUp(MouseEvent e) {
			}

			@Override
			public void mouseDown(MouseEvent e) {
//				notifyWidgetSelected(createSelectionEvent(e));
				sendSelectionEvent(SWT.Selection);
			}

			@Override
			public void mouseDoubleClick(MouseEvent e) {
//				notifyWidgetDoubleClicked(createSelectionEvent(e));
				sendSelectionEvent(SWT.DefaultSelection);
			}
		});
	}

	@Override
	public void paintControl(PaintEvent e) {
		GC gc = e.gc;
		Rectangle bounds = getBounds();
		bounds.x = 0;
		bounds.y = 0;

		gc.setBackground(getActualBackgroundColor());
		gc.fillRectangle(bounds);

		if (alwaysShowBorder == true) {
			gc.setForeground(getDefaultBorderSwtColor());
		} else {
			gc.setForeground(getActualBorderColor());
		}
		gc.drawRectangle(0, 0, bounds.width - 1, bounds.height - 1);

		gc.setForeground(new Color(getDisplay(), 0, 0, 0));
		gc.drawImage(getImage(), //
				(bounds.width - getImage().getBounds().width) / 2, //
				(bounds.height - getImage().getBounds().width) / 2);
	}

	private Image getImage() {
		if (images.size() == 1) {
			return images.get(0);
		}

		if (setActivated == true) {
			return RichUtils.getLast(images);
		}

		int index = sPortalsToggleTween.getTweenIndex() / 20;

		if (index < images.size()) {
			return images.get(index);
		} else {
			return RichUtils.getLast(images);
		}
	}

	// TODO should respect SWT.DEFAULT values for hints
	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {
		Point superSize = super.computeSize(wHint, hHint, changed);

//		GC gc = new GC(this);
//		Point extent = gc.textExtent("A");
//		gc.dispose();
//
//		if (wHint == SWT.DEFAULT) {
//			superSize.x = DOWN_BUTTON_WIDTH + 2 * BORDER_WIDTH; // TODO images width
//		}
//
//		if (hHint == SWT.DEFAULT) {
//			superSize.y = extent.y + 2 * BORDER_WIDTH + 2 * MARGIN; // TODO images height
//		}

		if (wHint == SWT.DEFAULT) {
			superSize.x = imageWidth + 2 * BORDER_WIDTH + 2 * MARGIN; // TODO images width
		}

		if (hHint == SWT.DEFAULT) {
			superSize.y = imageHeight + 2 * BORDER_WIDTH + 2 * MARGIN; // TODO images height
		}

		return superSize;
	}

	public void addSelectionListener(SelectionListener listener) {
		checkWidget();
		if (listener == null)
			error(SWT.ERROR_NULL_ARGUMENT);
		TypedListener typedListener = new TypedListener(listener);
		addListener(SWT.Selection, typedListener);
		addListener(SWT.DefaultSelection, typedListener);
	}

	public void removeSelectionListener(SelectionListener listener) {
		checkWidget();
		if (listener == null)
			error(SWT.ERROR_NULL_ARGUMENT);
		if (eventTable == null)
			return;
		eventTable.unhook(SWT.Selection, listener);
		eventTable.unhook(SWT.DefaultSelection, listener);
	}

	public void setActivated(boolean activated) {
		this.setActivated = activated;

		redraw();
	}

}
