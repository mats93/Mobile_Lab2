package com.example.mobile_lab2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent background = new Intent(context, RSSPullService.class);
        context.startService(background);
    }
}
