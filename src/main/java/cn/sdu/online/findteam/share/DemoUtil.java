package cn.sdu.online.findteam.share;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import com.alibaba.wukong.auth.AuthInfo;
import com.alibaba.wukong.auth.AuthService;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import cn.sdu.online.findteam.R;
import cn.sdu.online.findteam.activity.MyMessageActivity;

/**
 * Created by wn on 2015/8/13.
 */
public class DemoUtil {
    private static ProgressDialog mProgressDialog;
    private static String mNotifyTitle;
    private static PendingIntent mPendIntent;
    private static Notification mNotification;
    private static NotificationManager mNotificationManager;
    private static Executor mExecutor = Executors.newFixedThreadPool(2);

    /**
     * 显示进度对话框
     * @param context
     * @param title
     */
    public synchronized static void showProgressDialog(Context context,String  title){
        if(mProgressDialog == null){
            mProgressDialog = new ProgressDialog(context);
        }
        if(!TextUtils.isEmpty(title))mProgressDialog.setTitle(title);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setMessage("Please wait for a moment...");
        mProgressDialog.show();
    }

    /**
     * 关闭进度对话框
     */
    public synchronized static void dismissProgressDialog(){
        if(mProgressDialog != null)
            mProgressDialog.dismiss();
        mProgressDialog = null;
    }

    /**
     * 创建并显示一个只包含“是”与“否”按钮简单对话框
     * @param context
     * @param title
     * @param callback
     */
    public static void showAlertDialog(final Context context,final String title,final DialogCallback callback){
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setPositiveButton("是",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callback.onPositive();
                    }
                })
                .setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    /**
     * 点击对话框确定按钮后的回调接口
     */
    public interface DialogCallback{
        public void onPositive();
    }

    /**
     * 检测当前App是否在前台运行
     * @param context
     * @return true 前台运行，false 后台运行
     */
    public static boolean isAppForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTasks = activityManager.getRunningTasks(Integer.MAX_VALUE);

        // 正在运行的应用
        ActivityManager.RunningTaskInfo foregroundTask = runningTasks.get(0);
        String packageName = foregroundTask.topActivity.getPackageName();
        String myPackageName = context.getPackageName();

        // 比较包名
        return packageName.equals(myPackageName);
    }

    /**
     * 发出有新消息通知
     * @param unReadCount 新消息数
     */
    public static void sendNotification(int unReadCount) {
        Context ctx = MyApplication.getInstance();

        if(mNotificationManager == null) {
            mNotificationManager = (NotificationManager) ctx.getSystemService(Service.NOTIFICATION_SERVICE);
        }

        if(mNotification == null) {
            NotificationManager notificationManager = (NotificationManager) ctx
                    .getSystemService(Service.NOTIFICATION_SERVICE);
            mNotification = new Notification();
            mNotification.icon = R.drawable.discuss_head_icon; // 设置图标，公用图标
            mNotification.tickerText = ctx.getString(R.string.app_name);
            mNotification.defaults = Notification.DEFAULT_ALL;   // 提示音

            mNotification.defaults = Notification.DEFAULT_VIBRATE;
            long[] vibrate = {0,100,200,300};
            mNotification.vibrate = vibrate;

            // LED 灯闪烁
//        notification.defaults = Notification.DEFAULT_LIGHTS;
//        notification.ledARGB = 0xff00ff00;
//        notification.ledOffMS = 1000;
//        notification.ledOnMS = 300; // 闪光时间，毫秒

        /*
         * 设置Flag的值：说明
         * FLAG_AUTO_CANCEL : 通知能被状态按钮清除掉
         * FLAG_NO_CLEAR : 点击清除按钮，不清除
         * FLAG_ONGOING_EVENT:  该通知放置在正在运行组中
         * FLAG_INSISTENT : 是否一直进行，比如播放音乐，直到用户响应
         */
            mNotification.flags = Notification.FLAG_AUTO_CANCEL;
        }

        if(TextUtils.isEmpty(mNotifyTitle)) {
            mNotifyTitle = ctx.getString(R.string.app_name);
        }

        if(mPendIntent == null) {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setClassName(ctx.getPackageName(), MyMessageActivity.class.getName());
            mPendIntent = PendingIntent.getActivity(ctx, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            /*MyMessageActivity.badgeView.setBadgeCount(MyMessageActivity.badgeView.getBadgeCount() + unReadCount + 1);
            MyMessageActivity.badgeView.setVisibility(View.VISIBLE);*/
        }

        mNotification.when = System.currentTimeMillis(); // 当前时间 ，通知时间
        String notifyContent = ctx.getString(R.string.new_message_notify_content,unReadCount + 1);
        mNotification.setLatestEventInfo(ctx, mNotifyTitle, notifyContent, mPendIntent);
        mNotificationManager.notify(1, mNotification);
    }

    /**
     * 获取当前用户的openId
     */
    public static long currentOpenId(){
        AuthInfo info = AuthService.getInstance().latestAuthInfo();
        return info == null?0L : info.getOpenId();
    }

    /**
     * 获取当前用户的昵称
     */
    public static String currentNickname(){
        AuthInfo info = AuthService.getInstance().latestAuthInfo();
        return info == null?null : info.getNickname();
    }

    public static Executor getExecutor(){
        return mExecutor;
    }

}

