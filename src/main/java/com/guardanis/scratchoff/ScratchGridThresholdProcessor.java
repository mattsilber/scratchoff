package com.guardanis.scratchoff;

import android.graphics.Bitmap;
import android.graphics.Color;

public class ScratchGridThresholdProcessor extends ProcessorThread {

    private interface CachedImageEventListener {
        public void onCachedImageAvailable(Bitmap bitmap);
    }

    private static final int SLEEP_DELAY = 500;

    private ScratchViewController controller;
    private Bitmap cachedBitmap;

    public ScratchGridThresholdProcessor(ScratchViewController controller) {
        this.controller = controller;
    }

    @Override
    protected void doInBackground() throws Exception {
        while(isActive()){
            handleThresholdCheck();
            Thread.sleep(SLEEP_DELAY);
        }
    }

    private void handleThresholdCheck() throws Exception {
        if(controller.isThresholdReached()) return;
        else{
            resetCachedImage();

            while(cachedBitmap == null) Thread.sleep(50);

            double percentScratched = getScratchedCount() / (cachedBitmap.getWidth() * cachedBitmap.getHeight());
            if(controller.getThresholdPercent() < percentScratched) postThresholdReached();
        }
    }

    private double getScratchedCount() {
        int[] pixels = new int[cachedBitmap.getWidth() * cachedBitmap.getHeight()];
        cachedBitmap.getPixels(pixels, 0, cachedBitmap.getWidth(), 0, 0, cachedBitmap.getWidth(), cachedBitmap.getHeight());

        double scratched = 0;
        for(int x = 0; x < cachedBitmap.getWidth(); x++){
            for(int y = 0; y < cachedBitmap.getHeight(); y++){
                if(Color.alpha(pixels[y * cachedBitmap.getWidth() + x]) == 0) scratched++;
            }
        }

        return scratched;
    }

    private void resetCachedImage() {
        if(cachedBitmap != null) cachedBitmap.recycle();
        cachedBitmap = null;

        getDrawingCachedBitmap(new CachedImageEventListener() {
            public void onCachedImageAvailable(Bitmap bitmap) {
                cachedBitmap = bitmap;
            }
        });
    }

    private void getDrawingCachedBitmap(final CachedImageEventListener eventListener) {
        controller.getScratchImageLayout().post(new Runnable() {
            public void run() {
                controller.getScratchImageLayout().setDrawingCacheEnabled(true);
                eventListener.onCachedImageAvailable(Bitmap.createBitmap(controller.getScratchImageLayout().getDrawingCache()));
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
    public void cancel() {
        super.cancel();

        if(cachedBitmap != null) cachedBitmap.recycle();
    }

}
