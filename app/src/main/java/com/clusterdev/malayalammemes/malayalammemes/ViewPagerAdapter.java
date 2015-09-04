package com.clusterdev.malayalammemes.malayalammemes;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by Edwin on 15/02/2015.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    CharSequence Titles[]; // This will Store the Titles of the Tabs which are Going to be passed when ViewPagerAdapter is created
    int NumbOfTabs; // Store the number of tabs, this will also be passed when the ViewPagerAdapter is created
    public Newsfeed icu, trollmalayalam;
    public Favourite favourites;

    // Build a Constructor and assign the passed Values to appropriate values in the class
    public ViewPagerAdapter(FragmentManager fm,CharSequence mTitles[], int mNumbOfTabsumb) {
        super(fm);

        this.Titles = mTitles;
        this.NumbOfTabs = mNumbOfTabsumb;



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
                return icu;
            case 1:
                //trollmalayalam = Newsfeed.newInstance("Troll.Malayalam");
                return trollmalayalam;
            case 2:
                //favourites = new Favourite();
                return favourites;
            default:
                //favourites = new Favourite();
                return favourites;

        }

    }

    // This method return the titles for the Tabs in the Tab Strip

    @Override
    public CharSequence getPageTitle(int position) {
        return Titles[position];
    }

    // This method return the Number of tabs for the tabs Strip

    @Override
    public int getCount() {
        return NumbOfTabs;
    }
}