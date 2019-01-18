package cn.stride1025.live;

import android.Manifest;
import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.tbruyelle.rxpermissions.RxPermissions;

import rx.Observer;

public class MainActivity extends AppCompatActivity {
    private static final SparseIntArray orientations = new SparseIntArray();//手机旋转对应的调整角度
    static {
        orientations.append(Surface.ROTATION_0, 90);
        orientations.append(Surface.ROTATION_90, 0);
        orientations.append(Surface.ROTATION_180, 270);
        orientations.append(Surface.ROTATION_270, 180);
    }

    private String TAG = MainActivity.class.getSimpleName();
    private TextureView mSurfaceView;
    private String mime = "video/avc";
    private TextureView mTextureView;
    private SurfaceTexture mSurfaceTexture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        requestPermission();
        Button openGl = (Button) findViewById(R.id.gl);
        openGl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,OpenGLActivity.class));
            }
        });

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

}
