package com.example.myfirstapp;

import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Size;

/**
 * Created by Kheops on 6/5/2017.
 */

public class CameraHelper {
    private CameraManager mCameraManager    = null;
    private String mCameraID    = null;

    public CameraHelper(@NonNull CameraManager cameraManager, @NonNull String cameraID) {
        mCameraManager  = cameraManager;
        mCameraID       = cameraID;
    }

    public void viewFormatSize(int formatSize) {
        // Получения характеристик камеры
        CameraCharacteristics cc = null;
        try {
            cc = mCameraManager.getCameraCharacteristics(mCameraID);

            // Получения списка выходного формата, который поддерживает камера
            StreamConfigurationMap configurationMap =
                    cc.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

            // Получения списка разрешений которые поддерживаются для формата jpeg
            Size[] sizesJPEG = configurationMap.getOutputSizes(ImageFormat.JPEG);

            if (sizesJPEG != null) {
                for (Size item:sizesJPEG) {
                    Log.i(MainActivity.LOG_TAG, "w:" + item.getWidth() + " h:" + item.getHeight());
                }
            } else {
                Log.e(MainActivity.LOG_TAG, "camera with id: "+mCameraID+" don`t support format: "+formatSize);
            }

        } catch (CameraAccessException e) {
            Log.e(MainActivity.LOG_TAG,e.getMessage());
            //e.printStackTrace();
        }
    }

}
