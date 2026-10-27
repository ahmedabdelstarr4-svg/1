package com.shiftbasma.app;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import java.util.Calendar;

public class AlarmScheduler {

    public static void scheduleShiftAlarms(Context context, String checkInTime, float shiftHours) {
        try {
            String[] parts = checkInTime.split(":");
            int hour = Integer.parseInt(parts[0]);
            int minute = Integer.parseInt(parts[1]);

            Calendar checkInCal = Calendar.getInstance();
            checkInCal.set(Calendar.HOUR_OF_DAY, hour);
            checkInCal.set(Calendar.MINUTE, minute);
            checkInCal.set(Calendar.SECOND, 0);

            if (checkInCal.getTimeInMillis() > System.currentTimeMillis() + 3600000) {
                checkInCal.add(Calendar.DAY_OF_YEAR, -1);
            }

            // 1. تنبيه بصمة المنتصف (بعد 4 ساعات)
            Calendar midCal = (Calendar) checkInCal.clone();
            midCal.add(Calendar.HOUR_OF_DAY, 4);

            // 2. تنبيه الانصراف (حسب ساعات الدوام)
            Calendar endCal = (Calendar) checkInCal.clone();
            int shiftMin = (int) (shiftHours * 60);
            endCal.add(Calendar.MINUTE, shiftMin);

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            if (midCal.getTimeInMillis() > System.currentTimeMillis()) {
                setExactAlarm(context, alarmManager, 101, midCal.getTimeInMillis(), "بصمة منتصف الدوام", "حان موعد تسجيل بصمة المنتصف!");
            }

            if (endCal.getTimeInMillis() > System.currentTimeMillis()) {
                setExactAlarm(context, alarmManager, 102, endCal.getTimeInMillis(), "انتهى الدوام", "حان موعد تسجيل الانصراف!");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void setExactAlarm(Context context, AlarmManager am, int requestCode, long triggerAtMillis, String title, String message) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("title", title);
        intent.putExtra("message", message);
        
        PendingIntent pi = PendingIntent.getBroadcast(
            context, requestCode, intent, 
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pi);
        } else {
            am.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pi);
        }
    }

    public static void cancelAlarms(Context context) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pi1 = PendingIntent.getBroadcast(context, 101, intent, PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE);
        PendingIntent pi2 = PendingIntent.getBroadcast(context, 102, intent, PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE);
        if (pi1 != null) am.cancel(pi1);
        if (pi2 != null) am.cancel(pi2);
    }
}
