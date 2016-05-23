package com.scut.spider.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.scut.spider.R;
import com.scut.spider.activitys.DetailsActivity;
import com.scut.spider.model.NewsItemModel;

/**
 * Created by shimn on 2016/5/17.
 */
public class ContentListAdapter extends CommonListAdapter {

    private Context context;
    private LayoutInflater mInflater;

    public ContentListAdapter(Context context) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View bindView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.expandlist_item, null);
            holder.llItem = (LinearLayout) convertView.findViewById(R.id.ll_list_item);
            holder.tvItem = (TextView) convertView.findViewById(R.id.tv_item_title);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        NewsItemModel model = (NewsItemModel) getDatas().get(position);
        holder.tvItem.setText(model.getName());

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
            /*model.setType(MainActivity.CONTENT);
            EventBus.getDefault().post(model);*/
            //TODO activity
            Intent intent = new Intent(context, DetailsActivity.class);
            intent.putExtra(DetailsActivity.KEY, model);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    class ViewHolder {
        LinearLayout llItem;
        TextView tvItem;
    }
}
