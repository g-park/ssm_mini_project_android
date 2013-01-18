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


#include <sys/time.h>

#ifndef	__ANDROID_LOG_H__
#define	__ANDROID_LOG_H__

#include <android/log.h>

#define	LOGV(...)	__android_log_print(ANDROID_LOG_VERBOSE, "libnav", __VA_ARGS__)
#define	LOGD(...)	__android_log_print(ANDROID_LOG_DEBUG, "libnav", __VA_ARGS__)
#define	LOGI(...)	__android_log_print(ANDROID_LOG_INFO, "libnav", __VA_ARGS__)
#define	LOGW(...)	__android_log_print(ANDROID_LOG_WARN, "libnav", __VA_ARGS__)
#define	LOGE(...)	__android_log_print(ANDROID_LOG_ERROR, "libnav", __VA_ARGS__)

#endif /* __ANDROID_LOG_H__ */

using namespace std;
using namespace cv;
//#define LOG_TAG "FaceDetection/DetectionBasedTracker"
//#define LOGD(...) ((void)__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__))

extern "C" {

char * data;
int c_socket;//, s_socket;
struct sockaddr_in c_addr;

char * ID_ADDRESS = "192.168.0.14";//"192.168.0.4";//"192.168.0.101";//"192.168.0.92";//"192.168.0.101";//192.168.1.140

vector<int> param = vector<int>(2);

/**시간 측정을 위한 변수 시작*/
struct timeval start, end;

long mtime, seconds, useconds;

/**시간 측정을 위한 변수 끝*/


/**
 * 서버와 연결하는 함수
 *
 * */
JNIEXPORT jint JNICALL Java_org_opencv_samples_tutorial4_Sample4View_Connect(JNIEnv* env, jobject thiz ){

			param[0]=CV_IMWRITE_JPEG_QUALITY;
		    param[1]=70;//default(95) 0-100

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
char buffer[50] = "";

JNIEXPORT jint JNICALL Java_org_opencv_samples_tutorial4_Sample4View_FindFeatures(JNIEnv* env, jobject thiz, jlong addrGray)
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

//    gettimeofday(&start, NULL);
    //2 압축된 이미지의 사이즈를 구한 후 보낸다.
    int bytes = 0;
    int *dataSize = new int[1];
    dataSize[0] = burf.size();
    send(c_socket, dataSize, 4, 0);

    bytes = send(c_socket,burf.data(),burf.size(),0);
//    gettimeofday(&end, NULL);
//       seconds  = end.tv_sec  - start.tv_sec;
//       useconds = end.tv_usec - start.tv_usec;
//       mtime = ((seconds) * 1000 + useconds/1000.0) + 0.5;
//       sprintf(buffer,"time %d",mtime);LOGI(buffer);
    return 0;
}

JNIEXPORT jlong Java_org_opencv_samples_tutorial4_Sample4View_DisConnect( JNIEnv* env,jobject thiz )
{
		int bytes = 0;
	    int *dataSize = new int[1];
	    dataSize[0] = -2;
	    send(c_socket, dataSize, 4, 0);
}

}
