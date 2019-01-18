package cn.stride1025.live;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.Log;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CameraAPI implements ICamera {


    private final CameraSizeComparator sizeComparator;
    private final CameraConfig mCameraConfig;
    private List<Camera.Size> mSupportedPreviewSizes;
    private List<Camera.Size> mSupportedPictureSizes;
    private Camera.Size mPropPreviewSize;
    private static final String TAG = "CameraAPI";
    private Camera mCamera;
    private int cameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
private SurfaceTexture mSurfaceTexture;
    public CameraAPI() {
        sizeComparator = new CameraSizeComparator();
        mCameraConfig = new CameraConfig();
        mCameraConfig.width = 1280;
        mCameraConfig.height = 720;
        mCameraConfig.fps = 30;
    }

    @Override

    public boolean openCamera(int cameraId) {
        this.cameraId = cameraId;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, cameraInfo);

        mCamera = Camera.open(cameraInfo.facing);

        if (null != mCamera) {
            Camera.Parameters parameters = mCamera.getParameters();
            mSupportedPreviewSizes = parameters.getSupportedPreviewSizes();
            mSupportedPictureSizes = parameters.getSupportedPictureSizes();
            Camera.Size previewSize = getMatchingPreviewSize(mSupportedPreviewSizes, mCameraConfig.width, mCameraConfig.height);
            parameters.setPreviewSize(previewSize.width, previewSize.height);
            mCamera.setParameters(parameters);
            return true;
        }

        return false;
    }

    @Override
    public boolean openCamera() {

       return openCamera(cameraId);

    }
    public int getCameraId() {

        return cameraId;
    }

    @Override
    public void startPreview() {
        if (null != mSurfaceTexture) {
            try {
                mCamera.startPreview();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void stopPreview() {

        if (null != mCamera) {
            mCamera.stopPreview();
            mCamera.release();
        }
        mCamera = null;
    }

    @Override
    public void setPreviewTexture(SurfaceTexture surfaceTexture) {
        this.mSurfaceTexture = surfaceTexture;
        try {
            mCamera.setPreviewTexture(mSurfaceTexture);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public SurfaceTexture getSurfaceTexure() {
        return mSurfaceTexture;
    }

    @Override
    public void setPreviewConfig(CameraConfig config) {

    }

    @Override
    public Camera.Size getPreviewSize() {
        return mPropPreviewSize;
    }

    @Override
    public Camera.Size getPictureSize() {
        return null;
    }

    private Camera.Size getMatchingPreviewSize(List<Camera.Size> supportSize, int width, int height) {


        mPropPreviewSize = getOptimalPreviewSize(supportSize, width, height);
        Log.i(TAG, "OptimalPreviewSize - > W " + mPropPreviewSize.width + " ->H " + mPropPreviewSize.height);

        return mPropPreviewSize;
    }


    //寻找与surfaceView合适的size
    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) w / h;
        if (sizes == null)
            return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        // Try to find an size match aspect ratio and size
        for (Camera.Size size : sizes) {
            Log.i(TAG, "Camera.Size - > W " + size.width + " ->H " + size.height);

            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
                continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    private Camera.Size getPropPictureSize(List<Camera.Size> list, float rate, int minWidth) {
        Collections.sort(list, sizeComparator);

        int i = 0;
        for (Camera.Size s : list) {
            if ((s.height >= minWidth) && equalRate(s, rate)) {
                break;
            }
            i++;
        }
        if (i == list.size()) {
            i = 0;
        }
        return list.get(i);
    }

    private boolean equalRate(Camera.Size s, float rate) {
        float r = (float) (s.width) / (float) (s.height);
        if (Math.abs(r - rate) <= 0.03) {
            return true;
        } else {
            return false;
        }
    }

    private class CameraSizeComparator implements Comparator<Camera.Size> {
        public int compare(Camera.Size lhs, Camera.Size rhs) {
            // TODO Auto-generated method stub
            if (lhs.height == rhs.height) {
                return 0;
            } else if (lhs.height > rhs.height) {
                return 1;
            } else {
                return -1;
            }
        }

    }
}
