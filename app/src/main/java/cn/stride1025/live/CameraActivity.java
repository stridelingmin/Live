package cn.stride1025.live;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.tbruyelle.rxpermissions.RxPermissions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.stride1025.live.utils.ImageUtil;
import rx.Observer;

public class CameraActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener {

    private static final SparseIntArray orientations = new SparseIntArray();//手机旋转对应的调整角度

    static {
        orientations.append(Surface.ROTATION_0, 90);
        orientations.append(Surface.ROTATION_90, 0);
        orientations.append(Surface.ROTATION_180, 270);
        orientations.append(Surface.ROTATION_270, 180);
    }

    private Button mOpenCamera;
    private Button mCheckPhoto;
    private Camera mCamera;
    private String TAG = CameraActivity.class.getSimpleName();
    private TextureView mTextureView;
    private int mSurface_h;
    private int mSurface_w;
    private Button capPhoto,record;
    private String photoPath = null;
    private byte[] mBuffer;
    private boolean recording = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.mediarecoder_activity);

        mTextureView = (TextureView) findViewById(R.id.textureView);
        mOpenCamera = (Button) findViewById(R.id.openCamera);
        mCheckPhoto = (Button) findViewById(R.id.checkPhoto);
        capPhoto = (Button) findViewById(R.id.capPhoto);
        record = (Button) findViewById(R.id.record);
        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recording = true;
            }
        });
        requestPermission();

        mOpenCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();
            }
        });

        capPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();

            }
        });

        mCheckPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mCamera) {
                    mCamera.stopPreview();
                    mCamera.release();
                    mTextureView.clearFocus();
                    mCamera = null;
                }
                checkPhoto();
            }
        });
        mTextureView.post(new Runnable() {
            @Override
            public void run() {
                mSurface_h = mTextureView.getWidth();
                mSurface_w = mTextureView.getHeight();
            }
        });
        mTextureView.setSurfaceTextureListener(this);
    }

    private void checkPhoto() {
        Intent intent = new Intent();
        intent.setClass(this,ImageViewActivity.class);
        intent.putExtra("path",photoPath);
        startActivity(intent);

    }

    //权限申请
    private void requestPermission() {

        RxPermissions rxPermissions = new RxPermissions(this);

        rxPermissions.request(Manifest.permission.CAMERA
                , Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        )
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {


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

        mTextureView.post(new Runnable() {
            @Override
            public void run() {
                ViewGroup.LayoutParams layoutParams = mTextureView.getLayoutParams();
                layoutParams.width = previewSize.height;//相机支持的宽高和布局相反
                layoutParams.height = previewSize.width;
                mTextureView.setLayoutParams(layoutParams);
                mTextureView.invalidate();
            }
        });
        parameters.setPreviewSize(previewSize.width, previewSize.height);
        parameters.setPictureSize(previewSize.width, previewSize.height);
        Camera.Size pictureSize = parameters.getPictureSize();
        Log.i(TAG, "mTextureView_w - >  " + mTextureView.getWidth() + " ->mTextureView_h " + mTextureView.getHeight());

        Log.i(TAG, "pictureSize - > pictureSize " + pictureSize.width + " ->pictureSize_h " + pictureSize.height);


        Log.i(TAG, "setPreviewSize - > previewSize_w " + previewSize.width + " ->previewSize_h " + previewSize.height);
        List<int[]> supportedPreviewFpsRange = parameters.getSupportedPreviewFpsRange();//支持的刷新频率

//        min 14000 ->max 25000
//         min 25000 ->max 25000
//         min 2000 ->max 30000
//         min 30000 ->max 30000
        for (int[] size :
                supportedPreviewFpsRange) {
            Log.i(TAG, "supportedPreviewFpsRange - > min " +size[0] + " ->max "+size[1]);
        }
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        parameters.setPictureFormat(ImageFormat.JPEG);
        parameters.setPreviewFormat(ImageFormat.NV21);
        parameters.setPreviewFpsRange(30000,30000);
        try {
            mCamera.setPreviewTexture(mTextureView.getSurfaceTexture());

        } catch (IOException e) {
            e.printStackTrace();
        }
        int cameraDisplayOrientation = getCameraDisplayOrientation(this, cameraInfo.facing, null);
        Log.i(TAG, "cameraDisplayOrientation " + cameraDisplayOrientation);
        parameters.setRotation(cameraDisplayOrientation);//用来旋转照片角度
        mCamera.setParameters(parameters);

        mBuffer = new byte[previewSize.height * previewSize.width *4];
        mCamera.addCallbackBuffer(mBuffer);
        mCamera.setPreviewCallbackWithBuffer(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {
                Log.i(TAG, "cameraDisplayOrientation ");
                if (null != camera) {
                    mCamera.addCallbackBuffer(mBuffer);
                }


            }
        });

        mCamera.setDisplayOrientation(cameraDisplayOrientation);
        mCamera.startPreview();


    }


    private void takePicture() {

        mCamera.takePicture(null, null, mPicture);
    }

    //拍照回调
    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            File publicDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM + File.separator);

            if (!publicDirectory.exists()) {
                publicDirectory.mkdir();
            }
            // 获取Jpeg图片，并保存在sd卡上
            File pictureFile = new File(publicDirectory.getAbsolutePath() + "/VIEW" + System.currentTimeMillis()
                    + ".jpg");
            if (!pictureFile.exists()) {
                try {
                    pictureFile.createNewFile();
                } catch (IOException e) {
                    Log.d(TAG, "保存图片失败");

                    e.printStackTrace();
                }
            }
            try {
                //数据要转一步bitmap ,否则手机预览图片缩率图会产生蓝绿
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                //添加水印
                Bitmap TextBitmap = ImageUtil.drawTextToLeftTop(CameraActivity.this, bitmap, "华康", 16, getResources().getColor(R.color.white), 10, 10);
                FileOutputStream fos = new FileOutputStream(pictureFile);
                TextBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);

//                fos.write(data);

                fos.flush();
                fos.close();
            } catch (Exception e) {
                Log.d(TAG, "保存图片失败");
            }
            photoPath = pictureFile.getAbsolutePath();
            Toast.makeText(CameraActivity.this, "图片保存路径:" + pictureFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            mCamera.startPreview();
        }

    };

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

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        //
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }
}
