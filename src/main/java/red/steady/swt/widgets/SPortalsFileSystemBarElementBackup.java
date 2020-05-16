package red.steady.swt.widgets;

//import java.io.IOException;
//import java.io.InputStream;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//
//import org.eclipse.swt.SWT;
//import org.eclipse.swt.events.PaintEvent;
//import org.eclipse.swt.events.PaintListener;
//import org.eclipse.swt.graphics.Color;
//import org.eclipse.swt.graphics.GC;
//import org.eclipse.swt.graphics.Image;
//import org.eclipse.swt.graphics.ImageData;
//import org.eclipse.swt.graphics.Point;
//import org.eclipse.swt.graphics.Rectangle;
//import org.eclipse.swt.widgets.Canvas;
//import org.eclipse.swt.widgets.Event;
//import org.eclipse.swt.widgets.Listener;
//
//import com.redmindset.common.utils.FirstUtils;

public class SPortalsFileSystemBarElementBackup {
}
//extends Canvas implements PaintListener {
//
//	private static final int DOWN_BUTTON_SPACER = 1;
//
//	private static final int MARGIN = 1;
//
//	private static final int BORDER_WIDTH = 1;
//
//	private final static int DOWN_BUTTON_WIDTH = 15;
//
//	private final List<Boolean> _tweeningQueue;
//	private final Thread tweenThread;
//
//	private Image downButtonImage;
//	private String displayText;
//
//	private final SPortalsBreadcrumbItemListener breadcrumbItemListener;
//	private final SPortalsFileSystemBar fileSystemBarParent;
//	private final Color defaultBackgroundColor;
//	private final int defaultBackgroundRed;
//	private final int defaultBackgroundGreen;
//	private final int defaultBackgroundBlue;
//
//	private Object syncObject = new Object();
//
////	private final SPortalsToggleTween sPortalsToggleTween;
////	this.sPortalsToggleTween = new SPortalsToggleTween(this);
//
//	public SPortalsFileSystemBarElementBackup(SPortalsFileSystemBar fileSystemBarParent, //
//			String displayText, //
//			SPortalsBreadcrumbItemListener breadcrumbItemListener) {
//		super(fileSystemBarParent, SWT.NULL);
//
//		this.fileSystemBarParent = fileSystemBarParent;
//		this.displayText = displayText;
//		this.breadcrumbItemListener = breadcrumbItemListener;
//
//		downButtonImage = getImage("com/redmindset/images/arrow-minimal-down-16x16.png");
//
//		createControlActions();
//
//		this.fileSystemBarParent.addSPortalsBreadcrumbItem(this);
//
//		this.defaultBackgroundColor = getBackground();
//		defaultBackgroundRed = defaultBackgroundColor.getRed();
//		defaultBackgroundGreen = defaultBackgroundColor.getGreen();
//		defaultBackgroundBlue = defaultBackgroundColor.getBlue();
//
//		addPaintListener(this);
//
//		_tweeningQueue = new ArrayList<Boolean>();
//		tweenThread //
//				= new Thread(new Runnable() {
//					private Optional<Boolean> hovering = Optional.<Boolean>empty();
//
//					@Override
//					public void run() {
//						while (true) {
//							try {
//								if (hovering.isPresent() == false) {
//									hovering = nextInTweenQueue();
//
//									if (hovering.isPresent() == false) {
//										synchronized (syncObject) {
//											try {
//												syncObject.wait();
//											} catch (InterruptedException e) {
//												e.printStackTrace();
//											}
//										}
//									}
//								} else {
//									while (true) {
//										if (hovering.get() == true) {
//											tweenIndex += TWEEN_INCREMENT;
//											if (tweenIndex >= MAX_TWEEN_INDEX) {
//												tweenIndex = MAX_TWEEN_INDEX;
//												hovering = Optional.<Boolean>empty();
//											}
//										} else {
//											tweenIndex -= TWEEN_INCREMENT;
//											if (tweenIndex <= 0) {
//												tweenIndex = 0;
//												hovering = Optional.<Boolean>empty();
//											}
//										}
//
//										if (isDisposed() == false) {
//											getDisplay().syncExec(new Runnable() {
//												@Override
//												public void run() {
//													if (SPortalsFileSystemBarElementBackup.this.isDisposed() == false) {
//														SPortalsFileSystemBarElementBackup.this.redraw();
//													}
//												}
//											});
//										}
//
//										if (hovering.isPresent() == false) {
//											break;
//										}
//										FirstUtils.sleep(20);
//									}
//								}
//							} catch (Exception e) {
//								e.printStackTrace();
//							}
//						}
//					}
//				});
//
//		tweenThread.setDaemon(true);
//		tweenThread.start();
//	}
//
//	@Override
//	public void dispose() {
//		tweenThread.stop();
//	}
//
//	private int tweenIndex = 0;
//	private static final int MAX_TWEEN_INDEX = 100;
//	private static final int TWEEN_INCREMENT = 20;
//
//	private int getTween(int first, int second) {
//		float floatFirst = first;
//		float floatSecond = second;
//
//		float result //
//				= floatFirst * (((float) MAX_TWEEN_INDEX - (float) tweenIndex) / MAX_TWEEN_INDEX) //
//						+ floatSecond * ((float) tweenIndex / (float) MAX_TWEEN_INDEX);
//
//		return (int) result;
//	}
//
//	private void addToTweenQueue(boolean hovering) {
//		synchronized (_tweeningQueue) {
//			_tweeningQueue.add(hovering);
//
//			synchronized (syncObject) {
//				syncObject.notify();
//			}
//		}
//	}
//
//	private Optional<Boolean> nextInTweenQueue() {
//		synchronized (_tweeningQueue) {
//			if (_tweeningQueue.size() == 0) {
//				return Optional.<Boolean>empty();
//			}
//			return Optional.<Boolean>of(FirstUtils.removeLast(_tweeningQueue));
//		}
//	}
//
//	private Color getActualBackgroundColor() {
//		return new Color(getDisplay(), //
//				getTween(defaultBackgroundRed, 192), //
//				getTween(defaultBackgroundGreen, 229), //
//				getTween(defaultBackgroundBlue, 255));
//	}
//
//	private Color getActualBorderColor() {
//		return new Color(getDisplay(), //
//				getTween(defaultBackgroundRed, 157), //
//				getTween(defaultBackgroundGreen, 197), //
//				getTween(defaultBackgroundBlue, 228));
//	}
//
//	private void createControlActions() {
//		addListener(SWT.MouseEnter, new Listener() {
//			@Override
//			public void handleEvent(Event e) {
//				addToTweenQueue(true);
//			}
//		});
//
//		addListener(SWT.MouseExit, new Listener() {
//			@Override
//			public void handleEvent(Event e) {
//				addToTweenQueue(false);
//			}
//		});
//
//		addListener(SWT.MouseDown, new Listener() {
//			@Override
//			public void handleEvent(Event event) {
//				if (event.x < getBounds().width - DOWN_BUTTON_WIDTH) {
//					breadcrumbItemListener.selected();
//				} else {
//					String[] dropdownValues //
//					= breadcrumbItemListener.dropdownSelected(SPortalsFileSystemBarElementBackup.this);
//
////					Table 
//				}
//			}
//		});
//
//	}
//
//	private Image getImage(String imageName) {
//		try (InputStream inputStream = SPortalsFileSystemBarElementBackup.class.getClassLoader()
//				.getResourceAsStream(imageName);) {
//
//			ImageData imageData = new ImageData(inputStream);
//			Image image = new Image(getDisplay(), imageData);
//
//			return image;
//		} catch (IOException e) {
//			throw new RuntimeException("Could not load image: " + imageName, e);
//		}
//	}
//
//	@Override
//	public void paintControl(PaintEvent e) {
//		GC gc = e.gc;
//		Rectangle bounds = getBounds();
//		bounds.x = 0;
//		bounds.y = 0;
//
//		int buttonLeft = bounds.width - DOWN_BUTTON_WIDTH;
//
//		gc.setBackground(getActualBackgroundColor());
//		gc.fillRectangle(bounds);
//
//		gc.setForeground(getActualBorderColor());
//		gc.drawRectangle(buttonLeft, bounds.y, bounds.width - buttonLeft - 1, bounds.height - 1);
//		gc.drawRectangle(bounds.x, bounds.y, buttonLeft, bounds.height - 1);
//
//		Point extent = gc.textExtent(displayText);
//
//		gc.setForeground(new Color(getDisplay(), 0, 0, 0));
//		gc.drawText(//
//				displayText, //
//				(buttonLeft + DOWN_BUTTON_SPACER - extent.x) / 2, //
//				(bounds.height - extent.y) / 2, //
//				true);
//
//		gc.drawImage(downButtonImage, //
//				buttonLeft + (DOWN_BUTTON_WIDTH - downButtonImage.getBounds().width) / 2 - 1, //
//				bounds.y + (bounds.height - downButtonImage.getBounds().width) / 2);
//	}
//
//	@Override
//	public Point computeSize(int wHint, int hHint, boolean changed) {
//		GC gc = new GC(this);
//		Point extent = gc.textExtent(displayText);
//		gc.dispose();
//
//		int width = DOWN_BUTTON_WIDTH + DOWN_BUTTON_SPACER + extent.x + 2 * BORDER_WIDTH + 2 * MARGIN;
//		int height = extent.y + 2 * BORDER_WIDTH + 2 * MARGIN;
//
//		return new Point(width, height);
//	}
//
//	public String getDisplayText() {
//		return displayText;
//	}
//
//}
