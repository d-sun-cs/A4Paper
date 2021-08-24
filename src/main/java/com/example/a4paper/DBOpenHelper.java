package com.example.a4paper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator.
 */
public class DBOpenHelper extends SQLiteOpenHelper {
    //定义创建数据表dict的SQL语句
    final String CREATE_TABLE_SQL =
            "create table if not exists dict(_id integer primary " +
                    "key autoincrement , word , detail)";
    final String CREATE_TABLE_SQL2 =
            "create table if not exists notebook(_id integer primary " +
                    "key autoincrement , word , detail)";


    public DBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, null, version); //重写构造方法并设置工厂为null
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_SQL);
        db.execSQL(CREATE_TABLE_SQL2);
        //创建单词信息表
    }
    @Override
    // 重写基类的onUpgrade()方法，以便数据库版本更新
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //提示版本更新并输出旧版本信息与新版本信息
        db.execSQL("drop table if exists dict");
        db.execSQL("drop table if exists notebook");

        System.out.println("---版本更新-----" + oldVersion + "--->" + newVersion);
    }
}

