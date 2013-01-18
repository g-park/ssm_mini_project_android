package com.androidhuman.example.CameraPreview;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import java.io.IOException;


public class CameraPreview extends Activity {    
    private Preview mPreview;
    
    @Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Hide the window title.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    
        // Create our Preview view and set it as the content of our activity.
        mPreview = new Preview(this);
        setContentView(mPreview);
    }

}

class Preview extends SurfaceView implements SurfaceHolder.Callback {
    SurfaceHolder mHolder;
    Camera mCamera;
    
    Preview(Context context) {
        super(context);
        
        // SurfaceHolder.Callback을 설정함으로써 Surface가 생성/소멸되었음을
        // 알 수 있습니다.
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // Surface가 생성되었다면, 카메라의 인스턴스를 받아온 후 카메라의
        // Preview 를 표시할 위치를 설정합니다.
        mCamera = Camera.open();
        try {
           mCamera.setPreviewDisplay(holder);
        } catch (IOException exception) {
            mCamera.release();
            mCamera = null;
            // TODO: add more exception handling logic here
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // 다른 화면으로 돌아가면, Surface가 소멸됩니다. 따라서 카메라의 Preview도 
        // 중지해야 합니다. 카메라는 공유할 수 있는 자원이 아니기에, 사용하지 않을
        // 경우 -액티비티가 일시정지 상태가 된 경우 등 - 자원을 반환해야합니다.
        mCamera.stopPreview();
        mCamera = null;
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // 표시할 영역의 크기를 알았으므로 해당 크기로 Preview를 시작합니다.
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setPreviewSize(w, h);
        mCamera.setParameters(parameters);
        mCamera.startPreview();
    }

}