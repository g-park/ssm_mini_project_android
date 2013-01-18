LOCAL_PATH := $(call my-dir)


include $(CLEAR_VARS)
LOCAL_MODULE := avcodec-prebuilt
LOCAL_SRC_FILES := $(TARGET_ARCH_ABI)/ffmpeg_build/lib/libavcodec.a
LOCAL_EXPORT_C_INCLUDES := $(TARGET_ARCH_ABI)/ffmpeg_build/include
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := avfilter-prebuilt
LOCAL_SRC_FILES := $(TARGET_ARCH_ABI)/ffmpeg_build/lib/libavfilter.a
LOCAL_EXPORT_C_INCLUDES := $(TARGET_ARCH_ABI)/ffmpeg_build/include
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := avformat-prebuilt
LOCAL_SRC_FILES := $(TARGET_ARCH_ABI)/ffmpeg_build/lib/libavformat.a
LOCAL_EXPORT_C_INCLUDES := $(TARGET_ARCH_ABI)/ffmpeg_build/include
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := avutil-prebuilt
LOCAL_SRC_FILES := $(TARGET_ARCH_ABI)/ffmpeg_build/lib/libavutil.a
LOCAL_EXPORT_C_INCLUDES := $(TARGET_ARCH_ABI)/ffmpeg_build/include
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := postproc-prebuilt
LOCAL_SRC_FILES := $(TARGET_ARCH_ABI)/ffmpeg_build/lib/libpostproc.a
LOCAL_EXPORT_C_INCLUDES := $(TARGET_ARCH_ABI)/ffmpeg_build/include
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := swresample-prebuilt
LOCAL_SRC_FILES := $(TARGET_ARCH_ABI)/ffmpeg_build/lib/libswresample.a
LOCAL_EXPORT_C_INCLUDES := $(TARGET_ARCH_ABI)/ffmpeg_build/include
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := swscale-prebuilt
LOCAL_SRC_FILES := $(TARGET_ARCH_ABI)/ffmpeg_build/lib/libswscale.a
LOCAL_EXPORT_C_INCLUDES := $(TARGET_ARCH_ABI)/ffmpeg_build/include
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)

OPENCV_CAMERA_MODULES:=off

include ../includeOpenCV.mk
ifeq ("$(wildcard $(OPENCV_MK_PATH))","")
	#try to load OpenCV.mk from default install location
	include $(TOOLCHAIN_PREBUILT_ROOT)/user/share/OpenCV/OpenCV.mk
else
	include $(OPENCV_MK_PATH)
endif

LOCAL_MODULE    := mixed_sample
LOCAL_SRC_FILES := jni_part.cpp
LOCAL_LDLIBS +=  -llog -ldl
LOCAL_STATIC_LIBRARIES := avcodec-prebuilt avfilter-prebuilt avformat-prebuilt avutil-prebuilt postproc-prebuilt swresample-prebuilt swscale-prebuilt

include $(BUILD_SHARED_LIBRARY)
