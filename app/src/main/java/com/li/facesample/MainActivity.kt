package com.li.facesample

import android.Manifest
import android.hardware.Camera
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceHolder
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.li.face.FaceSDK
import com.li.faceSample.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


class MainActivity : AppCompatActivity(), SurfaceHolder.Callback, Camera.PreviewCallback {

    private var cameraId = 0
    private var mDelayTime = 500L
    private var mRunTime = 0L
    private lateinit var binding: ActivityMainBinding
    private var cameraHelper: CameraHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        XXPermissions.with(this)
            .permission(
                Permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
            ).request { permissions, all ->
                initView()
            }
    }

    override fun onResume() {
        super.onResume()
        cameraHelper?.resumePreview()
    }

    override fun onPause() {
        super.onPause()
        cameraHelper?.pausePreview()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraHelper?.stopPreview()
    }

    private fun initFaceSDK() {
        FaceSDK.getInstance().copyAssets(this, "face_detection.onnx")
        val file = File(filesDir, "face_detection.onnx")
        FaceSDK.getInstance().init(file.absolutePath)
        binding.surfaceView.holder.addCallback(this)
    }

    private fun initView() {
        initFaceSDK()
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        cameraHelper = CameraHelper(cameraId, binding.surfaceView)
        cameraHelper?.setPreviewCallback(this)
        cameraHelper?.startPreview()


    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {

    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
    }


    override fun onPreviewFrame(data: ByteArray, camera: Camera) {

        if (System.currentTimeMillis() - mRunTime < mDelayTime) {
            return
        }
        mRunTime = System.currentTimeMillis()
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val path = File(cacheDir, "img_${System.currentTimeMillis()}.jpg").absolutePath
                val findFace = FaceSDK.getInstance().findFace(data, 640, 480, path)
            }
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {

        when(ev.action){
            MotionEvent.ACTION_DOWN->{
                cameraHelper?.setAutoFocus()
            }
        }
        return super.dispatchTouchEvent(ev)
    }
}

