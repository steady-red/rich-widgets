/*******************************************************************************
 * Copyright (c) 2018 Laurent CARON
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors: Laurent CARON (laurent.caron at gmail dot com) - Initial
 * implementation and API
 *******************************************************************************/
package red.steady.richWidgets;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Widget;

/**
 * Instances of this class are selectable user interface objects that represent
 * a range of positive, numeric values. It is like an horizontal slider
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>(None)</dd>
 * <dt><b>Events:</b></dt>
 * <dd>Selection</dd>
 * </dl>
 */
public class RichSlider extends Canvas {

	private static final int H_MARGIN = 5;
//	private static final int BAR_HEIGHT = 12;
	private static final int BAR_HEIGHT = 5;
//	private static final int SELECTOR_WIDTH = 78;
//	private static final int SELECTOR_HEIGHT = 32;
	private static final int SELECTOR_WIDTH = 40; // TODO: this should be base on width of text of maximum
	private static final int SELECTOR_HEIGHT = 20;

	private final RichSliderColorScheme nebulaSliderColorScheme;

	private int minimum;
	private int maximum;
	private int value;
	private final List<SelectionListener> selectionListeners;
	private final Font textFont;

	private boolean moving = false;
	private int previousX;

	/**
	 * Constructs a new instance of this class given its parent and a style value
	 * describing its behavior and appearance.
	 * <p>
	 * The style value is either one of the style constants defined in class
	 * <code>SWT</code> which is applicable to instances of this class, or must be
	 * built by <em>bitwise OR</em>'ing together (that is, using the
	 * <code>int</code> "|" operator) two or more of those <code>SWT</code> style
	 * constants. The class description lists the style constants that are
	 * applicable to the class. Style bits are also inherited from superclasses.
	 * </p>
	 *
	 * @param parent a composite control which will be the parent of the new
	 *               instance (cannot be null)
	 * @param style  the style of control to construct
	 *
	 * @exception IllegalArgumentException
	 *                                     <ul>
	 *                                     <li>ERROR_NULL_ARGUMENT - if the parent
	 *                                     is null</li>
	 *                                     </ul>
	 * @exception SWTException
	 *                                     <ul>
	 *                                     <li>ERROR_THREAD_INVALID_ACCESS - if not
	 *                                     called from the thread that created the
	 *                                     parent</li>
	 *                                     <li>ERROR_INVALID_SUBCLASS - if this
	 *                                     class is not an allowed subclass</li>
	 *                                     </ul>
	 *
	 * @see Widget#getStyle()
	 */
	public RichSlider(Composite parent, int style) {
		this(parent, style, (RichSliderColorScheme) null);
	}

	public RichSlider(Composite parent, int style, RichSliderColorScheme nebulaSliderColorScheme) {
		super(parent, checkStyle(style) | SWT.DOUBLE_BUFFERED);

		this.nebulaSliderColorScheme //
				= ((nebulaSliderColorScheme == null) ? RichSliderColorScheme.builder().control(this).build()
						: nebulaSliderColorScheme);

		selectionListeners = new ArrayList<>();
		minimum = Integer.MIN_VALUE;
		maximum = Integer.MAX_VALUE;
		value = 0;

		textFont = createTextFont();

		addPaintListener(e -> {
			paintControl(e.gc);
		});
		addMouseListeners();
	}

	private static int checkStyle(final int style) {
		if ((style & SWT.BORDER) != 0) {
			return style & ~SWT.BORDER;
		}
		return 0;
	}

	private Font createTextFont() {
		final FontData fontData = getFont().getFontData()[0];
		final Font newFont = new Font(getDisplay(), fontData.getName(), Math.max(fontData.getHeight(), 14), SWT.BOLD);
		addDisposeListener(e -> {
			if (!newFont.isDisposed()) {
				newFont.dispose();
			}
		});
		return newFont;
	}

	private void paintControl(GC gc) {
		gc.setAdvanced(true);
		gc.setAntialias(SWT.ON);

		drawBar(gc);
		drawSelectionPart(gc);
		drawSelector(gc);
	}

