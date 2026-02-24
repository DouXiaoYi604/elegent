package com.example.demo_redrock.DataBase;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.demo_redrock.Infomation.UserInfo;

public class UserDbHelper extends SQLiteOpenHelper {
    private static UserDbHelper myHelper;
    private static final String Db_Name="user.db";
    private static final int Version=1;

    public UserDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,int version){    //这里面的顺序一定要和实体类里面一样才行
        super(context,name,factory,version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table user_table(user_id integer primary key autoincrement, "
                +"username text,"+"password text,"+"nickname text"+")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    public UserInfo login(String username){
        SQLiteDatabase db=getReadableDatabase();
        UserInfo userInfo=null;
        String sql="select user_id,username,password,nickname from user_table where username=?";
        String[] selectionArray={username};
        Cursor cursor=db.rawQuery(sql,selectionArray);
        if (cursor.moveToNext()){
            @SuppressLint("Range") int user_id=cursor.getInt(cursor.getColumnIndex("user_id"));
            @SuppressLint("Range") String name=cursor.getString(cursor.getColumnIndex("username"));   //注意
            @SuppressLint("Range") String password=cursor.getString(cursor.getColumnIndex("password"));
            @SuppressLint("Range") String nickname=cursor.getString(cursor.getColumnIndex("nickname"));
            //todo
            userInfo=new UserInfo(user_id,name,password,nickname);
        }
        cursor.close();
        db.close();
        return userInfo;

    }


    public int register(String username,String password,String nickname){
        SQLiteDatabase db=getReadableDatabase();
        ContentValues values=new ContentValues();
        values.put("username",username);
        values.put("password",password);
        values.put("nickname",nickname);
        String nullColumHack="values(null,?,?,?)";
        int insert=(int) db.insert("user_table",nullColumHack,values);
        db.close();
        return insert;
    }

    public int changePassword(String username,String password){
        SQLiteDatabase db=getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put("password",password);
        int change=db.update("user_table",values,"username=?",new String[]{username});
        db.close();
        return change;

    }

    public synchronized static UserDbHelper getInstance(Context context){
        if (null==myHelper){
            myHelper=new UserDbHelper(context,Db_Name,null, Version);
        }
        return myHelper;
    }


}
