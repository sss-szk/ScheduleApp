package com.example.scheduleapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.CalendarView
import android.widget.EditText
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*


class DetailActivity : AppCompatActivity(),
    TimePickerDialogFragment.OnSelectedTimeListener {

    //DBヘルパー
    private val helper = DatabaseHelper(this@DetailActivity)

    private var selectedDate = ""
    private val sdf = SimpleDateFormat("yyyy/MM/dd")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        //インテントからメイン画面で選択されたIDを取得
        val selectedId = intent.getStringExtra("selectedId")

        //IDで検索して結果をEditTextに入力しておく
        //ヘルパーからDB接続オブジェクトを取得
        val db = helper.writableDatabase
        //INSERT用SQLの用意
        val sql = "SELECT * FROM schedule WHERE _id = $selectedId"
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

        //フィールドに日付をセット
        selectedDate = date

        //日付をcalendarViewにセット
        //Calendar型の変数を用意
        val tmpCalendar:Calendar = Calendar.getInstance()
        //Calendarの日付を検索結果に合わせる
        tmpCalendar.time = sdf.parse(date)
        //画面部品を取得
        val calendar = findViewById<CalendarView>(R.id.calendarView)
        //画面部品に日付をセット
        calendar.date = tmpCalendar.timeInMillis

        //画面部品にリスナをセット
        calendar.setOnDateChangeListener(DateChangeListener())

        //EditTextにセット
        val etTime = findViewById<EditText>(R.id.etTime)
        etTime.setText(time.toString())
        val etDesc = findViewById<EditText>(R.id.etDesc)
        etDesc.setText(desc.toString())
        db.close()

        //時間入力用のダイアログをセット
        etTime.setOnClickListener{
            showTimePickerDialog()
        }
    }

    /**
     * 更新ボタンが押されたときの処理
     */
    fun onUpdateButtonClick(view: View) {
        //前画面で選択された行のプライマリIDをインテントから取得
        val selectedId = intent.getStringExtra("selectedId")
        val etTime = findViewById<EditText>(R.id.etTime)
        val etDesc = findViewById<EditText>(R.id.etDesc)

        //ヘルパーからDB接続オブジェクトを取得
        val db = helper.writableDatabase
        //INSERT用SQLの用意
        val sqlUpdate = "UPDATE schedule SET date = ?,time = ?,desc = ? WHERE _id = $selectedId"
        //プリペアドステートメントの取得
        val stmt = db.compileStatement(sqlUpdate)
        //変数のバインド
        stmt.bindString(1, selectedDate)
        stmt.bindString(2, etTime.text.toString())
        stmt.bindString(3, etDesc.text.toString())
        //実行
        stmt.executeUpdateDelete()
        db.close()
        Toast.makeText(applicationContext, R.string.toast_update, Toast.LENGTH_SHORT).show()
        //アクティビティを閉じる
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

    /**
     * カレンダーの日付を変えたときのリスナ
     */
    private inner class DateChangeListener : CalendarView.OnDateChangeListener{
        override fun onSelectedDayChange(calendarView: CalendarView, year: Int, month: Int, dayOfMonth: Int) {
            // monthは0起算のため+1します。
            val displayMonth = month + 1
            selectedDate = "$year/$displayMonth/$dayOfMonth"
        }
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

    override fun onDestroy() {
        //ヘルパーオブジェクトの開放
        helper.close()
        super.onDestroy()
    }
}