package org.opencv.samples.tutorial4;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

public class Sample4Mixed extends Activity {
    private static final String TAG = "Sample::Activity";
    private GLSurfaceView mGLSurfaceView;

    private MenuItem            mItemPreviewRGBA;
    private MenuItem            mItemPreviewGray;
    private MenuItem            mItemPreviewCanny;
    private MenuItem            mItemPreviewFeatures;
    private Sample4View         mView;    

	static int displayWidth;
	static int displayHeight; 
    public Sample4Mixed() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    @Override
	protected void onPause() {
        Log.i(TAG, "onPause");
        /**openGL code*/
		mGLSurfaceView.onPause();
		
		super.onPause();
		mView.releaseCamera();
	}

	@Override
	protected void onResume() {
        Log.i(TAG, "onResume");
		super.onResume();
		
		/**openGL code*/
		mGLSurfaceView.onResume();
		
		if( !mView.openCamera() ) {
			AlertDialog ad = new AlertDialog.Builder(this).create();  
			ad.setCancelable(false); // This blocks the 'BACK' button  
			ad.setMessage("Fatal error: can't open camera!");  
			ad.setButton("OK", new DialogInterface.OnClickListener() {  
			    public void onClick(DialogInterface dialog, int which) {  
			        dialog.dismiss();                      
					finish();
			    }  
			});  
			ad.show();
		}
	}

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mView = new Sample4View(this);
        setContentView(R.layout.main);
        
        FrameLayout frameLayout = (FrameLayout)findViewById(R.id.frame);
        
        
        /**openGL Code*/
        
        
        mGLSurfaceView = new GLSurfaceView(this);
        mGLSurfaceView.setEGLConfigChooser(8,8,8,8,16,0);
        mGLSurfaceView.setRenderer(new Renderer());
        mGLSurfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);

        Display display = ((WindowManager)getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
    	 displayWidth = display.getWidth();
    	 displayHeight = display.getHeight();
    	
    	 Log.i(TAG, "onCreate start add ");
    	 frameLayout.addView(mGLSurfaceView);
    	 frameLayout.addView(mView);
    	 Log.i(TAG, "onCreate start add");
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(TAG, "onCreateOptionsMenu");
        mItemPreviewRGBA = menu.add("Preview RGBA");
        mItemPreviewGray = menu.add("Preview GRAY");
        mItemPreviewCanny = menu.add("Canny");
        mItemPreviewFeatures = menu.add("Find features");
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG, "Menu Item selected " + item);
        if (item == mItemPreviewRGBA) {
        	mView.setViewMode(Sample4View.VIEW_MODE_RGBA);
        } else if (item == mItemPreviewGray) {
        	mView.setViewMode(Sample4View.VIEW_MODE_GRAY);
        } else if (item == mItemPreviewCanny) {
        	mView.setViewMode(Sample4View.VIEW_MODE_CANNY);
        } else if (item == mItemPreviewFeatures) {
        	mView.setViewMode(Sample4View.VIEW_MODE_FEATURES);
        }
        return true;
    }
    
    class Renderer implements GLSurfaceView.Renderer {
        public Renderer() {
        }

        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            gl.glClearColor(0.0f,0.0f,0.0f,0);
            gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        }

        public void onSurfaceChanged(GL10 gl, int width, int height) {
            gl.glViewport(0, 0, width, height);
        }

        public void onDrawFrame(GL10 gl) {
            gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
            gl.glColor4f(1,1,0,1);

            float vert[] = {0, 0.5f, -0.5f, -0.5f, 0.5f, -0.5f };
            ByteBuffer bytebuf = ByteBuffer.allocateDirect(vert.length*4);
            bytebuf.order(ByteOrder.nativeOrder());
            FloatBuffer vertbuf = bytebuf.asFloatBuffer();
            vertbuf.put(vert);
            vertbuf.position(0);
           
            gl.glVertexPointer(2, GL10.GL_FLOAT, 0, vertbuf);
            gl.glDrawArrays(GL10.GL_TRIANGLES, 0, 3);
        }
   }
    
}
