package com.guardanis.scratchoff;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class ScratchViewController implements OnTouchListener, ScratchImageLayout.GridAvailableListener {

    public interface ScratchEventListener {
        public void onScratchThresholdReached();
    }

    private static final int TOUCH_RADIUS_DIP = 30;

    private ScratchImageLayout imageLayout;
    private ScratchEventListener eventListener;
    private ScratchGridActionProcessor processor;
    private View behindView;

    private int touchRadius;
    private boolean thresholdReached = false;
    private double thresholdPercent = 0.4d;

    private int totalGridItemsCount;

    private boolean enabled = true;
    private boolean fadeOnClear = true;

    private long lastTouchEvent = 0;

    public ScratchViewController(ScratchImageLayout imageLayout, View behindView, ScratchEventListener eventListener) {
        this.eventListener = eventListener;
        this.imageLayout = imageLayout;
        this.imageLayout.setOnTouchListener(this);
        this.behindView = behindView;

        touchRadius = ViewHelper.getPxFromDip(imageLayout.getContext(), TOUCH_RADIUS_DIP);
        reset();
    }

    public void reset() {
        imageLayout.initialize(this);
        imageLayout.setVisibility(View.VISIBLE);
        imageLayout.requestLayout();
    }

    @Override
    public void onGridAvailable(int width, int height) {
        totalGridItemsCount = width * height;
        setupProcessor();
        enabled = true;
    }

    private void setupProcessor() {
        if(processor != null) processor.cancel();

        processor = new ScratchGridActionProcessor(this);
        processor.start();
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        if(!enabled)
            return false;

        processor.onReceieveMotionEvent(event, event.getAction() == MotionEvent.ACTION_DOWN);
        lastTouchEvent = System.currentTimeMillis();

        return true;
    }

    public void onPause() {
        if(processor != null)
            processor.cancel();
    }

    public void onResume() {
        if(enabled && !(processor == null || processor.isActive()))
            processor.start();
    }

    public void onDestroy() {
        if(processor != null && processor.isActive())
            processor.cancel();

        if(imageLayout != null)
            imageLayout.onDestroy();
    }

    public void setFadeOnClear(boolean fadeOnClear) {
        this.fadeOnClear = fadeOnClear;
    }

    public void onThresholdReached() {
        thresholdReached = true;
        eventListener.onScratchThresholdReached();
    }

    public void clear() {
        enabled = false;
        imageLayout.onClear(fadeOnClear);
    }

    public ScratchImageLayout getScratchImageLayout() {
        return imageLayout;
    }

    public void setThresholdPercent(double thresholdPercent) {
        this.thresholdPercent = thresholdPercent;
    }

    public double getThresholdPercent() {
        return thresholdPercent;
    }

    public int getTouchRadius() {
        return touchRadius;
    }

    public boolean isThresholdReached() {
        return thresholdReached;
    }

    public int getTotalGridItemsCount() {
        return totalGridItemsCount;
    }

    public View getViewBehind() {
        return behindView;
    }
}
