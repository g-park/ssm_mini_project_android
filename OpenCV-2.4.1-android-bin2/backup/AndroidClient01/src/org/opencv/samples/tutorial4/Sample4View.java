package org.opencv.samples.tutorial4;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

class Sample4View extends SampleViewBase {

    public static final int     VIEW_MODE_RGBA     = 0;
    public static final int     VIEW_MODE_GRAY     = 1;
    public static final int     VIEW_MODE_CANNY    = 2;
    public static final int     VIEW_MODE_FEATURES = 5;
    
    private Mat mYuv;
    private Mat mRgba;
    private Mat mGraySubmat;
    private Mat mIntermediateMat;

    private int mViewMode;
	private Bitmap mBitmap;
	private int connection;

    public Sample4View(Context context) {
        super(context);
    }
    
	@Override
	protected void onPreviewStarted(int previewWidtd, int previewHeight) {
        // initialize Mats before usage
        mYuv = new Mat(getFrameHeight() + getFrameHeight() / 2, getFrameWidth(), CvType.CV_8UC1);
//        mYuv = new Mat(640, 480, CvType.CV_8UC1);
        mGraySubmat = mYuv.submat(0, getFrameHeight(), 0, getFrameWidth());
        mRgba = new Mat();
        mIntermediateMat = new Mat();
        
        mBitmap = Bitmap.createBitmap(previewWidtd, previewHeight, Bitmap.Config.ARGB_8888);
        
        
        
        /** 연결 */
        int a = connection = Connect();
        if(a == 0 ){
        	Log.i("test", "connecttion");
        }
        else {
        	Log.i("test", "not connected");
        }
        /** 연결 */
        
        
	}

	@Override
	protected void onPreviewStopped() {
		
		if (mBitmap != null) {
			mBitmap.recycle();
			mBitmap = null;
		}
		
        // Explicitly deallocate Mats
        if (mYuv != null)
            mYuv.release();
        if (mRgba != null)
            mRgba.release();
        if (mGraySubmat != null)
            mGraySubmat.release();
        if (mIntermediateMat != null)
            mIntermediateMat.release();

        mYuv = null;
        mRgba = null;
        mGraySubmat = null;
        mIntermediateMat = null;
		
	}


    @Override
    protected Bitmap processFrame(byte[] data) {
        mYuv.put(0, 0, data);

        final int viewMode = mViewMode;

        switch (viewMode) {
        case VIEW_MODE_GRAY:
            Imgproc.cvtColor(mGraySubmat, mRgba, Imgproc.COLOR_GRAY2RGBA, 4);
            break;
        case VIEW_MODE_RGBA:
            Imgproc.cvtColor(mYuv, mRgba, Imgproc.COLOR_YUV420sp2RGB, 4);
            break;
        case VIEW_MODE_CANNY:
            Imgproc.Canny(mGraySubmat, mIntermediateMat, 80, 100);
            Imgproc.cvtColor(mIntermediateMat, mRgba, Imgproc.COLOR_GRAY2BGRA, 4);
            break;
        case VIEW_MODE_FEATURES:
            Imgproc.cvtColor(mYuv, mRgba, Imgproc.COLOR_YUV420sp2RGB, 4);

            if(connection == 0){
            	int result = FindFeatures(mGraySubmat.getNativeObjAddr(), mRgba.getNativeObjAddr());
            	Log.i("test", "result : Connected "+result);
            }
            else{
            	Log.i("test","result : unConnection");
            }
            break;
        }

        Bitmap bmp = mBitmap;

        try {
            Utils.matToBitmap(mRgba, bmp);
        } catch(Exception e) {
            Log.e("org.opencv.samples.puzzle15", "Utils.matToBitmap() throws an exception: " + e.getMessage());
            bmp.recycle();
            bmp = null;
        }

        return bmp;
    }

//    public native void FindFeatures(long matAddrGr, long matAddrRgba);
    public native int FindFeatures(long matAddrGr, long matAddrRgba);
//    public native String FindFeatures(long matAddrGr, long matAddrRgba);	
    public native int  stringFromJNI();
    public native int Connect();

    static {
    	System.loadLibrary("opencv_java");
        System.loadLibrary("mixed_sample");
    }

    public void setViewMode(int viewMode) {
		mViewMode = viewMode;
    }
}
