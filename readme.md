# Scratchoff

A simply library for implementing a scratchoff-type system. (screenshots coming soon)

# Installation

```
    repositories {
        maven { url "http://dl.bintray.com/mattsilber/maven" }
    }

    dependencies {
        compile('com.guardanis:scratchoff:1.0.0')
    }
```

I should have it on *jcenter* soon enough...

# Usage

The goal of this library is to use the drawing cache of a View below it and allow what appears to the user as scratching away the surface to reveal what's hidden below. 

First, you need a RelativeLayout (to align layouts on top of one another) consisting of 2 sub-layouts, a behind-View and the *ScratchImageLayout* (which is a LinearLayout containing a single ImageView) to be displayed in the foreground. Here is a simple example:

```
    <RelativeLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent" >

        <RelativeLayout
            android:id="@+id/scratch_view_behind"
            android:layout_width="fill_parent"
            android:layout_height="fill_parent"
            android:background="#818B8D" >

            <ImageView
                android:layout_width="fill_parent"
                android:layout_height="wrap_content"
                android:layout_centerInParent="true"
                android:layout_margin="25dip"
                android:adjustViewBounds="true"
                android:src="@drawable/some_drawable_to_be_revealed" />
        </RelativeLayout>

        <com.guardanis.scratchoff.ScratchImageLayout
            android:id="@+id/scratch_view"
            android:layout_width="fill_parent"
            android:layout_height="fill_parent"
            android:background="#3C9ADF" >

            <ImageView
                android:layout_width="fill_parent"
                android:layout_height="wrap_content"
                android:layout_gravity="center"
                android:adjustViewBounds="true"
                android:gravity="center"
                android:src="@drawable/some_top_drawable" />
        </com.guardanis.scratchoff.ScratchImageLayout>
    </RelativeLayout>
```

Note: be careful with the dimensions of both the behind-View and the ScratchImageLayout. If they don't overlap perfectly, the scratchoff system could become visually distorted.

Now that you have a layout, we need to attach the *ScratchViewController* to it:

```
    ScratchImageLayout scratchLayout = (ScratchImageLayout) view.findViewById(R.id.scratch_view);
    ScratchViewController controller = new ScratchViewController(scratchLayout,
            view.findViewById(R.id.scratch_view_behind), // The behind view
            new ScratchViewController.ScratchEventListener() {
                public void onScratchThresholdReached() {
                    controller.clear(); // Tell the controller to clear the rest of the grid, if we want that behavior  
                }
            });
    //controller.setThresholdPercent(0.40d); 
    //controller.setFadeOnClear(true);
```

As a final note, if using the ScratchViewController in the context of an Activity, you may want to also ensure you call the correct lifecycle methods for *onPause()*, *onResume()*, and *onDestroy()* as needed, to ensure the processors will stop/restart and not run needlessly in the background.

