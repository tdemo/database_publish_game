package cn.sdu.online.findteam.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import cn.sdu.online.findteam.resource.RoundImageView;
import cn.sdu.online.findteam.share.MyApplication;

public class ChangeHeader {

    RoundImageView headerImg;

    public final String photo_path = getSDPath() + "/FindTeam";
    public String headerImgname;

    public static final int PHOTO_REQUEST = 1001;
    public static final int CAMERA_REQUEST = 1002;
    public static final int CAMERA_CUT_REQUEST = 1003;

    public static final int TEAMHEADER = 11;
    public static final int PERSONHEADER = 12;

    public Activity activity;
    public int headerClass;

    public ChangeHeader(Activity activity, RoundImageView headerImg, int headerClass) {
        this.activity = activity;
        this.headerImg = headerImg;
        this.headerClass = headerClass;
    }

    // 调用系统相册，选择并调用裁切，并储存路径
    public void chooseAlbum() {
        Intent innerIntent = new Intent(Intent.ACTION_PICK); // 调用相册
        innerIntent.putExtra("crop", "true");// 剪辑功能
        innerIntent.putExtra("aspectX", 1);// 放大和缩小功能
        innerIntent.putExtra("aspectY", 1);
        innerIntent.putExtra("outputX", 140);// 输出图片大小
        innerIntent.putExtra("outputY", 140);
        innerIntent.setType("image/*");// 查看类型
        innerIntent.putExtra("output", Uri.fromFile(makeDir()));// 传入目标文件
        innerIntent.putExtra("outputFormat", "JPEG"); // 输入文件格式

        activity.startActivityForResult(innerIntent, PHOTO_REQUEST); // 设返回
        // 码为PHOTO_REQUEST
        // onActivityResult
        // 中的 requestCode 对应
    }

    // 调用系统相机
    public void chooseCamera() {
        // photoUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);// 调用android自带的照相机
        // cameraIntent.putExtra("crop", "true");
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(makeDir()));// 传入目标文件
        activity.startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    // 照完后剪切
    public void chooseCamera_cut(Uri photo_uri) {
        Intent cutIntent = new Intent("com.android.camera.action.CROP");// 调用Android系统自带的一个图片剪裁页面,
        cutIntent.setDataAndType(photo_uri, "image/*");
        cutIntent.putExtra("crop", "true");// 进行修剪
        cutIntent.putExtra("aspectX", 1);// 放大和缩小功能
        cutIntent.putExtra("aspectY", 1);
        cutIntent.putExtra("outputX", 140);// 输出图片大小
        cutIntent.putExtra("outputY", 140);
        cutIntent.putExtra("return-data", true);
        cutIntent.putExtra("outputFormat", "JPEG"); //输入文件格式
        activity.startActivityForResult(cutIntent, CAMERA_CUT_REQUEST);
    }

    // 建立头像文件夹功能
    public File makeDir() {
        // ////////////////////////////////////////////////////////////////
        // 照片的命名，目标文件夹下，以当前时间数字串为名称，即可确保每张照片名称不相同。网上流传的其他Demo这里的照片名称都写死了，
        // 则会发生无论拍照多少张，后一张总会把前一张照片覆盖。细心的同学还可以设置这个字符串，比如加上“ＩＭＧ”字样等；然后就会发现
        // sd卡中myimage这个文件夹下，会保存刚刚调用相机拍出来的照片，照片名称不会重复。

        Date date = new Date(); // 获取当前时间
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINESE); // 时间字符串格式
        // 团队头像名称是时间 + team + 创建人名字
        if (headerClass == TEAMHEADER) {
            headerImgname = format.format(date) + "team" +
                    MyApplication.getInstance().getSharedPreferences("loginmessage", Context.MODE_PRIVATE).
                            getString("loginName", ""); // 将时间转换成对应格式字符串
            MyApplication.getInstance().getSharedPreferences("teamHeader", Context.MODE_PRIVATE).edit().
                    putString("headerImg", headerImgname).apply();
        } else if (headerClass == PERSONHEADER) {
            headerImgname = MyApplication.getInstance().getSharedPreferences("loginmessage", Context.MODE_PRIVATE).
                    getString("loginName", "") + format.format(date); // 将时间转换成对应格式字符串
            MyApplication.getInstance().getSharedPreferences("personHeader", Context.MODE_PRIVATE).edit().
                    putString("headerImg", headerImgname).apply();
        }
        File tempFile = new File(photo_path + "/" + headerImgname + ".jpg");
        File file = new File(photo_path);
        if (!file.exists()) {
            file.mkdir();
        }
        return tempFile;
    }

    public String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(android.os.Environment.MEDIA_MOUNTED); //判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();
        }
        return sdDir.getPath();
    }

    //从相册中选中的照片设置头像
    public void setHeaderImgAlbum() {
        if (headerClass == TEAMHEADER) {
            headerImgname = MyApplication.getInstance().getSharedPreferences("teamHeader", Context.MODE_PRIVATE).
                    getString("headerImg", "");
        }else if (headerClass == PERSONHEADER){
            headerImgname = MyApplication.getInstance().getSharedPreferences("personHeader", Context.MODE_PRIVATE).
                    getString("headerImg", "");
        }
        Bitmap header = BitmapFactory.decodeFile(photo_path + "/" + headerImgname + ".jpg");
        /*BitmapDrawable bd = new BitmapDrawable(header);*/
        headerImg.setImageBitmap(header);
    }

    //从相机中选中照片设置头像
    public void getHeaderImgCamera() {
        if (!isSDcardAble()) // 检测sd是否可用
        { // 不可用
            Toast.makeText(activity,
                    "当前SD卡不可用！", Toast.LENGTH_SHORT).show();
            return;
        }
        // 找到文件
        File file = new File(photo_path + "/" + headerImgname + ".jpg");
        // 开始剪切
        chooseCamera_cut(Uri.fromFile(file));
    }

    public boolean isSDcardAble() {
        String sdStatus = Environment.getExternalStorageState();// sd card 状态
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) // 检测sd是否可用
        { // 不可用
            return false;
        }
        return true;
    }

    public void outputPhoto(Intent data) {
        Bundle bundle = data.getExtras();
        Bitmap bitmap = (Bitmap) bundle.get("data");// 获取相机返回的数据，并转换为Bitmap图片格式
        // new_head=bitmap;//为当前头像赋值
        FileOutputStream b = null;

        makeDir();
        try {
            b = new FileOutputStream(photo_path + "/" + headerImgname + ".jpg");
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                b.flush();
                b.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setHeaderImgCamera(Intent data) {
        if (!isSDcardAble()) { // 不可用
            Toast.makeText(activity,
                    "当前SD卡不可用！", Toast.LENGTH_SHORT).show();
            return;
        }
        // 将切好图片存入sd卡
        outputPhoto(data);

        Bitmap headerBmp = BitmapFactory.decodeFile(photo_path + "/" + headerImgname
                + ".jpg");// 赋值给新头像容器
        headerImg.setImageBitmap(headerBmp);// 反应于界面
    }
}
