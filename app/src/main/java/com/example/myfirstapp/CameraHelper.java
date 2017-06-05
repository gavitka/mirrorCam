package com.example.myfirstapp;

import android.Manifest;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;

import java.util.Arrays;

/**
 * Created by Kheops on 6/5/2017.
 */

public class CameraHelper {
    private CameraManager mCameraManager    = null;
    private String mCameraID    = null;

    private CameraDevice mCameraDevice  = null;
    private CameraCaptureSession mSession;
    private TextureView mTextureView;

    public boolean isOpen() {
        if (mCameraDevice == null) {
            return false;
        } else {
            return true;
        }
    }

    public void openCamera() {
        try {
            mCameraManager.openCamera(mCameraID,mCameraCallback,null);
        } catch (CameraAccessException e) {
            Log.e(MainActivity.LOG_TAG,e.getMessage());
            //e.printStackTrace();
        }
    }

    public void closeCamera() {

        if (mCameraDevice != null) {
            mCameraDevice.close();
            mCameraDevice = null;
        }
    }

    private CameraDevice.StateCallback mCameraCallback = new CameraDevice.StateCallback() {

        @Override
        public void onOpened(CameraDevice camera) {
            mCameraDevice = camera;
            createCameraPreviewSession();
            Log.i(MainActivity.LOG_TAG, "Open camera  with id:"+mCameraDevice.getId());
        }

        @Override
        public void onDisconnected(CameraDevice camera) {
            mCameraDevice.close();
            mCameraDevice = null;
            Log.i(MainActivity.LOG_TAG, "disconnect camera  with id:"+mCameraDevice.getId());
        }

        @Override
        public void onError(CameraDevice camera, int error) {
            Log.i(MainActivity.LOG_TAG, "error! camera id:"+camera.getId()+" error:"+error);
        }
    };

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

    private void createCameraPreviewSession() {

        SurfaceTexture texture = mTextureView.getSurfaceTexture();
        texture.setDefaultBufferSize(1920,1080);
        Surface surface = new Surface(texture);

        try {
            final CaptureRequest.Builder builder =
                    mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);

            builder.addTarget(surface);

            mCameraDevice.createCaptureSession(
                    Arrays.asList(surface),
                    new CameraCaptureSession.StateCallback() {

                        @Override
                        public void onConfigured(CameraCaptureSession session) {
                            mSession = session;
                            try {
                                mSession.setRepeatingRequest(builder.build(),null,null);
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onConfigureFailed(CameraCaptureSession session) {
                        }

                    },
                    null

            );
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }

    public void setTextureView(TextureView value) {
        mTextureView = value;
    }

}
