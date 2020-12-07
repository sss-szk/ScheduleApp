package com.example.scheduleapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import java.text.SimpleDateFormat


class MainActivity : AppCompatActivity() {

    private var selectedDate = ""
    //DBヘルパー
    private val helper = DatabaseHelper(this@MainActivity)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        //通知チャネルの設定
        createNotificationChannel()

        val lvMain = findViewById<ListView>(R.id.lvMain)
        //リストにリスナを設定
        lvMain.onItemClickListener = ListItemClickListener()
        //リストの取得
        reloadListView(lvMain)

        //TODO 通知の送信テスト
        //createNotification()

    }

    override fun onResume() {
        super.onResume()
        val lvMain = findViewById<ListView>(R.id.lvMain)
        //リストの更新
        reloadListView(lvMain)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * リストタップ時のリスナ
     */
    private inner class ListItemClickListener : AdapterView.OnItemClickListener{
        override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            //タップされた項目のidを取得
            //隠しTextViewからプライマリキーの値を取得
            val idPrimary = view?.findViewById<TextView>(R.id.idPrimary)
            val selectedId = idPrimary?.text.toString()

            //idを更新画面に渡して起動
            val intent = Intent(applicationContext, DetailActivity::class.java)
            intent.putExtra("selectedId",selectedId)
            startActivity(intent)
        }
    }

    /**
     * DBから取得したデータをListViewに詰めて表示する処理
     */
    private fun reloadListView(lv: ListView){
        //DBから取ってきた値をリスト表示
        val db = helper.writableDatabase
        //TODO 表示された月の一覧のみ取得するよう調整したい
        val sql = "SELECT * FROM schedule ORDER BY date ASC , time ASC;"
        //SQL実行
        val cursor = db.rawQuery(sql,null)
        //cursorに格納された結果をListViewに詰め替えて表示する処理
        val from = arrayOf("date","time","desc","_id")
        val to = intArrayOf(R.id.tvDateRow,R.id.tvTimeRow,R.id.tvDescRow,R.id.idPrimary)
        val adapter = SimpleCursorAdapter(applicationContext,R.layout.row,cursor,from,to,0)
        lv.adapter = adapter
        db.close()
    }

    /**
     * フローティングアクションボタンをタップしたときのリスナ
     */
    fun onFABClick(view: View){
        val intent = Intent(applicationContext, AddActivity::class.java)
        startActivity(intent)
    }

    /**
     * 通知チャネルの設定
     */
    private fun createNotificationChannel(){
        //通知チャネルのIDの文字列
        val id = "scheduleapp_notification_channel"
        //通知チャネル名
        val name = getString(R.string.notification_channel_name)
        //通知チャネルの重要度:中（音は鳴らない）
        val importance = NotificationManager.IMPORTANCE_LOW
        //通知チャネル生成
        val channel = NotificationChannel(id,name,importance)
        //NotificationManagerオブジェクトを取得
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        //通知チャネルを設定
        manager.createNotificationChannel(channel)

    }

    /**
     * 通知の呼び出し
     */
    fun createNotification(){
        //builderクラスの作成
        val builder = NotificationCompat.Builder(applicationContext,"scheduleapp_notification_channel")
        //通知エリアに表示されるアイコン
        builder.setSmallIcon(android.R.drawable.ic_dialog_info)
        //通知ドロワーでの表示タイトル
        builder.setContentTitle("通知タイトル")
        //表示メッセージ
        builder.setContentText("通知の内容")
        //Notificationオブジェクトの作成
        val notification = builder.build()
        //NotificationManagerオブジェクトの取得
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        //通知を送る
        manager.notify(0,notification)

    }
}