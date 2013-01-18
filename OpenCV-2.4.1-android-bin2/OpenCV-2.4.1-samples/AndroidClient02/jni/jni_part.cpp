#include <jni.h>
#include <opencv2/core/core.hpp>
#include <opencv/cv.h>
#include <opencv/highgui.h>
#include <opencv/cxcore.h>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/features2d/features2d.hpp>
#include <vector>

#include <stdio.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <string.h>


using namespace std;
using namespace cv;
//#define LOG_TAG "FaceDetection/DetectionBasedTracker"
//#define LOGD(...) ((void)__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__))

extern "C" {

#include <math.h>

#include <libavutil/opt.h>
#include <libavcodec/avcodec.h>
#include <libavutil/channel_layout.h>
#include <libavutil/common.h>
#include <libavutil/imgutils.h>
#include <libavutil/mathematics.h>
#include <libavutil/samplefmt.h>


/*
 * Video encoding example
 */
static void video_encode_example(const char *filename, int codec_id)
{
	/*필요한 변수 선언*/
    AVCodec *codec;
    AVCodecContext *c= NULL;
    int i, ret, x, y, got_output;
    FILE *f;
    AVFrame *frame;
    AVPacket pkt;
    uint8_t endcode[] = { 0, 0, 1, 0xb7 };

    printf("Encode video file %s\n", filename);

    /* find the mpeg1 video encoder
     *	mpeg1뿐만 아니라 codec_id 파라매터로 여러가지 ID값이 들어올 수도 있을 뜻
     */
    codec = avcodec_find_encoder((AVCodecID)codec_id);
    if (!codec) {
        fprintf(stderr, "Codec not found\n");
        exit(1);
    }

	/*컨텍스트 초기화*/
    c = avcodec_alloc_context3(codec);
    if (!c) {
        fprintf(stderr, "Could not allocate video codec context\n");
        exit(1);
    }

	/**
     * 컨텍스트 멤버 변수 초기화
     */
    /* put sample parameters */
    c->bit_rate = 400000;
    /* resolution must be a multiple of two */
    c->width = 352;
    c->height = 288;
    /* frames per second */
    c->time_base= (AVRational){1,25};
    c->gop_size = 10; /* emit one intra frame every ten frames */
    c->max_b_frames=1;
    c->pix_fmt = AV_PIX_FMT_YUV420P;

    if(codec_id == AV_CODEC_ID_H264)
        av_opt_set(c->priv_data, "preset", "slow", 0);

    /* open it
     * 열기-코덱과 컨텍스트
     */
    if (avcodec_open2(c, codec, NULL) < 0) {
        fprintf(stderr, "Could not open codec\n");
        exit(1);
    }

	/**읽기 쓰기로 파일을 열기*/
    f = fopen(filename, "wb");
    if (!f) {
        fprintf(stderr, "Could not open %s\n", filename);
        exit(1);
    }

	/*프레임에 할당*/
    frame = avcodec_alloc_frame();
    if (!frame) {
        fprintf(stderr, "Could not allocate video frame\n");
        exit(1);
    }

	/**프레임 멤버변수 초기화? 컨텍스트하고 맞춰주는듯?*/
    frame->format = c->pix_fmt;
    frame->width  = c->width;
    frame->height = c->height;

    /* the image can be allocated by any means and av_image_alloc() is
     * just the most convenient way if av_malloc() is to be used */
    /*
	 *결과로 들어갈 이미지 메모리할당을 해주는건가?
	 */
    ret = av_image_alloc(frame->data, frame->linesize, c->width, c->height,
                         c->pix_fmt, 32);
    if (ret < 0) {
        fprintf(stderr, "Could not allocate raw picture buffer\n");
        exit(1);
    }

    /* encode 1 second of video */
    for(i=0;i<25;i++) {
        av_init_packet(&pkt);
        pkt.data = NULL;    // packet data will be allocated by the encoder
        pkt.size = 0;

        fflush(stdout);
        /* prepare a dummy image */
        /* Y */
        for(y=0;y<c->height;y++) {
            for(x=0;x<c->width;x++) {
                frame->data[0][y * frame->linesize[0] + x] = x + y + i * 3;
            }
        }

        /* Cb and Cr */
        for(y=0;y<c->height/2;y++) {
            for(x=0;x<c->width/2;x++) {
                frame->data[1][y * frame->linesize[1] + x] = 128 + y + i * 2;
                frame->data[2][y * frame->linesize[2] + x] = 64 + x + i * 5;
            }
        }

        frame->pts = i;

        /* encode the image */
        ret = avcodec_encode_video2(c, &pkt, frame, &got_output);
        if (ret < 0) {
            fprintf(stderr, "Error encoding frame\n");
            exit(1);
        }

        if (got_output) {
            printf("Write frame %3d (size=%5d)\n", i, pkt.size);
            fwrite(pkt.data, 1, pkt.size, f);
            av_free_packet(&pkt);
        }
    }

    /* get the delayed frames */
    for (got_output = 1; got_output; i++) {
        fflush(stdout);

        ret = avcodec_encode_video2(c, &pkt, NULL, &got_output);
        if (ret < 0) {
            fprintf(stderr, "Error encoding frame\n");
            exit(1);
        }

        if (got_output) {
            printf("Write frame %3d (size=%5d)\n", i, pkt.size);
            fwrite(pkt.data, 1, pkt.size, f);
            av_free_packet(&pkt);
        }
    }

    /* add sequence end code to have a real mpeg file */
    fwrite(endcode, 1, sizeof(endcode), f);
    fclose(f);

	/*
     *메모리해제
     */
    avcodec_close(c);
    av_free(c);
    av_freep(&frame->data[0]);
    avcodec_free_frame(&frame);
    printf("\n");
}


char * data;
int c_socket;//, s_socket;
struct sockaddr_in c_addr;

char * ID_ADDRESS = "192.168.0.101";//192.168.1.140

vector<int> param = vector<int>(2);

/**
 * 서버와 연결하는 함수
 *
 * */
JNIEXPORT jint JNICALL Java_org_opencv_samples_tutorial4_Sample4View_Connect(JNIEnv* env, jobject thiz ){

			param[0]=CV_IMWRITE_JPEG_QUALITY;
		    param[1]=60;//default(95) 0-100

			c_socket = socket(PF_INET,SOCK_STREAM,0);

	    	memset(&c_addr,0,sizeof(c_addr));
	    	c_addr.sin_addr.s_addr = inet_addr(ID_ADDRESS);
	    	c_addr.sin_family = AF_INET;
	    	c_addr.sin_port = htons(8888);

	    	if(connect(c_socket,(struct sockaddr*) &c_addr, sizeof(c_addr))==-1){
	    		close(c_socket);
	    		return -1;
	    	}
	    	return 0;
}


/**
 * 이미지를 압축해서 보내는 함수
 * */
JNIEXPORT jint JNICALL Java_org_opencv_samples_tutorial4_Sample4View_FindFeatures(JNIEnv* env, jobject thiz, jlong addrGray, jlong addrRgba)
{
    /**
     * 알고리즘
     * 1. 이미지를 압축한다.
     * 2. 압축된 이미지의 사이즈를 구한후 보낸다.
     * 3. 이미지를 보낸다.
     * */

    Mat* pMatGr=(Mat*)addrGray;
    vector<uchar> burf;
    //IplImage trans = IplImage(*pMatGr);

    //1 이미지를 압축한다.
    imencode(".jpg", *pMatGr, burf,param);

    //2 압축된 이미지의 사이즈를 구한 후 보낸다.
    int bytes = 0;
    int *dataSize = new int[1];
    dataSize[0] = burf.size();
    send(c_socket, dataSize, 4, 0);

    bytes = send(c_socket,burf.data(),burf.size(),0);

    return 0;
}

JNIEXPORT jint Java_org_opencv_samples_tutorial4_Sample4View_DisConnect( JNIEnv* env,jobject thiz )
{
		int bytes = 0;
	    int *dataSize = new int[1];
	    dataSize[0] = -2;
	    send(c_socket, dataSize, 4, 0);
	    return 0;
}


JNIEXPORT jint Java_org_opencv_samples_tutorial4_Sample4View_TestFuncTion( JNIEnv* env,jobject thiz )
{
	avcodec_register_all();
	return 0;
}




}
