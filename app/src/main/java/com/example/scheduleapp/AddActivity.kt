package com.example.scheduleapp

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.CalendarView
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*


class AddActivity : AppCompatActivity(),
    TimePickerDialogFragment.OnSelectedTimeListener {

    //DBヘルパー
    private val helper = DatabaseHelper(this@AddActivity)
    private var selectedDate = "";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        //選択されている日付の初期値を設定
        val calendar = findViewById<CalendarView>(R.id.calendarView)
        val sdf = SimpleDateFormat("yyyy/MM/dd")
        selectedDate = sdf.format(calendar.date)
        calendar.setOnDateChangeListener(DateChangeListener())

        //時間入力欄の取得
        val etTime = findViewById<EditText>(R.id.etTime)
        //選択時にダイアログを表示するためのリスナ設定
        etTime.setOnClickListener{
            showTimePickerDialog()
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

    /**
     * 作成ボタン押下時のリスナ
     */
    fun onMakeButtonClick(view: View){
        //入力内容をDBに登録する処理
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
        stmt.bindString(1, selectedDate)
        stmt.bindString(2, etTimeText)
        stmt.bindString(3, etDescText)
        //実行
        stmt.executeInsert()
        db.close()

        //TODO 指定時刻に通知を出すように設定する
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        calendar.add(Calendar.SECOND, 10)
        scheduleNotification("10秒後に届く通知です", calendar)

        finish()
    }

    override fun onDestroy() {
        //ヘルパーオブジェクトの開放
        helper.close()
        super.onDestroy()
    }

    /**
     * カレンダーの日付を変えたときのリスナ
     */
    private inner class DateChangeListener : CalendarView.OnDateChangeListener{
        override fun onSelectedDayChange(
            calendarView: CalendarView,
            year: Int,
            month: Int,
            dayOfMonth: Int
        ) {
            // monthは0起算のため+1します。
            val displayMonth = month + 1
            selectedDate = "$year/$displayMonth/$dayOfMonth"
        }
    }

    /**
     * 指定した時間にintentを飛ばす処理
     */
    private fun scheduleNotification(content: String, calendar: Calendar) {
        val notificationIntent = Intent(this@AddActivity, AlarmReceiver::class.java)
        notificationIntent.putExtra(AlarmReceiver.NOTIFICATION_ID, 1)
        notificationIntent.putExtra(AlarmReceiver.NOTIFICATION_CONTENT, content)
        val pendingIntent = PendingIntent.getBroadcast(
            this@AddActivity,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val alarmManager = applicationContext.getSystemService(ALARM_SERVICE) as AlarmManager
        //指定した時間になったらintentを飛ばす
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent)
    }
}