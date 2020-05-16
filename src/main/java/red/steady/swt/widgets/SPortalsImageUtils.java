package red.steady.swt.widgets;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;

import red.steady.richWidgets.utils.RichUtils;

public class SPortalsImageUtils {

	// "com/redmindset/images/resize-20x20.png"

	public static enum TransparencyCorner {
		TopLeft, TopRight, BottomLeft, BottomRight;
	}

	public static Image getClassImage(Display display, String imagePath) {
		return getClassImage(display, imagePath, (TransparencyCorner) null);
	}

	public static Image getClassImage(Display display, String imagePath, TransparencyCorner transparencyCorner) {
		try (InputStream inputStream = SPortalsImageUtils.class.getClassLoader().getResourceAsStream(imagePath);) {

			ImageData imageData = new ImageData(inputStream);
			if (transparencyCorner != null) {
				switch (transparencyCorner) {
				case TopLeft: {
					imageData.transparentPixel = imageData.getPixel(0, 0);
					break;
				}
				case TopRight: {
					imageData.transparentPixel = imageData.getPixel(imageData.width - 1, 0);
					break;
				}
				case BottomLeft: {
					imageData.transparentPixel = imageData.getPixel(0, imageData.height - 1);
					break;
				}
				case BottomRight: {
					imageData.transparentPixel = imageData.getPixel(imageData.width - 1, imageData.height - 1);
					break;
				}
				default:
					RichUtils.throwUnsupportedEnumException(transparencyCorner);
				}
			}
			Image image = new Image(display, imageData);

			return image;
		} catch (IOException e) {
			throw new RuntimeException("Failed to load image from classpath: " + imagePath, e);
		}

	}
}
