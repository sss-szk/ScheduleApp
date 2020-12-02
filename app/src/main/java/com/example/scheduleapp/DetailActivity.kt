package com.example.scheduleapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast


class DetailActivity : AppCompatActivity(),
    DatePickerDialogFragment.OnSelectedDateListener,
    TimePickerDialogFragment.OnSelectedTimeListener {

    //DBヘルパー
    private val helper = DatabaseHelper(this@DetailActivity)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        //インテントからメイン画面で選択されたIDを取得
        val selectedId = intent.getStringExtra("selectedId")

        //IDで検索してEditTextに入力しておく
        //ヘルパーからDB接続オブジェクトを取得
        val db = helper.writableDatabase
        //INSERT用SQLの用意
        val sql = "SELECT * FROM schedule WHERE _id = ${selectedId}"
        //sql実行
        val cursor = db.rawQuery(sql, null)
        cursor.moveToFirst()
        //実行結果を取り出す
        val idxDate = cursor.getColumnIndex("date")
        val date = cursor.getString(idxDate)
        val idxTime = cursor.getColumnIndex("time")
        val time = cursor.getString(idxTime)
        val idxDesc = cursor.getColumnIndex("desc")
        val desc = cursor.getString(idxDesc)
        //EditTextにセット
        val etDate = findViewById<EditText>(R.id.etDate)
        etDate.setText(date.toString())
        val etTime = findViewById<EditText>(R.id.etTime)
        etTime.setText(time.toString())
        val etDesc = findViewById<EditText>(R.id.etDesc)
        etDesc.setText(desc.toString())
        db.close()

        //リスナの設定
        etDate.setOnClickListener {
            showDatePickerDialog()
        }

        etTime.setOnClickListener{
            showTimePickerDialog()
        }
    }

    /**
     * 更新ボタンが押されたときの処理
     */
    fun onUpdateButtonClick(view: View) {
        val selectedId = intent.getStringExtra("selectedId")
        val etDate = findViewById<EditText>(R.id.etDate)
        val etTime = findViewById<EditText>(R.id.etTime)
        val etDesc = findViewById<EditText>(R.id.etDesc)

        //ヘルパーからDB接続オブジェクトを取得
        val db = helper.writableDatabase
        //INSERT用SQLの用意
        val sqlUpdate = "UPDATE schedule SET date = ?,time = ?,desc = ? WHERE _id = ${selectedId}"
        //プリペアドステートメントの取得
        val stmt = db.compileStatement(sqlUpdate)
        //変数のバインド
        stmt.bindString(1, etDate.text.toString())
        stmt.bindString(2, etTime.text.toString())
        stmt.bindString(3, etDesc.text.toString())
        //実行
        stmt.executeUpdateDelete()
        db.close()
        Toast.makeText(applicationContext, R.string.toast_update, Toast.LENGTH_SHORT).show()
        finish()

    }

    /**
     * 削除ボタンが押されたときの処理
     */
    fun onDeleteButtonClick(view: View) {
        //入力内容をDBから削除する処理
        val selectedId = intent.getStringExtra("selectedId")

        //ヘルパーからDB接続オブジェクトを取得
        val db = helper.writableDatabase
        //INSERT用SQLの用意
        val sqlDelete = "DELETE FROM schedule WHERE _id = ?"
        //プリペアドステートメントの取得
        val stmt = db.compileStatement(sqlDelete)
        //変数のバインド
        stmt.bindString(1, selectedId)
        //実行
        stmt.executeUpdateDelete()
        db.close()
        Toast.makeText(applicationContext, R.string.toast_delete, Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun showDatePickerDialog() {
        val datePickerDialogFragment = DatePickerDialogFragment()
        datePickerDialogFragment.show(supportFragmentManager, null)
    }

    override fun selectedDate(year: Int, month: Int, date: Int) {
        val text = "$year/${month + 1}/$date"
        val etDate = findViewById<EditText>(R.id.etDate)
        etDate.setText(text)
    }

    private fun showTimePickerDialog() {
        val timePickerDialogFragment = TimePickerDialogFragment()
        timePickerDialogFragment.show(supportFragmentManager, null)
    }

    override fun selectedTime(hour: Int, minute: Int) {
        val text = String.format("%02d", hour) + ":" + String.format("%02d", minute)
        val etTime = findViewById<EditText>(R.id.etTime)
        etTime.setText(text)
    }
}