LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

#include ../includeOpenCV.mk
ifeq ("$(wildcard $(OPENCV_MK_PATH))","")
	#try to load OpenCV.mk from default install location
	include $(TOOLCHAIN_PREBUILT_ROOT)/user/share/OpenCV/OpenCV.mk
else
	include $(OPENCV_MK_PATH)
endif

LOCAL_MODULE    := ocrtextfinder

LOCAL_SRC_FILES := FindTextJNIWrapper.cpp
LOCAL_SRC_FILES += find-text-libs/cpp/AutoPerspective.cpp
LOCAL_SRC_FILES += find-text-libs/cpp/ConnectedRegions.cpp
LOCAL_SRC_FILES += find-text-libs/cpp/ConnectedRegionsLib.cpp
LOCAL_SRC_FILES += find-text-libs/cpp/Lines.cpp
LOCAL_SRC_FILES += find-text-libs/cpp/AnalyzeRegions.cpp
LOCAL_SRC_FILES += find-text-libs/cpp/RegionsCluster.cpp
LOCAL_SRC_FILES += find-text-libs/cpp/FindText.cpp
LOCAL_SRC_FILES += log_helper.cpp

LOCAL_LDLIBS +=  -llog -ldl -ljnigraphics

include $(BUILD_SHARED_LIBRARY)
