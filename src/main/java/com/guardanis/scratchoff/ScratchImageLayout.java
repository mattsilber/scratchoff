package com.guardanis.scratchoff;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

public class ScratchImageLayout extends LinearLayout {

    public interface GridAvailableListener {
        public void onGridAvailable(int width, int height);
    }

    private Bitmap imageMutable;

    private GridAvailableListener gridListener;

    private List<Path> paths = new ArrayList<Path>();

    private Paint clearPaint;
    private boolean cleared = false;

    public ScratchImageLayout(Context context) {
        super(context);
    }

    public ScratchImageLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @SuppressLint("NewApi")
    public ScratchImageLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void initialize(ScratchViewController controller) {
        this.gridListener = controller;
        this.cleared = false;
        this.imageMutable = null;
        this.paths = new ArrayList<Path>();

        initializeClearPaint(controller.getTouchRadius());

        setWillNotDraw(false);
        ViewHelper.disableHardwareAcceleration(this);

        setBehindView(controller.getViewBehind());
        requestLayout();
    }

    private void initializeClearPaint(int touchRadius) {
        clearPaint = new Paint();
        clearPaint.setAlpha(0xFF);
        clearPaint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
        clearPaint.setStyle(Paint.Style.STROKE);
        clearPaint.setStrokeCap(Paint.Cap.ROUND);
        clearPaint.setStrokeJoin(Paint.Join.ROUND);
        clearPaint.setAntiAlias(true);
        clearPaint.setStrokeWidth(touchRadius * 2);
    }

    private void setBehindView(final View view) {
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                initializeBehindView(view);
                waitForDisplay();
                ViewHelper.removeOnGlobalLayoutListener(view, this);
            }
        });
        view.requestLayout();
    }

    private void initializeBehindView(View view) {
        ViewGroup.LayoutParams params = getLayoutParams();
        params.width = view.getWidth();
        params.height = view.getHeight();

        this.setLayoutParams(params);
    }

    private void waitForDisplay() {
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                initializePostDisplay();
                ViewHelper.removeOnGlobalLayoutListener(ScratchImageLayout.this, this);
            }
        });
        requestLayout();
    }

    private void initializePostDisplay() {
        setDrawingCacheEnabled(true);
        buildDrawingCache();

        Bitmap cached = getDrawingCache();
        imageMutable = Bitmap.createBitmap(cached);

        setDrawingCacheEnabled(false);

        setBackgroundColor(Color.TRANSPARENT);
        hideChildren();

        gridListener.onGridAvailable(imageMutable.getWidth(), imageMutable.getHeight());
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(cleared || imageMutable == null)
            return;
        else{
            canvas.drawBitmap(imageMutable, 0, 0, null);

            for(Path path : getPaths())
                canvas.drawPath(path, clearPaint);
        }
    }

    public void addPaths(List<Path> paths) {
        getPaths().addAll(paths);
    }

    private synchronized List<Path> getPaths() {
        return paths;
    }

    public void onDestroy() {
        if(imageMutable != null){
            imageMutable.recycle();
            imageMutable = null;
        }
    }

    public void onClear(boolean fade) {
        this.cleared = false;

        if(fade)
            fadeOut();
        else invalidate();
    }

    private void fadeOut() {
        AlphaAnimation anim = new AlphaAnimation(1f, 0f);
        anim.setDuration(1000);
        anim.setFillAfter(true);
        anim.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) { }

            public void onAnimationRepeat(Animation animation) { }

            public void onAnimationEnd(Animation animation) {
                setVisibility(View.GONE);
                showChildren();
            }
        });
        startAnimation(anim);
    }

    private void hideChildren(){
        for(int i = 0; i < getChildCount(); i++)
            getChildAt(i).setVisibility(View.GONE);
    }

    private void showChildren(){
        for(int i = 0; i < getChildCount(); i++)
            getChildAt(i).setVisibility(View.VISIBLE);
    }

}