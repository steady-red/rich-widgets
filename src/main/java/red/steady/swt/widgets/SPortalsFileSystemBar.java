package red.steady.swt.widgets;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import red.steady.richWidgets.utils.RichUtils;
import red.steady.richWidgets.utils.FormLayoutDataFactory;

public class SPortalsFileSystemBar extends SimpleForm {

	private final Listener clickedListener;

	private final List<SPortalsFileSystemBarElement> breadcrumbItems;

	public SPortalsFileSystemBar(Composite parent, //
			Listener clickedListener) {
		super(parent, SWT.BORDER, false);

		this.clickedListener = clickedListener;

		this.breadcrumbItems = new ArrayList<SPortalsFileSystemBarElement>();

//		setLayout(new SPButtonBarLayout(SPButtonBarType.LeftAligned, -1));

		createControls(this);
	}

	@Override
	protected void createControls(Composite rapComposite) {
		addMouseListener(new MouseListener() {
			@Override
			public void mouseDown(MouseEvent e) {
				Event event = new Event();
				event.widget = SPortalsFileSystemBar.this;

				clickedListener.handleEvent(event);
			}

			@Override
			public void mouseUp(MouseEvent e) {
			}

			@Override
			public void mouseDoubleClick(MouseEvent e) {
			}
		});
	}

	public void clear() {
		for (SPortalsFileSystemBarElement sPortalsFileSystemBarElement : breadcrumbItems) {
			sPortalsFileSystemBarElement.dispose();
		}

		breadcrumbItems.clear();
	}

	// Called by SPortalsFileSystemBarElement constructor
	protected void addSPortalsBreadcrumbItem(SPortalsFileSystemBarElement sPortalsFileSystemBarElement) {
		System.out.println("Adding " + sPortalsFileSystemBarElement.getDisplayText());
		breadcrumbItems.add(sPortalsFileSystemBarElement);

		updateLayoutData();
	}

	private void updateLayoutData() {
		SPortalsFileSystemBarElement lastSPortalsFileSystemBarElement = null;

		for (SPortalsFileSystemBarElement fileSystemBarElement : breadcrumbItems) {
			if (lastSPortalsFileSystemBarElement == null) {
				FormLayoutDataFactory.builder()//
						.toLeft(0)//
						.toTop(0)//
						.toBottom(0)//
						.build().apply(fileSystemBarElement);
			} else {
				FormLayoutDataFactory.builder()//
						.leftControl(lastSPortalsFileSystemBarElement).leftOffset(0)//
						.toTop(0)//
						.toBottom(0)//
						.build().apply(fileSystemBarElement);
			}

			lastSPortalsFileSystemBarElement = fileSystemBarElement;
		}
	}

	public void setText(String newValue) {
		clear();

		if (RichUtils.isEmpty(newValue) == true) {
			return;
		}

		String[] parts = newValue.split("/", -1);

		for (String element : parts) {
			new SPortalsFileSystemBarElement(this, element, //
					new SPortalsBreadcrumbItemListener() {
						@Override
						public void selected() {
							System.out.println("Selected");
						}

						@Override
						public String[] dropdownSelected(SPortalsFileSystemBarElement fileSystemBarElement) {
							System.out.println("Dropdown");
							return new String[] {};
						}
					});
		}
	}
}
