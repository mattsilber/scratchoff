package com.guardanis.scratchoff;

import android.graphics.Path;

import java.util.ArrayList;

public class ScratchGridInvalidationProcessor extends ProcessorThread {

    private static final int SLEEP_DELAY = 20;

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

            if(tempEvents.size() > 0)
                controller.getScratchImageLayout().postInvalidate();

            Thread.sleep(SLEEP_DELAY);
        }
    }

}
