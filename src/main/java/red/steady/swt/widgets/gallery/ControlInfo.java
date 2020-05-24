package red.steady.swt.widgets.gallery;

import java.util.function.Consumer;
import java.util.function.Function;

import org.eclipse.swt.widgets.Control;

import lombok.Builder;
import lombok.Data;
import red.steady.richWidgets.RichComposite;

@Builder
@Data
public class ControlInfo {
	private Class<? extends Control> controlClass;
	private String displayName;
	private Function<RichComposite, Control> createControlFunction;
	private Consumer<RichComposite> createExampleControlsConsumer;

	public String getImageName() {
		return getControlClass().getSimpleName() + ".png";
	}

	private Control createdControl;

}