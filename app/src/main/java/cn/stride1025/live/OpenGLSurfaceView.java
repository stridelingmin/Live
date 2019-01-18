package cn.stride1025.live;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class OpenGLSurfaceView extends GLSurfaceView implements GLSurfaceView.Renderer {

    private SurfaceTexture mSurfaceTexture;
    private Filter mFilter;
    private static final String TAG = "OpenGLSurfaceView";
    private CameraAPI mCameraAPI;
    private GLESAPI mGlesapi;
    private int mHCoordMatrix;

    public OpenGLSurfaceView(Context context) {

        this(context, null);
    }

    public OpenGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);

        linkGLESContext();

    }

    private void linkGLESContext() {
        setEGLContextClientVersion(2);
        setRenderer(this);
        setRenderMode(RENDERMODE_WHEN_DIRTY);
        mCameraAPI = new CameraAPI();
        mGlesapi = new GLESAPI(getResources());

    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        boolean hasOpen = mCameraAPI.openCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);
        Log.i(TAG, "- > " + hasOpen);
        int textureID = mGlesapi.createTextureID();
        SurfaceTexture texture = new SurfaceTexture(textureID);
        mGlesapi.createProgramByAssetsFile("shader/oes_base_vertex.sh", "shader/oes_base_fragment.sh");
        Camera.Size previewSize = mCameraAPI.getPreviewSize();
        mGlesapi.setCameraId(mCameraAPI.getCameraId());
        mGlesapi.setDataSize(previewSize.height, previewSize.width);
        mCameraAPI.setPreviewTexture(texture);

        texture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
            @Override
            public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                requestRender();
            }
        });

        mCameraAPI.startPreview();


    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.e(TAG, "onSurfaceChanged - " + width + "/" + height);
        mGlesapi.setViewSize(width, height);
//        GLES20.glViewport(0, 0, width, height);

    }

    @Override
    public void onDrawFrame(GL10 gl) {
        SurfaceTexture surfaceTexture = mCameraAPI.getSurfaceTexure();
        if (null != surfaceTexture) {
            surfaceTexture.updateTexImage();
        }
        mGlesapi.callDraw();

    }


}
