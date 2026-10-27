package com.shiftbasma.app;

import android.content.Context;
import android.webkit.JavascriptInterface;

public class WebAppInterface {
    Context mContext;

    WebAppInterface(Context c) {
        mContext = c;
    }

    @JavascriptInterface
    public void setShiftAlarms(String checkInTime, float shiftHours) {
        AlarmScheduler.scheduleShiftAlarms(mContext, checkInTime, shiftHours);
    }

    @JavascriptInterface
    public void cancelAlarms() {
        AlarmScheduler.cancelAlarms(mContext);
    }
}
