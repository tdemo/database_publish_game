package cn.sdu.online.findteam.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import cn.sdu.online.findteam.R;
import cn.sdu.online.findteam.share.MyApplication;
import cn.sdu.online.findteam.util.AndTools;

public class StartActivity extends Activity implements View.OnClickListener {

    private Button start_login, start_see;
    public static StartActivity startActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.startactivity_layout);

        initView();
    }

    private void initView() {
        start_login = (Button) findViewById(R.id.start_login_btn);
        start_see = (Button) findViewById(R.id.start_seearound_btn);
        start_login.setOnClickListener(this);
        start_see.setOnClickListener(this);
        startActivity = this;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_login_btn:
                Intent intent = new Intent();
                intent.setClass(StartActivity.this, LoginActivity.class);
                String loginName = MyApplication.getInstance().getSharedPreferences("loginmessage", Context.MODE_PRIVATE).
                        getString("loginName", "");
                String loginPassword = MyApplication.getInstance().getSharedPreferences("loginmessage", Context.MODE_PRIVATE).
                        getString("loginPassword", "");
                if (!loginName.equals("") &&
                        !loginPassword.equals("")) {
                    intent.putExtra("loginName", loginName);
                    intent.putExtra("loginPassword", loginPassword);
                }
                startActivity(intent);
                break;

            case R.id.start_seearound_btn:
                if (!AndTools.isNetworkAvailable(StartActivity.this)){
                    AndTools.showToast(StartActivity.this, "网络错误！");
                    return;
                }

                MyApplication.USER_OR_NOT = 0;
                Intent intent1 = new Intent();
                intent1.setClass(StartActivity.this, MainActivity.class);
    /*                intent1.putExtra("loginIdentity", "<##游客##>");*/
                startActivity(intent1);
                StartActivity.this.finish();
        }
    }
}
