package com.loopeer.android.librarys.imagegroupview;

import android.content.Context;
import android.os.Looper;
import java.util.HashMap;
import java.util.Map;

public class ImageUploadHelper {

    public interface OnImageUploadListener{
        void onImageUploadStart();
        void onImageUploadComplete();
    }

    private Context context;
    private OnImageUploadListener listener;

    ImageUploadHandler handler;

    public ImageUploadHelper(Context context){
        this.context = context;
    }

    public void upload(final HashMap<String, String> map, OnImageUploadListener listener) {
        this.listener = listener;
        handler = new ImageUploadHandler(listener);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                doUpload(map);
                Looper.loop();
            }
        }).start();
    }

    private void doUpload(HashMap<String, String> map) {
        if (map == null || map.isEmpty()) {
            onFinish();
            return;
        }
        handler.onHandleStart();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (entry.getValue() != null && entry.getKey() != null) {
                //doUploadImage(entry.getValue(), entry.getKey(), map);
                break;
            }
        }
    }

    private void doUploadImage(String url, String key, String token, final HashMap<String, String> map) {
/*
        Bitmap scalebmp = ImageUtils.imageZoomByScreen(context, url);
        Bitmap scalebmp2 = ImageUtils.compressImage(scalebmp);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        scalebmp2.compress(Bitmap.CompressFormat.JPEG, 30, byteArrayOutputStream);
        scalebmp2.recycle();
        byte[] bytes = byteArrayOutputStream.toByteArray();
        if (TextUtils.isEmpty(token)) {
            onFinish();
            return;
        }
        UploadManager uploadManager = new UploadManager();
        uploadManager.put(bytes, key, token, new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject response) {
                map.remove(key);
                doUpload(map);
            }
        }, null);
*/
    }

    private void onFinish() {
        handler.onHandleComplete();
    }

}
