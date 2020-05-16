package red.steady.swt.widgets;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

import lombok.Builder;

public class AnimatedGifCanvas2 extends Canvas {

	private static Composite checkParent(Composite parent) {
		if (parent == null) {
			throw new RuntimeException("Parent can not be null");
		}

		return parent;
	}

	private final List<Integer> imagesDelay;
	private final List<Image> imagesList;
	private int imageDisplayIndex;
	private Thread thread;
	protected boolean stillRunning;
	private int slowDown;

	@Builder
	private AnimatedGifCanvas2(//
			Composite parent, //
			int style, //
			int slowDown, //
			InputStream animatedGifInputStream, boolean doNotStartByDefault) {
		super(checkParent(parent), style);

		// TODO: check is not null

		this.slowDown = slowDown;

		this.imagesList = new ArrayList<Image>();
		this.imagesDelay = new ArrayList<Integer>();

		ImageLoader loader = new ImageLoader();
		loader.load(animatedGifInputStream);

		for (int index = 0; index < loader.data.length; index++) {
			this.imagesList.add(new Image(getDisplay(), loader.data[index]));
			this.imagesDelay.add(loader.data[index].delayTime);
		}

		addPaintListener(new PaintListener() {
			@Override
			public void paintControl(PaintEvent event) {
				event.gc.drawImage(imagesList.get(imageDisplayIndex), 0, 0);
			}
		});

		if (doNotStartByDefault == false) {
			start();
		}
	}

	public void stop() {
		stillRunning = false;
	}

	public void start() {
		thread = new Thread() {
			@Override
			public void run() {
				while (stillRunning = true) {
					long currentTime = System.currentTimeMillis();
					int delayTime = imagesDelay.get(imageDisplayIndex) + slowDown;
//                    while (currentTime + delayTime * 10 > System.currentTimeMillis()) {
					while (currentTime + delayTime * 100 > System.currentTimeMillis()) {
						// Wait till the delay time has passed
//                        try {
//                            Thread.sleep(1);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
					}

					if (isDisposed() == true) {
						return;
					}

					getDisplay().asyncExec(new Runnable() {
						@Override
						public void run() {
							// Increase the variable holding the frame number
							imageDisplayIndex = (imageDisplayIndex + 1) % imagesList.size();

							// Draw the new data onto the image
							if (isDisposed() == false) {
								redraw();
							}
						}
					});
				}
			}
		};
		thread.setDaemon(true);
		thread.start();
	}

	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {
		Point point = new Point(imagesList.get(0).getBounds().width, imagesList.get(0).getBounds().height);

		if (wHint != SWT.DEFAULT) {
			point.x = wHint;
		}

		if (hHint != SWT.DEFAULT) {
			point.y = hHint;
		}

		System.out.println(point);
		return point;
	}

}
