package cn.stride1025.live;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;

public interface ICamera {

    boolean openCamera(int cameraId);
    boolean openCamera();
    void startPreview();

    void stopPreview();

    /**
     * 使用SurfaceTexture 来作为预览的画面
     *
     * @param surfaceTexture
     */
    void setPreviewTexture(SurfaceTexture surfaceTexture);


    void setPreviewConfig(CameraConfig config);


    Camera.Size getPreviewSize();

    Camera.Size getPictureSize();




    class CameraConfig{
        int width;
        int height;
        int fps;
    }
}
