package com.ygb.displayapp.util;

import android.os.AsyncTask;

import com.ygb.displayapp.constants.Constant;
import com.ygb.displayapp.model.DisplaySchedule;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DownloadScheduleTask extends AsyncTask<String, Void, List<DisplaySchedule>> {

    List<DisplaySchedule> list;

    public DownloadScheduleTask() {
        list = new ArrayList<>();
    }

    public List<DisplaySchedule> getList() {
        return list;
    }

    @Override
    protected List<DisplaySchedule> doInBackground(String... strings) {
        String url = strings[0];
        try {
            InputStream in = new URL(url).openStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            bufferedReader.close();
            String response = stringBuilder.toString();
            JSONObject jsonObject = new JSONObject(response);
            JSONArray schedules = jsonObject.getJSONArray(Constant.SCHEDULE);
            for (int i = 0; i < schedules.length(); i++) {
                JSONObject schedule = schedules.getJSONObject(i);
                DisplaySchedule displaySchedule = new DisplaySchedule();
                displaySchedule.setContentType(schedule.getString("contenttype"));
                displaySchedule.setContentLocation(schedule.getString("contentlocation"));
                displaySchedule.setStartDate(new Date(schedule.getLong("startdate")));
                displaySchedule.setEndDate(new Date(schedule.getLong("enddate")));
                list.add(displaySchedule);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getList();
    }
}
