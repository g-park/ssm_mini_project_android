package com.androidhuman.example.CameraPreview;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.IOException;


public class CameraPreview extends Activity{    
    
	private Preview mPreview;
    public static String TAG = "CameraPreview";
    private Context mContext;
    private GLRenderer mGRenderer;
    
    @Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        
        
        if(!detectOpenGLES20())Toast.makeText(mContext, "OpenGL ES 2.0 �� �������� �ʽ��ϴ�.", 0).show();
        
        //��ü ȭ�� ���� ������ �ʰ� ��� ������.
        setFullscreen();
      	disableScreenTurnOff();
    
        // Create our Preview view and set it as the content of our activity.
        mPreview = new Preview(this);
        setContentView(mPreview);
    }

    private boolean detectOpenGLES20() {
		ActivityManager am =
			(ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		ConfigurationInfo info = am.getDeviceConfigurationInfo();
		Log.d("OpenGL Ver:", info.getGlEsVersion());
		return (info.reqGlEsVersion >= 0x20000);
	}
    public void disableScreenTurnOff() {
		getWindow().setFlags(
							WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
							WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	public void setFullscreen() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(
							WindowManager.LayoutParams.FLAG_FULLSCREEN,
							WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}
	

class Preview extends SurfaceView implements SurfaceHolder.Callback{
    SurfaceHolder mHolder;
    Camera mCamera;
    
    Preview(Context context) {
        super(context);
        
        // SurfaceHolder.Callback�� ���������ν� Surface�� ����/�Ҹ�Ǿ�����
        // �� �� �ֽ��ϴ�.
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // Surface�� �����Ǿ��ٸ�, ī�޶��� �ν��Ͻ��� �޾ƿ� �� ī�޶���
        // Preview �� ǥ���� ��ġ�� �����մϴ�.
        mCamera = Camera.open();
        try {
           mCamera.setPreviewDisplay(holder);
           mCamera.setPreviewCallback(mGRenderer);
        } catch (IOException exception) {
            mCamera.release();
            mCamera = null;
            // TODO: add more exception handling logic here
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // �ٸ� ȭ������ ���ư���, Surface�� �Ҹ�˴ϴ�. ���� ī�޶��� Preview�� 
        // �����ؾ� �մϴ�. ī�޶�� ������ �� �ִ� �ڿ��� �ƴϱ⿡, ������� ����
        // ��� -��Ƽ��Ƽ�� �Ͻ����� ���°� �� ��� �� - �ڿ��� ��ȯ�ؾ��մϴ�.
        mCamera.stopPreview();
        mCamera = null;
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // ǥ���� ������ ũ�⸦ �˾����Ƿ� �ش� ũ��� Preview�� �����մϴ�.
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setPreviewSize(w, h);
        mCamera.setParameters(parameters);
        mCamera.startPreview();
    }

}
}