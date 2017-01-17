package com.video.tools;

import android.content.Context;
import android.util.Log;

import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

public class FFmpegHelper {
    private static final String TAG = "FFmpegHelper";
    private static FFmpegHelper fFmpegHelper;
    private FFmpeg fFmpeg;

    private FFmpegHelper(Context context) {
        fFmpeg = FFmpeg.getInstance(context);
        loadBinary();
    }

    public static FFmpegHelper getInstance(Context context) {
        if (fFmpegHelper == null) {
            fFmpegHelper = new FFmpegHelper(context);
        }
        return fFmpegHelper;
    }

    public void killRunningProcesses() {
        if (isFFmpegCommandRunning()) {
            try {
                fFmpeg.killRunningProcesses();
            } catch (Exception ex) {
                Log.i("xxxxx", ex.getMessage());
            }
        }
    }

    public boolean isFFmpegCommandRunning() {
        return fFmpeg.isFFmpegCommandRunning();
    }

    public FFmpeg getFFmpeg() {
        return fFmpeg;
    }

    private void loadBinary() {
        try {
            fFmpeg.loadBinary(new LoadBinaryResponseHandler() {

                @Override
                public void onStart() {
                    Log.i(TAG, "loadBinary onStart");
                }

                @Override
                public void onFailure() {
                    Log.i(TAG, "loadBinary onFailure");
                }

                @Override
                public void onSuccess() {
                    Log.i(TAG, "loadBinary onSuccess");
                }

                @Override
                public void onFinish() {
                    Log.i(TAG, "loadBinary onFinish");
                }
            });
        } catch (FFmpegNotSupportedException e) {
            Log.e(TAG, "FFmpegNotSupportedException" + e.getMessage());
        }
    }

}
