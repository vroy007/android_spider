package com.scut.spider.activitys;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.scut.spider.R;
import com.scut.spider.Tools;
import com.scut.spider.adapter.ContentListAdapter;
import com.scut.spider.adapter.IndexListAdapter;
import com.scut.spider.components.RefreshListView;
import com.scut.spider.model.Constant;
import com.scut.spider.model.NewsItemModel;
import com.scut.spider.presenter.Impl.MainPresenterImpl;
import com.scut.spider.presenter.MainPresenter;
import com.scut.spider.view.MainView;

import java.util.List;

import de.greenrobot.event.EventBus;

public class MainActivity extends Activity implements View.OnClickListener, MainView {

    private static final String TAG = "MainActivity";
    public static final int INDEX = 0;
    public static final int CONTENT = 1;
    public static final int PAGE_COUNT = 2;
    public static final int MORE = 3;
    private int pageIndex = 1; //分页检索
    private int pageCounts = 0; //总记录数
    private int pageSize = 14; //每页加载数据量
    private boolean isRefresh = false; //区分是否刷新
    private String httpUrl = null;
    private Context context;

    private Button btnLeft = null;
    private TextView tvTitle = null;
    private TextView tvPage = null;
    private DrawerLayout lDrawer = null;

    private ListView lvTree = null;
    private IndexListAdapter indexAdapter = null;
    private RefreshListView rlvMain = null;
    private ContentListAdapter contentAdapter = null;

    private boolean isDrawerOpen = false;
    private long exitTime = 0;
    private ProgressDialog dialog;

    //MVP
    private MainPresenter mainPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();
        EventBus.getDefault().register(this);

        mainPresenter = new MainPresenterImpl(this);

        initView();
        initData();
        initEvent();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (System.currentTimeMillis() - exitTime > 2000) {
                Toast.makeText(MainActivity.this, "再按一次退出", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                System.exit(0);  // 退出程序
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initView() {
        btnLeft = (Button) findViewById(R.id.btn_left);
        tvTitle = (TextView) findViewById(R.id.tv_toptitle);
        tvTitle.setText("电信新闻中心");
        tvPage = (TextView) findViewById(R.id.tvPage);
        lDrawer = (DrawerLayout) findViewById(R.id.lDrawer);
        rlvMain = (RefreshListView) findViewById(R.id.rlvMain);
        lvTree = (ListView) findViewById(R.id.lvTree);
    }

    private void initData() {
        indexAdapter = new IndexListAdapter(context);
        lvTree.setAdapter(indexAdapter);
        contentAdapter = new ContentListAdapter(context);
        rlvMain.setAdapter(contentAdapter);

        //刷新，爬虫获取列表内容
        httpUrl = null;
        refreshForUpdate(INDEX);
    }

    private void initEvent() {
        btnLeft.setOnClickListener(this);
        lDrawer.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                isDrawerOpen = true;
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                isDrawerOpen = false;
            }

            @Override
            public void onDrawerStateChanged(int newState) {
            }
        });
        rlvMain.setHeaderRefreshListener(new RefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //下拉刷新
                isRefresh = true;
                pageIndex--;
                loadData();
            }
        });
        rlvMain.setFooterClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //加载更多
                isRefresh = false;
                pageIndex++;
                loadData();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v == btnLeft) {
            if (isDrawerOpen)
                lDrawer.closeDrawer(Gravity.LEFT);
            else
                lDrawer.openDrawer(Gravity.LEFT);
        }
    }

    /*
    EventBus回调
     */
    public void onEventMainThread(NewsItemModel model) {
        //响应导航条的点击事件，都是加载第一个页面，即需要获取总记录数
        if (model != null) {
            lDrawer.closeDrawer(Gravity.LEFT);
            pageIndex = 1;
            //刷新爬虫获取信息
            httpUrl = model.getUrl();
            tvTitle.setText(model.getName());
            refreshForUpdate(INDEX);
        }
    }

    /*
    加载数据（更新数据）
     */
    private void loadData() {
        if(isRefresh) {
            //下拉刷新
            rlvMain.showHeaderLoading();
        }else {
            //查看更多
            rlvMain.showFooterLoading();
        }
        if (pageIndex < 1) {
            rlvMain.showHeaderDone();
            pageIndex = 1;
            return;
        }
        refreshForUpdate(MORE);
    }

    /*
    for index
     */
    private void refreshForUpdate(final int type) {
        if(Tools.isNetworkAvailable(MainActivity.this)) {
            // 显示“正在刷新”窗口
            dialog = new ProgressDialog(this);
            dialog.setMessage("正在刷新...");
            dialog.setCancelable(false);
            dialog.show();
            if (type == INDEX) {
                if (httpUrl == null) {
                    mainPresenter.getPageCount(Constant.NEWS_INDEX_URL);
                    httpUrl = Constant.NEWS_INDEX_URL;
                }
                else
                    mainPresenter.getPageCount(httpUrl);
            }
            else if (type == MORE) {
                mainPresenter.getPageLists(httpUrl, pageCounts / pageSize - pageIndex + 1);
            }

        } else {
            // 弹出提示框
            new AlertDialog.Builder(this)
                    .setTitle("刷新")
                    .setMessage("当前没有网络连接！")
                    .setPositiveButton("重试",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            refreshForUpdate(type);
                        }
                    }).setNegativeButton("退出",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    System.exit(0);  // 退出程序
                }
            }).show();
        }
    }

    @Override
    public void onUpdatePageCount(int count) {
        pageCounts = count;
        Message msg = Message.obtain();
        msg.obj = count;
        msg.what = PAGE_COUNT;
        handler.sendMessage(msg);
    }

    @Override
    public void onUpdateIndexList(List<NewsItemModel> list) {
        Message msg = Message.obtain();
        msg.obj = list;
        msg.what = INDEX;
        handler.sendMessage(msg);
    }

    @Override
    public void onUpdateContentList(List<NewsItemModel> list) {
        Message msg = Message.obtain();
        msg.obj = list;
        msg.what = CONTENT;
        handler.sendMessage(msg);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(msg != null) {
                if(msg.what == INDEX) {
                    //来自导航列表的刷新
                    indexAdapter.setLists((List<NewsItemModel>) msg.obj);
                    indexAdapter.notifyDataSetChanged();
                }
                else if(msg.what == CONTENT) {
                    //来自导航新闻列表的刷新
                    dialog.dismiss();
                    rlvMain.showHeaderDone();
                    List<NewsItemModel> lists = (List<NewsItemModel>) msg.obj;
                    contentAdapter.changeDatas(lists);
                    if (pageIndex < pageCounts / pageSize) {
                        rlvMain.showFooterMore();
                    } else {
                        rlvMain.hiddenFooter();
                    }
                    rlvMain.setSelection(0);
                    if (pageCounts / pageSize > 0)
                        tvPage.setText(pageIndex + "/" + (pageCounts / pageSize));
                    else if (pageCounts > 0)
                        tvPage.setText(pageIndex + "/" + (pageCounts / pageSize + 1));
                    else
                        tvPage.setText("0/0");
                }
                else if (msg.what == PAGE_COUNT) {
                    int pageNum;
                    if (pageCounts == 0)
                        pageNum = -1;
                    else if (pageCounts <= pageSize)
                        pageNum = 1;
                    else
                        pageNum = pageCounts / pageSize;
                    mainPresenter.getPageLists(httpUrl, pageNum);
                }
            }
        }
    };
}