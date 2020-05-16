package red.steady.richWidgets.utils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Control;

import lombok.Builder;

public class FormLayoutDataFactory {

	private FormAttachment leftFormAttachment;
	private FormAttachment rightFormAttachment;
	private FormAttachment topFormAttachment;
	private FormAttachment bottomFormAttachment;
	private boolean toHorizontalMiddle;
	private boolean toVerticalMiddle;

	@Builder
	private FormLayoutDataFactory(FormAttachment leftFormAttachment, FormAttachment rightFormAttachment,
			FormAttachment topFormAttachment, FormAttachment bottomFormAttachment, Integer fillHorizontal,
			Integer fillVertical, Integer fill, Integer offset, Integer toLeft, Integer toRight, Integer toTopLeft,
			Integer toTop, Integer toBottom, Control topControl, Integer topOffset, Control bottomControl,
			Integer bottomOffset, Control leftControl, Integer leftOffset, Control rightControl, Integer rightOffset,
			Integer topPercentage, Integer bottomPercentage, Integer leftPercentage, Integer rightPercentage,
			boolean toHorizontalMiddle, boolean toVerticalMiddle, boolean doNotFixNegitaveOffset) {
		super();

		offsetNegitaveFix = -1;
		if (doNotFixNegitaveOffset == true) {
			offsetNegitaveFix = 1;
		}
		this.leftFormAttachment = leftFormAttachment;
		this.rightFormAttachment = rightFormAttachment;
		this.topFormAttachment = topFormAttachment;
		this.bottomFormAttachment = bottomFormAttachment;

		this.toHorizontalMiddle = toHorizontalMiddle;
		this.toVerticalMiddle = toVerticalMiddle;

		if (fill != null) {
			fillHorizontal = fill;
			fillVertical = fill;
		}

		if (offset != null) {
			topOffset = offset;
			bottomOffset = offset;
			leftOffset = offset;
			rightOffset = offset;
		}

		if (toTopLeft != null) {
			if (toTopLeft < 0) {
				toTopLeft = offsetNegitaveFix * toTopLeft;
			}
			this.topFormAttachment = new FormAttachment(0, toTopLeft);
			this.leftFormAttachment = new FormAttachment(0, toTopLeft);
		}

		if (toLeft != null) {
			if (toLeft < 0) {
				toLeft = offsetNegitaveFix * toLeft;
			}
			this.leftFormAttachment = new FormAttachment(0, toLeft);
		}

		if (toRight != null) {
			if (toRight < 0) {
				toRight = offsetNegitaveFix * toRight;
			}
			this.rightFormAttachment = new FormAttachment(100, offsetNegitaveFix * toRight);
		}

		if (toTop != null) {
			if (toTop < 0) {
				toTop = offsetNegitaveFix * toTop;
			}
			this.topFormAttachment = new FormAttachment(0, toTop);
		}

		if (toBottom != null) {
			if (toBottom < 0) {
				toBottom = offsetNegitaveFix * toBottom;
			}
			this.bottomFormAttachment = new FormAttachment(100, offsetNegitaveFix * toBottom);
		}

		if (topPercentage != null) {
			int anOffset = 0;

			if (topOffset != null) {
				if (topOffset < 0) {
					anOffset = offsetNegitaveFix * topOffset;
				} else {
					anOffset = topOffset;
				}
			}

			this.topFormAttachment = new FormAttachment(topPercentage, anOffset);
		}

		if (bottomPercentage != null) {
			int anOffset = 0;

			if (bottomOffset != null) {
				if (bottomOffset > 0) {
					anOffset = offsetNegitaveFix * bottomOffset;
				} else {
					anOffset = bottomOffset;
				}
			}

			this.bottomFormAttachment = new FormAttachment(bottomPercentage, anOffset);
		}

		if (leftPercentage != null) {
			int anOffset = 0;

			if (leftOffset != null) {
				if (leftOffset < 0) {
					anOffset = offsetNegitaveFix * leftOffset;
				} else {
					anOffset = leftOffset;
				}
			}

			this.leftFormAttachment = new FormAttachment(leftPercentage, anOffset);
		}

		if (rightPercentage != null) {
			int anOffset = 0;

			if (rightOffset != null) {
				if (rightOffset > 0) {
					anOffset = offsetNegitaveFix * rightOffset;
				} else {
					anOffset = rightOffset;
				}
			}

			this.rightFormAttachment = new FormAttachment(rightPercentage, anOffset);
		}

		if (fillHorizontal != null) {
			if (fillHorizontal < 0) {
				fillHorizontal = offsetNegitaveFix * fillHorizontal;
			}
			this.leftFormAttachment = new FormAttachment(0, fillHorizontal);
			this.rightFormAttachment = new FormAttachment(100, offsetNegitaveFix * fillHorizontal);
		}

		if (fillVertical != null) {
			if (fillVertical < 0) {
				fillVertical = offsetNegitaveFix * fillVertical;
			}
			this.topFormAttachment = new FormAttachment(0, fillVertical);
			this.bottomFormAttachment = new FormAttachment(100, offsetNegitaveFix * fillVertical);
		}

		if (topControl != null) {
			this.topFormAttachment = new FormAttachment(topControl, (topOffset != null) ? topOffset : 0);
		}

		if (bottomControl != null) {
			this.bottomFormAttachment = new FormAttachment(bottomControl,
					(bottomOffset != null) ? makeNegative(bottomOffset) : 0);
		}

		if (leftControl != null) {
			this.leftFormAttachment = new FormAttachment(leftControl, (leftOffset != null) ? leftOffset : 0);
		}

		if (rightControl != null) {
			this.rightFormAttachment = new FormAttachment(rightControl,
					(rightOffset != null) ? makeNegative(rightOffset) : 0);
		}

	}

