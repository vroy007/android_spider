package com.scut.spider.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;
/**
 * 通用的List数据列表适配器的基类，带有缓存
 * @author shimn
 * 2015/8/19
 */
public class CommonAdapter extends BaseAdapter {
	/** 当前适配器中的数据列表 */
	private List list = null;

	public CommonAdapter() {};
	
	public CommonAdapter(List<?> list) {
		this.list = list;
	}

	@Override
	public int getCount() {
		return list == null ? 0 : list.size();
	}

	@Override
	public Object getItem(int position) {
		return list == null ? null : list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return null;
	}

	public List<?> getDatas() {
		return list;
	}

	/**
	 * 往当前ListView中增加数据列表
	 * 
	 * @param datas
	 *            增加的数据列表
	 */
	public void addDatas(List<?> datas) {
		if(list == null)
			list = new ArrayList();
		this.list.addAll(datas);
		notifyDataSetChanged();
	}

	/**
	 * 往当前ListView中增加数据列表
	 * 
	 * @param datas
	 *            增加的数据列表
	 * @param position
	 *            加入的位置
	 */
	public void addDatas(List<?> datas, int position) {
		if(list == null)
			list = new ArrayList();
		if(list.size() + 1 <= position) {
			list.addAll(list.size(), datas);
		}else {
			list.addAll(position, datas);
		}
		notifyDataSetChanged();
	}
	
	/**
	 * 更改当前ListView中的数据列表
	 * 
	 * @param datas
	 *            新的数据列表
	 */
	public void changeDatas(List<?> datas) {
		this.list = datas;
		notifyDataSetChanged();
	}
}
