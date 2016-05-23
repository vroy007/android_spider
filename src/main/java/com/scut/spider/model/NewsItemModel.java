package com.scut.spider.model;

import java.io.Serializable;

/**
 * 新闻item
 * Created by shimn on 2016/5/17.
 */
public class NewsItemModel implements Serializable{

    private String name;
    private String url;
    private int type;

    public NewsItemModel(String name, String url, int type) {
        this.name = name;
        this.url = url;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "NewsItemModel{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", type=" + type +
                '}';
    }
}
