package com.allyn.lives.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.allyn.lives.R;
import com.allyn.lives.app.MainApplication;
import com.allyn.lives.fragment.books.BooksClassifyFragment;
import com.allyn.lives.fragment.books.BooksListFragment;

/**
 * 主页面 FragmentPagerAdapter
 */
public class MainViewPagerAdapter extends FragmentPagerAdapter {

    String[] tab_title;

    public MainViewPagerAdapter(FragmentManager manager) {
        super(manager);
        tab_title = MainApplication.getContexts().getResources().getStringArray(R.array.books_classify_tab);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = BooksClassifyFragment.newInstance();
                break;
            case 1:
                fragment = BooksListFragment.newInstance(9); // setArguments 传值 , getArguments() 在 presenter
                break;
            case 2:
                fragment = BooksListFragment.newInstance(7);
                break;
            case 3:
                fragment = BooksListFragment.newInstance(4);
                break;
            case 4:
                fragment = BooksListFragment.newInstance(3);
                break;
            case 5:
                fragment = BooksListFragment.newInstance(5);
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return tab_title.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tab_title[position];
    }
}
