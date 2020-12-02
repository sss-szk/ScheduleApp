package com.example.scheduleapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast


class DetailActivity : AppCompatActivity() {

    //DBヘルパー
    private val helper = DatabaseHelper(this@DetailActivity)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        //インテントからメイン画面で選択されたIDを取得
        val selectedId = intent.getStringExtra("selectedId")
        Toast.makeText(applicationContext,selectedId, Toast.LENGTH_SHORT).show()
    }

    /**
     * 更新ボタンが押されたときの処理
     */
    fun onUpdateButtonClick(view: View){
        //テストコメント
        //テストコメント2

    }

    /**
     * 削除ボタンが押されたときの処理
     */
    fun onDeleteButtonClick(view: View){
        //入力内容をDBから削除する処理
        val selectedId = intent.getStringExtra("selectedId")

        //ヘルパーからDB接続オブジェクトを取得
        val db = helper.writableDatabase
        //INSERT用SQLの用意
        val sqlDelete = "DELETE FROM schedule WHERE _id = ?"
        //プリペアドステートメントの取得
        val stmt = db.compileStatement(sqlDelete)
        //変数のバインド
        stmt.bindString(1,selectedId)
        //実行
        stmt.executeUpdateDelete()
        db.close()
        Toast.makeText(applicationContext,R.string.toast_delete, Toast.LENGTH_SHORT).show()
        finish()
    }
}