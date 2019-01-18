package cn.stride1025.live;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

/**
 * 相片显示
 */
public class ImageViewActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String path = getIntent().getStringExtra("path");

        Bitmap bitmap = BitmapFactory.decodeFile(path);

        ImageView imageView = new ImageView(this);
        imageView.setImageBitmap(bitmap);
        setContentView(imageView);



    }
}
