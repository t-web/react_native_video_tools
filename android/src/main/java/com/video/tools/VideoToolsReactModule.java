package com.video.tools;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;

import javax.annotation.Nullable;

public class VideoToolsReactModule extends ReactContextBaseJavaModule {

    private static final String TAG = "VideoToolsReactModule";
    private final ReactApplicationContext mReactContext;
    private String compressInUri;
    private String compressOutPath;

    public VideoToolsReactModule(ReactApplicationContext reactContext) {
        super(reactContext);
        mReactContext = reactContext;
        Config.FileUtils.createApplicationFolder();
//        compress("content://media/external/video/media/1378",null);
    }

    @Override
    public String getName() {
        return "VideoTools";
    }

    @ReactMethod
    public void compress(String _uri, Promise promise) {
        Uri uri = Uri.parse(_uri);
        String inPath = VideoTools.getRealFilePath(getReactApplicationContext(), uri);
        String outPath = Config.FileUtils.getCompressDir() + System.currentTimeMillis() + "out.mp4";
        if (uri != null) {
            try {
                init(getReactApplicationContext(), _uri, outPath, "-y -i " + inPath + " -vcodec mpeg4 -an -q 3 " + outPath);
            } finally {
            }
        } else {
            promise.reject("1", "uri is not valid");
        }
    }
    @ReactMethod
    public void isCompressing(Promise promise) {
        boolean compressing = FFmpegHelper.getInstance(getReactApplicationContext()).isFFmpegCommandRunning();
        promise.resolve(compressing);
    }
    @ReactMethod
    public void deleteFile(String filePath,Promise promise) {
        promise.resolve(FileUtils.deleteFile(filePath));
    }
    @ReactMethod
    public void getCompressProgress(Promise promise) {
        promise.resolve(m_progress);
    }

    private void sendEvent(String eventName, @Nullable Object message) {
        if (mReactContext != null) {
            mReactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                    .emit(eventName, message);
        }
    }
    private WritableMap m_progress = null;

    private void init(final Context context, String _uri, String outPath, final String commandStr) {

        m_progress = null;
        FFmpegHelper.getInstance(context).killRunningProcesses();
        compressInUri = _uri;
        compressOutPath = outPath;
        new Thread((new Runnable() {
            @Override
            public void run() {
                try {
                    FFmpegHelper.getInstance(context).getFFmpeg().execute(commandStr.split(" "), new ExecuteBinaryResponseProgressHandler() {
                        private int p;
                        @Override
                        public void onProgress(float progress, String message) {
        //                    int n = (int) Math.floor(progress * 100);
        //                    if(p != n){
        //                        p = n ;
//                            m_progress = Arguments.createMap();
//                            m_progress.putDouble("progress", progress);
//                            m_progress.putString("uri", compressInUri);
//                            m_progress.putString("state", "onProgress");
//                            Message message1 =Message.obtain();
//                            message1.obj = progress;
//                            handler.sendMessage(message1);
        //                        sendEvent("onCompressProgress", result);
        //                    }

                        }

                        @Override
                        public void onFinish() {
//                            WritableMap result = Arguments.createMap();
//                            result.putString("uri", compressInUri);
//                            result.putString("out", compressOutPath);
//                            result.putString("state", "onFinish");
//                            sendEvent("onCompressProgress", result);
                        }
                    });
                } catch (FFmpegCommandAlreadyRunningException e) {
                    // Handle if FFmpeg is already running
                }
            }
        })).start();
    }

    @ReactMethod
    public void getVideoInfo(String uri, Promise promise) {
        WritableMap result = Arguments.createMap();
        if (TextUtils.isEmpty(uri)) {
            promise.reject("1", "uri is empty");
        } else {
            String path = VideoTools.getRealFilePath(getReactApplicationContext(), Uri.parse(uri));
            result.putDouble("duration", VideoTools.getDuration(path));
            result.putString("thumbnail", VideoTools.getThumbnail(path));
            promise.resolve(result);
        }
    }

    @ReactMethod
    public void getMetadata(@Nullable String uri, @Nullable ReadableArray options, Promise promise) {
        WritableMap result = Arguments.createMap();
        String path = VideoTools.getRealFilePath(getReactApplicationContext(), Uri.parse(uri));
        if (options != null) {
            for (int i = 0; i < options.size(); i++) {
                switch (options.getString(i)) {
                    case "duration":
                        result.putDouble("duration", VideoTools.getDuration(path));
                        break;
                    case "thumbnail":
                        result.putString("thumbnail", VideoTools.getThumbnail(path));
                        break;
                }
            }
        }
        promise.resolve(result);
    }

}
