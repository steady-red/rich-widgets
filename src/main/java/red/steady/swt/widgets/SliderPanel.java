package red.steady.swt.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import red.steady.richWidgets.RichSlider;
import red.steady.richWidgets.RichSliderColorScheme;
import red.steady.richWidgets.utils.FormLayoutDataFactory;
import red.steady.richWidgets.utils.SwtUtils;

class SliderPanel extends SpForm {

	private final String volumeControlName;
	private Color backgroundColor;

	private final RichSliderColorScheme nebulaSliderColorScheme;

	public SliderPanel(Composite parent, String volumeControlName) {
		this(parent, volumeControlName, (RichSliderColorScheme) null);
	}

	public SliderPanel(Composite parent, String volumeControlName, RichSliderColorScheme nebulaSliderColorScheme) {
		super(parent);

		this.volumeControlName = volumeControlName;
		this.nebulaSliderColorScheme = nebulaSliderColorScheme;

		backgroundColor = SwtUtils.getSystemColor(getDisplay(), SWT.COLOR_BLACK);

		createControls();
	}

	private void createControls() {
		CLabel clabel = new CLabel(this, SWT.CENTER);
		clabel.setForeground(new Color(getDisplay(), 255, 255, 255));
		clabel.setBackground(new Color(getDisplay(), 0, 0, 0));
		clabel.setText(volumeControlName);

		FormLayoutDataFactory.builder()//
				.toTop(0).topOffset(0)//
				.fillHorizontal(0)//
				.build().apply(clabel);

		ImageCanvas soundOnImageCanvas = new ImageCanvas(this,
				createImageFromClasspath("com/redmindset/images/autoGenerated/sound_on-white-20x20.png"));
		soundOnImageCanvas.setBackground(backgroundColor);

		soundOnImageCanvas.addListener(SWT.MouseDown, new Listener() {
			@Override
			public void handleEvent(Event event) {
//					muteSound();
			}
		});

		FormLayoutDataFactory.builder()//
				.topControl(clabel).topOffset(0)//
				.toBottom(0)//
				.toLeft(2)//
				.build().apply(soundOnImageCanvas);

		ImageCanvas soundOffImageCanvas = new ImageCanvas(this,
				createImageFromClasspath("com/redmindset/images/autoGenerated/sound_off-white-20x20.png"));
		soundOffImageCanvas.setBackground(backgroundColor);

		soundOffImageCanvas.addListener(SWT.MouseDown, new Listener() {
			@Override
			public void handleEvent(Event event) {
//					unmuteSound();
			}
		});

		FormLayoutDataFactory.builder()//
				.topControl(clabel).topOffset(0)//
				.toBottom(0)//
				.toLeft(2)//
				.build().apply(soundOffImageCanvas);

		final RichSlider slider = new RichSlider(this, SWT.NONE, nebulaSliderColorScheme);
		slider.setBackground(backgroundColor);
		slider.setMinimum(0);
		slider.setMaximum(100);
		slider.setValue(50);
		slider.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
//				numberLabel.setText(String.valueOf(slider.getSelection()));
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		FormLayoutDataFactory.builder()//
				.topControl(clabel).topOffset(0)//
				.leftControl(soundOffImageCanvas).leftOffset(2)//
				.toRight(0)//
				.toBottom(0)//
				.build().apply(slider);

//		CLabel numberLabel = new CLabel(this, SWT.CENTER);
//		numberLabel.setForeground(new Color(getDisplay(), 255, 255, 255));
//		numberLabel.setBackground(new Color(getDisplay(), 0, 0, 0));
//
//		FormLayoutDataFactory.builder()//
//				.topControl(clabel).topOffset(0)//
//				.toRight(0)//
//				.toBottom(0)//
//				.build().setWidth(30).apply(numberLabel);
//
//		Slider slider = new Slider(this, SWT.HORIZONTAL);
//		slider.setMinimum(0);
//		slider.setMaximum(100);
//		slider.setThumb(1);
////			slider.setSelection(0);
//		slider.addSelectionListener(new SelectionListener() {
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				numberLabel.setText(String.valueOf(slider.getSelection()));
//			}
//
//			@Override
//			public void widgetDefaultSelected(SelectionEvent e) {
//			}
//		});
//
//		FormLayoutDataFactory.builder()//
//				.topControl(clabel).topOffset(0)//
//				.leftControl(soundOffImageCanvas).leftOffset(2)//
//				.rightControl(numberLabel).rightOffset(2)//
//				.toBottom(0)//
//				.build().apply(slider);
	}
}