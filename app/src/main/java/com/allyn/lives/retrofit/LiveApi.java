package com.allyn.lives.retrofit;

import com.allyn.lives.bean.BooksBean;
import com.allyn.lives.bean.BooksClassifyBean;
import com.allyn.lives.bean.BooksDetailBean;
import com.allyn.lives.bean.TranslationBean;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Administrator on 2016/6/17.
 */
public interface LiveApi {

    /****
     * 翻译结果
     *
     * @param keyfrom
     * @param key
     * @param type
     * @param doctype
     * @param version
     * @param q
     * @return
     */
    @GET("openapi.do")
    rx.Observable<TranslationBean> getTranslationData(@Query("keyfrom") String keyfrom, @Query("key") String key, @Query("type") String type, @Query("doctype") String doctype, @Query("version") String version, @Query("q") String q);

    /***
     * 图书分类
     *
     * @return
     */
    @GET("http://www.tngou.net/api/book/classify")
    Observable<BooksClassifyBean> getBooksClassifyData();


    /***
     * 图书列表
     * http://www.tngou.net/api/book/list?page=1&rows=4&id=4
     *
     * @param page
     * @param rows
     * @param id
     * @return
     */
    @GET("api/book/list")
    Observable<BooksBean> getBooksList(@Query("page") int page, @Query("rows") int rows, @Query("id") int id);

    /***
     * 图书详情
     * http://www.tngou.net/api/book/show?id=125
     *
     * @param id
     * @return
     */
    @GET("api/book/show")
    Observable<BooksDetailBean> getBookDetails(@Query("id") int id);

}
