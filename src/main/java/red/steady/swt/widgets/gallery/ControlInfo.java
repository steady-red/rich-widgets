package red.steady.swt.widgets.gallery;

import java.util.function.Consumer;
import java.util.function.Function;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ControlInfo {
	private Class<? extends Control> controlClass;
	private String displayName;
	private Function<Composite, Control> createControlFunction;
	private Consumer<Composite> createExampleControlsConsumer;

	public String getImageName() {
		return getControlClass().getSimpleName() + ".png";
	}

	private Control createdControl;

}