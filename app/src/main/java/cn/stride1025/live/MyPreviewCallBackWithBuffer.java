package cn.stride1025.live;

import android.hardware.Camera;
import android.util.Log;

import static android.content.ContentValues.TAG;

/**
 * camera预览数据回调
 *
 */
public class MyPreviewCallBackWithBuffer implements Camera.PreviewCallback {


    static {
        System.loadLibrary("cameraactivity");
    }
    private final Native mANative;

    public MyPreviewCallBackWithBuffer() {

        mANative = new Native();
    }

    /**
     * C代码中执行 数据结构转化1920*1080分辨率 耗时20~30毫秒之间
     *  640 * 480 的分辨率 耗时 10毫秒内
     *
     * @param data
     * @param camera
     */
    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {

//
//        Queue<byte[]> q = new LinkedList<byte[]>();
//        q.add(data);
        Log.i(TAG, "onPreviewFrame ");

        long start = System.currentTimeMillis();
//        byte[] bytes1 = mANative.YUV420spToYUV420p(1920, 1080, data);
        byte[] bytes1 = mANative.YUV420spToYUV420p(640, 480, data);
        long end = System.currentTimeMillis();
        Log.i(TAG,"start "+start+" end "+end + " 耗时 "+ (end - start));
        camera.addCallbackBuffer(data);

    }
    private int NV21ToI420(byte[] nv21, byte[] yuv420, int width, int height) {
        if (nv21 == null || yuv420 == null)
            return -1;
        int frameSize = width * height;
        //Y
        for (int i = 0; i < frameSize; i++) {
            yuv420[i] = nv21[i];
        }
        int i = 0;
        //NV21格式中的 U 转化为 I420中的 U 。
        for (int k = 1; k < frameSize / 2; k += 2) {

            yuv420[i + frameSize] = nv21[k + frameSize];
            i++;
        }
        i = 0;
        for (int j = 0; j < frameSize / 2; j += 2) {
            yuv420[i + frameSize * 5 / 4] = nv21[j + frameSize];
            i++;
        }
        return 1;
    }


}
