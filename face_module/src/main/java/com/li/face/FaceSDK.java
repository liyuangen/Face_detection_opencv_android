package com.li.face;

import android.content.Context;
import android.graphics.Rect;
import android.view.Surface;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class FaceSDK {

    static {
        System.loadLibrary("face");
    }

    public static FaceSDK INSTANCE = new FaceSDK();

    public static FaceSDK getInstance() {
        return INSTANCE;
    }

    private FaceSDK() {
    }

    public native void init(String model);

    public native int findFace(byte[] data, int width, int height, String path);

    public native int faceByImage(String path);

    public void copyAssets(Context context, String path) {
        File model = new File(path);
        File file = new File(context.getFilesDir(), model.getName());
        if (file.exists()) {
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(file);
            InputStream is = context.getAssets().open(path);
            int len;
            byte[] b = new byte[2048];
            while ((len = is.read(b)) != -1) {
                fos.write(b, 0, len);
            }
            fos.close();
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
