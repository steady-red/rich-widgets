package org.eclipse.swt.custom;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;

public class RichSashForm extends SashForm {

	private List<SashMovedListener> peer_sash_moved_listeners = new ArrayList<SashMovedListener>();

	public RichSashForm(Composite parent, int style) {
		super(parent, style);
	}

	@Override
	void onDragSash(Event event) {
		super.onDragSash(event);

		notifySashMovedListeners();
	}

	public void addSashMovedListener(SashMovedListener listener) {
		synchronized (peer_sash_moved_listeners) {
			peer_sash_moved_listeners.add(listener);
		}
	}

	public void removeSashMovedListener(SashMovedListener listener) {
		synchronized (peer_sash_moved_listeners) {
			peer_sash_moved_listeners.remove(listener);
		}
	}

	protected void notifySashMovedListeners() {
		if (isDisposed() == true) {
			return;
		}

		int[] weights = getWeights();

		if (weights != null) {

			ArrayList<SashMovedListener> listeners = null;

			synchronized (peer_sash_moved_listeners) {
				listeners = new ArrayList<SashMovedListener>(peer_sash_moved_listeners);
			}

			for (SashMovedListener sashMovedListener : listeners) {
				sashMovedListener.sashMoved(weights);
			}
		}
	}
}
