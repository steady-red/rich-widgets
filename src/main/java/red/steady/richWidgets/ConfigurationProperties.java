/*
 * Created on Oct 5, 2005
 */
package red.steady.richWidgets;

import java.util.Map;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Rectangle;

/**
 * @author Paul S Davenport III
 */
public interface ConfigurationProperties extends Map<Object, Object> {

	public abstract boolean getBoolean(String value_name);

	public abstract boolean getBoolean(String value_name, boolean default_value);

	public abstract String getString(String value_name);

	public abstract String getString(String value_name, String default_value);

	public abstract int getInt(String value_name);

	public abstract int getInt(String value_name, int default_value);

	public abstract void setValue(String value_name, boolean value);

	public abstract void setValue(String value_name, String value);

	public abstract void setValue(String value_name, int value);

	public abstract int[] getIntArray(String value_name);

	public abstract int[] getIntArray(String value_name, int[] default_values);

	public abstract void setValue(String value_name, int[] value);

	public abstract void setColor(String string, Color color);

	public abstract Color getColor(String string, Color default_color);

	public abstract long getLong(String string);

	public abstract long getLong(String value_name, long default_value);

	public abstract void setValue(String string, long value);

	public abstract void setFont(String value_name, Font font);

	public abstract Font getFont(String value_name, Font default_font);

	public abstract Rectangle getRectangle(String value_name);

	public abstract Rectangle getRectangle(String value_name, Rectangle default_values);

	public abstract void setValue(String value_name, Rectangle default_values);

}
