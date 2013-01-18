/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

#include <string.h>
#include <jni.h>

#include <math.h>
#ifdef __cplusplus
extern "C" {
#endif

#include <libavutil/opt.h>
#include <libavcodec/avcodec.h>
#include <libavutil/channel_layout.h>
#include <libavutil/common.h>
#include <libavutil/imgutils.h>
#include <libavutil/mathematics.h>
#include <libavutil/samplefmt.h>
#include "android/log.h"


#define INBUF_SIZE 4096
#define AUDIO_INBUF_SIZE 20480
#define AUDIO_REFILL_THRESH 4096

#define INBUF_SIZE 4096
#define AUDIO_INBUF_SIZE 20480
#define AUDIO_REFILL_THRESH 4096

#ifndef  FF_MM_MMX
#define  FF_MM_MMX   0x0001
#endif
#ifndef  FF_MM_MMXEXT
#define  FF_MM_MMXEXT   0x0002
#endif
#ifndef  FF_MM_SSE
#define  FF_MM_SSE   0x0008
#endif


/* This is a trivial JNI example where we use a native method
 * to return a new VM String. See the corresponding Java source
 * file located at:
 *
 *   apps/samples/hello-jni/project/src/com/example/hellojni/HelloJni.java
 */
jstring Java_com_example_hellojni_HelloJni_stringFromJNI( JNIEnv* env, jobject thiz )
{
//	avformat_open_input(&gFormatCtx, "/mnt/sdcard/a.m4v", NULL, NULL);
//	(AVFormatContext **ps, const char *filename, AVInputFormat *fmt, AVDictionary **options)
	/*필요한 변수 선언*/

	/**
	 * part1
	 * 인코딩/디코딩에 사용할 변수 선언
	 * */
//	avcodec_init();

	avcodec_register_all();

	/*코덱 객체 선언*/
	AVCodec *codecEncode, *codecDecode;
	/**코덱의 컨텍스트*/
	AVCodecContext *ctxEncode= NULL, *ctxDecode = NULL;

	/**들어오는.. 나가는 파일*/
	FILE *fin, *fout;
	AVFrame *pictureEncoded, *pictureDecoded;

	/*인코딩된 결과?*/
	uint8_t *encoderOut, *picEncodeBuf;
	int encoderOutSize, decoderOutSize;
	int pic_size;

	AVPacket avpkt;
	int got_picture, len;

	const int clip_width = 640;
	const int clip_height = 480;

	int frame = 0;
	uint8_t *decodedOut;
	/**
	 * end of part1*/

	/**
	 * part2
	 *  코덱 초기화/디코더를 위한 picture 구조체
	 *  */
	codecDecode = avcodec_find_encoder(AV_CODEC_ID_H264);
	if (!codecDecode) {
	fprintf(stderr, "codec not found\n");
    __android_log_print(ANDROID_LOG_DEBUG, "DEBUG_TAG", "codec not found : %d , AV_CODEC_ID_H264", CODEC_ID_H264);
	exit(1);
	}

	ctxDecode= avcodec_alloc_context();
	avcodec_get_context_defaults(ctxDecode);
	ctxDecode->flags2 |= CODEC_FLAG2_FAST;
	ctxDecode->pix_fmt = PIX_FMT_YUV420P;
	ctxDecode->width = clip_width;
	ctxDecode->height = clip_height;
	ctxDecode->dsp_mask = (FF_MM_MMX | FF_MM_MMXEXT | FF_MM_SSE);

	if (avcodec_open(ctxDecode, codecDecode) < 0) {
	fprintf(stderr, "could not open codec\n");
	exit(1);
	}

	pictureDecoded= avcodec_alloc_frame();
	avcodec_get_frame_defaults(pictureDecoded);
	pic_size = avpicture_get_size(PIX_FMT_YUV420P, clip_width, clip_height);

	decodedOut = (uint8_t *)malloc(pic_size);
	fout = fopen("/mnt/sdcard/test2_640x480.yuv", "wb");
	if (!fout) {
	fprintf(stderr, "could not open %s\n", "/mnt/sdcard/test2_640x480.yuv");
	exit(1);
	}
	/**
	 * end of part2
	 * */

	/**
	 * part3 코덱 초기화/인코더를 위한 picture 구조체
	 * */
	codecEncode = avcodec_find_encoder(CODEC_ID_H264);
	if (!codecEncode) {
	printf("codec not found\n");
	exit(1);
	}
	    /* put sample parameters */
	ctxEncode->bit_rate = 400000;
	    /* resolution must be a multiple of two */
	ctxEncode->width = clip_width;
	ctxEncode->height = clip_height;
	    /* frames per second */
	ctxEncode->time_base= (AVRational){1,25};
	ctxEncode->gop_size = 10; /* emit one intra frame every ten frames */
	ctxEncode->max_b_frames=1;
	ctxEncode->pix_fmt = AV_PIX_FMT_YUV420P;

	/* open codec for encoder*/
	if (avcodec_open(ctxEncode, codecEncode) < 0) {
	printf("could not open codec\n");
	exit(1);
	}

	//open file to read
	fin = fopen("/mnt/sdcard/test_640x480.yuv", "rb");
	if (!fin) {
	printf("could not open %s\n", "/mnt/sdcard/test_640x480.yuv");
	exit(1);
	}

	/* alloc image and output buffer for encoder*/
	pictureEncoded= avcodec_alloc_frame();
	avcodec_get_frame_defaults(pictureEncoded);

	//encoderOutSize = 100000;
	encoderOut = (uint8_t *)malloc(100000);
	//int size = ctxEncode->width * ctxEncode->height;
	picEncodeBuf = (uint8_t *)malloc(3*pic_size/2); /* size for YUV 420 */
	pictureEncoded->data[0] = picEncodeBuf;
	pictureEncoded->data[1] = pictureEncoded->data[0] + pic_size;
	pictureEncoded->data[2] = pictureEncoded->data[1] + pic_size / 4;
	pictureEncoded->linesize[0] = ctxEncode->width;
	pictureEncoded->linesize[1] = ctxEncode->width / 2;
	pictureEncoded->linesize[2] = ctxEncode->width / 2;

	/**part 4
	 * */
	//encode and decode loop
	int i;
	for(i=0;i<30;i++)
	{
	fflush(stdout);
	//read qcif 1 frame to buufer
	fread(pictureEncoded->data[0],ctxEncode->width * ctxEncode->height, 1, fin);
	fread(pictureEncoded->data[1],ctxEncode->width * ctxEncode->height/4, 1, fin);
	fread(pictureEncoded->data[2],ctxEncode->width * ctxEncode->height/4, 1, fin);
	pictureEncoded->pts = AV_NOPTS_VALUE;

	/* encode frame */
	encoderOutSize = avcodec_encode_video(ctxEncode, encoderOut, 100000, pictureEncoded);
	printf("encoding frame %3d (size=%5d)\n", i, encoderOutSize);
	if(encoderOutSize <= 0)
	continue;

	//send encoderOut to decoder
	avpkt.size = encoderOutSize;
	avpkt.data = encoderOut;
	//decode frame
	len = avcodec_decode_video2(ctxDecode, pictureDecoded, &got_picture, &avpkt);
	if (len < 0) {
	printf("Error while decoding frame %d\n", frame);
	exit(1);
	}
	if (got_picture) {
	printf("len = %d saving frame %3d\n", len, frame);
	fflush(stdout);

	avpicture_layout((AVPicture *)pictureDecoded, ctxDecode->pix_fmt
	, clip_width, clip_height, decodedOut, pic_size);
	fwrite(decodedOut, pic_size, 1, fout);
	frame++;
	}
	}

	fclose(fout);
	fclose(fin);

	avcodec_close(ctxEncode);
	avcodec_close(ctxDecode);
	av_free(ctxEncode);
	av_free(ctxDecode);
	av_free(pictureEncoded);
	av_free(pictureDecoded);

    return (*env)->NewStringUTF(env, "Hello from JNI !");
}

jstring Java_com_example_hellojni_HelloJni_stringFromJNI2( JNIEnv* env,
                                                 jobject thiz )
{
    return (*env)->NewStringUTF(env, "Hello from JNI this is two!! !");
}

#ifdef __cplusplus
}
#endif
