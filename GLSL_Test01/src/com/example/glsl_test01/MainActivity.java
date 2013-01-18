package com.example.glsl_test01;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GLSurfaceView glSurfaceView = new GLSurfaceView(this);
        
        
        setContentView(glSurfaceView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    public String vertextShaderSource = "uniform mat4 u_mvpMatrix;"+
    		"attribute vec4 a_position;"+
    		"attribute vec4 a_color;"+
    		"varying vec4 v_color;"+
    		"void main()"+
    		"{"+
    		"   gl_Position = u_mvpMatrix * a_position;"+
    		"   v_color = a_color;"+
    		"}";
    public String pixelShaderSource = "varying vec4 v_color;"+
    		"void main()"+
    		"{"+
    		"	gl_FragColor = v_color;"+
    		"}";
	private String TAG = "TAG";
    
    class Renderer implements GLSurfaceView.Renderer {

		private int mProgram;
		private int maPositionHandle;

		@Override
		public void onDrawFrame(GL10 gl) {
			 GLES20.glClearColor(0.0f, 0.0f, 1.0f, 1.0f); //2

			  GLES20.glClear( GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

			  GLES20.glUseProgram(mProgram); // 쉐이더 프로그램에 접근할 때는 항상 호출한다. 여러번 호출해도 된다

			  GLES20.glActiveTexture(GLES20.GL_TEXTURE0); // Fragment Shader로 텍스쳐를 전달한다(sample2D)

			  GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureID); //5

			  mTriangleVertices.position(TRIANGLE_VERTICES_DATA_POS_OFFSET); //6

			  GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false, //7

			  TRIANGLE_VERTICES_DATA_STRIDE_BYTES, mTriangleVertices);//정점정보를 쉐이더 변수에 전달한다

			  GLES20.glEnableVertexAttribArray(maPositionHandle); //8

			  mTriangleVertices.position(TRIANGLE_VERTICES_DATA_UV_OFFSET); //9

			  GLES20.glVertexAttribPointer(maTextureHandle, 2, GLES20.GL_FLOAT, false,//10

			  TRIANGLE_VERTICES_DATA_STRIDE_BYTES, mTriangleVertices);

			  GLES20.glEnableVertexAttribArray(maTextureHandle); //11

			  long time = SystemClock.uptimeMillis() % 4000L;

			  float angle = 0.090f * ((int) time);

			  Matrix.setRotateM(mMMatrix, 0, angle, 0, 0, 1.0f); //12 OpenGL ES2 에서는 glRotate(), glTranslate(), glScale()등이 지원 안됨

			  Matrix.multiplyMM(mMVPMatrix, 0, mVMatrix, 0, mMMatrix, 0);

			  Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0);

			  GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, mMVPMatrix, 0); // 계산이 완료된 행렬을 Vertex Shader에 전달한다

			  GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3); //14			
		}

		@Override
		public void onSurfaceChanged(GL10 gl, int width, int height) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onSurfaceCreated(GL10 gl, EGLConfig config) {
			mProgram = createProgram(vertextShaderSource, pixelShaderSource); //1

			   if (mProgram == 0) {

			      return;

			   }

			   maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition"); //2

			   if (maPositionHandle == -1) {

			      throw new RuntimeException("Could not get attrib location for aPosition");

			   }

			   maTextureHandle = GLES20.glGetAttribLocation(mProgram, "aTextureCoord"); //3

			   if (maTextureHandle == -1) {

			      throw new RuntimeException("Could not get attrib location for aTextureCoord");

			   }

			   muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix"); //4

			      if (muMVPMatrixHandle == -1) {

			        throw new RuntimeException("Could not get attrib location for uMVPMatrix");

			      }

;
		}
    	
		
		private int createProgram(String vertexSource, String fragmentSource) {                          //1

	    	int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource);                 //2

	    	    if (vertexShader == 0) {

	    	       return 0;

	    	     }

	    	int pixelShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);

	    	if (pixelShader == 0) {

	    	    return 0;

	    	}

	    	int program = GLES20.glCreateProgram();                                                                  //3

	    	if (program != 0) {

	    	    GLES20.glAttachShader(program, vertexShader);                                                   //4

	    	    GLES20.glAttachShader(program, pixelShader);

	    	    GLES20.glLinkProgram(program);                                                                         //5

	    	    int[] linkStatus = new int[1];

	    	    GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);             //6

	    	    if (linkStatus[0] != GLES20.GL_TRUE) {

	    	       Log.e(TAG , "Could not link program: ");

	    	       Log.e(TAG, GLES20.glGetProgramInfoLog(program));

	    	       GLES20.glDeleteProgram(program);

	    	      program = 0;

	    	   }

	    	}

	    	return program;                                                                                                     //7

	    	}
	    
	    private int loadShader(int shaderType, String source) {

	    	int shader = GLES20.glCreateShader(shaderType);                                                   //8

	    	if (shader != 0) {

	    	    GLES20.glShaderSource(shader, source);                                                           //9

	    	    GLES20.glCompileShader(shader);                                                                 //10

	    	    int[] compiled = new int[1];

	    	    GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);        //11

	    	    if (compiled[0] == 0) {

	    	        Log.e(TAG, "Could not compile shader " + shaderType + ":");

	    	        Log.e(TAG, GLES20.glGetShaderInfoLog(shader));

	    	        GLES20.glDeleteShader(shader);

	    	        shader = 0;

	    	     }

	    	  }

	    	return shader;                                                                                                  //12

	    	}
    }
    
    
}

