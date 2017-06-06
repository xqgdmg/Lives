package com.allyn.lives.fragment.books;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.allyn.lives.R;
import com.allyn.lives.adapter.MainViewPagerAdapter;
import com.allyn.lives.fragment.base.BaseFragment;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * 主 fragment
 * tabLayout + ViewPager
 */
public class MainFragment extends BaseFragment {

    @Bind(R.id.tablayout)
    TabLayout mTabLayout;
    @Bind(R.id.viewpager)
    ViewPager mViewpager;

    public static MainFragment newInstance() {
        MainFragment imageMainFragment = new MainFragment();
        return imageMainFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment__img_tab, container, false);
        ButterKnife.bind(this, v);
        initView();
        return v;
    }

    private void initView() {
        MainViewPagerAdapter viewpageradapter = new MainViewPagerAdapter(getChildFragmentManager());
        mViewpager.setAdapter(viewpageradapter);
        mTabLayout.setupWithViewPager(mViewpager); // TabLayout 绑定 ViewPager
    }
}
