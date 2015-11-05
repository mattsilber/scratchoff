package com.guardanis.scratchoff;

import android.graphics.Path;
import android.view.MotionEvent;

import java.util.ArrayList;

public class ScratchGridActionProcessor extends ProcessorThread {

    private static final int SLEEP_DELAY = 10;

    private ScratchViewController controller;
    private ScratchGridThresholdProcessor thresholdProcessor;
    private ScratchGridInvalidationProcessor invalidationProcessor;

    private ArrayList<Path> queuedEvents = new ArrayList<Path>();

    private int[] lastTouchEvent;

    public ScratchGridActionProcessor(ScratchViewController controller) {
        this.controller = controller;

        this.thresholdProcessor = new ScratchGridThresholdProcessor(controller);
        this.invalidationProcessor = new ScratchGridInvalidationProcessor(controller);
    }

    public void onReceieveMotionEvent(MotionEvent e, boolean actionDown) {
        int[] event = new int[]{(int) e.getX(), (int) e.getY()};
        if(!actionDown){
            Path path = new Path();
            path.moveTo(lastTouchEvent[0], lastTouchEvent[1]);
            path.lineTo(event[0], event[1]);
            queuedEvents.add(path);
        }
        lastTouchEvent = event;
    }

    @Override
    protected void doInBackground() throws Exception {
        while(isActive()){
            ArrayList<Path> tempEvents = queuedEvents;
            queuedEvents = new ArrayList<Path>();

            if(tempEvents.size() > 0){
                postPaths(tempEvents);
                invalidationProcessor.onReceievePaths(tempEvents);
            }

            Thread.sleep(SLEEP_DELAY);
        }
    }

    private void postPaths(final ArrayList<Path> paths) {
        controller.getScratchImageLayout().post(new Runnable() {
            public void run() {
                controller.getScratchImageLayout().addPaths(paths);
            }
        });
    }

    @Override
    public void start() {
        if(thresholdProcessor != null)
            thresholdProcessor.start();

        if(invalidationProcessor != null)
            invalidationProcessor.start();

        super.start();
    }

    @Override
    public void cancel() {
        if(thresholdProcessor != null)
            thresholdProcessor.cancel();

        if(invalidationProcessor != null)
            invalidationProcessor.cancel();

        super.cancel();
    }

}
