package com.ssm.cameratracker;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.ssm.cameratracker.CameraView.GetFeaturedPointFromServer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CameraActivity extends Activity {
	String IP="112.108.40.205";
	GLSurfaceView mGLSurfaceView;
	float point[] = {0,0,0,0,0,0,0,0};
	
	final static String glTag = "glTag";
	CameraView cameraView;
	Context mContext;
	//
	Button btn;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        
        mGLSurfaceView = new GLSurfaceView(this);
        mGLSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        mGLSurfaceView.setRenderer(new Renderer());
        mGLSurfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        mGLSurfaceView.setZOrderOnTop (true);
        
        mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        
        btn = new Button(getApplicationContext());
        btn.setText("btn");
        btn.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				mGLSurfaceView.requestRender();
			}
		});
        
        
        cameraView = new CameraView(IP, this);
        setContentView(cameraView);
        addContentView(mGLSurfaceView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        addContentView(btn, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        
        cameraView.setOnGetFeaturedPointFromServerListener(new GetFeaturedPointFromServer() {
			
			public void getFeaturePoint(float eightPoint[]) {
				if(mGLSurfaceView != null){
					//
				mGLSurfaceView.requestRender();
				btn.setText("변함?" + eightPoint[0]+""+eightPoint[1]+""+eightPoint[2]+""+eightPoint[3]+""+eightPoint[4]+""+eightPoint[5]+""+eightPoint[6]+""+eightPoint[7] );
				point = setGLPoints(eightPoint);
				}
			}
		});
    }
    /**
     *인터페이스를 만들어서 여기에서 사용할 수 있도록 해야지.. */

    public boolean onKeyDown(int KeyCode, KeyEvent event) {

		if (KeyCode == KeyEvent.KEYCODE_BACK) {
			System.exit(0);
			return true;	//exit
		}
		
		if (KeyCode == KeyEvent.KEYCODE_MENU) {
			showDialog(this, "Setting", "Please Insert IP address");
			setContentView(new CameraView(IP, this));
			return true;
		}
		
		return super.onKeyDown(KeyCode, event);
	}
    
    private static void showDialog(final Activity activity, String title, String text){
    	AlertDialog.Builder adb=new AlertDialog.Builder(activity);
    	EditText et=(EditText)activity.findViewById(R.id.popup);
    	
    	et.setText("192.168.0.0", EditText.BufferType.NORMAL);

    	adb.setTitle(title);
    	adb.setMessage(text);
    	adb.setPositiveButton("Start Tracking", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				activity.setResult(Activity.RESULT_OK);
			}
		});
    	
    	adb.create();
    	adb.show();
    }    

    protected void onResume() {
        super.onResume();
        mGLSurfaceView.onResume();
   }

   protected void onPause() {
        super.onPause();
        mGLSurfaceView.onPause();
   }
   
   class Renderer implements GLSurfaceView.Renderer {
       public Renderer() {
       }

       public void onSurfaceCreated(GL10 gl, EGLConfig config) {
    	   Log.i(glTag, "--onSurfaceCreated--");
    	   gl.glClearColor(0,0,0,0);
           gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
       }

       public void onSurfaceChanged(GL10 gl, int width, int height) {
    	   Log.i(glTag, "!!!onSurfaceChanged!!!");
           gl.glViewport(0, 0, width, height);
       }

       //drawGL
       public void onDrawFrame(GL10 gl) {
    	   Log.i(glTag, "onDrawFrame()");
           gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
           gl.glColor4f(1,1,0,1);
//           float vert[] = {0, 0.5f, -0.5f, -0.5f, 0.5f, -0.5f };
           float vert[] = {point[0], point[1], point[2], point[3], point[4], point[5], point[6], point[7]};
           Log.i("glTag", "points : " + point[0]+","+point[1]+","+point[2]+","+point[3]+","+point[4]+","+point[5]+","+point[6]+","+point[7]);
           ByteBuffer bytebuf = ByteBuffer.allocateDirect(vert.length*4);
           bytebuf.order(ByteOrder.nativeOrder());
           FloatBuffer vertbuf = bytebuf.asFloatBuffer();
           vertbuf.put(vert);
           vertbuf.position(0);
          
           gl.glLineWidth(5);
           gl.glVertexPointer(2, GL10.GL_FLOAT, 0, vertbuf);
           gl.glDrawArrays(GL10.GL_LINE_LOOP, 0, 4);
       }
  }
   
   private float[] setGLPoints(float points[]){
	   float glPoints[] = {0,0,0,0,0,0,0,0};
	   //x좌표
	   for(int i = 0 ; i < 8 ; i = i+2){
		   
		   if(0<points[i]||points[i]<320){
			   points[i] = (-1)*(320-points[i])/320;
			   glPoints[i] = points[i];
		   }
		   else if(320<points[i]||points[i]<640){
			   
			   glPoints[i] = ((points[i]-320)/320);
		   }else if(points[i] == 320){
			   glPoints[i] = 0;
		   }
		   else if(points[i]==640){
			   glPoints[i] = 1;
		   }
	   }
	   
	   //y좌표
	   for(int i = 1 ; i < 8 ; i = i +2){
		   
		   if(0<points[i]||points[i]<240){
			   points[i] = (240 - points[i])/240;
			   glPoints[i] = points[i];
		   }
		   else if(240<points[i]||points[i]<480){
			   
			   glPoints[i] = (-1)*((points[i]-240)/240);
		   }else if(points[i] == 240){
			   glPoints[i] = 0;
		   }
		   else if(points[i]==480){
			   glPoints[i] = 1;
		   }
	   
		   
	   }
	   
	   return glPoints;
   }
}
