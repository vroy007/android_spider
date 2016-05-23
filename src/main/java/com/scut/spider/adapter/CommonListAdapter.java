package com.scut.spider.adapter;

import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * 通用的List数据列表适配器的基类
 * @author shimn
 * 2015/8/19
 */
public abstract class CommonListAdapter extends CommonAdapter {

	public CommonListAdapter() {};
	
	public CommonListAdapter(List<?> list) {
		super(list);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return bindView(position, convertView, parent);
	}
	
	/**
	 * 子类实现此方法，初始化每一个Item的View，并且进行数据绑定到View上
	 * @param position 当前数据在list中的索引
	 * @param convertView
	 * @param parent
	 * @return
	 */
	public abstract View bindView(int position, View convertView, ViewGroup parent);
}
