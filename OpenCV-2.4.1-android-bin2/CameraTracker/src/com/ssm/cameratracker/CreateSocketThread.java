package com.ssm.cameratracker;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import android.annotation.SuppressLint;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.StrictMode;
import android.util.Log;

public class CreateSocketThread extends Thread{
	Socket socket;
	Camera camera;
	TCPClient tcpClient;
	String IP;
	CameraView cameraView;
	EightPoints points ;
	final static String sysout = "sysout";
	
	public CreateSocketThread(Camera c) {
		// TODO Auto-generated constructor stub
		camera=c;
	}
	
	public CreateSocketThread(String ip, Camera c) {
		// TODO Auto-generated constructor stub
		camera=c;
		IP=ip;
	}
	
	public CreateSocketThread(String ip, Camera c,CameraView cv) {
		// TODO Auto-generated constructor stub
		camera=c;
		IP=ip;
		cameraView = cv;
		points = new EightPoints();
		points.randPoint();
		
		
		
	}

	public void run(){
		
		try {
			socket=new Socket(IP, 7000);
			//socket=new Socket("192.168.0.72", 7000);
			camera.setPreviewCallback(cb);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	static int testInt = 0;
	@SuppressLint("NewApi")
	private PreviewCallback cb = new PreviewCallback() {
		
		public void onPreviewFrame(byte[] data, Camera camera) {
			Log.i(sysout, "onPreviewFrame called");
			Camera.Parameters parameters = camera.getParameters();
			
			Size size = parameters.getPreviewSize();
			
//			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			FileOutputStream outputStream =null;
			
//			++testInt;
			try {
//				outputStream = new FileOutputStream(new File(Environment.getExternalStorageDirectory().getPath()+"/out.jpg"));
				outputStream = new FileOutputStream("/sdcard/ssm"+"/test"+testInt+".jpg");
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//Rect rect = new Rect(0, 0, size.width, size.height);
			Rect rect = new Rect(0, 0, 640, 480);
			//YuvImage yuvImage = new YuvImage(data, ImageFormat.NV21, size.width, size.height, null);
			YuvImage yuvImage = new YuvImage(data, ImageFormat.NV21, 640, 480, null);
			yuvImage.compressToJpeg(rect, 50, outputStream);//æ∆øÙ«≤ Ω∫∆Æ∏≤¿∏∑Œ ∫∏≥æ ºˆ ¿÷¥¬ ≈Î∑Œ.Compress a rectangle region in the YuvImage to a jpeg. Only ImageFormat.NV21 and ImageFormat.YUY2 are supported for now.
			Log.i("CreateSocketThread", "PreviewFrame Called");

			File file;
			file=new File("/sdcard/ssm"+"/test"+testInt+".jpg");
			
			StrictMode.enableDefaults();
			tcpClient=new TCPClient(file, "test"+testInt+".jpg", socket);
			
//			/** 리턴 값을 받아오면? Camera interface를 호출하는 것?*/
			points.randPoint();
			try {
				if(cameraView.getFeaturedPointFromServer != null){
					float test[] = {0,0,0,0,0,0,0,0};
					tcpClient.fileSend(test);
					cameraView.getFeaturedPointFromServer.getFeaturePoint(test);}	
			} catch (Exception e) {
				Log.i(sysout, e.getMessage()+" in CreateSocketThread");
			}
//			
			
//			Thread cThread = new Thread(new TCPClient(file, "test"+testInt+".jpg", socket));
//			cThread.start();
		}
	};
}