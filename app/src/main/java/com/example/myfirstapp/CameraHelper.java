package com.example.myfirstapp;

import android.Manifest;
import android.app.Activity;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.MediaRecorder;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static android.content.ContentValues.TAG;
import static android.os.Build.VERSION_CODES.M;
import static com.example.myfirstapp.MainActivity.LOG_TAG;

/**
 * Created by Kheops on 6/5/2017.
 */

public class CameraHelper {
    private CameraManager mCameraManager    = null;
    private String mCameraID    = null;

    private CameraDevice mCameraDevice  = null;
    private CameraCaptureSession mSession;
    private AutoFitTextureView mTextureView;
    private Size mPreviewSize;
    private Size mVideoSize;
    private StreamConfigurationMap map;
    private Activity mActivity;

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
            Log.e(LOG_TAG,e.getMessage());
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
            Log.i(LOG_TAG, "Open camera  with id:"+mCameraDevice.getId());
        }

        @Override
        public void onDisconnected(CameraDevice camera) {
            mCameraDevice.close();
            mCameraDevice = null;
            Log.i(LOG_TAG, "disconnect camera  with id:"+mCameraDevice.getId());
        }

        @Override
        public void onError(CameraDevice camera, int error) {
            Log.i(LOG_TAG, "error! camera id:"+camera.getId()+" error:"+error);
        }
    };

    public CameraHelper(@NonNull CameraManager cameraManager, @NonNull String cameraID, Activity activity) {
        mCameraManager  = cameraManager;
        mCameraID       = cameraID;
        mActivity       = activity;
    }

    public void viewFormatSize(int formatSize) {
        // Получения характеристик камеры
        CameraCharacteristics cc = null;
        try {
            cc = mCameraManager.getCameraCharacteristics(mCameraID);

            // Получения списка выходного формата, который поддерживает камера
            map =
                    cc.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

            // Получения списка разрешений которые поддерживаются для формата jpeg
            Size[] sizesJPEG = map.getOutputSizes(ImageFormat.JPEG);

            if (sizesJPEG != null) {
                for (Size item:sizesJPEG) {
                    Log.i(LOG_TAG, "w:" + item.getWidth() + " h:" + item.getHeight());
                }
            } else {
                Log.e(LOG_TAG, "camera with id: "+mCameraID+" don`t support format: "+formatSize);
            }

        } catch (CameraAccessException e) {
            Log.e(LOG_TAG,e.getMessage());
            //e.printStackTrace();
        }
    }

    private void createCameraPreviewSession() {

        mTextureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture texture, int width, int height) {
                Log.i(LOG_TAG, "width: " + width);
                Log.i(LOG_TAG, "height: " + height);

                mVideoSize = chooseVideoSize(map.getOutputSizes(MediaRecorder.class));
                mPreviewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class),
                        width, height, mVideoSize);

                texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
                mTextureView.setAspectRatio(mPreviewSize.getHeight(), mPreviewSize.getWidth());
//                RectF viewRect = new RectF(0, 0, 1080, 1920);
//                RectF bufferRect = new RectF(0, 0, 480, 640);

//                Matrix matrix = new Matrix();
//                matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);

//                mTextureView.setTransform(matrix);
                //mTextureView.setScaleX(-1.0f);

                //texture.setDefaultBufferSize(640, 480);
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
                                        mSession.setRepeatingRequest(builder.build(), null, null);
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
                configureTransform(width, height);
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                return true;
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
                configureTransform(width, height);
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {
            }

        });
    }

    public void setTextureView(AutoFitTextureView value) {
        mTextureView = value;
    }

    private static Size chooseVideoSize(Size[] choices) {
        for (Size size : choices) {
            if (size.getWidth() == size.getHeight() * 4 / 3 && size.getWidth() <= 1080) {
                return size;
            }
        }
        Log.e(TAG, "Couldn't find any suitable video size");
        return choices[choices.length - 1];
    }

    private static Size chooseOptimalSize(Size[] choices, int width, int height, Size aspectRatio) {
        // Collect the supported resolutions that are at least as big as the preview Surface
        List<Size> bigEnough = new ArrayList<>();
        int w = aspectRatio.getWidth();
        int h = aspectRatio.getHeight();
        for (Size option : choices) {
            if (option.getHeight() == option.getWidth() * h / w &&
                    option.getWidth() >= width && option.getHeight() >= height) {
                bigEnough.add(option);
            }
        }

        // Pick the smallest of those, assuming we found any
        if (bigEnough.size() > 0) {
            return Collections.min(bigEnough, new CompareSizesByArea());
        } else {
            Log.e(TAG, "Couldn't find any suitable preview size");
            return choices[0];
        }
    }

    static class CompareSizesByArea implements Comparator<Size> {

        @Override
        public int compare(Size lhs, Size rhs) {
            // We cast here to ensure the multiplications won't overflow
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                    (long) rhs.getWidth() * rhs.getHeight());
        }
    }

    private void configureTransform(int viewWidth, int viewHeight) {
        if (null == mTextureView || null == mPreviewSize || null == mActivity) {
            return;
        }
        int rotation = mActivity.getWindowManager().getDefaultDisplay().getRotation();
        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
        Log.i(LOG_TAG, " view" +  viewWidth + " " + viewHeight);
        RectF bufferRect = new RectF(0, 0, mPreviewSize.getHeight(), mPreviewSize.getWidth());
        Log.i(LOG_TAG, " bufferRect" +  mPreviewSize.getHeight() + " " + mPreviewSize.getWidth());
        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.CENTER);
            float scale = Math.max(
                    (float) viewHeight / mPreviewSize.getHeight(),
                    (float) viewWidth / mPreviewSize.getWidth());
            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
        }
        mTextureView.setTransform(matrix);
    }

}
