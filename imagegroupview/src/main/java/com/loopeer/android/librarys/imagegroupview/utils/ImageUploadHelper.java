package com.loopeer.android.librarys.imagegroupview.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImageUploadHelper {

    public interface OnImageUploadListener {
        void onImageUploadStart();

        void onImageUploadComplete();
    }

    private OnImageUploadListener mListener;
    private ImageUploadHandler mHandler;
    private Thread mThread;
    private String mToken;
    private List<String> mUrlList = new ArrayList<>();
    private HashMap<String, String> mTmpMap = new HashMap<>();

    private static ImageUploadHelper INSTANCE;

    public static ImageUploadHelper getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new ImageUploadHelper();
        }
        return INSTANCE;
    }

    //called when finish activity/fragment
    public void stopThread() {
        if (mThread != null) {
            mThread.interrupt();
            mThread = null;
        }
        if (mHandler != null) {
            mHandler = null;
        }
        if (mListener != null) {
            mListener = null;
        }
    }

    public void upload(final HashMap<String, String> map, String token, OnImageUploadListener listener) {
        mTmpMap.clear();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (entry.getValue() != null && entry.getKey() != null) {
                if (!mUrlList.contains(entry.getValue())) {
                    mTmpMap.put(entry.getKey(), entry.getValue());
                    mUrlList.add(entry.getValue());
                }
            }
        }

        this.mListener = listener;
        this.mToken = token;
        mHandler = new ImageUploadHandler(mListener);
        mHandler.onHandleStart();
        doUpload(mTmpMap);
    }

    private void doUpload(final HashMap<String, String> map) {
        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (map == null || map.isEmpty()) {
                    onFinish();
                    return;
                }
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    if (entry.getValue() != null && entry.getKey() != null) {
                        doUploadImage(entry.getValue(), entry.getKey(), map);
                        break;
                    }
                }
            }
        });
        mThread.start();
    }

    private void doUploadImage(String url, String key, final HashMap<String, String> map) {
//        Bitmap scalebmp = ImageUtils.imageZoomByScreen(getAppContext(), url);
//        Bitmap scalebmp2 = ImageUtils.compressImage(scalebmp);
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        scalebmp2.compress(Bitmap.CompressFormat.JPEG, 30, byteArrayOutputStream);
//        scalebmp2.recycle();
//        byte[] bytes = byteArrayOutputStream.toByteArray();
//        if (TextUtils.isEmpty(mToken)) {
//            onFinish();
//            return;
//        }
//
//        Configuration config = new Configuration.Builder()
//                .zone(BuildConfig.DEBUG ? Zone.zone0 : Zone.zoneNa0) // 设置区域，指定不同区域的上传域名、备用域名、备用IP。
//                .build();
//        UploadManager uploadManager = new UploadManager(config);
//        uploadManager.put(bytes, key, mToken, new UpCompletionHandler() {
//            @Override
//            public void complete(String key, ResponseInfo info, JSONObject response) {
//                map.remove(key);
//                doUpload(map);
//            }
//        }, null);

    }

    private void onFinish() {
        mHandler.onHandleComplete();
    }

}