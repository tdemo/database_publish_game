package cn.sdu.online.findteam.util;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import cn.sdu.online.findteam.R;

/**
 * Created by wn on 2015/8/13.
 */
public class AndTools {

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dp2px(Context context, float dpValue) {

        if(null != context && context.getResources() != null && context.getResources().getDisplayMetrics() != null) {
            final float scale = context.getResources().getDisplayMetrics().density;
            return (int) (dpValue * scale + 0.5f);
        }

        return (int)dpValue;
    }
    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {

        if(null != context && context.getResources() != null && context.getResources().getDisplayMetrics() != null) {
            final float scale = context.getResources().getDisplayMetrics().density;
            return (int) (pxValue / scale + 0.5f);
        }

        return (int)pxValue;
    }

    /**
     * 检测网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(final Context context){
        boolean netStatus = false;

        ConnectivityManager connectManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectManager.getActiveNetworkInfo();

        if (networkInfo != null) {
            netStatus = networkInfo.isAvailable();
        }
        return netStatus;
    }

    /**
     * 判断是否是wifi连接
     */
    public static boolean isWifi(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm == null)
            return false;
        return cm.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_WIFI;

    }

    /**
     * 打开网络设置界面
     */
    public static void openSetting(Activity activity) {
        Intent intent = new Intent("/");
        ComponentName cm = new ComponentName("com.android.settings",
                "com.android.settings.WirelessSettings");
        intent.setComponent(cm);
        intent.setAction("android.intent.action.VIEW");
        activity.startActivityForResult(intent, 0);
    }

    /**
     * 隐藏键盘
     */
    public static void hideKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    /**
     * 绘制导航标签
     * @param context
     * @param total  总的位置信息
     * @param currentPosition  当前位置
     * @param basicImageId  基本的图形信息
     * @param currentImageId  当前位置所要显示的图形信息
     * @return
     */
    public static Bitmap drawNavigator(Context context, int total,
                                       int currentPosition, int basicImageId, int currentImageId){
        return drawNavigator(context, total,
                currentPosition, basicImageId, currentImageId, 6);
    }
    /**
     * 绘制导航标签
     * @param context
     * @param total  总的位置信息
     * @param currentPosition  当前位置
     * @param basicImageId  基本的图形信息
     * @param currentImageId  当前位置所要显示的图形信息
     * @param marginSpace 导航条圆点中间的间距
     * @return
     */
    public static Bitmap drawNavigator(Context context, int total, int currentPosition,
                                       int basicImageId, int currentImageId, int marginSpace){
        if(total <= 0){
            total = 1;
        }
        int current = currentPosition;
        Drawable drawable = context.getResources().getDrawable(currentImageId);
        int imageWidth = drawable.getIntrinsicWidth();
        int imageHeight = drawable.getIntrinsicHeight();
        //生成最终要绘制的bitmap，长度是总的标签数乘以图片本身的宽度
        Bitmap navigatorBitmap = Bitmap.createBitmap((imageWidth+6)*total, imageHeight, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(navigatorBitmap);
        //往画板上填充基本的图像内容
        Bitmap basic = BitmapFactory.decodeResource(context.getResources(), basicImageId);
        for(int i=0; i < total; i++){
            canvas.drawBitmap(basic, (marginSpace + imageWidth) * i, 0, null);
        }
        drawable.setBounds((imageWidth+marginSpace)*(current - 1), 0, (imageWidth + marginSpace) * current - marginSpace, imageHeight);
        drawable.draw(canvas);
        return navigatorBitmap;
    }

    /**
     * 打开图片剪裁页面
     * @param context
     * @param imageUri  图片剪裁的原图
     * @param defaultSizeWithSource 剪裁区域的默认大小是否与原图保持一致  true 保持一致
     * @param requestCode
     * @param cropImageFileName  图片剪裁成功后的缓存文件名称，如果不为空，则获取剪裁后的文件方式为：
     * <pre>String fileName = extras.getString("file-data");
    File file = this.getFileStreamPath(fileName);
    Uri uri = Uri.fromFile(file);</pre>
     * 如果为空，则获取剪裁后的图片方式为：
     * <pre>Bundle extras = data.getExtras();
    if (extras != null) {
    Bitmap photo = extras.getParcelable("data");
    }</pre>
    使用的时候，建议按照文件路径（也就是传入有效的cropImageFileName）值使用，原因在于：intent在传递值的时候，对于bitmap这样的值有40kb的大小限制
     */
    public static void startCropImageActivityForResult(Activity context, Uri imageUri, boolean defaultSizeWithSource,
                                                       int requestCode, String cropImageFileName){
        startCropImageActivityForResult(context, imageUri, defaultSizeWithSource, requestCode, cropImageFileName, 100, 100);
    }

    public static void startCropImageActivityForResult(Activity context, Uri imageUri, boolean defaultSizeWithSource,
                                                       int requestCode, String cropImageFileName,int aspectX,int aspectY){
        try{
            Intent intent = new Intent();
            //this will open all images
            intent.setDataAndType(imageUri, "image/*");
            intent.putExtra("aspectX", aspectX);
            intent.putExtra("aspectY", aspectY);
            intent.putExtra("crop", "true");
            // true to return a Bitmap, false to directly save the cropped iamge
            intent.putExtra("return-data", true);
            if(defaultSizeWithSource){
                intent.putExtra("defaultCropRectangleSize", "same-with-same-image");
            }
            if(cropImageFileName != null){
                intent.putExtra("return-data-file-name", cropImageFileName);
            }
            context.startActivityForResult(intent, requestCode);
        }catch(Throwable tr){

        }
    }

    public static void showToast(Context context, int resId) {
        String content = context.getString(resId);
        showToast(context, content);
    }

    public static void showToast(Context context, String content) {
        if(!TextUtils.isEmpty(content)) {
            Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
        }
    }

    public static String getImsi(Context context){
        TelephonyManager mTelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String imsi = mTelephonyMgr.getSubscriberId();
        return imsi;
    }

    public static String getImei(Context context){
        TelephonyManager mTelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String imei = mTelephonyMgr.getDeviceId();
        return imei;
    }

    /**
     * 图片翻转
     * @param bitmap
     * @param orientation
     * @return
     */
    public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {

        try {
            Matrix matrix = new Matrix();
            switch (orientation) {
                case ExifInterface.ORIENTATION_NORMAL:
                    return bitmap;
                case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                    matrix.setScale(-1, 1);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    matrix.setRotate(180);
                    break;
                case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                    matrix.setRotate(180);
                    matrix.postScale(-1, 1);
                    break;
                case ExifInterface.ORIENTATION_TRANSPOSE:
                    matrix.setRotate(90);
                    matrix.postScale(-1, 1);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    matrix.setRotate(90);
                    break;
                case ExifInterface.ORIENTATION_TRANSVERSE:
                    matrix.setRotate(-90);
                    matrix.postScale(-1, 1);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    matrix.setRotate(-90);
                    break;
                default:
                    return bitmap;
            }
            try {
                Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                bitmap.recycle();
                return bmRotated;
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return bitmap;
        }
    }


/*    public static void showTips(Context context,String tipsString){
        showTips(context,tipsString,0);
    }
    public static void showTips(Context context,String tipsString,int duration) {
        final Toast t = new Toast(context);
        t.setGravity(Gravity.TOP, 0, 0);
        if (duration == 0) {
            t.setDuration(Toast.LENGTH_SHORT);
        } else {
            t.setDuration(duration);
        }
        final LayoutInflater mInflater = LayoutInflater.from(context);
        final View v = mInflater.inflate(R.layout.wukong_tips_layout, null);
        final TextView text = (TextView) v.findViewById(R.id.tips_text);
        text.setText(tipsString);
        v.setMinimumWidth(9999);
        v.setMinimumHeight(dp2px(context,48));
        t.setView(v);
        t.show();
    }*/
}
