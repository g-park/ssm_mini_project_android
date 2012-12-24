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

char * data;
int c_socket;//, s_socket;
struct sockaddr_in c_addr;

char * ID_ADDRESS = "192.168.1.140";

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
    vector<KeyPoint> burf;
    IplImage trans = IplImage(*pMatGr);

    //1 이미지를 압축한다.
    imencode(".jpg", trans, burf,param);

    //2 압축된 이미지의 사이즈를 구한 후 보낸다.
    int bytes = 0;
    int *dataSize = new int[1];
    dataSize[0] = burf.size();
    send(c_socket, dataSize, 4, 0);

    bytes = send(c_socket,burf.data(),burf.size(),0);

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
