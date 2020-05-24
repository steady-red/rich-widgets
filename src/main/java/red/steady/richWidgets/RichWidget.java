package red.steady.richWidgets;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;

import red.steady.richWidgets.application.RichApplication;

public interface RichWidget {

	void setBackground(int red, int green, int blue);

	Color createColor(int red, int green, int blue);

	void empathizeBorderForTesting(int width, int red, int green, int blue);

	RichApplication getRichApplication();

	//

	default boolean hasUiPropertyValue(String propertyName) {
		return getRichApplication().getConfigurationValues().containsKey(propertyName);
	}

	//

	default int getUiPropertyIntValue(String propertyName, int defaultValue) {
		return getRichApplication().getConfigurationValues().getInt(propertyName, defaultValue);
	}

	default int getUiPropertyIntValue(String propertyName) {
		return getRichApplication().getConfigurationValues().getInt(propertyName);
	}

	default void setUiPropertyValue(String propertyName, int value) {
		getRichApplication().getConfigurationValues().setValue(propertyName, value);
	}

	//

	default int[] getUiPropertyIntArrayValue(String propertyName, int[] defaultValue) {
		return getRichApplication().getConfigurationValues().getIntArray(propertyName, defaultValue);
	}

	default int[] getUiPropertyIntArrayValue(String propertyName) {
		return getRichApplication().getConfigurationValues().getIntArray(propertyName);
	}

	default void setUiPropertyValue(String propertyName, int[] value) {
		getRichApplication().getConfigurationValues().setValue(propertyName, value);
	}

	//

	default void setUiPropertyValue(String propertyName, Rectangle value) {
		getRichApplication().getConfigurationValues().setValue(propertyName, value);
	}

	default Rectangle getUiPropertyRectangleValue(String propertyName, Rectangle defaultValue) {
		return getRichApplication().getConfigurationValues().getRectangle(propertyName, defaultValue);
	}

	default Rectangle getUiPropertyRectangleValue(String propertyName) {
		return getRichApplication().getConfigurationValues().getRectangle(propertyName);
	}

	//

	default void setUiPropertyValue(String propertyName, boolean value) {
		getRichApplication().getConfigurationValues().setValue(propertyName, value);
	}

	default boolean getUiPropertyBooleanValue(String propertyName, boolean defaultValue) {
		return getRichApplication().getConfigurationValues().getBoolean(propertyName, defaultValue);
	}

	default boolean getUiPropertyBooleanValue(String propertyName) {
		return getRichApplication().getConfigurationValues().getBoolean(propertyName);
	}

	default Image createImageFromClasspath(Device device, String imageInClassPath) {
		try (InputStream image_input_stream = getClass().getClassLoader().getResourceAsStream(imageInClassPath);) {

			return new Image(device, image_input_stream);
		} catch (IOException e) {
			e.printStackTrace();

			throw new RuntimeException("Could not createImage: " + imageInClassPath, e);
		}
	}

}
