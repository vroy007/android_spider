package com.scut.spider.view;

import com.scut.spider.model.NewsItemModel;

import java.util.List;

/**
 * Created by shimn on 2016/5/16.
 */
public interface MainView {
    void onUpdatePageCount(int count);
    void onUpdateIndexList(List<NewsItemModel> list);
    void onUpdateContentList(List<NewsItemModel> list);
}
