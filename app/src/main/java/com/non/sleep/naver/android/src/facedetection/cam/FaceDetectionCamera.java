package com.non.sleep.naver.android.src.facedetection.cam;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.media.FaceDetector;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.widget.Toast;

import com.non.sleep.naver.android.src.facedetection.FaceDetectionActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Manages the android camera and sets it up for face detection
 * can throw an error if face detection is not supported on this device
 */
public class FaceDetectionCamera implements OneShotFaceDetectionListener.Listener {

    private final Camera camera;

    private Listener listener;

    public FaceDetectionCamera(Camera camera) {
        this.camera = camera;
    }

    /**
     * Use this to detect faces when you have a custom surface to display upon
     *
     * @param listener the {@link com.non.sleep.naver.android.src.facedetection.cam.FaceDetectionCamera.Listener} for when faces are detected
     * @param holder   the {@link android.view.SurfaceHolder} to display upon
     */
    public void initialise(Listener listener, SurfaceHolder holder) {
        this.listener = listener;
        try {
            camera.stopPreview();
        } catch (Exception swallow) {
            // ignore: tried to stop a non-existent preview
        }
        try {
            camera.setPreviewDisplay(holder);
            camera.setDisplayOrientation(90);
            camera.startPreview();
            camera.setPreviewCallback(previewCallback);
            camera.setFaceDetectionListener(new OneShotFaceDetectionListener(this));
            camera.startFaceDetection();
        } catch (IOException e) {
            this.listener.onFaceDetectionNonRecoverableError();
        }
    }

    Camera.PreviewCallback previewCallback = new Camera.PreviewCallback() {

        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            if(FaceDetectionActivity.isCapture == true) {

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Camera.Parameters parameters = camera.getParameters();
                int width = parameters.getPreviewSize().width;
                int height = parameters.getPreviewSize().height;
                ByteArrayOutputStream outstr = new ByteArrayOutputStream();
                Rect rect = new Rect(0, 0, width, height);
                YuvImage yuvimage = new YuvImage(data, ImageFormat.NV21, width, height, null);
                yuvimage.compressToJpeg(rect, 100, outstr);
                Bitmap bmp = BitmapFactory.decodeByteArray(outstr.toByteArray(), 0, outstr.size());
                saveImage(bmp, "facedetection");
                FaceDetectionActivity.isCapture = false;
                Log.i("VSDVDS", "SVDSDV");
                Toast.makeText(FaceDetectionActivity.context, "CAPTURE", Toast.LENGTH_SHORT).show();
            }
        }
    };
    // face detect screen -> get byte array -> bitmap -> jpg

    private void saveImage(Bitmap finalBitmap, String image_name) {

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root);
        myDir.mkdirs();
        String fname = "Image-" + image_name+ ".jpg";
        File file = new File(myDir, fname);
        if (file.exists()) file.delete();
        Log.i("LOAD", root + fname);
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // save bitmap -> image

    @Override
    public void onFaceDetected() {
        listener.onFaceDetected();
    }

    @Override
    public void onFaceTimedOut() {
        listener.onFaceTimedOut();
    }

    public void recycle() {
        if (camera != null) {
            camera.release();
        }
    }

    public interface Listener {
        void onFaceDetected();

        void onFaceTimedOut();

        void onFaceDetectionNonRecoverableError();

    }


}
