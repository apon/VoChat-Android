package me.apon.vochat.service

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import me.apon.vochat.R
import android.app.NotificationChannel
import me.apon.vochat.features.message.ChatActivity


/**
 * Created by yaopeng(aponone@gmail.com) on 2019/3/8.
 */
object NewMessageNotification {

    private const val NOTIFICATION_TAG = "ChatNewMessage"


    fun notify(
        context: Context,
        fromId: String,
        chatName: String,
        text: String,
        number: Int
    ) {
        val res = context.resources

        // This image is used as the notification's large icon (thumbnail).
        // TODO: Remove this if your notification has no relevant thumbnail.
        val picture = BitmapFactory.decodeResource(res, R.mipmap.ic_launcher)


        val title = "$chatName :"

        val intent = Intent(context, ChatActivity::class.java)
        intent.putExtra("toId", fromId)
        intent.putExtra("chatName", chatName)

        val builder = NotificationCompat.Builder(context, "vochat")


            .setDefaults(Notification.DEFAULT_ALL)
            .setSmallIcon(R.drawable.ic_stat_new_message)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setLargeIcon(picture)
            .setTicker(text)
            .setNumber(number)
            .setFullScreenIntent(
                PendingIntent.getActivity(
                    context,
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                ), true
            )
            .setAutoCancel(true)

        notify(context, builder.build(), fromId)
    }

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    private fun notify(context: Context, notification: Notification, id: String) {
        val nm = context
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mChannel = NotificationChannel("vochat", "vochat", NotificationManager.IMPORTANCE_DEFAULT)
            nm.createNotificationChannel(mChannel)
        }

        nm.notify(NOTIFICATION_TAG, id.toInt(), notification)
//        nm.notify(id.toInt(), notification)

    }


    fun cancel(context: Context, toId: String) {
        val nm = context
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.cancel(NOTIFICATION_TAG, toId.toInt())

    }


}
