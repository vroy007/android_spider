package com.scut.spider.presenter;

/**
 * Created by shimn on 2016/5/16.
 */
public interface MainPresenter {
    void getPageLists(String url, int pageIndex);
    void getPageCount(String url);
}
