package com.example.myfirstapp;

import android.Manifest;
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

public class MainActivity extends AppCompatActivity {

    private CameraManager mCameraManager    = null;
    static final String LOG_TAG = "ssibal";
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    private final int CAMERA1   = 0;
    private final int CAMERA2   = 1;
    private CameraHelper[] myCameras = null;
    private Button mButtonOpenCamera1 = null;
    private Button mButtonOpenCamera2 = null;
    private TextureView mImageView = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        mImageView = (TextureView) findViewById(R.id.image_view);

        //Matrix matrix = mImageView.getMatrix();
        //matrix.preScale(-1.0f,1.0f);
        //matrix.postScale(-1.0f,1.0f);
        //mImageView.setTransform(matrix);
        mImageView.setScaleX(-1.0f);
        //Log.i(LOG_TAG, "Matrix:" + matrix.toString());

        try {
            // Получения списка камер в устрйстве
            String[] cameraList = mCameraManager.getCameraIdList();
            myCameras = new CameraHelper[cameraList.length];
            for (String cameraID : cameraList) {
                Log.i(LOG_TAG, "cameraID: "+cameraID);
                int id = Integer.parseInt(cameraID);

                // создаем обработчик для камеры
                myCameras[id] = new CameraHelper(mCameraManager,cameraID);

                // выводим инормацию по камере
                myCameras[id].viewFormatSize(ImageFormat.JPEG);
                myCameras[id].setTextureView(mImageView);
            }
        } catch (CameraAccessException e) {
            Log.e(LOG_TAG,e.getMessage());
            e.printStackTrace();
        }

        //requestPermissions(new String[]{Manifest.permission.CAMERA}, REQ_ACCESS_FINE_LOCATION);

        mButtonOpenCamera1 = (Button) findViewById(R.id.btn_open_camera1);
        mButtonOpenCamera2 = (Button) findViewById(R.id.btn_open_camera2);
        mButtonOpenCamera1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myCameras[CAMERA2].isOpen()) myCameras[CAMERA2].closeCamera();
                if (myCameras[CAMERA1] != null) {
                    if (!myCameras[CAMERA1].isOpen()) {
                        myCameras[CAMERA1].openCamera();
                    }
                }
            }
        });

        mButtonOpenCamera2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myCameras[CAMERA1].isOpen()) myCameras[CAMERA1].closeCamera();
                if (myCameras[CAMERA2] != null) {
                    if (!myCameras[CAMERA2].isOpen()) myCameras[CAMERA2].openCamera();
                }
            }
        });

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            }
        }
    }



}
