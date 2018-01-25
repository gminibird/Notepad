package com.yd.notepad.activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.yd.notepad.R;
import com.yd.notepad.db.MyDbHelper;
import com.yd.notepad.model.NoteListItem;
import com.yd.notepad.model.RecyclerAdapter;
import com.yd.notepad.model.RecyclerItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private SQLiteDatabase db;
    private List<RecyclerItem> mDataList = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecyclerAdapter adapter;
    private static final int NOTE_ACTIVITY = 1;
    private boolean isMultiCheckMode = false;
    private OnBackPressedListener mBackPressedListener;
    private LinearLayout selectAll;
    private CheckBox checkAll;
    private LinearLayout deleteSelected;
    private boolean isAllSelected = false;
    FloatingActionButton fButton;
    private  SimpleDateFormat yFormatter = new SimpleDateFormat("yyyy", Locale.CHINA);
    private  SimpleDateFormat ymdFormatter = new SimpleDateFormat("yyyy年MM月dd日",Locale.CHINA);
    private  SimpleDateFormat mdFormatter = new SimpleDateFormat("MM月dd日",Locale.CHINA);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fButton = (FloatingActionButton) findViewById(R.id.floating_button);
        recyclerView = (RecyclerView) findViewById(R.id.main_recycler_view);
        selectAll = (LinearLayout) findViewById(R.id.select_all);
        checkAll = (CheckBox) findViewById(R.id.check_all);
        deleteSelected = (LinearLayout) findViewById(R.id.delete_selected);
        selectAll.setOnClickListener(this);
        checkAll.setOnClickListener(this);
        fButton.setOnClickListener(this);
        deleteSelected.setOnClickListener(this);
        init();
    }


    public void setSelectAllVisible(boolean visible){
        selectAll.setVisibility(visible?View.VISIBLE:View.INVISIBLE);
    }

    public void setDeleteVisible(boolean visiable){
        deleteSelected.setVisibility(visiable?View.VISIBLE:View.GONE);
    }

    public void setfButtonVisible(boolean visible){
        fButton.setVisibility(visible?View.VISIBLE:View.INVISIBLE);
    }

    //初始化
    private void init() {
        MyDbHelper dbHelper = new MyDbHelper(this, NotepadActivity.DATABASE_NAME, null, 1);
        db = dbHelper.getWritableDatabase();
        initDataList();
        adapter = new RecyclerAdapter(this, mDataList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    //初始化数据列表
    private void initDataList() {
        Cursor cursor = db.rawQuery("select * from Note order by time desc", null);
        if (cursor.moveToFirst()) {
            do {
                String content = cursor.getString(cursor.getColumnIndex("content"));
                long time = cursor.getLong(cursor.getColumnIndex("time"));
                Log.e("时间=========",String.valueOf(time));
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                NoteListItem item = new NoteListItem(id, content, formatTime(time));
                mDataList.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    private String formatTime(long time){
        Date currDate = new Date();
        Date date = new Date(time);
        String currYear = yFormatter.format(currDate);
        String year = yFormatter.format(date);
        if (currYear.compareTo(year)>0){
            return ymdFormatter.format(date);
        }else {
            return mdFormatter.format(date);
        }
    }




    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.floating_button:
                Intent intent = new Intent(MainActivity.this, NotepadActivity.class);
                startActivityForResult(intent, NOTE_ACTIVITY);
                break;
            case R.id.select_all:
            case R.id.check_all:
                selectAll();
                break;
            case R.id.delete_selected:
                deleteSelected();
                break;
        }
    }

    //删除选中数据
    private void deleteSelected(){
        for (int i=0;i<mDataList.size();i++){
            NoteListItem item = (NoteListItem) mDataList.get(i);
            if (item.isChecked()){
                mDataList.remove(i);
                db.execSQL("delete from Note where id =?",new String[]{String.valueOf(item.getId())});
            }
        }
        adapter.notifyDataSetChanged();
    }




    private void selectAll() {
        deleteSelected.setVisibility(View.VISIBLE);
        fButton.setVisibility(View.INVISIBLE);
        if (isAllSelected) {
            for (int i = 0; i < mDataList.size(); i++) {
                NoteListItem item = (NoteListItem) mDataList.get(i);
                item.setChecked(false);
                item.setCheckable(true);
            }
            isAllSelected = false;
            checkAll.setChecked(false);
            adapter.notifyDataSetChanged();
        } else {
            for (int i = 0; i < mDataList.size(); i++) {
                NoteListItem item = (NoteListItem) mDataList.get(i);
                item.setChecked(true);
                item.setCheckable(true);
            }
            isAllSelected = true;
            checkAll.setChecked(true);
            adapter.notifyDataSetChanged();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case NOTE_ACTIVITY:
                    mDataList.removeAll(mDataList);
                    initDataList();
                    adapter.notifyDataSetChanged();
                    break;
            }
        }

    }

    @Override
    public void onBackPressed() {

        if (mBackPressedListener != null) {
            mBackPressedListener.onBackPressed();
        } else {
            super.onBackPressed();
        }
    }

    public void setOnBackPressedListener(OnBackPressedListener listener) {
        mBackPressedListener = listener;
    }


    public interface OnBackPressedListener {
        void onBackPressed();
    }


}
