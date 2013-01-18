package com.example.testshader;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends Activity {

    private GLSurfaceView mGLSurfaceView;
	private Renderer renderer;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGLSurfaceView = new GLSurfaceView(this);

		// detect if OpenGL ES 2.0 support exists - if it doesn't, exit.
		if (detectOpenGLES20()) {
			// Tell the surface view we want to create an OpenGL ES 2.0-compatible
			// context, and set an OpenGL ES 2.0-compatible renderer.
			mGLSurfaceView.setEGLContextClientVersion(2);
			renderer = new Renderer(this);
			mGLSurfaceView.setRenderer(renderer);
		} 
		else { // quit if no support - get a better phone! :P
			quit();
		}

		// set the content view
		setContentView(mGLSurfaceView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    private void quit() {
		//super.onDestroy();
		this.finish();
	}
    
    private boolean detectOpenGLES20() {
		ActivityManager am =
			(ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		ConfigurationInfo info = am.getDeviceConfigurationInfo();
		Log.d("OpenGL Ver:", info.getGlEsVersion());
		return (info.reqGlEsVersion >= 0x20000);
	}


}
