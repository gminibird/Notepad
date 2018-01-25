package com.yd.notepad.model;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by a on 2017/12/25.
 */

public  class  ViewHolder extends  RecyclerView.ViewHolder{

    private View mView;
    private Context mContext;
    private SparseArray<View> mViewMap = new SparseArray<>();


    public <T extends RecyclerItem> ViewHolder(Context context, T item, ViewGroup parent){
        super(item.getView(context,parent));
        mView=itemView;
        mContext = context;
    }

    public View getView(){
        return mView;
    }

    public <T extends View> T getView(int ResId){
        View view=mViewMap.get(ResId);
        if (view==null){
            view = mView.findViewById(ResId);
            mViewMap.put(ResId,view);
        }
        return (T) view;
    }

}
