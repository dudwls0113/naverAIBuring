package com.non.sleep.naver.android.src.facedetection;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.non.sleep.naver.android.R;
import com.non.sleep.naver.android.src.facedetection.cam.FaceDetectionCamera;
import com.non.sleep.naver.android.src.facedetection.cam.FrontCameraRetriever;

/**
 * Don't forget to add the permissions to the AndroidManifest.xml!
 * <p/>
 * <uses-feature android:name="android.hardware.camera" />
 * <uses-feature android:name="android.hardware.camera.front" />
 * <p/>
 * <uses-permission android:name="android.permission.CAMERA" />
 */
public class FaceDetectionActivity extends Activity implements FrontCameraRetriever.Listener, FaceDetectionCamera.Listener {

    private static final String TAG = "FDT" + FaceDetectionActivity.class.getSimpleName();
    public static ImageView iv;
    private TextView helloWorldTextView;

    public static FrameLayout layout;
    public static Context context;
    private SurfaceView cameraSurface;
    public static Bitmap shareBitmap;
    public static boolean isCapture;
    public static int count=0;
    private TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facedetection);
        isCapture = false;
        // Go get the front facing camera of the device
        // best practice is to do this asynchronously

        context = this;
        layout = (FrameLayout) findViewById(R.id.helloWorldCameraPreview);
        FrontCameraRetriever.retrieveFor(this);
        count=0;
    }

    @Override
    public void onLoaded(FaceDetectionCamera camera) {
        // When the front facing camera has been retrieved we still need to ensure our display is ready
        // so we will let the camera surface view initialise the camera i.e turn face detection on
        cameraSurface = new CameraSurfaceView(this, camera, this);
        // Add the surface view (i.e. camera preview to our layout)
        ((FrameLayout) findViewById(R.id.helloWorldCameraPreview)).addView(cameraSurface);
    }

    @Override
    public void onFailedToLoadFaceDetectionCamera() {
        // This can happen if
        // there is no front facing camera
        // or another app is using the camera
        // or our app or another app failed to release the camera properly
        Log.wtf(TAG, "Failed to load camera, what went wrong?");
//        helloWorldTextView.setText(R.string.error_with_face_detection);
    }

    @Override
    public void onFaceDetected() {
//        helloWorldTextView.setText(R.string.face_detected_message);
        View v1 = FaceDetectionActivity.layout.getRootView();
        v1.buildDrawingCache();
        v1.setDrawingCacheEnabled(true);

        isCapture = true;
        // set face detect

//        Toast.makeText(context, "DETECTED", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onFaceTimedOut() {
//        helloWorldTextView.setText(R.string.face_detected_then_lost_message);
//        Toast.makeText(context, "TIMEOUT", Toast.LENGTH_SHORT).show();
        isCapture = false;
    }

    @Override
    public void onFaceDetectionNonRecoverableError() {
        // This can happen if
        // Face detection not supported on this device
        // Something went wrong in the Android api
        // or our app or another app failed to release the camera properly
//        helloWorldTextView.setText(R.string.error_with_face_detection);
//        Toast.makeText(context, "NONRECOVER", Toast.LENGTH_SHORT).show();
        isCapture = false;

    }
}