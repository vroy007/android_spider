package com.scut.spider.model;

import android.text.Spanned;

/**
 * Created by victor on 2016/2/27.
 */
public class ContentModel {

    private String title;
    private String time;
    private String countUrl;
    private String content;
    private Spanned htmlContent;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCountUrl() {
        return countUrl;
    }

    public void setCountUrl(String countUrl) {
        this.countUrl = countUrl;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Spanned getHtmlContent() {
        return htmlContent;
    }

    public void setHtmlContent(Spanned htmlContent) {
        this.htmlContent = htmlContent;
    }

    @Override
    public String toString() {
        return "ContentModel{" +
                "title='" + title + '\'' +
                ", time='" + time + '\'' +
                ", countUrl='" + countUrl + '\'' +
                ", content='" + content + '\'' +
                ", htmlContent=" + htmlContent +
                '}';
    }
}
