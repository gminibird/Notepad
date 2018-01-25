package com.yd.notepad.activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.yd.notepad.R;
import com.yd.notepad.db.MyDbHelper;

public class NotepadActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText note;
    private static final int NO_DATA =-1; //数据库中没有该数据
    public static final String DATABASE_NAME = "Note.db";
    private int id=NO_DATA;
    private SQLiteDatabase db;
    private String mOldContent = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notepad);
        ImageView editImg = (ImageView) findViewById(R.id.ic_edit);
        ImageView saveImg = (ImageView) findViewById(R.id.ic_save);
        editImg.setOnClickListener(this);
        saveImg.setOnClickListener(this);
        init();

    }

    //初始化activity
    private void  init(){
        note= (EditText) findViewById(R.id.edit_note);
        MyDbHelper dbHelper = new MyDbHelper(this, "Note.db", null, 1);
        db  = dbHelper.getWritableDatabase();
        Intent intent = getIntent();
        id = intent.getIntExtra("id",NO_DATA);
        if (id!=NO_DATA){
            Cursor cursor = db.rawQuery("select * from Note where id = ?",new String[]{String.valueOf(id)});
            String content="";
            if (cursor.moveToFirst()){
                content = cursor.getString(cursor.getColumnIndex("content"));
            }
            cursor.close();
            note.setText(content);
            mOldContent = content;
            note.setFocusable(false);
            note.setFocusableInTouchMode(false);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ic_edit:
                note.setFocusable(true);
                note.setFocusableInTouchMode(true);
                note.requestFocus();
                note.setSelection(note.getText().toString().length());
                break;
            case R.id.ic_save:
                if (save()){
                    setResult(RESULT_OK);
                    note.setFocusable(false);
                    note.setFocusableInTouchMode(false);
                }
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (save()){
            setResult(RESULT_OK);
        }
    }


    //保存当前文本
    private boolean save(){
        String content = note.getText().toString();
        if (content.equals("")){
            if (id!=NO_DATA){
                db.execSQL("delete from Note where id = ?",new String[]{String.valueOf(id)});
                id=NO_DATA;
                return true;
            }
        }else {
            if (id==NO_DATA){
                db.execSQL("insert into Note (content,time) values(?,?)",new String[]{content,
                        String.valueOf(System.currentTimeMillis())});
                Cursor cursor = db.rawQuery("select id from Note",null);
                if (cursor.moveToLast()){
                    id = cursor.getInt(cursor.getColumnIndex("id"));
                    Toast.makeText(this,"保存成功"+id,Toast.LENGTH_SHORT).show();
                }
                cursor.close();
                return true;
            }else if (!content.equals(mOldContent)){
                db.execSQL("update Note set content =? ,time=? where id =?",
                        new String[]{content,String.valueOf(System.currentTimeMillis()),String.valueOf(id)});
                return true;
            }
        }
        return false;
    }
}
