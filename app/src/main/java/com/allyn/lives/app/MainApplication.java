package com.allyn.lives.app;

import android.app.Application;

import com.jude.beam.Beam;
import com.jude.utils.JUtils;
import com.litesuits.orm.LiteOrm;

/**
 * Created by Administrator on 2016/3/25.
 */
public class MainApplication extends Application {

    static MainApplication mainApplication;
    static LiteOrm liteOrm;

    @Override
    public void onCreate() {
        mainApplication = this;
        super.onCreate();
        JUtils.initialize(this);
        Beam.init(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        if (liteOrm != null) {
            liteOrm.close();
        }
    }

    public static MainApplication getContexts() {
        return mainApplication;
    }

    /*
     * LiteOrm 是数据库的 orm 框架
     */
    public static LiteOrm getLiteOrm() {
        if (liteOrm == null) {
            liteOrm = LiteOrm.newCascadeInstance(mainApplication.getApplicationContext(), "musiclike.db");
        }
        return liteOrm;
    }
}
