package com.clusterdev.malayalammemes.malayalammemes;

import android.app.Activity;
import android.graphics.Typeface;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


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