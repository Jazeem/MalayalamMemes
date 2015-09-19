package com.procrastinators.malayalammemes.malayalammemes;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
/**
 * Created by Edwin on 15/02/2015.
 */
public class MainActivity extends RefreshableFragmentActivity {

    // Declaring Your View and Variables

    Toolbar toolbar;
    ViewPager pager;
    ViewPagerAdapter adapter;
    SlidingTabLayout tabs;
    CharSequence Titles[]={"ICU","Troll Malayalam","Favourites"};
    int Numboftabs = 3;
    private int currentPage;
    private TextView topTextView;

    TrollApp application ;
    Tracker tracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        application = (TrollApp) getApplication();
        tracker = application.getDefaultTracker();
        GoogleAnalytics.getInstance(this).setLocalDispatchPeriod(1);
        FileUtil.deleteTempFiles();
        // Creating The Toolbar and setting it as the Toolbar for the activity

//        toolbar = (Toolbar) findViewById(R.id.tool_bar);
//        setSupportActionBar(toolbar);


        // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
        adapter =  new ViewPagerAdapter(getSupportFragmentManager(),getApplicationContext(),Titles,Numboftabs);

        Typeface tf=Typeface.createFromAsset(getAssets(),"fonts/HelveticaNeue-Thin.otf");

        topTextView = (TextView)findViewById(R.id.top_text_view);

        topTextView.setTypeface(tf);

        // Assigning ViewPager View and setting the adapter
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(2);

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tracker.setScreenName((String) Titles[position]);
                tracker.send(new HitBuilders.ScreenViewBuilder().build());
                topTextView.setText(Titles[position]);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //pager.setCurrentItem(1);


        // Assiging the Sliding Tab Layout View
        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setCustomTabView(R.layout.custom_tab, 0);
        //tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.photo_viewer_bottom_strip);
            }
        });

        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(pager);
        FileUtil.deleteTempFiles();
    }

    @Override
    public void onBackPressed() {

        new AlertDialog.Builder(this)
                //.setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Confirm")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                })
                .setNegativeButton("No", null)
                .show();

    }

    @Override
    public void refreshFavourites() {
        adapter.favourites.refreshView();
    }

    @Override
    public LinearLayout getICULinearLayout() {
        return adapter.icu.getLinearLayout();
    }
    @Override
    public LinearLayout getTrollMalayalamLinearLayout() {
        return adapter.trollmalayalam.getLinearLayout();
    }
}