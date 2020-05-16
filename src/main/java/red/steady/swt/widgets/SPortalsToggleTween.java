package red.steady.swt.widgets;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import red.steady.richWidgets.utils.RichUtils;

public class SPortalsToggleTween {

	private List<Boolean> _tweeningQueue;
	private Thread tweenThread;

	private int tweenIndex = 0;
	private final int tweenIncrement;
	private final int maximumTweenIndex;
	private final int sleepTime;

	private final Object syncObject;
	private boolean running;

	private final ToggleTweenChangeListener toggleTweenChangeListener;

	public SPortalsToggleTween(ToggleTweenChangeListener toggleTweenChangeListener) {
		this(toggleTweenChangeListener, 20, 100);
	}

	public SPortalsToggleTween(ToggleTweenChangeListener toggleTweenChangeListener, //
			int tweenIncrement, //
			int maximumTweenIndex) {
		this(toggleTweenChangeListener, tweenIncrement, maximumTweenIndex, 20);
	}

	public SPortalsToggleTween(ToggleTweenChangeListener toggleTweenChangeListener, //
			int tweenIncrement, //
			int maximumTweenIndex, //
			int sleepTime) {
		super();

		RichUtils.checkNotNullParameter(toggleTweenChangeListener, "toggleTweenChangeListener");

		this.tweenIncrement = tweenIncrement;
		this.maximumTweenIndex = maximumTweenIndex;
		this.sleepTime = sleepTime;
		this.toggleTweenChangeListener = toggleTweenChangeListener;

		syncObject = new Object();
		_tweeningQueue = new ArrayList<Boolean>();

		startTweenThread();
	}

	private void startTweenThread() {
		running = true;

		tweenThread //
				= new Thread(new Runnable() {
					private Optional<Boolean> toggleValue = Optional.<Boolean>empty();

					@Override
					public void run() {
						while (running) {
							try {
								if (toggleValue.isPresent() == false) {
									toggleValue = nextInTweenQueue();

									if (toggleValue.isPresent() == false) {
										synchronized (syncObject) {
											try {
												syncObject.wait();
											} catch (InterruptedException e) {
												e.printStackTrace();
											}
										}
									}
								} else {
									while (true) {
										boolean lastToggleValue = toggleValue.get();

										if (toggleValue.get() == true) {
											tweenIndex += tweenIncrement;
											if (tweenIndex >= maximumTweenIndex) {
												tweenIndex = maximumTweenIndex;
												toggleValue = Optional.<Boolean>empty();
											}
										} else {
											tweenIndex -= tweenIncrement;
											if (tweenIndex <= 0) {
												tweenIndex = 0;
												toggleValue = Optional.<Boolean>empty();
											}
										}

										toggleTweenChangeListener.tweenIndexChanged(tweenIndex);

										if (toggleValue.isPresent() == false) {
											toggleTweenChangeListener.completed(lastToggleValue);
											break;
										}
										RichUtils.sleep(20);
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				});

		tweenThread.setDaemon(true);
		tweenThread.start();
	}

	public void stop() {
//		tweenThread.stop(); // TODO use other mechanism
		running = false;

		synchronized (syncObject) {
			syncObject.notify();
		}
	}

	public int getTween(float first, float second) {
		float floatFirst = first;
		float floatSecond = second;

		float result //
				= floatFirst * (((float) maximumTweenIndex - (float) tweenIndex) / maximumTweenIndex) //
						+ floatSecond * ((float) tweenIndex / (float) maximumTweenIndex);

		return (int) result;
	}

	public int getTween(int first, int second) {
		float floatFirst = first;
		float floatSecond = second;

		float result //
				= floatFirst * (((float) maximumTweenIndex - (float) tweenIndex) / maximumTweenIndex) //
						+ floatSecond * ((float) tweenIndex / (float) maximumTweenIndex);

		return (int) result;
	}

	public void addToTweenQueue(boolean toggleValue) {
		synchronized (_tweeningQueue) {
			_tweeningQueue.add(toggleValue);

			synchronized (syncObject) {
				syncObject.notify();
			}
		}
	}

	private Optional<Boolean> nextInTweenQueue() {
		synchronized (_tweeningQueue) {
			if (_tweeningQueue.size() == 0) {
				return Optional.<Boolean>empty();
			}
			return Optional.<Boolean>of(RichUtils.removeLast(_tweeningQueue));
		}
	}

	public int getTweenIndex() {
		return tweenIndex;
	}

	public void setTweenIndex(int tweenIndex) {
		this.tweenIndex = tweenIndex;
	}

	public int getMaximumTweenIndex() {
		return maximumTweenIndex;
	}

}
