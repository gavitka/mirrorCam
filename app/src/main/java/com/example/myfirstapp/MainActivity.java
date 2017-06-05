package com.example.myfirstapp;

import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;

public class MainActivity extends AppCompatActivity {

    private CameraManager mCameraManager    = null;
    static final String LOG_TAG = "ssibal";
    CameraHelper myCameras[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

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
            }
        } catch (CameraAccessException e) {
            Log.e(LOG_TAG,e.getMessage());
            e.printStackTrace();
        }



    }
}
