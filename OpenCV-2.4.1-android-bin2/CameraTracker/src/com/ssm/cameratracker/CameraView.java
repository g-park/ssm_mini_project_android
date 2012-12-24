package com.ssm.cameratracker;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CameraView extends SurfaceView implements SurfaceHolder.Callback,
		Camera.PictureCallback {

	private static final String TAG = "CameraViewTest:camera.preview.CameraView";
	private SurfaceHolder holder;// ����
	private Camera camera;// ������
	Socket socket;
	String IP;

	public CameraView(String ip, Context context) {
		super(context);
		IP = ip;
		holder = getHolder();
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	public void onPictureTaken(byte[] data, Camera camera) {
		// try {
		// data2sd(getContext(),data,"test.jpg");
		// Log.i(TAG, data.length+"byte");
		// } catch (Exception e) {
		// Log.i(TAG,"Exception : onPictureTaken - camera.preview.CameraView");
		// }
		camera.startPreview();
	}

	public void surfaceCreated(SurfaceHolder holder) {
		try {
			camera = Camera.open();
			camera.setPreviewDisplay(holder);
//			인터페이스 구현 중.. [ 수정전 ]
//			Thread cThread = new Thread(new CreateSocketThread(IP, camera));
			Thread cThread = new Thread(new CreateSocketThread(IP, camera, this));
			cThread.start();
			// camera.getParameters().setPreviewFormat(ImageFormat.)
		} catch (Exception e) {
			Log.i(TAG, "Exception : surfaceCreated - camera.preview.CameraView");
		}
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		camera.startPreview();
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		camera.setPreviewCallback(null);
		camera.stopPreview();
		camera.release();
		camera = null;
	}

	/**
	 * @Override public boolean onTouchEvent(MotionEvent event) {
	 * 
	 *           if(event.getAction() == MotionEvent.ACTION_DOWN){
	 *           camera.takePicture(null, null, this); }
	 * 
	 *           return true; }
	 */

	/**
	 * private void data2sd(Context context, byte[] data, String fileName)
	 * throws IOException { FileOutputStream fos = null; try { fos = new
	 * FileOutputStream("/sdcard/ssm/" + fileName); fos.write(data);
	 * fos.close();
	 * 
	 * } catch (Exception e) { Log.i(TAG,
	 * "Exception : data2sd - camera.preview.CameraView"); if (fos != null)
	 * fos.close(); } }
	 */
	
	public interface GetFeaturedPointFromServer {
		public void getFeaturePoint(float[] eightPoint);
	}
	
	public GetFeaturedPointFromServer getFeaturedPointFromServer;
	
	public void setOnGetFeaturedPointFromServerListener(GetFeaturedPointFromServer l){
		getFeaturedPointFromServer = l;
	}
}
