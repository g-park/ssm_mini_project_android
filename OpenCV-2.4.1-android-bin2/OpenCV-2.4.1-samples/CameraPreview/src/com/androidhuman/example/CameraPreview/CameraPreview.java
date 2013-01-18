package com.androidhuman.example.CameraPreview;

import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.hardware.Camera;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;


public class CameraPreview extends Activity implements Callback{    
    
	private Preview mPreview;
	private GLSurfaceView mGlSurfaceView;
    
	public static String TAG = "CameraPreview";
    private Context mContext;
    
    private GLRenderer mGRenderer;
    
    
    @SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        
        mGlSurfaceView = new GLSurfaceView(mContext);
        mGlSurfaceView.getHolder().addCallback(this);
        if(!detectOpenGLES20())
        	{
        	Toast.makeText(mContext, "OpenGL ES 2.0 �� �������� �ʽ��ϴ�.", 0).show();
        	}
        else{
        	mGlSurfaceView.setEGLContextClientVersion(2);
        }
        
        //��ü ȭ�� ���� ������ �ʰ� ��� ������.
        setFullscreen();
      	disableScreenTurnOff();
    
        // Create our Preview view and set it as the content of our activity.
      	FrameLayout mFrameLayout = new FrameLayout(this);
        mPreview = new Preview(this);
//        mPreview.setVisibility(View.INVISIBLE);
        
        
//        mGlSurfaceView.setEGLContextClientVersion(GLES20.GL_VERSION);
//      �ſ� �߿� 2.0�� ����ϱ� ���ؼ��� �� �ʿ���.
//      mGlSurfaceView.getHolder().addCallback(this);
        
        
        mGRenderer = new GLRenderer(this);
        mGlSurfaceView.setRenderer(mGRenderer);
        
        mFrameLayout.addView(mPreview);
        mFrameLayout.addView(mGlSurfaceView);
        
        setContentView(mFrameLayout);
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
        mCamera.setPreviewCallback(mGRenderer);
        
        try {
           mCamera.setPreviewDisplay(holder);
           
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


@Override
public void surfaceChanged(SurfaceHolder holder, int format, int width,
		int height) {
	// TODO Auto-generated method stub
	
}

@Override
public void surfaceCreated(SurfaceHolder holder) {
	// TODO Auto-generated method stub
	
}

@Override
public void surfaceDestroyed(SurfaceHolder holder) {
	// TODO Auto-generated method stub
	
}
}