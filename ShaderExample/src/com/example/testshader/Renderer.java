package com.example.testshader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

public class Renderer implements GLSurfaceView.Renderer {

	private Context mContext;
	private com.example.testshader.Shader _shaders;
	private int _program;

	/***************************
	 * CONSTRUCTOR(S)
	 **************************/
	public Renderer(Context context) {

		mContext = context;

		initShader();

	}
	
	/**���̴��� �����ϴ� �Լ�.*/
	private void initShader() {
		/**nothing*/
	}
	
	float vert[] = {
			0.0f, 0.5f, 0.0f,
			-0.5f, -0.5f, 0.0f,
			0.5f, -0.5f, 0.0f
			};
	
	@Override
	public void onDrawFrame(GL10 gl) {
		
		
		//�������ۺ�Ʈ�� �÷���Ʈ�� Ŭ������.
		GLES20.glClear( GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
		//����� Ŭ������?
		GLES20.glClearColor(.0f, .0f, .0f, 1.0f);
		
		//floatBuffer�� vertext�迭�� �־ ���� ���·� ����
		FloatBuffer  vVertices = ArrayToBuffer(vert);

		//���α׷� ��ü �̿�.
		GLES20.glUseProgram(_program);
		
		//���� ������ ����.
		GLES20.glVertexAttribPointer(0, 3, GLES20.GL_FLOAT, false, 0, vVertices);
		GLES20.glEnableVertexAttribArray(0);
		
		//�׸���.
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);
		
		/**���⿡ �� �ؾ��ұ�?*/
		
		//GPU�� �̹��� ������(Data �迭�̰���?)
//		GLES20.glTexImage2D(target, level, internalformat, width, height, border, format, type, pixels);
		//GPU���� ���� ó�� �� �� �ֵ��� ��.
//		GLES20.glDrawXXX(); //GPU ������, Buffer�� �ȱ�.
		//GPU���� ���� ó�� �� �޴� CPU�� �޵��� ��.
//		GLES20.glReadPixels(x, y, width, height, format, type, pixels)

	}
	
	public FloatBuffer ArrayToBuffer(float[] ar) {
        ByteBuffer bytebuf = ByteBuffer.allocateDirect(ar.length*4);
        bytebuf.order(ByteOrder.nativeOrder());
        FloatBuffer buf = bytebuf.asFloatBuffer();
        buf.put(ar);
        buf.position(0);
        return buf;
    }

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
	
		//view port ����.
		GLES20.glViewport(0, 0, width, height);
		
	}

	/**init()�� ����� �����.*/
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		try {
			_shaders = new Shader(vShader, fShader, false, 0); // gouraud
			_program = _shaders.get_program();
		} catch (Exception e) {
			Log.d("SHADER 0 SETUP", e.getLocalizedMessage());
		}		
	}
	
	private String vShader =
			"attribute vec4 vPosition;" +
			"void main(){" +
			"gl_Position = vPosition;" +
			"}";
	private String fShader = 
			"precision mediump float;\n" +
			"void main(){\n" +
			"gl_FragColor = vec4 (1.0, 0.0 ,0.0 ,1.0);\n" +
			"}\n";
			
			/*"uniform sampler2D sampler0;"+
"uniform sampler2D sampler1;"+
"uniform sampler2D sampler2;"+
"varying highp vec2 _texcoord;"+
"void main()"+
"{"+
"highp float y = texture2D(sampler0, _texcoord).r;"+
"highp float u = texture2D(sampler1, _texcoord).r;"+
"highp float v = texture2D(sampler2, _texcoord).r;"+
"y = 1.1643 * (y - 0.0625);"+
"u = u - 0.5;"+
"v = v - 0.5;"+
"highp float r = y + 1.5958 * v;"+
"highp float g = y - 0.39173 * u - 0.81290 * v;"+
"highp float b = y + 2.017 * u;"+
"gl_FragColor = vec4(r, g, b, 1.0);"+
"}";
	*/


}
