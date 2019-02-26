package com.dyyj.idd.chatmore.weiget;

import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
  * desc
  * @author  zhangcx
  * 2018/12/25 10:47 PM
  */

public abstract class FlowAdapter<T> {
    private List<T> mList;

    public FlowAdapter(List<T> datas) {
        mList = datas;
    }
    public FlowAdapter(T[] datas) {
        mList = new ArrayList<T>(Arrays.asList(datas));
    }

    public T getItem(int position) {
        return mList.get(position);
    }

    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    public abstract View getView(int position);
}
