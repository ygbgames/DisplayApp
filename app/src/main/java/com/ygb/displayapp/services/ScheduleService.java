package com.ygb.displayapp.services;

import android.app.Service;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

import com.ygb.displayapp.util.Util;

public class ScheduleService extends JobService {
    public static boolean flag = false;

    public ScheduleService() {
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        System.out.println("Service Started");
        Intent messageIntent = new Intent("com.display");
        Bundle extras = new Bundle();
        extras.putBoolean("type", flag);
        messageIntent.putExtras(extras);
        getApplicationContext().sendBroadcast(messageIntent);
        flag = !flag;

        Util.scheduleJob(getApplicationContext()); // reschedule the job
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }

}
