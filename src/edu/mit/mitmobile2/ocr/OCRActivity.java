package edu.mit.mitmobile2.ocr;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.googlecode.tesseract.android.TessBaseAPI;

import edu.mit.mitmobile2.R;


import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;
import android.widget.TextView;

public class OCRActivity extends Activity implements SurfaceHolder.Callback, PreviewCallback, SensorEventListener {
    SurfaceView mSurfaceView;
    private Camera mCamera;
    private SurfaceHolder mHolder;
    
    private PreviewImageHolder mPreviewImageHolder; 
    private Handler mUIHandler;
    private Bitmap mResultBitmap;
    
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    
    private TextFinder mTextFinder; 
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ocr);
        mPreviewImageHolder = new PreviewImageHolder();
        mUIHandler = new Handler();
        
        File file = new File(getFilesDir() + "/tessdata/eng.traineddata");
        if (!file.exists()) {
            try {
                importTrainingData();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.d("Tesseract", "failed to import tesseract training data");
            }
        }
        
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mTextFinder = new TextFinder();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Open the default i.e. the first rear facing camera.
        mCamera = Camera.open();
        
        if (mSurfaceView == null) {
            mSurfaceView = (SurfaceView) findViewById(R.id.ocrSurfaceView);
            mHolder = mSurfaceView.getHolder();
            mHolder.addCallback(this);
        }
        
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Because the Camera object is a shared resource, it's very
        // important to release it when the activity is paused.
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
        
        mSensorManager.unregisterListener(this);
    }

    
    Handler mImageHandler;

    private final static int PROCESS_IMAGE = 1;
    private final static int PROCESS_IMAGE_STOP = 2;
    
    private synchronized void startImageHandler() throws InterruptedException {
        if (mImageHandler != null) {
            return;
        }
        
        
       new Thread() {
            @Override
            public void run() {
                
                final TessBaseAPI tessBaseApi = new TessBaseAPI();
                tessBaseApi.init(getFilesDir() + "/", "eng");
                tessBaseApi.setDebug(true);
                tessBaseApi.setPageSegMode(TessBaseAPI.PSM_SINGLE_LINE);
                
                Looper.prepare();
                mImageHandler = new Handler() {
                    public void handleMessage(Message msg) {
                        if (msg.arg1 == PROCESS_IMAGE_STOP) {
                            Looper.myLooper().quit();
                            return;
                        }
                        
                        if (msg.arg1 == PROCESS_IMAGE) {
                        	
                            final Bitmap bitmap = getBitmap(mPreviewImageHolder.mWidth/2, mPreviewImageHolder.mHeight/2);
                            
                            long start = System.currentTimeMillis();

                           
                            Log.d("ocr", "width="+ bitmap.getWidth() + "height=" + bitmap.getHeight());
                            final ArrayList<String> foundStrings = new ArrayList<String>();
                            RegionBounds[] allBounds = mTextFinder.findTextImages(mPreviewImageHolder.mBytes, mPreviewImageHolder.mWidth, mPreviewImageHolder.mHeight, bitmap);
                            for (int i = 0; i < allBounds.length; i++) {
                            	RegionBounds bounds = allBounds[i];
                            	Bitmap croppedBitmap = Bitmap.createBitmap(bitmap, bounds.left, bounds.top, bounds.right-bounds.left, bounds.bottom-bounds.top);
                            	tessBaseApi.setImage(croppedBitmap);
                            	String ocr = tessBaseApi.getUTF8Text();
                            	int meanConfidence = tessBaseApi.meanConfidence();
                            	if (ocr.trim().length() > 0) {
                            		foundStrings.add(ocr.trim() +  " conf:" + meanConfidence);
                            	}
                            }
                            
                            Canvas canvas = new Canvas(bitmap);
                            for (int i = 0; i < allBounds.length; i++) {
                            	RegionBounds bounds = allBounds[i];
                            	Rect rect = new Rect(bounds.left, bounds.top, bounds.right, bounds.bottom);
                            	Paint paint = new Paint();
                            	paint.setStrokeWidth(3);
                            	paint.setColor(Color.MAGENTA);
                            	paint.setStyle(Style.STROKE);
                            	canvas.drawRect(rect, paint);
                            }
     
                            Log.d("Timing", "" + (System.currentTimeMillis() - start));
                            mUIHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                	ImageView imageView = (ImageView) findViewById(R.id.ocrImageView);
                                    imageView.setImageBitmap(bitmap);
                                    int[] textIds = new int[] {R.id.ocrTextView1, R.id.ocrTextView2, R.id.ocrTextView3, R.id.ocrTextView4, R.id.ocrTextView5};
                                    
                                    int i = 0;
                                    for (String text : foundStrings) {
                                        int textViewID = textIds[i % 5];
                                    	i++;
    									TextView textView = (TextView) findViewById(textViewID);
    									textView.setText(text);
                                    }
                                }                                
                            });

                            mPreviewImageHolder.clear();
                        }
                    }
                };
                Looper.loop();

            }
        }.start();
        
        // wait until handler is non null
        while (mImageHandler == null) {
            Thread.sleep(1);
        }
    }
    
    private void importTrainingData() throws IOException {
        InputStream inStream = getResources().openRawResource(R.raw.eng);
        
        // make the directory for the output file
        File parentDir = new File(getFilesDir() + "/tessdata");
        parentDir.mkdirs();
        
        // open a file for writing
        File trainedDataDestination = new File(getFilesDir() + "/tessdata/eng.traineddata");
        FileOutputStream outStream = new FileOutputStream(trainedDataDestination);
        
        
        byte[] buffer = new byte[1024];
        int bufferSize;
        while((bufferSize = inStream.read(buffer)) > 0) {
            outStream.write(buffer, 0, bufferSize);
        }
        inStream.close();
        outStream.close();
    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        Camera.Parameters parameters = mCamera.getParameters();
        
        List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();

        // You need to choose the most appropriate previewSize for your app
        Camera.Size previewSize = previewSizes.get(0);// .... select one of previewSizes here
        parameters.setPreviewSize(previewSize.width, previewSize.height);
        mCamera.setParameters(parameters);
        mCamera.startPreview();
        mCamera.setPreviewCallback(this);
        
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            mCamera.setPreviewDisplay(holder);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Camera", "IOException caused by setPreviewDisplay()", e);
        }
    }


    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
        if (mCamera != null) {
            mCamera.stopPreview();
        }
    }

    @Override
    public void onPreviewFrame(byte[] imageBytes, Camera camera) {
        
        if (mJoltTotal > 1.0e-5) {
            return;
        }
        
        if (mPreviewImageHolder.isEmpty()) {
            Parameters params = camera.getParameters();
            Size previewSize = params.getPreviewSize();
            mPreviewImageHolder.setImageBytes(imageBytes, params.getPreviewFormat(), previewSize.width, previewSize.height);
            
            
            try {
                startImageHandler();
                Message msg = Message.obtain();
                msg.arg1 = PROCESS_IMAGE;
                mImageHandler.sendMessage(msg);
                
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.d("OCR", "Failed to start image analysis loop");
            }

        }        
    }
    
    private Bitmap getBitmap(int width, int height) {
        if ((mResultBitmap != null) && 
            (mResultBitmap.getWidth() == width) &&
            (mResultBitmap.getHeight() == height)) {
        } else {
            mResultBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        }
        return mResultBitmap;
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    float mGravityX, mGravityY, mGravityZ;
    float mJoltX, mJoltY, mJoltZ;
    double mJoltTotal = 5;
    
    long mEventTimestamp;
    public void onSensorChanged(SensorEvent event) {
        float deltaT = 1.f * (event.timestamp - mEventTimestamp) / 1000;
        mJoltX = (event.values[0] - mGravityX) / deltaT;
        mJoltY = (event.values[1] - mGravityY) / deltaT;
        mJoltZ = (event.values[2] - mGravityZ) / deltaT;
        
        mJoltTotal = Math.sqrt(mJoltX*mJoltX + mJoltY*mJoltY + mJoltZ*mJoltZ);
        mGravityX = event.values[0];
        mGravityY = event.values[1];
        mGravityZ = event.values[2];
        mEventTimestamp = event.timestamp;
    }
    
    private class PreviewImageHolder {
        byte[] mBytes;
        int mHeight;
        int mWidth;
        int mFormat;
        
        public synchronized boolean isEmpty() {
            return (mBytes == null);
        }
        
        public synchronized void clear() {
            mBytes = null;
            mHeight = -1;
            mWidth = -1;
        }
        
        public synchronized void setImageBytes(byte[] bytes, int format, int width, int height) {
            mBytes = bytes;
            mFormat = format;
            mHeight = height;
            mWidth = width;
        }
    }
}

