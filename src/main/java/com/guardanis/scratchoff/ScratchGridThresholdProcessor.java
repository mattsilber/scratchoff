package com.guardanis.scratchoff;

import android.graphics.Bitmap;
import android.graphics.Color;

public class ScratchGridThresholdProcessor extends ProcessorThread {

    private static final int SLEEP_DELAY = 750;

    private ScratchViewController controller;

    private Bitmap currentBitmap;
    private boolean thresholdReached = false;

    public ScratchGridThresholdProcessor(ScratchViewController controller) {
        this.controller = controller;
    }

    @Override
    public void start(){
        thresholdReached = false;
        safelyReleaseCurrentBitmap();

        super.start();
    }

    @Override
    protected void doInBackground() throws Exception {
        while(isActive() && !controller.isThresholdReached()){
            getUpdatedDrawingCache();

            while(currentBitmap == null)
                Thread.sleep(50);

            processImage();

            Thread.sleep(SLEEP_DELAY);
        }
    }

    private void processImage(){
        double percentScratched = getScratchedCount(currentBitmap) / (currentBitmap.getWidth() * currentBitmap.getHeight());

        if(controller.getThresholdPercent() < percentScratched && !thresholdReached){
            thresholdReached = true;
            postThresholdReached();
        }

        safelyReleaseCurrentBitmap();
    }

    private double getScratchedCount(Bitmap bitmap) {
        int[] pixels = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        double scratched = 0;
        for(int x = 0; x < bitmap.getWidth(); x++)
            for(int y = 0; y < bitmap.getHeight(); y++)
                if(Color.alpha(pixels[y * bitmap.getWidth() + x]) == 0)
                    scratched++;

        return scratched;
    }

    private void getUpdatedDrawingCache() {
        controller.getScratchImageLayout().post(new Runnable() {
            public void run() {
                controller.getScratchImageLayout().setDrawingCacheEnabled(true);
                currentBitmap = Bitmap.createBitmap(controller.getScratchImageLayout().getDrawingCache());
                controller.getScratchImageLayout().setDrawingCacheEnabled(false);
            }
        });
    }

    private void postThresholdReached() {
        controller.getScratchImageLayout().post(new Runnable() {
            public void run() {
                controller.onThresholdReached();
            }
        });
    }

    @Override
    public void cancel(){
        super.cancel();

        safelyReleaseCurrentBitmap();
    }

    private void safelyReleaseCurrentBitmap(){
        try{
            currentBitmap.recycle();
            currentBitmap = null;
        }
        catch(Exception e){ e.printStackTrace(); }
    }

}
