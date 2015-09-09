/**
 * @Title: FragmentSetting.java
 * @Package com.example.shareseed
 * @Description: TODO
 * @author YBZ
 * @date 2014-5-14 涓嬪崍10:58:59
 * @version V1.0
 */
package cn.sdu.online.findteam.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.alibaba.wukong.auth.AuthService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import cn.sdu.online.findteam.R;
import cn.sdu.online.findteam.activity.MainActivity;
import cn.sdu.online.findteam.net.NetCore;
import cn.sdu.online.findteam.resource.DialogDefine;
import cn.sdu.online.findteam.share.MyApplication;


public class FragmentSetting extends Fragment implements OnClickListener {
    private LinearLayout llModifyPwd;
    private LinearLayout llCompleteInfo;
    private LinearLayout llLogout;
    private LinearLayout llUpdate;
    private LinearLayout llAbout;
    private LinearLayout llShareSoft;
    View view;

    Dialog dialogDefine;
    SharedPreferences sharedPreferences;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_setting, container, false);
//		sp = getActivity().getSharedPreferences("userInfo",
//				Context.MODE_WORLD_READABLE);
        FindViews();
        AddListeners();
        return view;
    }

    private void FindViews() {
        llModifyPwd = (LinearLayout) view.findViewById(R.id.ll_modify_pwd);
        llCompleteInfo = (LinearLayout) view
                .findViewById(R.id.ll_complete_info);
        llLogout = (LinearLayout) view.findViewById(R.id.ll_logout);
        llUpdate = (LinearLayout) view.findViewById(R.id.ll_update);
        llAbout = (LinearLayout) view.findViewById(R.id.ll_about);
        llShareSoft = (LinearLayout) view.findViewById(R.id.ll_shareSoft);
    }

    private void AddListeners() {
        llModifyPwd.setOnClickListener(this);
        llCompleteInfo.setOnClickListener(this);
        llLogout.setOnClickListener(this);
        llUpdate.setOnClickListener(this);
        llAbout.setOnClickListener(this);
        llShareSoft.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_complete_info: {
//			Intent intent = new Intent(getActivity(),
//					ActivityModifyUserinfo.class);
//			startActivity(intent);
                break;
            }
            case R.id.ll_modify_pwd: {
//			Intent intent = new Intent(getActivity(), ActivityModifyPwd.class);
//			startActivity(intent);
                break;
            }

            case R.id.ll_logout: {
//			SharedPreferences.Editor mEditor = sp.edit();
//			mEditor.putBoolean("auto_login", false);
//			mEditor.commit();
//			Intent i = new Intent(getActivity(), ActivityLogin.class);
//			startActivity(i);
//			getActivity().finish();
                logoutDialog();
                break;
            }
            case R.id.ll_about: {
//			Intent intent = new Intent(getActivity(), ActivityAbout.class);
//			startActivity(intent);
//			break;
            }
            case R.id.ll_shareSoft: {
//			Intent intent = new Intent(Intent.ACTION_SEND);
//
//			intent.setType("text/plain");
//			intent.putExtra(Intent.EXTRA_SUBJECT, "");
//			intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share));
//			startActivity(Intent.createChooser(intent, getTag()));
                break;
            }
        }
    }

    protected void logoutDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(FragmentSetting.this.getActivity());
        dialog.setMessage("确定注销吗");
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                dialogDefine = DialogDefine.createLoadingDialog(FragmentSetting.this.getActivity(),
                        "注销中......");
                dialogDefine.show();
                new Thread(new LoginOutThread()).start();
            }
        });
        dialog.create().show();
    }

    class LoginOutThread implements Runnable {
        @Override
        public void run() {
            String jsonData = "";
            try {
                jsonData = new NetCore().loginOut(NetCore.LogingOutAddr);
            } catch (IOException e) {
                e.printStackTrace();
            }
            int codeResult = 404;
            String messageResult = "";
            try {
                codeResult = new JSONObject(jsonData).getInt("code");
                messageResult = new JSONObject(jsonData).getString("msg");
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Bundle bundle = new Bundle();
            bundle.putInt("code", codeResult);
            bundle.putString("msg", messageResult);
            Message message = new Message();
            message.setData(bundle);
            handler.sendMessage(message);
        }
    }

    Handler handler = new Handler(MainActivity.mainActivity.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            if (bundle.getInt("code") == 0) {
                if (dialogDefine != null) {
                    dialogDefine.dismiss();
                }
                if (bundle.getString("msg").trim().length() == 0) {
                    Toast.makeText(FragmentSetting.this.getActivity(),
                            "网络错误！", Toast.LENGTH_SHORT).show();
                    return;
                }

                exitSuccess();

/*                Toast.makeText(FragmentSetting.this.getActivity(),
                        bundle.getString("msg"), Toast.LENGTH_SHORT).show();*/

                Intent intent = new Intent();
                intent.setClass(FragmentSetting.this.getActivity(),
                        MainActivity.class);
                MyApplication.USER_OR_NOT = 0;
                MyApplication.currentFragment = MainActivity.MAIN_FRAGMENT;
                FragmentSetting.this.getActivity().finish();
                startActivity(intent);
            } else {
                if (dialogDefine != null) {
                    dialogDefine.dismiss();
                }
                if (bundle.getString("msg").trim().length() == 0) {
                    Toast.makeText(FragmentSetting.this.getActivity(),
                            "网络错误！", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(FragmentSetting.this.getActivity(),
                        bundle.getString("msg"), Toast.LENGTH_SHORT).show();
            }
        }
    };
//	public void onResume() {
//		super.onResume();
//		MobclickAgent.onPageStart("Setting"); // 统计页面
//	}
//
//	public void onPause() {
//		super.onPause();
//		MobclickAgent.onPageEnd("Setting");
//	}

    protected void exitSuccess() {
        AuthService.getInstance().logout();
        sharedPreferences = MyApplication.getInstance().getSharedPreferences("loginmessage", Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
    }
}
