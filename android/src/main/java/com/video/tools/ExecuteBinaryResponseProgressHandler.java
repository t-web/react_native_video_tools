package com.video.tools;

import android.text.TextUtils;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class ExecuteBinaryResponseProgressHandler extends ExecuteBinaryResponseHandler {
    private String Duration;
    private Date DurationDate;
    private SimpleDateFormat _simpleDateFormat;
    private Date ref;
    private float progress = 0;

    @Override
    public void onStart() {
        try {
            _simpleDateFormat = new SimpleDateFormat("HH:mm:ss.SS");
            ref = _simpleDateFormat.parse("00:00:00.00");
            ref.setYear(100);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        ;
    }

    @Override
    public void onProgress(String message) {
        float oldProgress = progress;
        calProgress(message);
        if (oldProgress != progress) {
            onProgress(progress, message);
        }
    }

    public abstract void onProgress(float progress, String message);

    private void calProgress(String message) {
        if (TextUtils.isEmpty(message)) {
            return;
        }
        if (Duration == null) {
            int i1 = message.indexOf("Duration:");
            int i2 = message.indexOf(", start");
            if (i1 != -1 && i2 != -1) {
                Duration = message.substring(i1 + 10, i2);
                try {
                    DurationDate = _simpleDateFormat.parse(Duration);
                    DurationDate.setYear(100);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                return;
            }
        }
        if (message.indexOf("frame=") == 0) {
            int i1 = message.indexOf("time=");
            int i2 = message.indexOf("bitrate=");
            if (i1 != -1 && i2 != -1) {
                String currentTime = message.substring(i1 + 6, i2).trim();

                try {
                    Date date1 = _simpleDateFormat.parse(currentTime);
                    date1.setYear(100);
                    long total = DurationDate.getTime() - ref.getTime();
                    progress = (float) (date1.getTime() - ref.getTime()) / total;
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                return;
            }
        }
    }

    @Override
    public void onFailure(String message) {
    }

    @Override
    public void onSuccess(String message) {
    }

    public abstract void onFinish();
}
