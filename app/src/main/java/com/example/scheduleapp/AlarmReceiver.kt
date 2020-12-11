package com.example.scheduleapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat


class AlarmReceiver : BroadcastReceiver() {

    companion object{
        const val NOTIFICATION_ID = "notificationId"
        const val NOTIFICATION_CONTENT = "content"
    }

    /**
     * アラーム受信時の処理
     */
    override fun onReceive(context: Context, intent: Intent) {
        val id = intent.getStringExtra(NOTIFICATION_ID)
        val content = intent.getStringExtra(NOTIFICATION_CONTENT)
        //通知チャネルの取得
        createNotificationChannel(context)
        createNotification(context,id,content)
    }

    /**
     * 通知チャネルの設定
     */
    private fun createNotificationChannel(context: Context){
        //通知チャネルのIDの文字列
        val id = "scheduleapp_notification_channel"
        //通知チャネル名
        val name = context.getString(R.string.notification_channel_name)
        //通知チャネルの重要度:中（音は鳴らない）
        val importance = NotificationManager.IMPORTANCE_LOW
        //通知チャネル生成
        val channel = NotificationChannel(id, name, importance)
        //NotificationManagerオブジェクトを取得
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        //通知チャネルを設定
        manager.createNotificationChannel(channel)
    }

    /**
     * 通知の呼び出し
     */
    private fun createNotification(context: Context, id: String?, text: String?){
        //builderクラスの作成
        val builder = NotificationCompat.Builder(context, "scheduleapp_notification_channel")
        //通知エリアに表示されるアイコン
        builder.setSmallIcon(android.R.drawable.ic_dialog_info)
        //表示メッセージ
        builder.setContentText(text)
        //Notificationオブジェクトの作成
        val notification = builder.build()
        //NotificationManagerオブジェクトの取得
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        //通知を送る
        if (id != null) {
            manager.notify(id.toInt(), notification)
        }
    }

}