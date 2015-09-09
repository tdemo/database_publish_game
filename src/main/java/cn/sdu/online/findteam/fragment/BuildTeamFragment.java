package cn.sdu.online.findteam.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cn.sdu.online.findteam.R;
import cn.sdu.online.findteam.activity.MainActivity;
import cn.sdu.online.findteam.resource.RoundImageView;
import cn.sdu.online.findteam.resource.SwitchButton;
import cn.sdu.online.findteam.share.MyApplication;


public class BuildTeamFragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    public TextView bt_confirm;
    public EditText text_teamname, text_introduction;
    public Button bt_changehead;
    private Spinner spinner_number, spinner_year, spinner_month, spinner_day;
    private final String[] number = {"请选择", "3", "4", "5", "6", "7", "8", "9", "10"};
    private final String[] year = {"年", "2015", "2016", "2017", "2018"};
    private final String[] month = {"月", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"};
    private final String[] day = {"日", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"};
    private ArrayAdapter<String> adapter_number, adapter_year, adapter_month, adapter_day;

    private View view;

    private SwitchButton switchButton1, switchButton2, switchButton3;

    RoundImageView headerImg;

    public final String photo_path = getSDPath() + "/FindTeam";
    public String headerImgname;

    public final static int PHOTO_REQUEST = 1001;
    public static final int CAMERA_REQUEST = 1002;
    public static final int CAMERA_CUT_REQUEST = 1003;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.buildteam_layout, null);

        initView();
        return view;
    }

    private void initView() {
        headerImg = (RoundImageView) view.findViewById(R.id.buildteam_head_img);
        text_teamname = (EditText) view.findViewById(R.id.text_teamname);
        text_introduction = (EditText) view.findViewById(R.id.text_introduction);
        bt_changehead = (Button) view.findViewById(R.id.bt_changehead);
        bt_changehead.setOnClickListener(this);
        bt_confirm = (TextView) view.findViewById(R.id.bt_confirm);
        bt_confirm.setOnClickListener(this);

        spinner_number = (Spinner) view.findViewById(R.id.spinnernumber);
        adapter_number = new ArrayAdapter<String>(BuildTeamFragment.this.getActivity(), android.R.layout.simple_spinner_item, number);
        adapter_number.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_number.setAdapter(adapter_number);
        spinner_number.setOnItemSelectedListener(new SpinnerSelectedListener_number());
        spinner_number.setVisibility(View.VISIBLE);

        spinner_year = (Spinner) view.findViewById(R.id.spinneryear);
        adapter_year = new ArrayAdapter<String>(BuildTeamFragment.this.getActivity(), android.R.layout.simple_spinner_item, year);
        adapter_year.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_year.setAdapter(adapter_year);
        spinner_year.setOnItemSelectedListener(new SpinnerSelectedListener_year());
        spinner_year.setVisibility(View.VISIBLE);

        spinner_month = (Spinner) view.findViewById(R.id.spinnermonth);
        adapter_month = new ArrayAdapter<String>(BuildTeamFragment.this.getActivity(), android.R.layout.simple_spinner_item, month);
        adapter_month.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_month.setAdapter(adapter_month);
        spinner_month.setOnItemSelectedListener(new SpinnerSelectedListener_month());
        spinner_month.setVisibility(View.VISIBLE);

        spinner_day = (Spinner) view.findViewById(R.id.spinnerday);
        adapter_day = new ArrayAdapter<String>(BuildTeamFragment.this.getActivity(), android.R.layout.simple_spinner_item, day);
        adapter_day.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_day.setAdapter(adapter_day);
        spinner_day.setOnItemSelectedListener(new SpinnerSelectedListener_day());
        spinner_day.setVisibility(View.VISIBLE);

        switchButton1 = (SwitchButton) view.findViewById(R.id.switch_test);
        switchButton2 = (SwitchButton) view.findViewById(R.id.switch_see);
        switchButton3 = (SwitchButton) view.findViewById(R.id.switch_comment);
        switchButton1.setChecked(false);
        switchButton2.setChecked(false);
        switchButton3.setChecked(false);
        switchButton1.setOnCheckedChangeListener(this);
        switchButton2.setOnCheckedChangeListener(this);
        switchButton3.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked == false) {
            Toast.makeText(BuildTeamFragment.this.getActivity(),
                    "您关闭了我", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(BuildTeamFragment.this.getActivity(),
                    "您打开了我", Toast.LENGTH_SHORT).show();
        }
    }

    class SpinnerSelectedListener_number implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            spinner_number.setTag(number[arg2]);
        }

        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }

    class SpinnerSelectedListener_year implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            spinner_year.setTag(year[arg2]);
        }

        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }

    class SpinnerSelectedListener_month implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            spinner_month.setTag(month[arg2]);
        }

        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }

    class SpinnerSelectedListener_day implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            spinner_day.setTag(day[arg2]);
        }

        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_changehead: {
                final String[] items = {"相册", "相机"};// 条目列表
                new AlertDialog.Builder(BuildTeamFragment.this.getActivity(), R.style.AlertDialogCustom)// 建立对话框
                        .setTitle("请选择方式")// 标题
                        .setItems(items, new DialogInterface.OnClickListener() {// 以下为监听

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                if (which == 0) {
                                    // 相册
                                    chooseAlbum();
                                }
                                if (which == 1) {
                                    // 相机
                                    chooseCamera();
                                }
                            }
                        }).show();
                break;
            }
            case R.id.bt_confirm: {

                break;
            }
        }
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

        BuildTeamFragment.this.getActivity().startActivityForResult(innerIntent, PHOTO_REQUEST); // 设返回
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
        BuildTeamFragment.this.getActivity().startActivityForResult(cameraIntent, CAMERA_REQUEST);
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
        BuildTeamFragment.this.getActivity().startActivityForResult(cutIntent, CAMERA_CUT_REQUEST);
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
        headerImgname = format.format(date) + "team" +
            MyApplication.getInstance().getSharedPreferences("loginmessage", Context.MODE_PRIVATE).
                getString("loginName", "");
        MyApplication.getInstance().getSharedPreferences("teamHeader", Context.MODE_PRIVATE).edit().
                putString("headerImg", headerImgname).apply();
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
        headerImgname = MyApplication.getInstance().getSharedPreferences("teamHeader", Context.MODE_PRIVATE).
                getString("headerImg", "");
        Bitmap header = BitmapFactory.decodeFile(photo_path + "/" + headerImgname + ".jpg");
        /*BitmapDrawable bd = new BitmapDrawable(header);*/
        headerImg.setImageBitmap(header);
    }

    //从相机中选中照片设置头像
    public void getHeaderImgCamera() {
        if (!isSDcardAble()) // 检测sd是否可用
        { // 不可用
            Toast.makeText(BuildTeamFragment.this.getActivity(),
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
            Toast.makeText(BuildTeamFragment.this.getActivity(),
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

