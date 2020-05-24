/*
 * Created on Oct 17, 2003
 */
package red.steady.richWidgets.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

import red.steady.richWidgets.ConfigurationProperties;

public class PropertiesFile extends Properties implements ConfigurationProperties {

	private static final long serialVersionUID = 1L;

	private File file;

	public PropertiesFile() {
		super();
	}

	public PropertiesFile(Properties defaults) {
		super(defaults);
	}

	public PropertiesFile(String file_name) {
		this(new File(file_name));
	}

	public PropertiesFile(String file_name, boolean create) {
		this(new File(file_name), create);
	}

	public PropertiesFile(File a_file) {
		this(a_file, false);
	}

	public PropertiesFile(File a_file, boolean create) {
		super();

		if (create == true) {

			if (a_file.exists() == false) {
				try {
					a_file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		loadFromFile(a_file);
	}

	public String getFileName() {
		if (file != null) {
			return file.getAbsolutePath();
		} else {
			return "[Undefined]";
		}
	}

	public void loadFromFile(String file_name) {
		loadFromFile(new File(file_name));
	}

	public void loadFromFile(File a_file) {
		this.file = a_file;

		clear();

		try {
			FileInputStream input_stream = new FileInputStream(a_file);

			try {
				this.load(input_stream);
			} finally {
				try {
					input_stream.close();
				} catch (IOException e) {
					e.printStackTrace(); // to do:???
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException(this.getClass().getName() + ".LoadFromFile() failed for file, \""
					+ a_file.getAbsolutePath() + "\"" + e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(this.getClass().getName() + ".LoadFromFile() failed for file, \""
					+ a_file.getAbsolutePath() + "\"" + e);
		}
	}

	public void saveToFile() {
		saveToFile(file);
	}

	public void saveToFile(File a_file) {
		saveToFile(a_file, "PropertiesFile");
	}

	public void saveToFile(File a_file, String header_string) {
		file = a_file;
		try {
			FileOutputStream output_stream = new FileOutputStream(a_file);

			try {
				this.store(output_stream, header_string);
			} finally {
				try {
					output_stream.close();
				} catch (IOException e) {
					e.printStackTrace(); // to do:???
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException(this.getClass().getName() + ".SaveToFile() failed\n" + e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(this.getClass().getName() + ".SaveToFile() failed\n" + e.getMessage());
		}
	}

	@Override
	public boolean getBoolean(String value_name) {
		boolean result = getBoolean(value_name, false);
		return result;
	}

	@Override
	public boolean getBoolean(String value_name, boolean default_value) {
		String string_value = getProperty(value_name, String.valueOf(default_value));

		return string_value.equalsIgnoreCase("true");
	}

	@Override
	public String getString(String value_name, String default_value) {
		String string_value = getProperty(value_name, default_value);

		return string_value;
	}

	@Override
	public String getString(String value_name) {
		return getString(value_name, "");
	}

	@Override
	public int getInt(String value_name, int default_value) {
		String string_value = getProperty(value_name, String.valueOf(default_value));

		return Integer.parseInt(string_value);
	}

	@Override
	public int getInt(String value_name) {
		return getInt(value_name, 0);
	}

	@Override
	public long getLong(String value_name) {
		return getLong(value_name, 0);
	}

	@Override
	public long getLong(String value_name, long default_value) {
		String string_value = getProperty(value_name, String.valueOf(default_value));

		return Long.parseLong(string_value);
	}

	@Override
	public void setValue(String value_name, long value) {
		setProperty(value_name, String.valueOf(value));
	}

	@Override
	public void setValue(String value_name, boolean value) {
		setProperty(value_name, String.valueOf(value));
	}

	@Override
	public void setValue(String value_name, String value) {
		setProperty(value_name, String.valueOf(value));
	}

	@Override
	public void setValue(String value_name, int value) {
		setProperty(value_name, String.valueOf(value));
	}

	@Override
	public int[] getIntArray(String value_name) {
		return getIntArray(value_name, new int[] {});
	}

	@Override
	public int[] getIntArray(String value_name, int[] default_values) {
		String property_value = getString(value_name);

		if (property_value.trim().equals("") == true) {
			return default_values;
		}

		String[] strings = RichUtils.splitParts(property_value, ",");
		int[] result = new int[strings.length];

		for (int index = 0; index < strings.length; index++) {
			String string = strings[index];

			result[index] = Integer.parseInt(string);
		}

		return result;
	}

	@Override
	public void setValue(String value_name, int[] value) {
		String property_value = "";

		for (int index = 0; index < value.length; index++) {
			if (property_value.equals("") == false) {
				property_value += ",";
			}

			property_value += String.valueOf(value[index]);
		}

		setValue(value_name, property_value);
	}

	@Override
	public void setColor(String value_name, Color color) {
		String color_string = color.getRed() + ", " + color.getGreen() + ", " + color.getBlue();

		setValue(value_name, color_string);
	}

	@Override
	public Color getColor(String value_name, Color default_color) {
		String color_string = getString(value_name);

		if (color_string.equals("") == true) {
			return default_color;
		} else {
			String[] colors = color_string.split(",");

			RichUtils.checkTrue(colors.length == 3);

			return new Color(Display.getDefault(), Integer.parseInt(colors[0].trim()),
					Integer.parseInt(colors[1].trim()), Integer.parseInt(colors[2].trim()));
		}
	}

	@Override
	public void setFont(String value_name, Font font) {
		String font_string = "";

		if (font != null) {
			font_string = font.getFontData()[0].getName() + ", " //
					+ font.getFontData()[0].getHeight() //
					+ ", " + String.valueOf(((font.getFontData()[0].getStyle() & SWT.ITALIC) != 0)) + ", "
					+ String.valueOf(((font.getFontData()[0].getStyle() & SWT.BOLD) != 0));
		}

		setValue(value_name, font_string);
	}

	@Override
	public Font getFont(String value_name, Font default_font) {
		try {
			String font_string = getString(value_name);

			if (font_string.trim().equals("") == true) {
				return default_font;
			} else {
				String[] value_strings = font_string.split(",");
				RichUtils.checkTrue(value_strings.length == 4);

				String font_name = value_strings[0];

				int font_height = Integer.parseInt(value_strings[1]);

				boolean is_italic = Boolean.parseBoolean(value_strings[2]);

				boolean is_bold = Boolean.parseBoolean(value_strings[3]);

//				int font_style = 0;
//				if (is_bold == true) {
//					font_style |= SWT.BOLD;
//				}
//				if (is_italic == true) {
//					font_style |= SWT.ITALIC;
//				}

				return new Font(Display.getDefault(), font_name, font_height,
						(is_bold ? SWT.BOLD : 0) | (is_italic ? SWT.ITALIC : 0));
			}
		} catch (Throwable e) {
			return default_font;
		}
	}

	public String getFileAsString() {
		if (file != null) {
			String result = "";

			try {
				FileInputStream input_stream = new FileInputStream(file);
				BufferedReader in = new BufferedReader(new InputStreamReader(input_stream, "8859_1"));

				try {
					while (true) {
						// Get next line
						String line = in.readLine();
						if (line == null) {
							return result;
						}

						result += line + "\n";
					}
				} finally {
					try {
						input_stream.close();
					} catch (IOException e) {
						e.printStackTrace(); // to do:???
					}
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				throw new RuntimeException(this.getClass().getName() + ".getFileAsString() failed for file, \""
						+ file.getAbsolutePath() + "\"" + e);
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException(this.getClass().getName() + ".getFileAsString() failed for file, \""
						+ file.getAbsolutePath() + "\"" + e);
			}
		} else {
			return "";
		}
	}

	@Override
	public Rectangle getRectangle(String value_name) {
		return getRectangle(value_name, new Rectangle(0, 0, 0, 0));
	}

	@Override
	public Rectangle getRectangle(String value_name, Rectangle defaultValue) {
		String valueString = getProperty(value_name, getStringValue(defaultValue));

		return createRectangleFromValueString(valueString);
	}

	@Override
	public void setValue(String value_name, Rectangle value) {
		setValue(value_name, getStringValue(value));
	}

	public static Rectangle createRectangleFromValueString(String valueString) {
		String[] parts = valueString.split(",", -1);

		int x = Integer.parseInt(parts[0]);
		int y = Integer.parseInt(parts[1]);
		int width = Integer.parseInt(parts[2]);
		int height = Integer.parseInt(parts[3]);

		return new Rectangle(x, y, width, height);
	}

	public static String getStringValue(Rectangle rectangle) {
		return "" + rectangle.x + "," + rectangle.y + "," + rectangle.width + "," + rectangle.height;
	}

}
