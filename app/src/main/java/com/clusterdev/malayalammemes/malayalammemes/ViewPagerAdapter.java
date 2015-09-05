package com.clusterdev.malayalammemes.malayalammemes;


import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.widget.DrawerLayout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;

/**
 * Created by Edwin on 15/02/2015.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    CharSequence Titles[]; // This will Store the Titles of the Tabs which are Going to be passed when ViewPagerAdapter is created
    int NumbOfTabs; // Store the number of tabs, this will also be passed when the ViewPagerAdapter is created
    public Newsfeed icu, trollmalayalam;
    public Favourite favourites;
    private Context context;
    private int currentPosition;

    private int[][] imageResId = new int[][]{{
                R.drawable.icu,
                R.drawable.troll_malayalam,
                R.drawable.favourites
            },
            {
                R.drawable.icu_grey,
                R.drawable.troll_malayalam_grey,
                R.drawable.favourites_grey
            }};


    // Build a Constructor and assign the passed Values to appropriate values in the class
    public ViewPagerAdapter(FragmentManager fm, Context applicationContext, CharSequence mTitles[], int mNumbOfTabsumb) {
        super(fm);

        this.context = applicationContext;
        this.Titles = mTitles;
        this.NumbOfTabs = mNumbOfTabsumb;
        currentPosition = 0;


        icu = Newsfeed.newInstance("internationalchaluunion");
        trollmalayalam = Newsfeed.newInstance("Troll.Malayalam");
        favourites = new Favourite();

    }



    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {

        switch (position){
            case 0:
                //icu = Newsfeed.newInstance("internationalchaluunion");
                currentPosition = 0;
                return icu;
            case 1:
                //trollmalayalam = Newsfeed.newInstance("Troll.Malayalam");
                currentPosition = 1;
                return trollmalayalam;
            case 2:
                //favourites = new Favourite();
                currentPosition = 2;
                return favourites;
            default:
                //favourites = new Favourite();
                currentPosition = 2;
                return favourites;

        }

    }

    // This method return the titles for the Tabs in the Tab Strip

    @Override
    public CharSequence getPageTitle(int position) {
        Log.v("currentPosition", currentPosition+"");
        Drawable image =  context.getResources().getDrawable(imageResId[1][position]);
        image.setBounds(0, 0, 75, 63);
        SpannableString sb = new SpannableString(" ");
        ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sb;

    }


    // This method return the Number of tabs for the tabs Strip

    @Override
    public int getCount() {
        return NumbOfTabs;
    }
}