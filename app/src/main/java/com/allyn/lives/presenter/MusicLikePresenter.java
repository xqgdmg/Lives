package com.allyn.lives.presenter;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;

import com.allyn.lives.activity.music.MusicPlayActivivy;
import com.allyn.lives.app.MainApplication;
import com.allyn.lives.bean.MusicBean;
import com.allyn.lives.fragment.music.MusicLocalLikeFragment;
import com.allyn.lives.service.MusicService;
import com.allyn.lives.utils.Config;
import com.jude.beam.expansion.list.BeamListFragmentPresenter;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 2016/7/11.
 */
public class MusicLikePresenter extends BeamListFragmentPresenter<MusicLocalLikeFragment, MusicBean> implements RecyclerArrayAdapter.OnItemClickListener,SwipeRefreshLayout.OnRefreshListener {


    List<MusicBean> musicBean;

    @Override
    protected void onCreate(@NonNull MusicLocalLikeFragment view, Bundle savedState) {
        super.onCreate(view, savedState);
        onRefresh();
    }

    @Override
    protected void onCreateView(@NonNull MusicLocalLikeFragment view) {
        super.onCreateView(view);
        getView().getListView().setRefreshListener(this);
        if (musicBean.size() == 0) {
            getView().getListView().showError();
        }
    }

    @Override
    public void onRefresh() {
        super.onRefresh();
        getAdapter().clear();
        musicBean = MainApplication.getLiteOrm().query(MusicBean.class);
        Collections.reverse(musicBean);
        getAdapter().addAll(musicBean);
        getAdapter().setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(getView().getActivity(), MusicService.class);
        Bundle bundle = new Bundle();
        bundle.putInt(Config.position_liteorm, position);
        intent.putExtra(Config.bunder, bundle);
        getView().getActivity().startService(intent);
        getView().getActivity().startActivity(new Intent(getView().getActivity(), MusicPlayActivivy.class));

    }
}
