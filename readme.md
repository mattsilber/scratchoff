# Scratchoff

A simple library for implementing a scratchoff-type system.

![Scratchoff Sample](https://github.com/mattsilber/scratchoff/raw/master/scratchoff.gif)

# Installation

```
    repositories {
        jcenter()
    }

    dependencies {
        compile('com.guardanis:scratchoff:1.0.1')
    }
```

# Usage

The goal of this library is to create a scratchoff interface. By storing and manipulating the drawing cache of a View, we can create the effect of scratching it away to reveal what's hidden below. 

First, you need a RelativeLayout (to align layouts on top of one another) consisting of 2 sub-layouts, a behind-View and the *ScratchImageLayout* (which is a LinearLayout containing any number of children) to be displayed in the foreground. Here is a simple example:

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

Note: be careful with the dimensions of both the behind-View and the ScratchImageLayout. The ScratchImageLayout will attempt to set its LayoutParam attributes for width/height to that of the behind View so they line up perfectly. 

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

As a final note, if using the ScratchViewController in the context of an Activity, you may want to also ensure you call the correct lifecycle methods for *onPause()*, *onResume()*, and *onDestroy()* as needed, to ensure the processors will stop/restart and not run needlessly in the background. e.g.

    @Override
    public void onPause(){
        controller.onPause();
        super.onPause();
    }

    @Override
    public void onResume(){
        super.onResume();
        controller.onResume();
    }

    @Override
    public void onDestroy(){
        controller.onDestroy();
        super.onDestroy();
    }

