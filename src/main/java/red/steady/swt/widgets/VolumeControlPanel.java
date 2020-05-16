package red.steady.swt.widgets;

import org.eclipse.nebula.widgets.opal.nebulaslider.NebulaSliderColorScheme;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import red.steady.richWidgets.utils.RichUtils;
import red.steady.richWidgets.utils.FormLayoutDataFactory;

public class VolumeControlPanel extends SpWindow {

	private boolean opened;

	private final VolumeControlButtonPanel volumeControlButtonPanel;
	private final String[] volumeControlNames;

	private final NebulaSliderColorScheme nebulaSliderColorScheme;

	public VolumeControlPanel(Shell parent, VolumeControlButtonPanel volumeControlButtonPanel,
			String[] volumeControlNames, //
			NebulaSliderColorScheme nebulaSliderColorScheme) {
		super(parent, //
				SWT.NO_TRIM);
		RichUtils.checkNotNullParameter(volumeControlButtonPanel, "volumeControlButtonPanel");
		RichUtils.checkPositiveIntParameter(volumeControlNames.length, "volumeControlNames.length");

		this.volumeControlButtonPanel = volumeControlButtonPanel;
		this.volumeControlNames = volumeControlNames;
		this.nebulaSliderColorScheme = nebulaSliderColorScheme;

		loadAssets();

		FormLayout formLayout = new FormLayout();
		formLayout.marginWidth = 5;
		formLayout.marginHeight = 5;
		setLayout(formLayout);

		setBackground(0, 0, 0);

		createControls();

		pack();
		setSize(200, getSize().y);

		createControlActions();
	}

	public void show() {
		Point displayPoint = volumeControlButtonPanel.toDisplay(0 + volumeControlButtonPanel.getBounds().width / 2, 0);

		int x = displayPoint.x - (getBounds().width / 2);
		System.out.println("x=" + x);

		int displayRightBorderOverlap = (x + this.getBounds().width) - getMonitor().getBounds().width;
		if (displayRightBorderOverlap > 0) {
			x = displayPoint.x - displayRightBorderOverlap;
		}
		System.out.println("x=" + x);

		setLocation(x, displayPoint.y - this.getBounds().height);

		if (opened == true) {
			System.out.println("setVisible(true);");
			setVisible(true);
		} else {
			System.out.println("open();");
			open();
			opened = true;
		}

		forceFocus();
	}

	private void createControlActions() {
		addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
				System.out.println("focusLost");
				setVisible(false);
			}

			@Override
			public void focusGained(FocusEvent e) {
				System.out.println("focusGained");
			}
		});
	}

	private void loadAssets() {
	}

	private void createControls() {
		Control lastControl = null;

		for (String volumeControlName : volumeControlNames) {
			SliderPanel sliderPanel = new SliderPanel(this, volumeControlName, nebulaSliderColorScheme);

			if (lastControl == null) {
				FormLayoutDataFactory.builder()//
						.toTop(0).topOffset(0)//
						.fillHorizontal(0)//
						.build().apply(sliderPanel);

			} else if (volumeControlName.equals(volumeControlNames[volumeControlNames.length - 1]) == true) {
				FormLayoutDataFactory.builder()//
						.topControl(lastControl).topOffset(2)//
						.toBottom(0)//
						.fillHorizontal(0)//
						.build().apply(sliderPanel);

			} else {
				FormLayoutDataFactory.builder()//
						.topControl(lastControl).topOffset(2)//
						.fillHorizontal(0)//
						.build().apply(sliderPanel);
			}

			lastControl = sliderPanel;
		}
	}

	public void toggleVisibility() {
		System.out.println("toggleVisibility");
		if (isVisible() == true) {
			setVisible(false);
			volumeControlButtonPanel.forceFocus();
		} else {
			show();
		}
	}
}