	private void drawBar(GC gc) {
		final Rectangle rect = getClientArea();
		gc.setForeground(nebulaSliderColorScheme.getBarBorderColor());
		gc.setBackground(nebulaSliderColorScheme.getBarInsideColor());

		final int x = H_MARGIN + SELECTOR_WIDTH / 2;
		final int y = (rect.height - BAR_HEIGHT) / 2;
		final int width = rect.width - H_MARGIN * 2 - SELECTOR_WIDTH;

		gc.fillRoundRectangle(x, y, width, BAR_HEIGHT, BAR_HEIGHT, BAR_HEIGHT);
		gc.drawRoundRectangle(x, y, width, BAR_HEIGHT, BAR_HEIGHT, BAR_HEIGHT);
	}

	private void drawSelectionPart(GC gc) {
		final Rectangle rect = getClientArea();
		gc.setForeground(nebulaSliderColorScheme.getBarBorderColor());
		gc.setBackground(nebulaSliderColorScheme.getBarSelectionColor());

		final int x = H_MARGIN + SELECTOR_WIDTH / 2;
		final int y = (rect.height - BAR_HEIGHT) / 2;

		final int width = getPosition();

		gc.fillRoundRectangle(x, y, width, BAR_HEIGHT, BAR_HEIGHT, BAR_HEIGHT);
		gc.drawRoundRectangle(x, y, width, BAR_HEIGHT, BAR_HEIGHT, BAR_HEIGHT);
	}

	private int getPosition() {
		final int originalWidth = getClientArea().width - H_MARGIN * 2 - SELECTOR_WIDTH;
		final float coeff = value * 1f / (maximum - minimum);
		final int position = (int) (coeff * originalWidth);
		return position;
	}

	private void drawSelector(GC gc) {
		final Rectangle rect = getClientArea();
		gc.setForeground(nebulaSliderColorScheme.getSelectorColorBorder());
		gc.setBackground(nebulaSliderColorScheme.getSelectorColor());

		final int position = getPosition();
		final int y = (rect.height - SELECTOR_HEIGHT) / 2;

		// Draw the body
		gc.fillRoundRectangle(H_MARGIN + position, y, SELECTOR_WIDTH, SELECTOR_HEIGHT, SELECTOR_HEIGHT,
				SELECTOR_HEIGHT);
		gc.drawRoundRectangle(H_MARGIN + position, y, SELECTOR_WIDTH, SELECTOR_HEIGHT, SELECTOR_HEIGHT,
				SELECTOR_HEIGHT);

//		// Draw the arrows
//		gc.setForeground(arrowColor);
//		gc.setLineWidth(3);
//		final int baseY = y + SELECTOR_HEIGHT / 2;
//		gc.drawLine(H_MARGIN + position + 10, baseY, H_MARGIN + position + 17, baseY - 7);
//		gc.drawLine(H_MARGIN + position + 10, baseY, H_MARGIN + position + 17, baseY + 7);
//
//		gc.drawLine(H_MARGIN + position + SELECTOR_WIDTH - 10, baseY, H_MARGIN + position + SELECTOR_WIDTH - 17,
//				baseY - 7);
//		gc.drawLine(H_MARGIN + position + SELECTOR_WIDTH - 10, baseY, H_MARGIN + position + SELECTOR_WIDTH - 17,
//				baseY + 7);

		// And the value
		gc.setForeground(nebulaSliderColorScheme.getSelectorTextColor());
		gc.setFont(textFont);
		final String valueAsString = String.valueOf(value);
		final Point textSize = gc.textExtent(valueAsString);

		final int xText = H_MARGIN + position + SELECTOR_WIDTH / 2;
		gc.drawText(valueAsString, xText - textSize.x / 2, y + 2, true);
	}

