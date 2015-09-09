package cn.sdu.online.findteam.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import java.io.Serializable;
import java.util.ArrayList;

import cn.sdu.online.findteam.R;
import cn.sdu.online.findteam.view.GestureImageView;

public class ImgShowerActivity extends Activity {

    private GestureImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.imageshower);
        imageView = (GestureImageView) findViewById(R.id.show_bigimg);

        if (getIntent().getExtras() != null){
            byte[] bitmap = (byte[]) getIntent().getSerializableExtra("bitmap");
            imageView.setImageBitmap(getBitmap(bitmap));
        }

        imageView.setClickable(true);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(0, R.anim.zoomout);
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(0, R.anim.zoomout);
        super.onBackPressed();
    }

    public Bitmap getBitmap(byte[] data){
        return BitmapFactory.decodeByteArray(data, 0, data.length);//从字节数组解码位图
    }
}


