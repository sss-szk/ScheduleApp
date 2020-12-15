package com.example.scheduleapp

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.CalendarView
import android.widget.EditText
import android.widget.Toast
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

        var checkTimeText = true
        checkTimeText = validationCheck(etTime)

        if(checkTimeText){
            //ヘルパーからDB接続オブジェクトを取得
            val db = helper.writableDatabase
            //INSERT用SQLの用意
            val sqlInsert = "INSERT INTO schedule (date,time,description,update_time) VALUES (?,?,?,?)"
            //プリペアドステートメントの取得
            val stmt = db.compileStatement(sqlInsert)
            //変数のバインド
            stmt.bindString(1, selectedDate)
            stmt.bindString(2, etTimeText)
            stmt.bindString(3, etDescText)
            //現在時刻をupdate_timeに入れる
            val now = Date()
            val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
            stmt.bindString(4,sdf.format(now).toString())
            //実行
            stmt.executeInsert()
            db.close()

            //今追加したスケジュールの通知をセットする
            createScheduledNotification()
            Toast.makeText(applicationContext, R.string.toast_add, Toast.LENGTH_SHORT).show()
            finish()
        }
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
    private fun scheduleNotification(id: String,content: String, calendar: Calendar) {
        val notificationIntent = Intent(this@AddActivity, AlarmReceiver::class.java)
        //NOTIFICATION_ID = 通知固有のID
        notificationIntent.putExtra(AlarmReceiver.NOTIFICATION_ID, id)
        //NOTIFICATION_CONTENT = 通知の表示メッセージ
        notificationIntent.putExtra(AlarmReceiver.NOTIFICATION_CONTENT, content)
        val pendingIntent = PendingIntent.getBroadcast(
            this@AddActivity,
            id.toInt(), //アラームごとの固有ID
            notificationIntent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )
        val alarmManager = applicationContext.getSystemService(ALARM_SERVICE) as AlarmManager
        //指定した時間になったらpendingIntentを飛ばす
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
    }

    /**
     * 今登録したスケジュールで通知を送るように設定する処理
     */
    private fun createScheduledNotification(){
        //ヘルパーからDB接続オブジェクトを取得
        val db = helper.writableDatabase
        //全取得してupdate_timeが最新の行のデータ = 今登録したデータ
        val sql = "SELECT * FROM schedule ORDER BY update_time DESC;"
        //SQL実行
        val cursor = db.rawQuery(sql,null)
        cursor.moveToNext()
        //通知作成に必要な情報を取得
        val idIdx = cursor.getColumnIndex("_id")
        val dateIdx = cursor.getColumnIndex("date")
        val timeIdx = cursor.getColumnIndex("time")
        val descIdx = cursor.getColumnIndex("description")
        val id = cursor.getString(idIdx).toString()
        val date = cursor.getString(dateIdx).toString()
        val time = cursor.getString(timeIdx).toString()
        val desc = cursor.getString(descIdx).toString()

        //dateとtimeからcalendarを生成
        val calendar = Calendar.getInstance()
        //calendarにセットするためにsplitで分割した値を使う
        val splitDate = date.split("/")
        val splitTime = time.split(":")
        //月は-1する
        calendar.set(splitDate[0].toInt(), splitDate[1].toInt() - 1, splitDate[2].toInt(),
            splitTime[0].toInt(), splitTime[1].toInt())

        db.close()

        //通知作成に必要なデータを渡す
        scheduleNotification(id,desc,calendar)
    }

    private fun validationCheck(etTime:EditText) : Boolean{
        if(etTime.text.toString().isEmpty()){
            etTime.requestFocus()
            //画面の下にToastエラーメッセージを表示
            Toast.makeText(applicationContext, R.string.validation_error, Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
}