package cn.stride1025.live;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * 图片处理 离屏渲染
 */
public class FBOActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SGLView sglView = new SGLView(this);
        setContentView(sglView);

    }
}
