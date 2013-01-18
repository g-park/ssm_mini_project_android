package com.filtergl.shader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.util.Log;

public class GLRenderer
implements Renderer, PreviewCallback {
	//어디서 붙어 데이터를 받는지 알아오기
	//YUV420에서 640x480 = 460800 이렇게 됨. 거기서 307200이 Y 데이터이고 u와 v는 76800이다.
	//그렇기 때문에 U데이터는 307200 부터는 U 데이터이다.
	private static final int U_INDEX = 307200;
	//30720 + 76800 =  384000
	//384000 부터 460799 까지 v 데이터다.
	private static final int V_INDEX = 384000;
	//y는 왼지 모르지만 4600
	private static final int LENGTH = 460800;//307200//115200;
	private static final int LENGTH_4 = 76800;//153600;//115200;//28800;//19200;

	private ActivityFilterGL activity;

	private FloatBuffer mVertices;
	private ShortBuffer mIndices;

	private int previewFrameWidth = 640;
	private int previewFrameHeight = 480;
	private int mProgramObject;
	private int mPositionLoc;
	private int mTexCoordLoc;
//	private int mSamplerLoc;
	private int yTexture;
	private int uTexture;
	private int vTexture;

	private final float[] mVerticesData = { 
	//postion x, y, z 
			-1.f, 1.f, 0.0f, // Position 0
			0.0f, 0.0f, // TexCoord 0
			
			-1.f, -1.f, 0.0f, // Position 1
			0.0f, 1.0f, // TexCoord 1
			
			1.f, -1.f, 0.0f, // Position 2
			1.0f, 1.0f, // TexCoord 2
			
			1.f, 1.f, 0.0f, // Position 3
			1.0f, 0.0f // TexCoord 3
	};

	private final short[] mIndicesData = 
		{ 0, 1, 2,
		  0, 2, 3 };

	private ByteBuffer frameData = null;
	private ByteBuffer yBuffer;
	private ByteBuffer uBuffer;
	private ByteBuffer vBuffer;

	byte[] yData = new byte[LENGTH];
	byte[] uData = new byte[LENGTH_4];
	byte[] vData = new byte[LENGTH_4];

	public GLRenderer(ActivityFilterGL activity) {
		this.activity = activity;

		mVertices = ByteBuffer.allocateDirect(mVerticesData.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
		mVertices.put(mVerticesData).position(0);

		mIndices = ByteBuffer.allocateDirect(mIndicesData.length * 2).order(ByteOrder.nativeOrder()).asShortBuffer();
		mIndices.put(mIndicesData).position(0);

		yBuffer = GraphicsUtil.makeByteBuffer(LENGTH);
		uBuffer = GraphicsUtil.makeByteBuffer(LENGTH_4);
		vBuffer = GraphicsUtil.makeByteBuffer(LENGTH_4);
	}

	@Override
	public final void onDrawFrame(GL10 gl) {
		// Clear the color buffer
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT|GLES20.GL_DEPTH_BUFFER_BIT);

		// Use the program object
		GLES20.glUseProgram(mProgramObject);

		// Load the vertex position
		mVertices.position(0);
		GLES20.glVertexAttribPointer(mPositionLoc, 3, GLES20.GL_FLOAT, false, 5 * 4, mVertices);
		// Load the texture coordinate
		mVertices.position(3);
		GLES20.glVertexAttribPointer(mTexCoordLoc, 2, GLES20.GL_FLOAT, false, 5 * 4, mVertices);

		GLES20.glEnableVertexAttribArray(mPositionLoc);
		GLES20.glEnableVertexAttribArray(mTexCoordLoc);

		GLES20.glUniform1i(yTexture, 1);
		/**삽입하라는 문구*/
		GLES20.glActiveTexture(GLES20.GL_TEXTURE2);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, yTexture);
		/**삽입하라는 문구*/
		GLES20.glTexImage2D(   GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE, previewFrameWidth, previewFrameHeight, 0, GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE, yBuffer);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

		GLES20.glUniform1i(uTexture, 2);
		/**삽입하라는 문구*/
		GLES20.glActiveTexture(GLES20.GL_TEXTURE2);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, uTexture);
		/**삽입하라는 문구*/
		GLES20.glTexImage2D(   GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE, previewFrameWidth/2, previewFrameHeight/2, 0, GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE, uBuffer);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

		GLES20.glUniform1i(vTexture, 3);
		/**삽입하라는 문구*/
		GLES20.glActiveTexture(GLES20.GL_TEXTURE2);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, vTexture);
		/**삽입하라는 문구*/
		GLES20.glTexImage2D(   GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE, previewFrameWidth/2, previewFrameHeight/2, 0, GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE, vBuffer);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

		GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_SHORT, mIndices);
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		GLES20.glViewport(0, 0, width, height);
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {

		// Define a simple shader program for our point.
		final String vShaderStr = readTextFileFromRawResource(activity, R.raw.v_simple);
		final String fShaderStr = readTextFileFromRawResource(activity, R.raw.f_convert);

		// Load the shaders and get a linked program object
		mProgramObject = loadProgram(vShaderStr, fShaderStr);

		// Get the attribute locations
		mPositionLoc = GLES20.glGetAttribLocation(mProgramObject, "a_position");
		mTexCoordLoc = GLES20.glGetAttribLocation(mProgramObject, "a_texCoord");

		GLES20.glEnable(GLES20.GL_TEXTURE_2D);
		yTexture = GLES20.glGetUniformLocation(mProgramObject, "y_texture");
		int[] yTextureNames = new int[1];
		GLES20.glGenTextures(1, yTextureNames, 0);
		int yTextureName = yTextureNames[0];
		GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, yTextureName);

		GLES20.glEnable(GLES20.GL_TEXTURE_2D);
		uTexture = GLES20.glGetUniformLocation(mProgramObject, "u_texture");
		int[] uTextureNames = new int[1];
		GLES20.glGenTextures(1, uTextureNames, 0);
		int uTextureName = uTextureNames[0];
		GLES20.glActiveTexture(GLES20.GL_TEXTURE2);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, uTextureName);

		GLES20.glEnable(GLES20.GL_TEXTURE_2D);
		vTexture = GLES20.glGetUniformLocation(mProgramObject, "v_texture");
		int[] vTextureNames = new int[1];
		GLES20.glGenTextures(1, vTextureNames, 0);
		int vTextureName = vTextureNames[0];
		GLES20.glActiveTexture(GLES20.GL_TEXTURE3);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, vTextureName);


		GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
	}

	public void setPreviewFrameSize(int realWidth, int realHeight) {
		previewFrameHeight = realHeight;
		previewFrameWidth = realWidth;
//		frameData = GraphicsUtil.makeByteBuffer(previewFrameHeight * previewFrameWidth * 3);
	}

	public static String readTextFileFromRawResource(final Context context, final int resourceId) {
		final InputStream inputStream = context.getResources().openRawResource(resourceId);
		final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
		final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

		String nextLine;
		final StringBuilder body = new StringBuilder();

		try {
			while ((nextLine = bufferedReader.readLine()) != null) {
				body.append(nextLine);
				body.append('\n');
			}
		} catch (IOException e) {
			return null;
		}

		return body.toString();
	}

	public static int loadShader(int type, String shaderSrc) {
		int shader;
		int[] compiled = new int[1];

		// Create the shader object
		shader = GLES20.glCreateShader(type);
		if (shader == 0) {
			return 0;
		}
		// Load the shader source
		GLES20.glShaderSource(shader, shaderSrc);
		// Compile the shader
		GLES20.glCompileShader(shader);
		// Check the compile status
		GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);

		if (compiled[0] == 0) {
			Log.e("ESShader", GLES20.glGetShaderInfoLog(shader));
			GLES20.glDeleteShader(shader);
			return 0;
		}
		return shader;
	}

	public static int loadProgram(String vertShaderSrc, String fragShaderSrc) {
		int vertexShader;
		int fragmentShader;
		int programObject;
		int[] linked = new int[1];

		// Load the vertex/fragment shaders
		vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertShaderSrc);
		if (vertexShader == 0) {
			return 0;
		}

		fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragShaderSrc);
		if (fragmentShader == 0) {
			GLES20.glDeleteShader(vertexShader);
			return 0;
		}

		// Create the program object
		programObject = GLES20.glCreateProgram();

		if (programObject == 0) {
			return 0;
		}

		GLES20.glAttachShader(programObject, vertexShader);
		GLES20.glAttachShader(programObject, fragmentShader);

		// Link the program
		GLES20.glLinkProgram(programObject);

		// Check the link status
		GLES20.glGetProgramiv(programObject, GLES20.GL_LINK_STATUS, linked, 0);

		if (linked[0] == 0) {
			Log.e("ESShader", "Error linking program:");
			Log.e("ESShader", GLES20.glGetProgramInfoLog(programObject));
			GLES20.glDeleteProgram(programObject);
			return 0;
		}

		// Free up no longer needed shader resources
		GLES20.glDeleteShader(vertexShader);
		GLES20.glDeleteShader(fragmentShader);

		return programObject;
	}

	@Override
	public void onPreviewFrame(byte[] data, Camera camera) {
		Log.e("AndARRendDefTest", "onPreviewFrame: " + data.length);
//		System.arraycopy(data, 0, yData, 0, LENGTH);
		yBuffer.put(data);
		yBuffer.position(0);

		System.arraycopy(data, U_INDEX, uData, 0, LENGTH_4);
		uBuffer.put(uData);
		uBuffer.position(0);
		Log.e("AndARRendDefTest", "uData: " + uData.length);
		
		System.arraycopy(data, V_INDEX, vData, 0, LENGTH_4);
		vBuffer.put(vData);
		vBuffer.position(0);
		Log.e("AndARRendDefTest", "vData: " + vData.length);
	}

}