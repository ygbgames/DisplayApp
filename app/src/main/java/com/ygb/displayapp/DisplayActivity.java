package com.ygb.displayapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.VideoView;

import com.ygb.displayapp.constants.Constant;
import com.ygb.displayapp.model.DisplaySchedule;
import com.ygb.displayapp.model.SessionInformation;
import com.ygb.displayapp.util.DownloadImageTask;
import com.ygb.displayapp.util.DownloadScheduleTask;
import com.ygb.displayapp.util.Util;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class DisplayActivity extends AppCompatActivity {

    private BroadcastReceiver broadcastReceiver;
    private ImageView imageView;
    private VideoView videoView;
    private ImageView logout;
    static boolean isLogoutVisible = false;
    static List<DisplaySchedule> list;
    static DisplaySchedule currentDisplayingSchedule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        imageView = (ImageView) findViewById(R.id.imageView);
        videoView = (VideoView) findViewById(R.id.videoView);
        logout = (ImageView) findViewById(R.id.logout);
        registerAlarmReceiver();
        currentDisplayingSchedule=Constant.DEFAULT_SCHEDULE;
        executeScheduledTask(currentDisplayingSchedule);
        // Fetch schedule or load default one
        try {
            list = new DownloadScheduleTask().execute(Constant.URL + "/" + SessionInformation.userId).get();
            System.out.println("Downloaded schedule list size : "+list.size());
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // TODO enable when required
        /*findViewById(R.id.frame).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // enable the logout button
                if(!isLogoutVisible)
                    logout.setVisibility(View.VISIBLE);
                else
                    logout.setVisibility(View.GONE);
                isLogoutVisible=!isLogoutVisible;
                return false;
            }
        });*/
        // TODO set logout image dynamically
        Util.scheduleJob(getApplicationContext());
    }


    private void registerAlarmReceiver() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                /* Dummy execution
                boolean flag = intent.getExtras().getBoolean("type");
                System.out.println("Internal Message Received :" + flag);
                if (flag) { // video
                    imageView.setVisibility(View.GONE);
                    initializePlayer(videoView);
                } else { // image
                    videoView.setVisibility(View.GONE);
                    imageView.setVisibility(View.VISIBLE);
                }*/

                // check for current schedule and display based on it.
                // if list is null display the default else check for current time
                final Date currentTime = new Date();
                for (int i = 0; i < list.size(); i++) {
                    DisplaySchedule schedule = list.get(i);
                    System.out.println("Start Date : "+schedule.getStartDate());
                    System.out.println("End Date : "+schedule.getEndDate());
                    System.out.println("Current Date : "+schedule.getEndDate());
                    if (schedule.getStartDate().before(currentTime) && schedule.getEndDate().after(currentTime)) {
                        if(schedule!=currentDisplayingSchedule) {
                            executeScheduledTask(schedule);
                            currentDisplayingSchedule=schedule;
                        }
                        break;// Once found break
                    }
                }
            }
        };
        registerReceiver(broadcastReceiver, new IntentFilter("com.display"));
        // TODO : unregisterReceiver(broadcastReceiver); once logged out
    }
    private void executeScheduledTask(DisplaySchedule schedule) {
        switch (schedule.getContentType()) {
            case "image":
                new DownloadImageTask(imageView).execute(schedule.getContentLocation());
                imageView.setVisibility(View.VISIBLE);
                videoView.setVisibility(View.GONE);
                break;
            case "video":
                imageView.setVisibility(View.GONE);
                initializePlayer(videoView,schedule.getContentLocation());
                break;
            case "defaultImage":
                videoView.setVisibility(View.GONE);
                imageView.setVisibility(View.VISIBLE);
                imageView.setImageResource(Integer.parseInt(schedule.getContentLocation()));
                break;
        }
    }

    private void initializePlayer(VideoView mVideoView,String url) {
        Uri videoUri = getMedia(url);
        mVideoView.setVideoURI(videoUri);
        mVideoView.setVisibility(View.VISIBLE);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        android.widget.FrameLayout.LayoutParams params = (android.widget.FrameLayout.LayoutParams) mVideoView.getLayoutParams();
        params.width = metrics.widthPixels;
        params.height = metrics.heightPixels;
        params.leftMargin = 0;
        mVideoView.setLayoutParams(params);
        mVideoView.bringToFront();
        mVideoView.start();
    }

    private Uri getMedia(String mediaName) {
        if (URLUtil.isValidUrl(mediaName)) {
            // media name is an external URL
            return Uri.parse(mediaName);
        } else { // media name is a raw resource embedded in the app
            return Uri.parse("android.resource://" + getPackageName() +
                    "/raw/" + mediaName);
        }
    }
}
