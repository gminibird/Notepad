package com.yd.notepad.model;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by a on 2017/12/25.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<ViewHolder> {


    private List<RecyclerItem> mItemList;
    private Context mContext;
    private int mPosition;

    public RecyclerAdapter(Context context, List<RecyclerItem> itemList) {
        mContext = context;
        mItemList = itemList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        MyLog.e("=======================", "onCreateViewHolder() 执行");
        return new ViewHolder(mContext, mItemList.get(mPosition), parent);

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
//        MyLog.e("=======================", "onBindViewHolder() 执行");
        mItemList.get(position).convert(holder, mItemList, this);
    }

    @Override
    public int getItemViewType(int position) {
        mPosition = position;
        return mItemList.get(position).getType();
    }



    @Override
    public int getItemCount() {
        return mItemList.size();
    }


}
