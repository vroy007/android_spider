package com.scut.spider.presenter.Impl;

import android.util.Log;

import com.scut.spider.activitys.MainActivity;
import com.scut.spider.model.Constant;
import com.scut.spider.model.NewsItemModel;
import com.scut.spider.presenter.MainPresenter;
import com.scut.spider.view.MainView;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shimn on 2016/5/16.
 */
public class MainPresenterImpl implements MainPresenter {

    private static final String TAG = "MainPresenterImpl";
    private MainView mainView;

    private List<NewsItemModel> indexLists = null;
    private List<NewsItemModel> contentLists = null;
    private String contentUrl = null; //内容url
    private String pageUrl = null; //总页数url
    private int pageIndex = 0;

    public MainPresenterImpl(MainView mainView) {
        this.mainView = mainView;
        indexLists = new ArrayList<>();
        contentLists = new ArrayList<>();
    }

    @Override
    public void getPageLists(String url, int pageIndex) {
        this.contentUrl = url;
        this.pageIndex = pageIndex;
        if (url.equals(Constant.NEWS_INDEX_URL))
            indexLists.clear();
        contentLists = new ArrayList<>();
        new Thread(runnableForIndex).start();
    }

    @Override
    public void getPageCount(String url) {
        this.pageUrl = url.split("list")[0] + "i.htm";
        new Thread(runnableForPageCount).start();
    }

    /*
    获取总记录数
     */
    Runnable runnableForPageCount = new Runnable() {
        @Override
        public void run() {
            Connection conn = null;
            Document doc = null;
            conn = Jsoup.connect(pageUrl);

            // 修改http包中的header,伪装成浏览器进行抓取
            conn.header("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:32.0) Gecko/    20100101 Firefox/32.0");
            doc = null;
            try {
                doc = conn.timeout(5000).get();
            } catch (IOException e) {
                Log.e(TAG, e.toString());
            }
            String str = doc.getAllElements().text().split(" ")[0];
            mainView.onUpdatePageCount(Integer.parseInt(str));
        }
    };

    /*
    获取新闻导航条信息
     */
    Runnable runnableForIndex = new Runnable() {
        @Override
        public void run() {
            Connection conn = null;
            Document doc = null;
            Elements elements = null;
            if (pageIndex < 0)
                conn = Jsoup.connect(contentUrl);
            else {
                conn = Jsoup.connect(contentUrl.split("list")[0] + "i/" + pageIndex + "/list.htm");
            }

            // 修改http包中的header,伪装成浏览器进行抓取
            conn.header("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:32.0) Gecko/    20100101 Firefox/32.0");
            doc = null;
            try {
                doc = conn.timeout(5000).get();
            } catch (IOException e) {
                Log.e(TAG, e.toString());
            }
            //导航条
            if (contentUrl.equals(Constant.NEWS_INDEX_URL)) {
                elements = doc.select("[height=\"30\"]");
                if(elements != null) {
                    for (Element element : elements) {
                        String url = element.getElementsByTag("a").first().attr("href");
                        if (!url.contains("http")) {
                            url = Constant.SCUT_EE_URL + url;
                        }
                        String name = element.getElementsByTag("a").text();
                        //System.out.println("index---->" + name + ":::" + contentUrl);
                        NewsItemModel model = new NewsItemModel(name, url, MainActivity.INDEX);
                        indexLists.add(model);
                    }
                }
                mainView.onUpdateIndexList(indexLists);
            }

            elements = null;
            //新闻中心内容
            elements = doc.select("[class=\"columnStyle\"]");
            if (elements != null) {
                for (Element element : elements) {
                    String url = element.getElementsByTag("a").first().attr("href");
                    if (!url.contains("http")) {
                        url = Constant.SCUT_EE_URL + url;
                    }
                    String name = element.getElementsByTag("font").text();
                    //System.out.println("content---->" + name + ":::" + contentUrl);
                    NewsItemModel model = new NewsItemModel(name, url, MainActivity.CONTENT);
                    contentLists.add(model);
                }
            }
            mainView.onUpdateContentList(contentLists);
        }
    };

}
