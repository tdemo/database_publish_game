package cn.sdu.online.findteam.aliwukong.user;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;

import cn.sdu.online.findteam.R;
import cn.sdu.online.findteam.share.DemoUtil;

@TargetApi(11)
public class UserProfileActivity extends FragmentActivity {

    private static final String TAG = "UserProfileActivity";
    private static final int EDIT_PROFILE_CODE = 1;

    private long mOpenId;
    private String mMobilePhone;
    private Fragment mUserFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_user_profile);

        mOpenId = getIntent().getLongExtra("user_open_id",0);
        mMobilePhone = getIntent().getStringExtra("user_mobile_id");

        mUserFragment = UserProfileFragment.newInstance();
        Bundle bundle = new Bundle();
        bundle.putLong("user_open_id", mOpenId);
        bundle.putString("user_mobile_id", mMobilePhone);
        bundle.putBoolean("user_is_self", isMyOwn());
        mUserFragment.setArguments(bundle);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.user_profile_parent, mUserFragment);
        fragmentTransaction.commit();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_profile, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return isMyOwn()?true:false;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_edit_profile:
                Intent intent = new Intent(this, EditProfileActivity.class);
                intent.putExtra("user_profile", ((UserProfileFragment)mUserFragment).getUserProfile());
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivityForResult(intent, EDIT_PROFILE_CODE);
                break;
            default:
                break;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == EDIT_PROFILE_CODE){
            ((UserProfileFragment)mUserFragment).updateProfileData();
        }
    }

    private boolean isMyOwn() {
        if(mOpenId == DemoUtil.currentOpenId()) {
            return true;
        }
        return false;
    }
}
