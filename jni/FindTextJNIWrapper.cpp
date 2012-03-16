#include <jni.h>
#include <android/bitmap.h>
#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include "find-text-libs/cpp/FindText.hpp"
#include "log_helper.hpp"
#include <list>

using namespace cv;
using namespace std;

extern "C"
JNIEXPORT jobjectArray JNICALL Java_edu_mit_mitmobile2_ocr_TextFinder_findTextImages(
    JNIEnv *env, 
    jobject obj, 
    jbyteArray yuv,                                                                               
    jint width,
    jint height,
    jobject bitmap)
{
    
    AndroidBitmapInfo  info;
    void*            pixels;
    uint8_t*     pixelBytes;
    int                 ret;

    jbyte* _yuv  = env->GetByteArrayElements(yuv, 0);
    
    if ((ret = AndroidBitmap_getInfo(env, bitmap, &info)) < 0) {
        LOGE("AndroidBitmap_getInfo() failed ! error=%d", ret);
        return NULL;
    }
    
    if (info.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
        LOGE("Bitmap format is not RGBA_8888 !");
        return NULL;
    }
    
    
    // creates a Mat with just the gray portion of the camera image
    Mat gray(height, width, CV_8UC1, (unsigned char *)_yuv);
    Mat reduced(info.height, info.width, CV_8UC1);
    resize(gray, reduced, reduced.size());
    
    Mat dest(info.height, info.width, CV_8UC1);
    list<RegionBounds> bounds;
    findTextCandidates(reduced, dest, bounds);

    jclass boundsCls = env->FindClass("edu/mit/mitmobile2/ocr/RegionBounds");
    if (boundsCls == NULL) return NULL;

    jobjectArray jboundsArray = env->NewObjectArray(bounds.size(), boundsCls, NULL);
    if (jboundsArray == NULL) return NULL;

    jmethodID boundsInitID = env->GetMethodID(boundsCls, "<init>", "(IIII)V");

    list<RegionBounds>::iterator boundsIter;
    int i = 0;
    for (boundsIter = bounds.begin(); boundsIter != bounds.end(); boundsIter++, i++) {
        RegionBounds bounds = *boundsIter;
        jobject jbounds = env->NewObject(boundsCls, boundsInitID, bounds.left, bounds.top, bounds.right, bounds.bottom); 
        env->SetObjectArrayElement(jboundsArray, i, jbounds);
    }
    
    if ((ret = AndroidBitmap_lockPixels(env, bitmap, &pixels)) < 0) {
        LOGE("AndroidBitmap_lockPixels() failed ! error=%d", ret);
    }
    
    pixelBytes = (uint8_t*)pixels;
    for (int j=0; j < info.height; j++) {
        for (int i=0; i < info.width; i++) {
            uint8_t color = dest.at<uint8_t>(j, i);
            *pixelBytes = color;
            *(pixelBytes + 1) = color;
            *(pixelBytes + 2) = color;
            *(pixelBytes + 3) = 255;
            pixelBytes += 4;
        }
    }
    LOGD("AAAA");
    
    AndroidBitmap_unlockPixels(env, bitmap);
    env->ReleaseByteArrayElements(yuv, _yuv, 0);
    
    return jboundsArray;
}