	private void addMouseListeners() {
		addListener(SWT.MouseDown, e -> {
			final int position = getPosition();
			final int y = (getClientArea().height - SELECTOR_HEIGHT) / 2;
			final Rectangle pillRectangle = new Rectangle(position + H_MARGIN, y, SELECTOR_WIDTH, SELECTOR_HEIGHT);

			final Rectangle bounds = getClientArea();
			final int barX = H_MARGIN + SELECTOR_WIDTH / 2;
			final int barY = (bounds.height - BAR_HEIGHT) / 2;
			final int barWidth = bounds.width - H_MARGIN * 2 - SELECTOR_WIDTH;
			final Rectangle barRectangle = new Rectangle(barX, bounds.y, barWidth, bounds.height);

			if (pillRectangle.contains(e.x, e.y) == true) {
				moving = true;
				System.out.println("moving!!!!!!!!!!!!");
				previousX = getDisplay().getCursorLocation().x;
			} else if (barRectangle.contains(e.x, e.y) == true) {
				System.out.println("Clicking bar!!!!!!!!!!!!");

				value = (maximum - minimum) * (e.x - barX) / barWidth;
				previousX = getDisplay().getCursorLocation().x;
				fireSelectionEvent();
				redraw();
				update();
			} else {
				System.out.println("Click missed everything!!!!!!!!!!!!");
			}
		});

		addListener(SWT.MouseUp, e -> {
			moving = false;
		});

		addListener(SWT.MouseMove, e -> {
			if (!moving) {
				return;
			}

			final int originalWidth = getClientArea().width - H_MARGIN * 2 - SELECTOR_WIDTH; // This is wrong
//			final float step = (maximum - minimum) / originalWidth;
			final float step = originalWidth / (maximum - minimum); // Still wrong

			final int deltaX = previousX - getDisplay().getCursorLocation().x;

			System.out.println("(maximum - minimum): " + (maximum - minimum) + ", originalWidth: " + originalWidth
					+ ", deltaX: " + deltaX + ",step: " + step + ", deltaX * step * 2: " + deltaX * step * 2);

			value -= deltaX * step * 2;
			System.out.println("value: " + value);
			if (value < minimum) {
				value = minimum;
			}
			if (value > maximum) {
				value = maximum;
			}
			previousX = getDisplay().getCursorLocation().x;
			fireSelectionEvent();
			redraw();
			update();
		});
	}

	private void fireSelectionEvent() {
		final Event event = new Event();
		event.widget = this;
		event.display = getDisplay();
		event.type = SWT.Selection;
		for (final SelectionListener selectionListener : selectionListeners) {
			selectionListener.widgetSelected(new SelectionEvent(event));
		}
	}

