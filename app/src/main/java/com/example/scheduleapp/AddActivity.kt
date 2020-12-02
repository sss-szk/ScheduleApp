package com.example.scheduleapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.CalendarView
import android.widget.EditText

class AddActivity : AppCompatActivity(),
    DatePickerDialogFragment.OnSelectedDateListener,
    TimePickerDialogFragment.OnSelectedTimeListener {

    //DBヘルパー
    private val helper = DatabaseHelper(this@AddActivity)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        //日付入力欄の取得
        val etDate = findViewById<EditText>(R.id.etDate)
        //前画面から引き継いだ初期値を設定
        etDate.setText(intent.getStringExtra("selectedDate"))
        //選択時にダイアログを表示するためのリスナ設定
        etDate.setOnClickListener {
            showDatePickerDialog()
        }

        //時間入力欄の取得
        val etTime = findViewById<EditText>(R.id.etTime)
        //選択時にダイアログを表示するためのリスナ設定
        etTime.setOnClickListener{
            showTimePickerDialog()
        }

    }

    private fun showDatePickerDialog() {
        val datePickerDialogFragment = DatePickerDialogFragment()
        datePickerDialogFragment.show(supportFragmentManager, null)
    }

    override fun selectedDate(year: Int, month: Int, date: Int) {
        val text = "$year/${month+1}/$date"
        val etDate = findViewById<EditText>(R.id.etDate)
        etDate.setText(text)
    }

    private fun showTimePickerDialog() {
        val timePickerDialogFragment = TimePickerDialogFragment()
        timePickerDialogFragment.show(supportFragmentManager, null)
    }

    override fun selectedTime(hour: Int, minute: Int) {
        val text = String.format("%02d",hour) + ":" + String.format("%02d",minute)
        val etTime = findViewById<EditText>(R.id.etTime)
        etTime.setText(text)
    }

    /**
     * 作成ボタン押下時のリスナ
     */
    fun onMakeButtonClick(view: View){
        //入力内容をDBに登録する処理
        val etDate = findViewById<EditText>(R.id.etDate)
        val etDateText = etDate.text.toString()
        val etTime = findViewById<EditText>(R.id.etTime)
        val etTimeText = etTime.text.toString()
        val etDesc = findViewById<EditText>(R.id.etDesc)
        val etDescText = etDesc.text.toString()

        //ヘルパーからDB接続オブジェクトを取得
        val db = helper.writableDatabase
        //INSERT用SQLの用意
        val sqlInsert = "INSERT INTO schedule (date,time,desc) VALUES (?,?,?)"
        //プリペアドステートメントの取得
        val stmt = db.compileStatement(sqlInsert)
        //変数のバインド
        stmt.bindString(1,etDateText)
        stmt.bindString(2,etTimeText)
        stmt.bindString(3,etDescText)
        //実行
        stmt.executeInsert()
        db.close()

        finish()
    }

    override fun onDestroy() {
        //ヘルパーオブジェクトの開放
        helper.close()
        super.onDestroy()
    }
}