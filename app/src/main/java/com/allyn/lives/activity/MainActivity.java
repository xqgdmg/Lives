package com.allyn.lives.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.allyn.lives.R;
import com.allyn.lives.activity.base.BaseActivity;
import com.allyn.lives.app.MainApplication;
import com.allyn.lives.fragment.SettingsFragment;
import com.allyn.lives.fragment.TranslationFragment;
import com.allyn.lives.fragment.books.MainFragment;
import com.allyn.lives.fragment.books.RecommendBooksFragment;
import com.allyn.lives.fragment.music.MusicLocalFragment;
import com.allyn.lives.service.MusicService;
import com.allyn.lives.view.bottontab.BottomBarTab;
import com.allyn.lives.view.bottontab.BottomNavigationBar;

import butterknife.Bind;
import butterknife.ButterKnife;

/*
 * 主页面
 */
public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private boolean isok = true;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.drawer_layout)
    DrawerLayout drawer;
    @Bind(R.id.nav_view)
    NavigationView navigationView;
    @Bind(R.id.bottomLayout)
    BottomNavigationBar bottomLayout;

    MusicService mService;
    boolean mIsBind = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
        setTheme();
        initView();
    }

    /*
     * 设置主题
     */
    private void setTheme() {
        isok = getSharedPreferences("config", MODE_PRIVATE).getBoolean("isUserDarkMode", false);
        setTheme(R.style.AppTheme);
    }

    private void initView() {

         // 配置 DrawerLayout 和 toolbar
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

         // 设置 NavigationView
        navigationView.setNavigationItemSelectedListener(this);

        getSupportFragmentManager().beginTransaction().replace(R.id.container, MainFragment.newInstance()).commitAllowingStateLoss(); // commitAllowingStateLoss

        setUpBottomNavigationBar();

        toolbar.setTitle("图书");
    }

    /*
     * 解绑服务
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mServiceConnection != null)
            unbindService(mServiceConnection);
    }

    /*
     * 打开 DrawerLayout 的时候，点击返回键，关闭 DrawerLayout
     */
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Fragment fragment = null;
        int id = item.getItemId();
        if (id == R.id.nav_gallery) {
            fragment = MainFragment.newInstance();
            toolbar.setTitle("图书");

        } else if (id == R.id.nav_camera) {
            fragment = MusicLocalFragment.newInstance();
            toolbar.setTitle("音乐");

            bottomLayout.setVisibility(View.GONE);
        } else if (id == R.id.nav_manage) {
            fragment = TranslationFragment.newInstance();
            toolbar.setTitle("翻译");

            bottomLayout.setVisibility(View.GONE);
        }
        else if (id == R.id.nav_share) {
            setDarkTheme(isok);
            this.recreate();
            return true;
        } else if (id == R.id.nav_send) {
            fragment = SettingsFragment.newInstance();
            toolbar.setTitle("设置");

            bottomLayout.setVisibility(View.GONE);
        }

         // 替换中间的 container
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .commitAllowingStateLoss();

         // 还要自己关，drawerLayout
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /*
     * 配置夜间主题
     */
    private void setDarkTheme(boolean is) {
        SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("isUserDarkMode", !is);
        editor.commit();
    }

    /*
     * 设置底部导航栏，不可见
     */
    public void setUpBottomNavigationBar() {
        bottomLayout.addTab(R.drawable.ic_classfiy, getResources().getString(R.string.classify), MainApplication.getContexts().getResources().getColor(R.color.colorPrimary));
        bottomLayout.addTab(R.drawable.ic_book_remove, getResources().getString(R.string.recommend), MainApplication.getContexts().getResources().getColor(R.color.colorAccent));
        bottomLayout.setOnTabListener(new BottomNavigationBar.TabListener() {
            @Override
            public void onSelected(BottomBarTab tab, int position) {
                Fragment fragment = null;
                switch (position) {
                    case 0:
                        fragment = MainFragment.newInstance();
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        drawer.closeDrawer(GravityCompat.START);
                        break;
                    case 1:
                        fragment = RecommendBooksFragment.newInstance();
                        break;
                }
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, fragment)
                        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out) // fragment 切换动画，使用系统的动画
                        .commitAllowingStateLoss();
            }
        });
    }

    /*
     * ServiceConnection
     */
    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,IBinder service) {
            MusicService.MyBinder binder = (MusicService.MyBinder) service;
            mService = binder.getService(); // 返回 service
            mIsBind = true; // 返回是否在连接状态
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mIsBind = false;
        }
    };
}