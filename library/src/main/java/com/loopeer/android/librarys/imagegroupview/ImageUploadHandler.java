package com.loopeer.android.librarys.imagegroupview;

import android.os.Handler;
import android.os.Message;

public class ImageUploadHandler extends Handler {

    public final static int HANDLER_START = 1;
    public final static int HANDLER_COMPLETE = 2;

    private ImageUploadHelper.OnImageUploadListener listener;

    public ImageUploadHandler(ImageUploadHelper.OnImageUploadListener listener) {
        this.listener = listener;
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case HANDLER_START:
                listener.onImageUploadStart();
                break;
            case HANDLER_COMPLETE:
                listener.onImageUploadComplete();
                break;
        }
    }

    public void onHandleStart() {
        Message message = new Message();
        message.what = HANDLER_START;
        sendMessage(message);
    }

    public void onHandleComplete() {
        Message message = new Message();
        message.what = HANDLER_COMPLETE;
        sendMessage(message);
    }
}
