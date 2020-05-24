package red.steady.richWidgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.RichSashForm;
import org.eclipse.swt.custom.SashMovedListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import red.steady.richWidgets.application.RichApplication;
import red.steady.richWidgets.utils.FormLayoutDataFactory;

public class RichDualSashForm extends RichForm {

	private RichSashForm mainSashForm;
	private RichForm leftForm;
	private RichForm rightForm;
	private int orientation;

	private int leftWeight;
	private int rightWeight;

	public RichDualSashForm(RichComposite parent, int style, int leftWeight, int RightWeight) {
		this(parent.getRichApplication(), parent, style, leftWeight, RightWeight);
	}

	public RichDualSashForm(RichApplication richApplication, Composite parent, int style, int leftWeight,
			int rightWeight) {
		super(richApplication, parent, SWT.NULL);

		this.leftWeight = leftWeight;
		this.rightWeight = rightWeight;

		createControls(style);
	}

	protected void createControls(int style) {
		mainSashForm = new RichSashForm(this, (style | (SWT.HORIZONTAL & SWT.VERTICAL)));

		FormLayoutDataFactory.fill(mainSashForm);

		leftForm = new RichForm(getRichApplication(), mainSashForm);

		rightForm = new RichForm(getRichApplication(), mainSashForm);

		mainSashForm.setOrientation(getUiPropertyIntValue("orientation", (style | (SWT.HORIZONTAL & SWT.VERTICAL))));
		mainSashForm.setWeights(getUiPropertyIntArrayValue("weights", new int[] { leftWeight, rightWeight }));

		mainSashForm.addSashMovedListener(new SashMovedListener() {
			@Override
			public void sashMoved(int[] weights) {
				setUiPropertyValue("weights", weights);
			}
		});
	}

	@Override
	public int getOrientation() {
		return orientation;
	}

	@Override
	public void setOrientation(int orientation) {
		this.orientation = orientation;

		setUiPropertyValue("orientation", orientation);

		mainSashForm.setOrientation(orientation);
	}

	public Control getMaximizedControl() {
		return mainSashForm.getMaximizedControl();
	}

	public void setMaximizedControl(Control control) {
		mainSashForm.setMaximizedControl(control);
	}

	public int getSashWidth() {
		return mainSashForm.getSashWidth();
	}

	public RichForm getTopOrLeftForm() {
		return leftForm;
	}

	public RichForm getBottomOrRightForm() {
		return rightForm;
	}
}
