package com.example.scheduleapp

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.lang.StringBuilder

class DatabaseHelper(context : Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    //クラス内のprivate定数を宣言するためにcompanion objectブロックとする
    companion object{
        //データベース名の定数フィールド
        private const val DATABASE_NAME = "schedule.db"
        //バージョン情報のフィールド
        private const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase) {
        //テーブル作成用SQLの作成
        val sb = StringBuilder()
        sb.append("CREATE TABLE schedule (")
        sb.append("_id INTEGER PRIMARY KEY,")
        sb.append("date TEXT,")
        sb.append("time TEXT,")
        sb.append("description TEXT,")
        sb.append("update_time TEXT")
        sb.append(");")
        val sql = sb.toString()

        //SQLの実行
        db.execSQL(sql)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

    }
}