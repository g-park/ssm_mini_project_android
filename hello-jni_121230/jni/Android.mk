# Copyright (C) 2009 The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
LOCAL_PATH := $(call my-dir)

LOCAL_LDLIBS := -L$(SYSROOT)/usr/lib -llog
LOCAL_CFLAGS := -DCONFIG_EMBEDDED\ -DUSE_IND_THREAD\

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
LOCAL_MODULE    := hello-jni
LOCAL_SRC_FILES := hello-jni.c
LOCAL_STATIC_LIBRARIES := avcodec-prebuilt avfilter-prebuilt avformat-prebuilt avutil-prebuilt postproc-prebuilt swresample-prebuilt swscale-prebuilt
LOCAL_LDLIBS := -L$(SYSROOT)/usr/lib -llog   # 빌드시에 liblog.so를 링크하도록 함!
LOCAL_CFLAGS := -DCONFIG_EMBEDDED -DUSE_IND_THREAD 
include $(BUILD_SHARED_LIBRARY)


