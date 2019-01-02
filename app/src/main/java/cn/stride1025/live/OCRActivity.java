package cn.stride1025.live;

import android.app.Activity;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class OCRActivity extends AppCompatActivity {

    private SurfaceView mSurfaceView;
    private Button mOpenCamera;
    private Button mCheckPhoto;
    private Camera mCamera;
    private String TAG = CameraActivity.class.getSimpleName();
    private int mSurface_h;
    private int mSurface_w;
    private Button capPhoto;
    private String photoPath = null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.orc_activity);


        mSurfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        Button button = (Button) findViewById(R.id.btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                openCamera();
            }
        });

    }
    //打开预览
    private void openCamera() {
        int numberOfCameras = Camera.getNumberOfCameras();//获取手机上有效摄像头个数
        Log.i(TAG, "CameraNum - > " + numberOfCameras);

        if (numberOfCameras <= 0) {
            return;
        }
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK, cameraInfo);
        Log.i(TAG, "CameraInfo - > " + cameraInfo.facing);
        Log.i(TAG, "CameraInfo - > " + cameraInfo.orientation);

        mCamera = Camera.open(cameraInfo.facing);
        //        mCamera.lock();
        Camera.Parameters parameters = mCamera.getParameters();
        List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();//获取支持预览尺寸
        List<Camera.Size> supportedVideoSizes = parameters.getSupportedVideoSizes();//获取支持视频录制尺寸
        for (Camera.Size size :
                previewSizes) {
            Log.i(TAG, "supportedVideoSizes - > width " + size.width + " ->height " + size.height);
        }
        Camera.Size preferredPreviewSizeForVideo = parameters.getPreferredPreviewSizeForVideo();//获取最优预览的尺寸
        List<Camera.Size> supportedPictureSizes = parameters.getSupportedPictureSizes();//获取支持的照片尺寸
        for (Camera.Size size :
                supportedPictureSizes) {
            Log.i(TAG, "supportedPictureSizes - > width " + size.width + " ->height " + size.height);
        }
        //返回值为int类型，大于0表示正序，小于0表示逆序
        //supportedPictureSizes 原始数据，从小到大排序的。
        Collections.sort(supportedPictureSizes, new Comparator<Camera.Size>() {
            @Override
            public int compare(Camera.Size o1, Camera.Size o2) {
                boolean b = o1.width < o2.width;
                Log.i(TAG, "compare - > o1 width " + o1.width + " ->o2 width " + o2.width);

                if (b) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });
        for (Camera.Size size :
                supportedPictureSizes) {
            Log.i(TAG, "supportedPictureSizes - > 排序后 width " + size.width + " ->height " + size.height);
        }
        Log.i(TAG, "preferredPreviewSizeForVideo - > width " + preferredPreviewSizeForVideo.width + " ->height " + preferredPreviewSizeForVideo.height);

        final Camera.Size previewSize = getOptimalPreviewSize(previewSizes, mSurface_w, mSurface_h);

        mSurface_h = new Integer(previewSize.height);
        mSurface_w = new Integer(previewSize.width);
        Log.i(TAG, "OptimalPreviewSize - > mSurface_w " + mSurface_w + " ->mSurface_h " + mSurface_h);

        mSurfaceView.post(new Runnable() {
            @Override
            public void run() {
                ViewGroup.LayoutParams layoutParams = mSurfaceView.getLayoutParams();
                layoutParams.width = previewSize.height;//相机支持的宽高和布局相反
                layoutParams.height = previewSize.width;
                mSurfaceView.setLayoutParams(layoutParams);
                mSurfaceView.invalidate();
            }
        });
        parameters.setPreviewSize(previewSize.width, previewSize.height);
        parameters.setPictureSize(previewSize.width, previewSize.height);
        Camera.Size pictureSize = parameters.getPictureSize();
        Log.i(TAG, "mTextureView_w - >  " + mSurfaceView.getWidth() + " ->mTextureView_h " + mSurfaceView.getHeight());

        Log.i(TAG, "pictureSize - > pictureSize " + pictureSize.width + " ->pictureSize_h " + pictureSize.height);


        Log.i(TAG, "setPreviewSize - > previewSize_w " + previewSize.width + " ->previewSize_h " + previewSize.height);

        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        parameters.setPictureFormat(ImageFormat.JPEG);
        try {
            mCamera.setPreviewDisplay(mSurfaceView.getHolder());
        } catch (IOException e) {
            e.printStackTrace();
        }
        int cameraDisplayOrientation = getCameraDisplayOrientation(this, cameraInfo.facing, null);
        Log.i(TAG, "cameraDisplayOrientation " + cameraDisplayOrientation);
        parameters.setRotation(cameraDisplayOrientation);//用来旋转照片角度
        mCamera.setParameters(parameters);
        mCamera.setDisplayOrientation(cameraDisplayOrientation);
        mCamera.startPreview();
    }
    //获取正确的预览角度
    public static int getCameraDisplayOrientation(Activity activity,
                                                  int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        return result;
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
}
