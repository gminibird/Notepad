package com.yd.notepad.model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.yd.notepad.R;
import com.yd.notepad.activity.MainActivity;
import com.yd.notepad.activity.NotepadActivity;

import java.util.List;

/**
 * Created by a on 2017/12/25.
 */

public class NoteListItem implements RecyclerItem {

    private String content;
    private String time;
    private int id;


    private boolean isChecked = false;
    private boolean checkable = false;

    public NoteListItem(int id, String content, String time){
        this.id=id;
        this.content = content;
        this.time =time;
    }

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public View getView(Context context, ViewGroup parent) {
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        return inflater.inflate(R.layout.note_list_item,parent,false);
    }

    @Override
    public void convert(ViewHolder holder, List dataList, RecyclerAdapter adapter) {
        View itemRootView = holder.getView();
        Context context = itemRootView.getContext();
        TextView contentView = holder.getView(R.id.content);
        TextView timeView = holder.getView(R.id.time);
        CheckBox checkBox = holder.getView(R.id.check_box);
        contentView.setText(content);
        timeView.setText(time);
        checkBox.setChecked(isChecked);
        checkBox.setVisibility(checkable?View.VISIBLE:View.GONE);
        if (isChecked){
            itemRootView.setBackgroundColor(context.getResources().getColor(R.color.colorGreyLight));
        }else {
            itemRootView.setBackgroundColor(context.getResources().getColor(R.color.colorOrigin));
        }
        MyOnGestureListener listener = new MyOnGestureListener(holder,dataList,adapter);
        final GestureDetector detector = new GestureDetector(itemRootView.getContext(),listener);
        itemRootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                detector.onTouchEvent(event);
                return true;
            }
        });
    }

    public void setChecked(boolean isChecked){
        this.isChecked=isChecked;
    }

    public void setCheckable(boolean checkable){
        this.checkable=checkable;
    }
    public boolean isChecked() {
        return isChecked;
    }

    public boolean isCheckable() {
        return checkable;
    }

    public int getId() {
        return id;
    }

    private class MyOnGestureListener extends GestureDetector.SimpleOnGestureListener {
        private ViewHolder holder;
        private List dataList;
        private RecyclerAdapter adapter;
        private View itemRootView;
        private Context context;

        public MyOnGestureListener(ViewHolder holder, List dataList,RecyclerAdapter adapter){
            this.holder=holder;
            this.dataList = dataList;
            this.adapter = adapter;
            itemRootView =  holder.getView();
            context = itemRootView.getContext();
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if (checkable){
                if (!isChecked) {
                    itemRootView.setBackgroundColor(context.getResources().getColor(R.color.colorGreyLight));
                    ((CheckBox) holder.getView(R.id.check_box)).setChecked(true);
                    setChecked(true);
                    return true;
                }
                setChecked(false);
                itemRootView.setBackgroundColor(context.getResources().getColor(R.color.colorOrigin));
                ((CheckBox) holder.getView(R.id.check_box)).setChecked(false);
                return true;
            }else {
                Intent intent = new Intent(context,NotepadActivity.class);
                intent.putExtra("id",id);
                context.startActivity(intent);
            }
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            for (int i=0;i<dataList.size();i++){
                ((NoteListItem)dataList.get(i)).setCheckable(true);
            }
            setChecked(true);
            adapter.notifyDataSetChanged();
            ((MainActivity)context).setSelectAllVisible(true);
            ((MainActivity)context).setDeleteVisible(true);
            ((MainActivity)context).setfButtonVisible(false);
            ((MainActivity)context).setOnBackPressedListener(new MainActivity.OnBackPressedListener() {
                @Override
                public void onBackPressed() {
                    for (int i=0;i<dataList.size();i++){
                        NoteListItem item = ((NoteListItem)dataList.get(i));
                        item.setCheckable(false);
                        item.setChecked(false);
                        adapter.notifyDataSetChanged();
                    }
                    ((MainActivity) context).setOnBackPressedListener(null);
                    ((MainActivity)context).setSelectAllVisible(false);
                    ((MainActivity)context).setDeleteVisible(false);
                    ((MainActivity)context).setfButtonVisible(true);
                }
            });
        }


    }
}
