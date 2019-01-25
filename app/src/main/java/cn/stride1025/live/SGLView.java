package cn.stride1025.live;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import java.io.IOException;

public class SGLView extends GLSurfaceView  {
    public SGLView(Context context) {

        this(context,null);

    }

    public SGLView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setEGLContextClientVersion(2);
        SGLRender sglRender = new SGLRender(this.getContext());
        setRenderer(sglRender);
        setRenderMode(RENDERMODE_WHEN_DIRTY);
        try {
            sglRender.setImage(BitmapFactory.decodeStream(getResources().getAssets().open("texture/fengj.png")));
            requestRender();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
