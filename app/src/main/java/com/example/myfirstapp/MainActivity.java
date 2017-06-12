package com.example.myfirstapp;

import android.Manifest;
import android.app.Activity;
import android.graphics.Matrix;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;

import java.util.Arrays;

import static android.R.attr.id;

public class MainActivity extends AppCompatActivity
    implements  ActivityCompat.OnRequestPermissionsResultCallback{

    private CameraManager mCameraManager    = null;
    static final String LOG_TAG = "ssibal";
    private static final int REQUEST_CAM_PERMISSIONS = 1;
    private final int CAMERA1   = 0;
    private final int CAMERA2   = 1;
    private CameraHelper[] myCameras = null;
    private Button mButtonOpenCamera1 = null;
    private Button mButtonOpenCamera2 = null;
    private com.example.myfirstapp.AutoFitTextureView mImageView = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        mImageView = (com.example.myfirstapp.AutoFitTextureView) findViewById(R.id.image_view);

        try {
            // Получения списка камер в устрйстве
            String[] cameraList = mCameraManager.getCameraIdList();
            myCameras = new CameraHelper[cameraList.length];
            for (String cameraID : cameraList) {
                int id = Integer.parseInt(cameraID);
                myCameras[id] = new CameraHelper(mCameraManager,cameraID, this);
            }
            myCameras[CAMERA1].setTextureView(mImageView);
        } catch (CameraAccessException e) {
            Log.e(LOG_TAG,e.getMessage());
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        REQUEST_CAM_PERMISSIONS);
            }
        } else {
            openCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAM_PERMISSIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    openCamera();

                } else {

                }
                return;
            }
        }
    }

    private void openCamera() {
        if (myCameras[CAMERA1] != null) {
            myCameras[CAMERA1].showCamera();
        }
    }


}
