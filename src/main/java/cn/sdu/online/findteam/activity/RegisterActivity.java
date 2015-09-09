package cn.sdu.online.findteam.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.wukong.auth.ALoginParam;
import com.alibaba.wukong.auth.AuthInfo;
import com.alibaba.wukong.auth.AuthService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.sdu.online.findteam.R;
import cn.sdu.online.findteam.entity.User;
import cn.sdu.online.findteam.net.NetCore;
import cn.sdu.online.findteam.resource.DialogDefine;
import cn.sdu.online.findteam.share.DemoUtil;
import cn.sdu.online.findteam.share.MyApplication;
import cn.sdu.online.findteam.util.AndTools;
import cn.sdu.online.findteam.util.LoginUtils;

public class RegisterActivity extends Activity implements View.OnClickListener {

    private EditText registername, registerpassword, registerconfirm, registeremail;
    private Button registerbtn;

    private Dialog dialog;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registeractivity_layout);
        initView();
    }

    private void initView() {
        registername = (EditText) findViewById(R.id.register_name);
        registerpassword = (EditText) findViewById(R.id.register_password);
        registerconfirm = (EditText) findViewById(R.id.register_confirm);
        registeremail = (EditText) findViewById(R.id.register_email);
        registerbtn = (Button) findViewById(R.id.registerac_register_btn);
        registerbtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String name = registername.getText().toString();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(RegisterActivity.this, "用户名不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }

        if (name.length() > 11){
            Toast.makeText(RegisterActivity.this, "用户名不能超过11个字符！", Toast.LENGTH_SHORT).show();
            return;
        }

        if (name.contains(" ")){
            Toast.makeText(RegisterActivity.this, "用户名不能包含有空格！", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!AndTools.isNetworkAvailable(MyApplication.getInstance())){
            Toast.makeText(RegisterActivity.this, "当前网络不可用！", Toast.LENGTH_SHORT).show();
            return;
        }
        String email = registeremail.getText().toString();
        String password = registerpassword.getText().toString();
        String confirm = registerconfirm.getText().toString();

        dialog = DialogDefine.createLoadingDialog(RegisterActivity.this,
                "注册中.......");
        dialog.show();
        new Thread(new RegisterThread(name, email, password, confirm)).start();
    }

    class RegisterThread implements Runnable {

        String name, email, password, confirm;

        public RegisterThread(String name, String email, String password, String confirm) {
            this.name = name;
            this.email = email;
            this.password = password;
            this.confirm = confirm;
        }

        @Override
        public void run() {
            Bundle result = startRegister(name, email, password, confirm);
            Message message = new Message();
            message.setData(result);
            registerHandler.sendMessage(message);
        }
    }

    private Bundle startRegister(String name, String email, String password, String confirm) {
        user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setConfirm(confirm);
        String jsonResult = new NetCore().Register(user);

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

    Handler registerHandler = new Handler() {
        public void handleMessage(Message message) {
            final Bundle bundle = message.getData();
            switch (bundle.getInt("code")) {
                case NetCore.REGISTER_EMAIL_EXISTED:
                    // 邮箱存在
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    Toast.makeText(RegisterActivity.this,
                            bundle.getString("msg"), Toast.LENGTH_SHORT).show();
                    break;

                case NetCore.REGISTER_ERROR:
                    //数据库错误
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    Toast.makeText(RegisterActivity.this,
                            bundle.getString("msg"), Toast.LENGTH_SHORT).show();
                    break;

                case NetCore.REGISTER_NAME_EXISTED:
                    //用户名存在
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    Toast.makeText(RegisterActivity.this,
                            bundle.getString("msg"), Toast.LENGTH_SHORT).show();
                    break;

                case NetCore.REGISTER_SUCCESS:
                    //注册成功
                    if (bundle.getString("msg").trim().length() == 0) {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        Toast.makeText(RegisterActivity.this, "网络错误！", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Thread loginThread = new Thread(new LoginThread());
                    loginThread.start();
                    break;

                default:
                    break;
            }
        }
    };

    private void loginWuKong(ALoginParam param, final String nickname) {
        AuthService.getInstance().login(param, new com.alibaba.wukong.Callback<AuthInfo>() {
            @Override
            public void onSuccess(AuthInfo data) {
                AuthService.getInstance().setNickname(nickname);
                editor.remove("loginName").apply();
                editor.remove("loginPassword").apply();
                editor.putString("loginName", user.getName()).apply();
                editor.putString("loginPassword", user.getPassword()).apply();

                Intent intent = new Intent();
                if (MainActivity.mainActivity != null) {
                    MainActivity.mainActivity.finish();
                }
                intent.setClass(RegisterActivity.this, MainActivity.class);
/*                intent.putExtra("loginIdentity", "<##用户##>" + user.getName());
                intent.putExtra("loginID", preferences.getLong("loginID", 0));*/
                MyApplication.USER_OR_NOT = 1;
                if (dialog != null) {
                    dialog.dismiss();
                }
                AndTools.showToast(RegisterActivity.this, "注册成功");
                startActivity(intent);
                RegisterActivity.this.finish();
                if (StartActivity.startActivity != null) {
                    StartActivity.startActivity.finish();
                }
            }

            @Override
            public void onException(String code, String reason) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                AndTools.showToast(RegisterActivity.this, R.string.signup_failed + " " + reason);
            }

            @Override
            public void onProgress(AuthInfo s, int i) {
            }
        });
    }

    class LoginThread implements Runnable {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            Bundle result = startLogin();
            Message message = new Message();
            message.setData(result);
            loginHandler.sendMessage(message);
        }
    }

    private Bundle startLogin() {
        String jsonResult = new NetCore().Login(user);
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
                Toast.makeText(RegisterActivity.this,
                        bundle.getString("msg"), Toast.LENGTH_SHORT)
                        .show();
            } else if (bundle.getInt("code") > NetCore.LOGIN_ERROR) {
                // 登录成功
                if (dialog != null) {
                    dialog.dismiss();
                }

                if (bundle.getString("msg").trim().length() == 0) {
                    Toast.makeText(RegisterActivity.this, "网络错误！", Toast.LENGTH_SHORT).show();
                    return;
                }

                preferences = MyApplication.getInstance().getSharedPreferences("loginmessage", Context.MODE_PRIVATE);
                editor = preferences.edit();
                editor.putString("userID", bundle.getInt("code") + "").apply();
                DemoUtil.getExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        ALoginParam params = LoginUtils.loginRequest(user.getName(), user.getPassword());
                        loginWuKong(params, user.getName());
                    }
                });
            }
        }
    };
}
