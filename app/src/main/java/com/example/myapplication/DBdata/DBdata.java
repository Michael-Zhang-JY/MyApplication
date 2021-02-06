package com.example.myapplication.DBdata;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBdata extends SQLiteOpenHelper {

    private String sql = "create table commodity1(id integer primary key autoincrement, name, url, uid, status)";


    public DBdata(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DBdata(Context context, String name, int Version){
        this(context, name, null, Version);
    }

    public DBdata(Context context, String name){
        this(context, name, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public String toString() {
        return "DBdata{" +
                "sql='" + sql + '\'' +
                '}';
    }
}