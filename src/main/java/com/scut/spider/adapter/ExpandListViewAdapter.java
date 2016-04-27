package com.scut.spider.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.scut.spider.R;
import com.scut.spider.model.TeacherModel;

import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * 二级菜单适配器
 * Created by victor on 2016/2/25.
 */
public class ExpandListViewAdapter extends BaseExpandableListAdapter {

    private Context context;
    private LayoutInflater mInflater;
    private ExpandableListView listView;
    private List<List<TeacherModel>> list;

    public ExpandListViewAdapter(Context context) {
        this.context = context;
    }

    public ExpandListViewAdapter(Context context, ExpandableListView listView) {
        this.context = context;
        this.listView = listView;
    }

    public ExpandListViewAdapter(Context context, ExpandableListView listView, List<List<TeacherModel>> list) {
        this.context = context;
        this.listView = listView;
        this.list = list;
        mInflater = LayoutInflater.from(context);
    }

    public List<List<TeacherModel>> getList() {
        return list;
    }

    public void setList(List<List<TeacherModel>> list) {
        this.list = list;
    }

    @Override
    public int getGroupCount() {
        return list.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return list.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return list.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return list.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return groupPosition * 100 + childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        listHolder holder = null;
        if (convertView == null) {
            holder = new listHolder();
            convertView = mInflater.inflate(R.layout.expandlist_group, null);
            holder.llList = (LinearLayout) convertView.findViewById(R.id.ll_list_group);
            holder.imgGroup = (ImageView) convertView.findViewById(R.id.img_group);
            holder.tvGroup = (TextView) convertView.findViewById(R.id.tv_group_title);
            convertView.setTag(holder);
        } else {
            holder = (listHolder) convertView.getTag();
        }
        if (groupPosition == 0) {
            holder.tvGroup.setText("博导");
        } else {
            holder.tvGroup.setText("硕导");
        }
        if(isExpanded) {
            holder.imgGroup.setImageResource(android.R.drawable.arrow_up_float);
            convertView.setSelected(true);
        } else {
            holder.imgGroup.setImageResource(android.R.drawable.arrow_down_float);
            convertView.setSelected(false);
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        itemHolder holder = null;
        if (convertView == null) {
            holder = new itemHolder();
            convertView = mInflater.inflate(R.layout.expandlist_item, null);
            holder.llItem = (LinearLayout) convertView.findViewById(R.id.ll_list_item);
            holder.tvItem = (TextView) convertView.findViewById(R.id.tv_item_title);
            convertView.setTag(holder);
        } else {
            holder = (itemHolder) convertView.getTag();
        }

        TeacherModel model = list.get(groupPosition).get(childPosition);
        holder.tvItem.setText(model.getName());

        convertView.setOnClickListener(new itemClickListener(model));

        return convertView;
    }

    class itemClickListener implements OnClickListener {

        TeacherModel model;

        public itemClickListener(TeacherModel model) {
            this.model = model;
        }

        @Override
        public void onClick(View v) {
            //TODO
            /*String url = model.getUrl();
            Toast.makeText(context, url, Toast.LENGTH_SHORT).show();*/
            EventBus.getDefault().post(model);
        }
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public void onGroupExpanded(int groupPosition) {

        for (int i = 0, cnt = getGroupCount(); i < cnt; i++) {
            if(groupPosition != i && listView.isGroupExpanded(i)) {
                listView.collapseGroup(i);
            }
        }
        super.onGroupExpanded(groupPosition);
    }

    class listHolder {
        LinearLayout llList;
        ImageView imgGroup;
        TextView tvGroup;
    }

    class itemHolder {
        LinearLayout llItem;
        TextView tvItem;
    }
}