package red.steady.swt.widgets;

import org.eclipse.swt.graphics.Image;

public interface PortalsLabelProvider<T> {

	String getLabel(T element);

	Image getImage(T element);

}
