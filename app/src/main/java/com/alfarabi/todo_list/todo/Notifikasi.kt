package com.alfarabi.todo_list.todo

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.alfarabi.todo_list.R
import com.alfarabi.todo_list.reminder.Common
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class Notifikasi : BroadcastReceiver(){

    companion object{
        const val EXTRA_MESSAGE = "message"
        private const val ID_REMINDER = 100
    }

    override fun onReceive(context: Context, intent: Intent) {
        val message = intent.getStringExtra(EXTRA_MESSAGE) as String
        val title = "Pengingat Tugas"
        val notifId =  ID_REMINDER

        showAlarmNotification(context, title, message, notifId)
    }

    fun setNotifikasi(context: Context, date: String, time: String, message: String) {
        if (isDateInvalid(date, "dd-MM-yy") || isDateInvalid(time, "HH:mm")) return

        val parsedTanggal = Common.convertStringToDate("dd-MM-yy",date)
        val notifikasiTanggal = Common.formatDate(parsedTanggal, "dd-MM-yyyy")

        val notifikasiManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, Notifikasi::class.java)
        intent.putExtra(EXTRA_MESSAGE, message)

        val tanggalArray = notifikasiTanggal.split("-").toTypedArray()
        val waktuArray = time.split(":").toTypedArray()

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(tanggalArray[0]))
        calendar.set(Calendar.MONTH, Integer.parseInt(tanggalArray[1])-1)
        calendar.set(Calendar.YEAR, Integer.parseInt(tanggalArray[2]))
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(waktuArray[0])-1)
        calendar.set(Calendar.MINUTE, Integer.parseInt(waktuArray[1]))
        calendar.set(Calendar.SECOND, 0)

        val pendingIntent = PendingIntent.getBroadcast(context, ID_REMINDER, intent, 0)
        notifikasiManager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
    }

    private fun isDateInvalid(date: String, format: String): Boolean {
        return try {
            val df = SimpleDateFormat(format, Locale.getDefault())
            df.isLenient = false
            df.parse(date)
            false
        } catch (e: ParseException) {
            true
        }
    }

    private fun showAlarmNotification(context: Context, title: String, message: String, notifId: Int) {
        val CHANNEL_ID = "Channel_1"
        val CHANNEL_NAME = "Reminder channel"
        val notificationManagerCompat = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_time)
            .setContentTitle(title)
            .setContentText(message)
            .setColor(ContextCompat.getColor(context, android.R.color.transparent))
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
            .setSound(alarmSound)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT)
            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(1000, 1000, 1000, 1000, 1000)
            builder.setChannelId(CHANNEL_ID)
            notificationManagerCompat.createNotificationChannel(channel)
        }
        val notification = builder.build()
        notificationManagerCompat.notify(notifId, notification)
    }
}