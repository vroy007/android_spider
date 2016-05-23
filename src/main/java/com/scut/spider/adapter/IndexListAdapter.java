package com.scut.spider.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.scut.spider.R;
import com.scut.spider.activitys.MainActivity;
import com.scut.spider.model.NewsItemModel;

import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by shimn on 2016/5/17.
 */
public class IndexListAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater mInflater;
    private List<NewsItemModel> lists;

    public IndexListAdapter(Context context) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
    }

    public IndexListAdapter(Context context, List<NewsItemModel> lists) {
        this.context = context;
        this.lists = lists;
        mInflater = LayoutInflater.from(context);
    }

    public List<NewsItemModel> getLists() {
        return lists;
    }

    public void setLists(List<NewsItemModel> lists) {
        this.lists = lists;
    }

    @Override
    public int getCount() {
        return lists != null ? lists.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return lists != null ? lists.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.expandlist_group, null);
            holder.llItem = (LinearLayout) convertView.findViewById(R.id.ll_list_group);
            holder.tvItem = (TextView) convertView.findViewById(R.id.tv_group_title);
            holder.imgArrow = (ImageView) convertView.findViewById(R.id.img_group);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        NewsItemModel model = lists.get(position);
        holder.tvItem.setText(model.getName());
        holder.imgArrow.setVisibility(View.GONE);

        convertView.setOnClickListener(new itemClickListener(model));

        return convertView;
    }

    class itemClickListener implements View.OnClickListener {

        NewsItemModel model;

        public itemClickListener(NewsItemModel model) {
            this.model = model;
        }

        @Override
        public void onClick(View v) {
            //TODO
            /*String url = model.getCountUrl();
            Toast.makeText(context, url, Toast.LENGTH_SHORT).show();*/
            model.setType(MainActivity.INDEX);
            EventBus.getDefault().post(model);
        }
    }

    class ViewHolder {
        LinearLayout llItem;
        ImageView imgArrow;
        TextView tvItem;
    }
}
