package cn.sdu.online.findteam.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.wukong.Callback;
import com.alibaba.wukong.auth.ALoginParam;
import com.alibaba.wukong.auth.AuthInfo;
import com.alibaba.wukong.auth.AuthService;
import com.alibaba.wukong.im.IMEngine;
import com.alibaba.wukong.im.UserService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Timer;
import java.util.TimerTask;

import cn.sdu.online.findteam.R;
import cn.sdu.online.findteam.entity.User;
import cn.sdu.online.findteam.net.NetCore;
import cn.sdu.online.findteam.resource.DialogDefine;
import cn.sdu.online.findteam.share.DemoUtil;
import cn.sdu.online.findteam.share.MyApplication;
import cn.sdu.online.findteam.util.AndTools;
import cn.sdu.online.findteam.util.LoginUtils;


public class LoginActivity extends Activity implements View.OnClickListener {

    private EditText loginname;
    private EditText loginpassword;
    private Button login;
    private Button register;
    private Dialog dialog;

    User myUser;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    public static LoginActivity loginActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginactivity_layout);

        initView();
    }

    private void initView() {
        loginname = (EditText) findViewById(R.id.login_name);
        loginpassword = (EditText) findViewById(R.id.login_password);
        if (getIntent().getExtras() != null) {
            loginname.setText(getIntent().getExtras().getString("loginName"));
            loginpassword.setText(getIntent().getExtras().getString("loginPassword"));
        }
        login = (Button) findViewById(R.id.loginac_login_btn);
        register = (Button) findViewById(R.id.loginac_register_btn);

        loginActivity = this;

        login.setOnClickListener(this);
        register.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginac_login_btn:
                String name = loginname.getText().toString();
                String password = loginpassword.getText().toString();

                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(LoginActivity.this, "用户名不能为空！", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (name.length() > 11) {
                    Toast.makeText(LoginActivity.this, "用户名不能超过11个字符！", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (name.contains(" ")) {
                    Toast.makeText(LoginActivity.this, "用户名不能包含有空格！", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!AndTools.isNetworkAvailable(MyApplication.getInstance())) {
                    Toast.makeText(LoginActivity.this, "当前网络不可用！", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 打开progressDialog
                dialog = DialogDefine.createLoadingDialog(LoginActivity.this,
                        "登陆中...");
                dialog.show();
                Thread loginThread = new Thread(new LoginThread(name, password));
                loginThread.start();
                break;

            case R.id.loginac_register_btn:
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                break;
        }
    }

    class LoginThread implements Runnable {
        String name, password;

        public LoginThread(String name, String password) {
            this.name = name;
            this.password = password;
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            Bundle result = startLogin(name, password);
            Message message = new Message();
            message.setData(result);
            loginHandler.sendMessage(message);
        }
    }

    private Bundle startLogin(String name, String password) {
        myUser = new User();
        myUser.setName(name);
        myUser.setPassword(password);
        String jsonResult = new NetCore().Login(myUser);

        int codeResult = 404;
        String messageResult = "";
        try {
            codeResult = new JSONObject(jsonResult).getInt("code");
            messageResult = new JSONObject(jsonResult).getString("msg");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Bundle bundle = new Bundle();
        bundle.putInt("code", codeResult);
        bundle.putString("msg", messageResult);
        return bundle;
    }

    Handler loginHandler = new Handler() {
        public void handleMessage(Message message) {
            final Bundle bundle = message.getData();
            if (bundle.getInt("code") == NetCore.LOGIN_ERROR) {
                // 登录失败
                if (dialog != null) {
                    dialog.dismiss();
                }
                Toast.makeText(LoginActivity.this,
                        bundle.getString("msg"), Toast.LENGTH_SHORT)
                        .show();
            } else if (bundle.getInt("code") > NetCore.LOGIN_ERROR) {
                // 登录成功
                if (dialog != null) {
                    dialog.dismiss();
                }

                if (bundle.getString("msg").trim().length() == 0) {
                    Toast.makeText(LoginActivity.this, "网络错误！", Toast.LENGTH_SHORT).show();
                    return;
                }

                preferences = MyApplication.getInstance().getSharedPreferences("loginmessage", Context.MODE_PRIVATE);
                editor = preferences.edit();
                editor.putString("userID", bundle.getInt("code") + "").apply();
                DemoUtil.getExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        ALoginParam params = LoginUtils.loginRequest(myUser.getName(), myUser.getPassword());
                        loginWukong(params, myUser.getName());
                    }
                });
            }
        }
    };

    private void loginWukong(ALoginParam param, final String nickname) {
        AuthService.getInstance().login(param, new Callback<AuthInfo>() {
            @Override
            public void onSuccess(AuthInfo authInfo) {
                AuthService.getInstance().setNickname(nickname);
                editor.putString("loginName", myUser.getName()).apply();
                editor.putString("loginPassword", myUser.getPassword()).apply();

                if (MainActivity.mainActivity != null) {
                    MainActivity.mainActivity.finish();
                }
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, MainActivity.class);
/*                intent.putExtra("loginIdentity", "<##用户##>" + myUser.getName());
                intent.putExtra("loginID", preferences.getLong("loginID", 0));*/
                MyApplication.USER_OR_NOT = 1;
                if (dialog != null) {
                    dialog.dismiss();
                }
                AndTools.showToast(LoginActivity.this, "登陆成功");
                startActivity(intent);
                LoginActivity.this.finish();
                if (StartActivity.startActivity != null) {
                    StartActivity.startActivity.finish();
                }
            }

            @Override
            public void onException(String code, String reason) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                AndTools.showToast(LoginActivity.this, "登录失败" + "  " + reason);
            }

            @Override
            public void onProgress(AuthInfo authInfo, int i) {
            }
        });
    }
}
