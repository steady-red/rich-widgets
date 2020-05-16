package red.steady.swt.widgets;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

import lombok.Builder;

public class LayeredCanvas extends Canvas implements PaintListener {

	private static Composite checkParent(Composite parent) {
		if (parent == null) {
			throw new RuntimeException("Parent can not be null");
		}

		return parent;
	}

	private final List<Image> imagesList;
	private final List<Boolean> imagesVisibilityList;
	private final Rectangle layerBounds;

	@Builder
	private LayeredCanvas(//
			Composite parent, //
			int style, Rectangle layerBounds) {
		super(checkParent(parent), style);

		this.imagesList = new ArrayList<Image>();
		this.imagesVisibilityList = new ArrayList<Boolean>();
		this.layerBounds = layerBounds;

		addPaintListener(this);
	}

	public void showLayer(int layerIndex, boolean show) {
		imagesVisibilityList.set(layerIndex, show);
	}

	public Rectangle getlayerBounds() {
		return layerBounds;
	}

	public GC addLayer() {
//		ImageData imageData = new ImageData(width, height, depth, palette)
		Image image = new Image(getDisplay(), getlayerBounds());

		GC imageGC = new GC(image);
		imageGC.fillRectangle(layerBounds);

		imagesList.add(image);
		imagesVisibilityList.add(true);

		return imageGC;
	}

	@Override
	public void paintControl(PaintEvent paintEvent) {
		GC gc = paintEvent.gc;

		for (Image image : imagesList) {
			gc.drawImage(image, 0, 0);
		}
	}
}
