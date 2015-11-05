package com.guardanis.scratchoff;

import android.graphics.Path;

import java.util.ArrayList;

public class ScratchGridInvalidationProcessor extends ProcessorThread {

    private static final int SLEEP_DELAY = 15;

    private ScratchViewController controller;
    private ArrayList<Path> queuedEvents = new ArrayList<Path>();

    public ScratchGridInvalidationProcessor(ScratchViewController controller) {
        this.controller = controller;
    }

    public void onReceievePaths(ArrayList<Path> paths) {
        queuedEvents.addAll(paths);
    }

    @Override
    protected void doInBackground() throws Exception {
        while(isActive()){
            ArrayList<Path> tempEvents = queuedEvents;
            queuedEvents = new ArrayList<Path>();

            if(tempEvents.size() > 0) requestUIUpdate();
            Thread.sleep(SLEEP_DELAY);
        }
    }

    private void requestUIUpdate() {
        controller.getScratchImageLayout().post(new Runnable() {
            public void run() {
                controller.getScratchImageLayout().invalidate();
            }
        });
    }

}
