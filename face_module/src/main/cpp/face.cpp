#include <jni.h>
#include <string>
#include "android/log.h"
#include <opencv2/opencv.hpp>
#include <android/native_window_jni.h>
#include "yunet.h"

#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,"TAG",__VA_ARGS__);

using namespace cv;
using namespace std;

ANativeWindow *window = 0;
Ptr<YuNet> yuNet;

extern "C"
JNIEXPORT void JNICALL
Java_com_li_face_FaceSDK_init(JNIEnv *env, jobject thiz, jstring model_) {
    const char *model = env->GetStringUTFChars(model_, 0);
    yuNet = makePtr<YuNet>(YuNet(model));
    env->ReleaseStringUTFChars(model_, model);
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_li_face_FaceSDK_faceByImage(JNIEnv *env, jobject thiz, jstring path_) {

    //判断是否初始化
    if (yuNet.empty()) {
        return 0;
    }
    //图片路径
    const char *path = env->GetStringUTFChars(path_, 0);
    Mat src = imread(path);
    if (src.empty()) {
        return 0;
    }

    yuNet->setInputSize(src.size());
    auto faces = yuNet->infer(src);

    int findFace = 0;

    for (int i = 0; i < faces.rows; ++i) {
        int x1 = static_cast<int>(faces.at<float>(i, 0));
        int y1 = static_cast<int>(faces.at<float>(i, 1));
        int w = static_cast<int>(faces.at<float>(i, 2));
        int h = static_cast<int>(faces.at<float>(i, 3));
        float conf = faces.at<float>(i, 14);
        if (conf >= 0.9) {
            findFace++;
        }
    }

    env->ReleaseStringUTFChars(path_, path);

    return findFace;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_li_face_FaceSDK_findFace(JNIEnv *env, jobject thiz, jbyteArray data_, jint width,
                                  jint height, jstring path_) {
    //判断是否初始化
    if (yuNet.empty()) {
        return 0;
    }
    //图片保存路径
    const char *path = env->GetStringUTFChars(path_, 0);
    //摄像头数据
    jbyte *data = env->GetByteArrayElements(data_, 0);
    //NV21
    Mat src = Mat(height + height / 2, width, CV_8UC1, data);
    //判断是否有图片
    if (src.empty()) {
        return 0;
    }
    //转换成rgb
    cvtColor(src, src, COLOR_YUV2BGR_NV21);

    //图片旋转
    rotate(src, src, ROTATE_90_CLOCKWISE);
    //检测
    yuNet->setInputSize(src.size());
    Mat faces = yuNet->infer(src);

    int findFace = 0;

    for (int i = 0; i < faces.rows; ++i) {
        //检测到区域
        int x1 = static_cast<int>(faces.at<float>(i, 0));
        int y1 = static_cast<int>(faces.at<float>(i, 1));
        int w = static_cast<int>(faces.at<float>(i, 2));
        int h = static_cast<int>(faces.at<float>(i, 3));
        //置信度
        float conf = faces.at<float>(i, 14);
        if (conf >= 0.9) {
            findFace++;
            rectangle(src, Rect(x1, y1, w, h), Scalar(255, 0, 0));
        }
    }

    if (findFace > 0) {
        //保存图片
        imwrite(path, src);
    }

    env->ReleaseStringUTFChars(path_, path);
    env->ReleaseByteArrayElements(data_, data, 0);

    return findFace;
}