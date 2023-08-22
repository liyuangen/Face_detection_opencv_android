package com.li.facesample;

import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.view.SurfaceView;

import java.util.List;

public class CameraHelper implements Camera.PreviewCallback, Camera.ErrorCallback {

    public static final int WIDTH = 640;
    public static final int HEIGHT = 480;
    private int mCameraId;
    private Camera mCamera;
    private byte[] buffer;
    private Camera.PreviewCallback mPreviewCallback;
    private Camera.ErrorCallback mErrorCallback;
    private Camera.Size size;

    private SurfaceView surfaceView;
    public CameraHelper(int cameraId, SurfaceView surfaceView) {
        mCameraId = cameraId;
        this.surfaceView = surfaceView;
    }

    public void switchCamera() {
        if (mCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
            mCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
        } else {
            mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        }
        stopPreview();
        startPreview();
    }

    public int getCameraId() {
        return mCameraId;
    }

    public void resumePreview(){
        if (mCamera != null){
            mCamera.startPreview();
        }
    }

    public void pausePreview(){
        if (mCamera != null){
            mCamera.stopPreview();
        }
    }

    public void stopPreview() {
        if (mCamera != null) {

            //预览数据回调接口
            mCamera.setPreviewCallback(null);
            //停止预览
            mCamera.stopPreview();

            mCamera.release();
            mCamera = null;
        }
    }

    public void startPreview() {
        try {
            //获得camera对象
            mCamera = Camera.open(mCameraId);
            //配置camera的属性
            Camera.Parameters parameters = mCamera.getParameters();

//            Camera.Size optimalPreviewSize = getOptimalPreviewSize(parameters.getSupportedVideoSizes(), 640, 480);
//            Log.e("TAG",String.format("size:[w = %s , h = %s]",optimalPreviewSize.width,optimalPreviewSize.height));
            //设置预览数据格式为nv21
            parameters.setPreviewFormat(ImageFormat.NV21);
            //这是摄像头宽、高
            parameters.setPreviewSize(WIDTH, HEIGHT);
            // 设置摄像头 图像传感器的角度、方向
            mCamera.setParameters(parameters);
            size = parameters.getPreviewSize();
            buffer = new byte[WIDTH * HEIGHT * 3 / 2];
            mCamera.setDisplayOrientation(90);
            //数据缓存区
            mCamera.addCallbackBuffer(buffer);
            mCamera.setPreviewCallbackWithBuffer(this);
            //设置预览画面
            mCamera.setErrorCallback(this);
            mCamera.setPreviewDisplay(surfaceView.getHolder());
            mCamera.startPreview();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setAutoFocus(){
        if (mCamera!=null){
            mCamera.autoFocus(null);
        }
    }

    public void setPreviewCallback(Camera.PreviewCallback previewCallback) {
        mPreviewCallback = previewCallback;
    }


    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        mPreviewCallback.onPreviewFrame(data, camera);
        camera.addCallbackBuffer(buffer);
    }

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double aspectTolerance = 0.1;
        double targetRatio = (double) w / h;
        if (sizes == null) {
            return null;
        }
        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        // Try to find an size match aspect ratio and size
        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > aspectTolerance) {
                continue;
            }
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    public void setErrorCallback(Camera.ErrorCallback errorCallback){
        mErrorCallback = errorCallback;
    }

    @Override
    public void onError(int error, Camera camera) {
        if (mErrorCallback!=null){
            mErrorCallback.onError(error, camera);
        }
    }
}