	/**
	 * @see org.eclipse.swt.widgets.Widget#addListener(int,
	 *      org.eclipse.swt.widgets.Listener)
	 */
	@Override
	public void addListener(int eventType, Listener listener) {
		if (eventType == SWT.Selection) {
			selectionListeners.add(new SelectionListener() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					widgetSelection(e);
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelection(e);
				}

				private void widgetSelection(SelectionEvent e) {
					final Event event = new Event();
					event.widget = RichSlider.this;
					event.display = getDisplay();
					event.type = SWT.Selection;
					listener.handleEvent(event);
				}
			});
			return;
		}
		super.addListener(eventType, listener);
	}

	/**
	 * Adds the listener to the collection of listeners who will be notified when
	 * the control is selected by the user, by sending it one of the messages
	 * defined in the <code>SelectionListener</code> interface.
	 * <p>
	 * <code>widgetDefaultSelected</code> is not called.
	 * </p>
	 *
	 * @param listener the listener which should be notified when the control is
	 *                 selected by the user,
	 *
	 * @exception IllegalArgumentException
	 *                                     <ul>
	 *                                     <li>ERROR_NULL_ARGUMENT - if the listener
	 *                                     is null</li>
	 *                                     </ul>
	 * @exception SWTException
	 *                                     <ul>
	 *                                     <li>ERROR_WIDGET_DISPOSED - if the
	 *                                     receiver has been disposed</li>
	 *                                     <li>ERROR_THREAD_INVALID_ACCESS - if not
	 *                                     called from the thread that created the
	 *                                     receiver</li>
	 *                                     </ul>
	 *
	 * @see SelectionListener
	 * @see #removeSelectionListener
	 * @see SelectionEvent
	 */
	public void addSelectionListener(final SelectionListener listener) {
		checkWidget();
		if (listener == null) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}
		selectionListeners.add(listener);
	}

	/**
	 * @see org.eclipse.swt.widgets.Control#computeSize(int, int, boolean)
	 */
	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {
//		return new Point(Math.max(300, wHint), Math.max(40, hHint));
		return new Point(Math.max(300, wHint), Math.max(SELECTOR_HEIGHT, hHint));
	}

	/**
	 * Removes the listener from the collection of listeners who will be notified
	 * when the control is selected by the user.
	 *
	 * @param listener the listener which should no longer be notified
	 *
	 * @exception IllegalArgumentException
	 *                                     <ul>
	 *                                     <li>ERROR_NULL_ARGUMENT - if the listener
	 *                                     is null</li>
	 *                                     </ul>
	 * @exception SWTException
	 *                                     <ul>
	 *                                     <li>ERROR_WIDGET_DISPOSED - if the
	 *                                     receiver has been disposed</li>
	 *                                     <li>ERROR_THREAD_INVALID_ACCESS - if not
	 *                                     called from the thread that created the
	 *                                     receiver</li>
	 *                                     </ul>
	 *
	 * @see SelectionListener
	 * @see #addSelectionListener
	 */
	public void removeSelectionListener(final SelectionListener listener) {
		checkWidget();
		if (listener == null) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}
		selectionListeners.remove(listener);
	}

	// ----------------------- Getters & Setters

	/**
	 * Returns the minimum value which the receiver will allow.
	 *
	 * @return the minimum
	 *
	 * @exception SWTException
	 *                         <ul>
	 *                         <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                         disposed</li>
	 *                         <li>ERROR_THREAD_INVALID_ACCESS - if not called from
	 *                         the thread that created the receiver</li>
	 *                         </ul>
	 */
	public int getMinimum() {
		checkWidget();
		return minimum;
	}

	/**
	 * Sets the minimum value. If this value is greater than the maximum, an
	 * exception is thrown
	 *
	 * @param value the new minimum
	 *
	 * @exception SWTException
	 *                         <ul>
	 *                         <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                         disposed</li>
	 *                         <li>ERROR_THREAD_INVALID_ACCESS - if not called from
	 *                         the thread that created the receiver</li>
	 *                         </ul>
	 */
	public void setMinimum(int minimum) {
		checkWidget();
		if (minimum > maximum) {
			SWT.error(SWT.ERROR_INVALID_ARGUMENT, new IllegalArgumentException(
					String.format("Value %d is greater than the maximum value (%d)", minimum, maximum)));
		}
		this.minimum = minimum;
		redraw();
		update();
	}

	/**
	 * Returns the maximum value which the receiver will allow.
	 *
	 * @return the maximum
	 *
	 * @exception SWTException
	 *                         <ul>
	 *                         <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                         disposed</li>
	 *                         <li>ERROR_THREAD_INVALID_ACCESS - if not called from
	 *                         the thread that created the receiver</li>
	 *                         </ul>
	 */
	public int getMaximum() {
		checkWidget();
		return maximum;
	}

	/**
	 * Sets the maximum value. If this value is lower than the minimum, an exception
	 * is thrown
	 *
	 * @param value the new minimum
	 *
	 * @exception SWTException
	 *                         <ul>
	 *                         <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                         disposed</li>
	 *                         <li>ERROR_THREAD_INVALID_ACCESS - if not called from
	 *                         the thread that created the receiver</li>
	 *                         </ul>
	 */
	public void setMaximum(int maximum) {
		checkWidget();
		if (maximum < minimum) {
			SWT.error(SWT.ERROR_INVALID_ARGUMENT, new IllegalArgumentException(
					String.format("Value %d is lower than the minimum value (%d)", maximum, minimum)));
		}
		this.maximum = maximum;
		redraw();
		update();
	}

	/**
	 * Returns the receiver's value.
	 *
	 * @return the selection
	 *
	 * @exception SWTException
	 *                         <ul>
	 *                         <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                         disposed</li>
	 *                         <li>ERROR_THREAD_INVALID_ACCESS - if not called from
	 *                         the thread that created the receiver</li>
	 *                         </ul>
	 */
	public int getValue() {
		checkWidget();
		return value;
	}

	/**
	 * Sets the receiver's value. If the value is lower to minimum or greater than
	 * the maximum, an exception is thrown
	 *
	 * @param value the new selection (must be zero or greater)
	 *
	 * @exception SWTException
	 *                         <ul>
	 *                         <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                         disposed</li>
	 *                         <li>ERROR_THREAD_INVALID_ACCESS - if not called from
	 *                         the thread that created the receiver</li>
	 *                         </ul>
	 */
	public void setValue(int value) {
		checkWidget();
		if (value < minimum || value > maximum) {
			SWT.error(SWT.ERROR_INVALID_ARGUMENT, new IllegalArgumentException(
					String.format("Value %d is not int the range [%d - %d]", value, minimum, maximum)));
		}
		this.value = value;
		redraw();
		update();
	}

}
