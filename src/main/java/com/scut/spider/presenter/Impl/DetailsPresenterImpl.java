package com.scut.spider.presenter.Impl;

import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;

import com.scut.spider.model.Constant;
import com.scut.spider.model.ContentModel;
import com.scut.spider.presenter.DetailsPresenter;
import com.scut.spider.view.DetailsView;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;

/**
 * Created by shimn on 2016/5/18.
 */
public class DetailsPresenterImpl implements DetailsPresenter {

    private DetailsView detailsView;
    private String httpUrl;

    public DetailsPresenterImpl(DetailsView detailsView) {
        this.detailsView = detailsView;
    }

    @Override
    public void getContents(String url) {
        httpUrl = url;
        new Thread(runnableForContent).start();
    }
    /*
    获取导师正文内容的子线程
     */
    Runnable runnableForContent = new Runnable() {
        @Override
        public void run() {

            Connection conn = Jsoup.connect(httpUrl);
            Document doc = null;
            ContentModel model = new ContentModel();
            try {
                doc = conn.timeout(5000).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Elements elements = null;

            if (doc != null) {
                //访问正常时
                //content title
                elements = doc.select("[class=\"biaoti3\"]");
                for (Element element : elements) {
                    model.setTitle(element.text());
                }
                //content time
                elements = doc.select("[height=\"28\"]");
                for (Element element : elements) {
                    model.setTime(element.getElementsByTag("span").text());
                    model.setCountUrl(element.getElementsByTag("img").attr("src"));
                }
                //content
                elements = doc.select("[class=\"content\"]");
                //System.out.println(elements.html());
                //model.setContent(elements.html());
                Spanned sp = Html.fromHtml(elements.html(), new Html.ImageGetter() {
                    @Override
                    public Drawable getDrawable(String source) {
                        Drawable drawable = null;
                        URL url;
                        try {
                            url = new URL(Constant.SCUT_EE_URL + source);
                            drawable = Drawable.createFromStream(url.openStream(), "");
                        } catch (java.io.IOException e) {
                            Log.e("Html.fromHtml", e.toString());
                            return null;
                        }
                        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                                drawable.getIntrinsicHeight());
                        return drawable;
                    }
                }, null);
                model.setHtmlContent(sp);
            } else {
                //访问失效
                model.setTitle("404");
            }

            detailsView.onUpdateContent(model);
        }
    };
}
