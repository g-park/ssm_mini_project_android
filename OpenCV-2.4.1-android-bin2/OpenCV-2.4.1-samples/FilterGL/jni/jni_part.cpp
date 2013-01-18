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

/**��� 痢≪������ 蹂�� ���*/
struct timeval start, end;

long mtime, seconds, useconds;

/**��� 痢≪������ 蹂�� ��/


/**
 * ������곌껐��� �⑥�
 *
 * */
JNIEXPORT jint JNICALL Java_com_filtergl_shader_ActivityFilterGL_Connect(JNIEnv* env, jobject thiz ){

			param[0]=CV_IMWRITE_JPEG_QUALITY;
		    param[1]=90;//default(95) 0-100

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
 * �대�吏�� ����댁� 蹂대����⑥�
 * */
char buffer[50] = "";

JNIEXPORT jint JNICALL Java_com_filtergl_shader_GLRenderer_FindFeatures(JNIEnv* env, jobject thiz, jlong addrGray)
{
    /**
     * ���由ъ�
     * 1. �대�吏�� ������.
     * 2. ������대�吏�� �ъ�利�� 援ы���蹂대���
     * 3. �대�吏�� 蹂대���
     * */

    Mat* pMatGr=(Mat*)addrGray;
    vector<uchar> burf;

    //IplImage trans = IplImage(*pMatGr);

    //1 �대�吏�� ������.

    imencode(".jpg", *pMatGr, burf,param);


//    gettimeofday(&start, NULL);
    //2 ������대�吏�� �ъ�利�� 援ы� ��蹂대���
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

JNIEXPORT jlong Java_com_filtergl_shader_ActivityFilterGL_DisConnect( JNIEnv* env,jobject thiz )
{
		int bytes = 0;
	    int *dataSize = new int[1];
	    dataSize[0] = -2;
	    send(c_socket, dataSize, 4, 0);
}

}
