package cn.stride1025.live;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class SGLRender implements GLSurfaceView.Renderer {

    private Bitmap mBitmap;
    private AFilter mAFilter;

    public SGLRender() {
    }

    public SGLRender(Context context) {
        mAFilter = new AFilter(context, "filter/half_color_vertex.sh", "filter/half_color_fragment.sh");

    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mAFilter.onSurfaceCreated(gl,config);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mAFilter.onSurfaceChanged(gl,width,height);

    }

    @Override
    public void onDrawFrame(GL10 gl) {
        mAFilter.onDrawFrame(gl);
    }
    public void setImage(Bitmap bitmap) {
        this.mBitmap = bitmap;
        mAFilter.setImage(mBitmap);
    }
}