	public void apply(Control control) {
		if (toHorizontalMiddle == true) {
			if (getLayoutData().width != SWT.DEFAULT) {
				getLayoutData().left = new FormAttachment(50, offsetNegitaveFix * getLayoutData().width / 2);
			} else {
				getLayoutData().left = new FormAttachment(50,
						offsetNegitaveFix * control.computeSize(SWT.DEFAULT, SWT.DEFAULT).x / 2);
			}
			getLayoutData().right = null;
		}

		if (toVerticalMiddle == true) {
			if (getLayoutData().height != SWT.DEFAULT) {
				getLayoutData().top = new FormAttachment(50, offsetNegitaveFix * getLayoutData().height / 2);
			} else {
				getLayoutData().top = new FormAttachment(50,
						offsetNegitaveFix * control.computeSize(SWT.DEFAULT, SWT.DEFAULT).y / 2);
			}
			getLayoutData().bottom = null;
		}

		control.setLayoutData(getLayoutData());
	}

	public FormLayoutDataFactory setHeight(int height) {
		if (height != SWT.DEFAULT) {
			getLayoutData().height = height;
		}

		return FormLayoutDataFactory.this;
	}

	public FormLayoutDataFactory setWidth(int width) {
		if (width != SWT.DEFAULT) {
			getLayoutData().width = width;
		}

		return FormLayoutDataFactory.this;
	}

	private FormData data;
	private int offsetNegitaveFix;

	private FormData getLayoutData() {
		if (data == null) {
			data = new FormData();

			data.left = leftFormAttachment;
			data.right = rightFormAttachment;
			data.top = topFormAttachment;
			data.bottom = bottomFormAttachment;
		}

		return data;
	}

	private static int makePostive(int value) {
		return Math.abs(value);
	}

	private static int makeNegative(int value) {
		return -1 * Math.abs(value);
	}

	public static void toTopAndHorizontalFill(Control control, int offset) {
		FormLayoutDataFactory.builder().toTop(offset).fillHorizontal(offset).build().apply(control);
	}

	public static void toTopControlAndHorizontalFill(Control control, Control topControl, int offset) {
		FormLayoutDataFactory.builder().topControl(topControl).topOffset(offset).fillHorizontal(offset).build()
				.apply(control);
	}

	public static void toTopControlToBottomAndHorizontalFill(Control control, Control topControl, int offset) {
		FormLayoutDataFactory.builder().topControl(topControl).topOffset(offset).toBottom(offset).fillHorizontal(offset)
				.build().apply(control);
	}

	public static void toBottomControlToTopAndHorizontalFill(Control control, Control bottomControl, int offset) {
		FormLayoutDataFactory.builder()//
				.bottomControl(bottomControl).bottomOffset(offset)//
				.toTop(offset)//
				.fillHorizontal(offset)//
				.build().apply(control);
	}

	public static void toTopLeft(Control control) {
		toTopLeft(control, 2);
	}

	public static void toTopLeft(Control control, int offset) {
		FormLayoutDataFactory.builder().toTopLeft(offset).build().apply(control);
	}

	public static void toBottomAndHorizontalFill(Control control) {
		toBottomAndHorizontalFill(control, 2);
	}

	public static void toBottomAndHorizontalFill(Control control, int offset) {
		FormLayoutDataFactory.builder().toBottom(2).fillHorizontal(offset).build().apply(control);
	}

	public static void fill(Control control) {
		fill(control, 0);
	}

	public static void fill(Control control, int border) {
		{
			FormData data = new FormData();
			data.left = new FormAttachment(0, border);
			data.right = new FormAttachment(100, -1 * border);
			data.top = new FormAttachment(0, border);
			data.bottom = new FormAttachment(100, -1 * border);
			control.setLayoutData(data);
		}
	}

}
