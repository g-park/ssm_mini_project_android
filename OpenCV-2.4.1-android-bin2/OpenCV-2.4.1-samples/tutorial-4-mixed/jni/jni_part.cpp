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

JNIEXPORT jint JNICALL Java_org_opencv_samples_tutorial4_Sample4View_Connect(JNIEnv* env, jobject thiz ){
	c_socket = socket(PF_INET,SOCK_STREAM,0);

	    	memset(&c_addr,0,sizeof(c_addr));
	    	c_addr.sin_addr.s_addr = inet_addr("112.108.40.10");
	    	c_addr.sin_family = AF_INET;
	    	c_addr.sin_port = htons(8888);

	    	if(connect(c_socket,(struct sockaddr*) &c_addr, sizeof(c_addr))==-1){
	    		//error printf"..
	    		close(c_socket);
	    		return -1;
	    	}
	    	return 0;
}

JNIEXPORT jint JNICALL Java_org_opencv_samples_tutorial4_Sample4View_ConnectWithIP(JNIEnv* env, jobject thiz, jstring str ){

			char strBuff[128];
			const char* sz = env->GetStringUTFChars(str,0);
			strcpy(strBuff,sz);

			c_socket = socket(PF_INET,SOCK_STREAM,0);

	    	memset(&c_addr,0,sizeof(c_addr));


	    	//	    	c_addr.sin_addr.s_addr = inet_addr("112.108.40.10");
	    	c_addr.sin_addr.s_addr = inet_addr(*sz);
	    	c_addr.sin_family = AF_INET;
	    	c_addr.sin_port = htons(8888);

	    	if(connect(c_socket,(struct sockaddr*) &c_addr, sizeof(c_addr))==-1){
	    		//error printf"..
	    		close(c_socket);
//	    		env->ReleaseStringUTFChars(env,str,sz);
	    		return -1;
	    	}

//	    	env->ReleaseStringUTFChars(env,str,sz);
	    	return 0;
}

JNIEXPORT jint JNICALL Java_org_opencv_samples_tutorial4_Sample4View_FindFeatures(JNIEnv* env, jobject thiz, jlong addrGray, jlong addrRgba)
{
    Mat* pMatGr=(Mat*)addrGray;
    Mat* pMatRgb=(Mat*)addrRgba;
    vector<KeyPoint> v;
    IplImage trans = IplImage(*pMatGr);
    IplImage imageBg= IplImage(*pMatRgb);

    	int bytes = 0;
    	bytes = send(c_socket,trans.imageData,trans.imageSize,0);

    	if(bytes != trans.imageSize){
    		//�ȵȰ�.
//    		return 102;
    	}
    	else{//�Ȱ�}
//    		return 0;
    	}
//    	for( size_t i = 0; i < v.size(); i++ )
//    	        circle(*pMatRgb, Point(v[i].pt.x, v[i].pt.y), 10, Scalar(255,0,0,255));
//��
    	char arr[260];
    	char* szTok;
    	CvPoint dst_corners[4];
    	int nTmp[8] = {0};

    	int result = recv(c_socket,(char*)arr,260,0);

    	if(result == 260 ){
//    		return 0;
    	}
    	else {
//    		return -2;
    	}

    	szTok = strtok(arr, " ");
    	int i = 0;
    	while(szTok != NULL )
    	{
    		nTmp[i++] = atoi( szTok );
    		szTok = strtok( NULL, " " );
    	}

    	dst_corners[0].x = nTmp[0];
    	dst_corners[0].y = nTmp[1];
    	dst_corners[1].x = nTmp[2];
    	dst_corners[1].y = nTmp[3];
    	dst_corners[2].x = nTmp[4];
    	dst_corners[2].y = nTmp[5];
    	dst_corners[3].x = nTmp[6];
    	dst_corners[3].y = nTmp[7];

    	for( int i = 0; i < 4; i++ )
    	{
    		CvPoint r1 = dst_corners[i%4];
    		CvPoint r2 = dst_corners[(i+1)%4];
    		cvLine( &imageBg , cvPoint(r1.x, r1.y), cvPoint(r2.x, r2.y), cvScalar(0, 255, 255), 3 );
    	}

    return 0;
}

JNIEXPORT jlong Java_org_opencv_samples_tutorial4_Sample4View_stringFromJNI( JNIEnv* env,jobject thiz )
{

	int c_socket;//, s_socket;
	struct sockaddr_in c_addr;

	char buffer[100];

	int len;
	int n;

//	s_socket = socket(PF_INET,SOCK_STREAM, 0);
//	s_addr.sin_addr.s_addr = htonl(INADDR_ANY);//?
//	s_addr.sin_family = AF_INET;
////	s_addr.sin_port = htnos(1000);//error


//	if(bind(s_socket,(struct sockaddr*)&s_addr,sizeof(s_addr))==-1){
//		//error
//	}
//
//	if(listen(s_socket,5)==-1){
//		//listen failed;
//	}
//
//	while(1){
//		len = sizeof(c_addr);
//		c_socket = acc
//	}
	c_socket = socket(PF_INET,SOCK_STREAM,0);

	memset(&c_addr,0,sizeof(c_addr));
	c_addr.sin_addr.s_addr = inet_addr("112.1.1.1");
	c_addr.sin_family = AF_INET;
	c_addr.sin_port = htons(9000);

	if(connect(c_socket,(struct sockaddr*) &c_addr, sizeof(c_addr))==-1){
		//error printf"..
		close(c_socket);
		return -1;
	}

	int bytes = 0;
	while(1){
//		bytes = send(c_socket,,,0)
	}
	jlong result = 1;
    return result;
}

//JNIEXPORT jstring Java_org_opencv_samples_tutorial4_Sample4View_initiateTcpConnection(JNIEnv* env, jobject javaThis){
//    int tcp_socket = socket(AF_INET, SOCK_STREAM,0);
//
//    if(tcp_socket < 0){
//        return (env).NewStringUTF(*env, "ERROR CREATING SOCKET");
//    }
//
//    const char* server_host = "some.numbers.that.work"; //It's a valid IP I don't feel like sharing
//    unsigned short server_port = 43000;
//
//    struct sockaddr_in server_tcp_addr;
//    server_tcp_addr.sin_family = AF_INET;
//    server_tcp_addr.sin_port = htons(server_port);
//    struct hostent *hostp = gethostbyname(server_host);
//    memcpy(&server_tcp_addr, hostp->h_addr, hostp->h_length);
//    socklen_t slen = sizeof(server_tcp_addr);
//    if(connect(tcp_socket,(struct sockaddr*)&server_tcp_addr, slen) < 0){ //fails here
//        close(tcp_socket);
//        return (env).NewStringUTF(*env, "ERROR CONNECTING TO SERVER");
//    }
//
//    char* message = "hello from android!";
//    send(tcp_socket, &message, sizeof(message),0);
//
//
//    return (env).NewStringUTF(*env, "TCP message sent!");
//}

}
