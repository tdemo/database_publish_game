package cn.sdu.online.findteam.share;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.alibaba.wukong.AuthConstants;

import cn.sdu.online.findteam.R;
import cn.sdu.online.findteam.activity.LoginActivity;
import cn.sdu.online.findteam.activity.MainActivity;
import cn.sdu.online.findteam.util.AndTools;

/**
 * Created by wn on 2015/8/13.
 */
public class AuthReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (AuthConstants.Event.EVENT_AUTH_KICKOUT.equals(action)) {  // 其它终端登录
            clearLocalCache();
            AndTools.showToast(context, R.string.kickout);
            loginOut(context);
        }
    }

    private void goToLogin(Context context) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClassName(context.getPackageName(),LoginActivity.class.getName());
        context.startActivity(intent);
    }

    private void loginOut(Context context){
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClassName(context.getPackageName(), MainActivity.class.getName());
        intent.putExtra("loginIdentity", "<##游客##>");
        context.startActivity(intent);
    }

    /**
     * 用户登出，清除本地缓存数据
     */
    private void clearLocalCache() {
        // 可以在这里清理原登录帐号的内存和持久化数据

    }
}
