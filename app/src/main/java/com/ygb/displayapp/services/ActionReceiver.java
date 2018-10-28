package com.ygb.displayapp.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ygb.displayapp.util.Util;

public class ActionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("Data received : " + context.getApplicationInfo() + " / " + intent.getDataString());
        Util.scheduleJob(context);
    }
}