package cn.sdu.online.findteam.aliwukong.user;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;

import com.alibaba.doraemon.Doraemon;
import com.alibaba.doraemon.image.ImageMagician;

import cn.sdu.online.findteam.R;

@TargetApi(11)
public class UserAvatarActivity extends Activity {

    private ImageView mAvatarImageView;
    private String mAvatarUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        mAvatarUrl = getIntent().getStringExtra("user_avatar_url");

        setContentView(R.layout.activity_user_avatar);
        mAvatarImageView = (ImageView)findViewById(R.id.user_avatar_big);

        ImageMagician imageMagician = (ImageMagician) Doraemon.getArtifact(ImageMagician.IMAGE_ARTIFACT);
        imageMagician.setImageDrawable(mAvatarImageView,mAvatarUrl,null);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
