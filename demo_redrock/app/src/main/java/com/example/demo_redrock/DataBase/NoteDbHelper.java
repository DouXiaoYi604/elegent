package com.example.demo_redrock.DataBase;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.demo_redrock.Infomation.NoteInfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NoteDbHelper extends SQLiteOpenHelper {
    private static NoteDbHelper myHelper;
    private static final String Db_Name="note.db";
    private static final int Version = 1;



    public NoteDbHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }





    //创建笔记表格，有自增id 用户名 笔记 时间
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table note_table(note_id integer primary key autoincrement,"+"username text,"+"note_title text,"+
                "note_content text,"+"note_create_time text"+")");

    }

    //获取当前时间
    private String getCurrentTime(){
        SimpleDateFormat Time=new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return Time.format(new Date());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

     //增
    public int createNote(String username,String note_title,String note_content){
        SQLiteDatabase db=getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put("username",username);
        values.put("note_title",note_title);;
        values.put("note_content",note_content);
        values.put("note_create_time",getCurrentTime());
        String nullColumnHaCK="Values(null,?,?,?,?)";
        int insert=(int) db.insert("note_table",nullColumnHaCK,values);
        db.close();
        return insert;
    }
//查
public List<NoteInfo> queryNoteListData(String username){
    SQLiteDatabase db=getReadableDatabase();
    List<NoteInfo> noteInfoList=new ArrayList<>();
    String sql="select note_id,username,note_title,note_content,note_create_time from note_table where username=?";
    String[] selectionArray={username};
    Cursor cursor=db.rawQuery(sql,selectionArray);
    while (cursor.moveToNext()){
        @SuppressLint("Range") int note_id=cursor.getInt(cursor.getColumnIndex("note_id"));

        //TODO
        @SuppressLint("Range") String name=cursor.getString(cursor.getColumnIndex("username"));
        @SuppressLint("Range") String note_title=cursor.getString(cursor.getColumnIndex("note_title"));
        @SuppressLint("Range") String note_content=cursor.getString(cursor.getColumnIndex("note_content"));
        @SuppressLint("Range") String note_create_time=cursor.getString(cursor.getColumnIndex("note_create_time"));
        noteInfoList.add(new NoteInfo(note_id ,username,note_title,note_content,note_create_time));
    }
    cursor.close();
    db.close();
    return noteInfoList;

}


    //改
    public int updateNote(int note_id, String new_title, String new_content){
        SQLiteDatabase db=getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put("note_title", new_title);
        values.put("note_content", new_content);

        int updateCount = db.update("note_table", values, "note_id=?", new String[]{String.valueOf(note_id)});
        db.close();
        return updateCount;
    }

    //删
    public int deleteNoteById(int note_id){
        SQLiteDatabase db=getWritableDatabase();
        int deleteCount = db.delete("note_table", "note_id=?", new String[]{String.valueOf(note_id)});
        db.close();
        return deleteCount;
    }

    //单例
    public static NoteDbHelper getInstance(Context context) {

        if (myHelper == null) {

            myHelper = new NoteDbHelper(context, Db_Name, null, Version);
        }
        return myHelper;
    }

}
