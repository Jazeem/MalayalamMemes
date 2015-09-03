package com.clusterdev.malayalammemes.malayalammemes;

import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;

/**
 * Created by Jazeem on 03/09/15.
 */
public abstract class RefreshableFragmentActivity extends AppCompatActivity{
    public abstract void refreshFavourites();
    public abstract LinearLayout getNewsfeedLinearLayout();
}
