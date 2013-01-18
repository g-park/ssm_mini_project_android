package com.example.testshader;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class Example {
	/*ByteBuffer byteBuffer = ByteBuffer.allocateDirect(bitmap.getWidth() * bitmap.getHeight() * 4);
	byteBuffer.order(ByteOrder.BIG_ENDIAN);
	IntBuffer ib = byteBuffer.asIntBuffer();

	int[] pixels = new int[bitmap.getWidth() * bitmap.getHeight()];
	bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
	for(int i=0; i<pixels.length; i++){
	 ib.put(pixels[i] << 8 | pixels[i] >>> 24);
	}

	GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, curTexture);
	//Create Nearest Filtered Texture
	GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
	GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
	GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
	GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
	GLES20.glTexImage2D ( GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, bitmap.getWidth(), bitmap.getHeight(), 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, byteBuffer );
*/}
