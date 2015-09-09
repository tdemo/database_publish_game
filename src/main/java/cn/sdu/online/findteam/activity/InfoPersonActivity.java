package cn.sdu.online.findteam.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.DrawableRes;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import cn.sdu.online.findteam.R;
import cn.sdu.online.findteam.entity.User;
import cn.sdu.online.findteam.fragment.BuildTeamFragment;
import cn.sdu.online.findteam.net.NetCore;
import cn.sdu.online.findteam.resource.DialogDefine;
import cn.sdu.online.findteam.resource.RoundImageView;
import cn.sdu.online.findteam.share.MyApplication;
import cn.sdu.online.findteam.util.AndTools;
import cn.sdu.online.findteam.util.ChangeHeader;


public class InfoPersonActivity extends Activity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {
    public Button bt_return;
    public EditText text_tag1, text_tag2, text_tag3, text_tag4, text_realname,
            text_address, text_school, text_phonenumber, text_introduction;
    public TextView text_edit, text_nickname, text_gender, text_email, text_openID, text_ID;
    public Boolean isEdited;
    private RadioGroup radioGroup;
    private RelativeLayout relativeLayout;
    private RoundImageView head;

    private ChangeHeader changeHeader;
    Dialog dialog;
    View contentView;
    JSONObject person;
    String gender = "男";
    private final int maxNun = 40;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog = DialogDefine.createLoadingDialog(InfoPersonActivity.this,
                "加载中...");
        isEdited = false;
        dialog.show();
        if (!AndTools.isNetworkAvailable(MyApplication.getInstance())) {
            dialog.dismiss();
            AndTools.showToast(InfoPersonActivity.this, "当前网络不可用！");
            return;
        }
        findview();
        Thread loadUserInfo = new Thread(new loadUserInfo());
        loadUserInfo.start();
    }

    // 获取个人信息
    class loadUserInfo implements Runnable {
        @Override
        public void run() {
            try {
                String info = new NetCore().getUserInfo("");
                if (!info.equals("")) {
                    JSONTokener jsonParser = new JSONTokener(info);
                    try {
                        person = (JSONObject) jsonParser.nextValue();
                        if (person != null) {
                            handler.sendEmptyMessage(0);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // 修改个人信息的Runable类
    class ModifyUserInfo implements Runnable {
        User user;

        ModifyUserInfo(String name, String contact,
                       String password, String introduce,
                       String address, String school,
                       String sex) {
            user = new User();
            user.setName(name);
            user.setPassword(password);
            user.setContact(contact);
            user.setIntroduce(introduce);
            user.setAddress(address);
            user.setSchool(school);
            user.setSex(sex);
        }

        @Override
        public void run() {
            try {
                String jsonData = new NetCore().modifyUserInfo(user);
                if (jsonData != null) {
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
                    modifyHandler.sendMessage(message);
                } else {
                    AndTools.showToast(InfoPersonActivity.this, "未知错误");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // 修改个人信息
    Handler modifyHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            switch (bundle.getInt("code")) {
                case NetCore.MODIFY_SUCCESS:
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    if (bundle.getString("msg") == null || bundle.getString("msg").trim().equals("")) {
                        AndTools.showToast(InfoPersonActivity.this, "未知错误");
                        return;
                    }
                    AndTools.showToast(InfoPersonActivity.this, bundle.getString("msg"));
                    finishEditInfo();
                    break;

                case NetCore.MODIFY_ERROR:
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    AndTools.showToast(InfoPersonActivity.this, "修改失败 " + bundle.getString("msg"));
                    break;

                default:
                    break;
            }
        }
    };

    // 获取个人信息
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try {
                text_phonenumber.setText(person.getString("contact"));
                text_nickname.setText(person.getString("username"));
                text_introduction.setText(person.getString("introduce"));
                text_email.setText(person.getString("mail"));
                text_realname.setText(person.getString("realName"));
                text_gender.setText(person.getString("sex"));
                text_address.setText(person.getString("address"));
                text_school.setText(person.getString("college"));
                text_ID.setText(person.getString("id"));
                text_openID.setText(person.getString("openId"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (dialog != null) {
                dialog.dismiss();
            }
            InfoPersonActivity.this.setContentView(contentView);
        }
    };

    private void findview() {
        contentView = View.inflate(InfoPersonActivity.this, R.layout.activity_info_person, null);
        bt_return = (Button) contentView.findViewById(R.id.bt_return);
        text_nickname = (TextView) contentView.findViewById(R.id.text_nickname);
        text_introduction = (EditText) contentView.findViewById(R.id.text_introduction);
        text_tag1 = (EditText) contentView.findViewById(R.id.text_tag1);
        text_tag2 = (EditText) contentView.findViewById(R.id.text_tag2);
        text_tag3 = (EditText) contentView.findViewById(R.id.text_tag3);
        text_realname = (EditText) contentView.findViewById(R.id.text_realname);
        text_gender = (TextView) contentView.findViewById(R.id.text_gender);
        text_address = (EditText) contentView.findViewById(R.id.text_address);
        text_school = (EditText) contentView.findViewById(R.id.text_school);
        text_phonenumber = (EditText) contentView.findViewById(R.id.text_phonenumber);
        text_email = (TextView) contentView.findViewById(R.id.text_email);
        text_edit = (TextView) contentView.findViewById(R.id.text_edit);
        head = (RoundImageView) contentView.findViewById(R.id.head);
        text_openID = (TextView) contentView.findViewById(R.id.text_openID);
        text_ID = (TextView) contentView.findViewById(R.id.text_userID);
        relativeLayout = (RelativeLayout) contentView.findViewById(R.id.info_person_headlayout);
        relativeLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                getWindowManager().getDefaultDisplay().getWidth()));
        radioGroup = (RadioGroup) contentView.findViewById(R.id.sex_group);
        radioGroup.setOnCheckedChangeListener(this);

        text_edit.setOnClickListener(InfoPersonActivity.this);
        bt_return.setOnClickListener(InfoPersonActivity.this);
        head.setOnClickListener(InfoPersonActivity.this);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        gender = checkedId == R.id.sex_man ? "男" : "女";
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_edit:
                isEdited = !isEdited;
                if (isEdited) {
                    editInfo();

                } else {
                    if (!AndTools.isNetworkAvailable(MyApplication.getInstance())) {
                        Toast.makeText(InfoPersonActivity.this, "当前网络不可用！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (text_introduction.getText().toString().length() > maxNun) {
                        AndTools.showToast(InfoPersonActivity.this, "亲，最多只能输入40个字哦！");
                        return;
                    }
                    dialog.show();
                    new Thread(new ModifyUserInfo(
                            text_realname.getText().toString(),
                            text_phonenumber.getText().toString(),
                            MyApplication.getInstance().getSharedPreferences("loginmessage", Context.MODE_PRIVATE).getString("loginPassword", ""),
                            text_introduction.getText().toString(),
                            text_address.getText().toString(),
                            text_school.getText().toString(),
                            gender))
                            .start();
                }
                break;

            case R.id.bt_return:
                InfoPersonActivity.this.finish();
                break;

            case R.id.head:
                if (isEdited) {
                    final String[] itemsfirst = {"修改个人头像", "查看大图"};// 第一级条目列表
                    new AlertDialog.Builder(InfoPersonActivity.this, R.style.AlertDialogCustom)// 建立对话框
                            .setItems(itemsfirst, new DialogInterface.OnClickListener() {// 以下为监听
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    if (which == 0) {
                                        final String[] itemssecond = {"相册", "相机"}; // 第二级条目列表
                                        new AlertDialog.Builder(InfoPersonActivity.this, R.style.AlertDialogCustom)// 建立对话框
                                                .setTitle("请选择方式")// 标题
                                                .setItems(itemssecond, new DialogInterface.OnClickListener() {// 以下为监听

                                                    @Override
                                                    public void onClick(DialogInterface dialog,
                                                                        int which) {
                                                        if (which == 0) {
                                                            // 相册
                                                            changeHeader =
                                                                    new ChangeHeader(InfoPersonActivity.this,
                                                                            head, ChangeHeader.PERSONHEADER);
                                                            changeHeader.chooseAlbum();
                                                        }
                                                        if (which == 1) {
                                                            // 相机
                                                            changeHeader =
                                                                    new ChangeHeader(InfoPersonActivity.this,
                                                                            head, ChangeHeader.PERSONHEADER);
                                                            changeHeader.chooseCamera();
                                                        }
                                                    }
                                                }).show();
                                    }
                                    if (which == 1) {
                                        Intent intent = new Intent();
                                        intent.setClass(InfoPersonActivity.this, ImgShowerActivity.class);
                                        intent.putExtra("bitmap", getBytes(head.getBmp()));
                                        InfoPersonActivity.this.startActivity(intent);
                                        overridePendingTransition(R.anim.zoomin, 0);
                                    }
                                }
                            }).show();
                } else {
                    Intent intent = new Intent();
                    intent.setClass(InfoPersonActivity.this, ImgShowerActivity.class);
                    intent.putExtra("bitmap", getBytes(head.getBmp()));
                    InfoPersonActivity.this.startActivity(intent);
                    overridePendingTransition(R.anim.zoomin, 0);
                }
                break;
        }
    }

    private void editInfo() {
        text_tag1.setFocusableInTouchMode(true);
        text_tag1.requestFocus();
        text_tag2.setFocusableInTouchMode(true);
        text_tag2.requestFocus();
        text_tag3.setFocusableInTouchMode(true);
        text_tag3.requestFocus();
        text_realname.setFocusableInTouchMode(true);
        text_realname.requestFocus();
        text_realname.setBackgroundResource(R.drawable.info_person_edit);
        text_introduction.setFocusableInTouchMode(true);
        text_introduction.requestFocus();
        text_gender.setVisibility(View.GONE);
        radioGroup.setVisibility(View.VISIBLE);
        text_address.setFocusableInTouchMode(true);
        text_address.requestFocus();
        text_address.setBackgroundResource(R.drawable.info_person_edit);
        text_school.setFocusableInTouchMode(true);
        text_school.requestFocus();
        text_school.setBackgroundResource(R.drawable.info_person_edit);
        text_phonenumber.setFocusableInTouchMode(true);
        text_phonenumber.requestFocus();
        text_phonenumber.setBackgroundResource(R.drawable.info_person_edit);
        text_edit.setBackgroundColor(0xff519aff);
        text_edit.setText("保存资料");
        text_edit.setTextColor(Color.WHITE);
    }

    private void finishEditInfo() {
        text_tag1.setFocusableInTouchMode(false);
        text_tag1.requestFocus();
        text_tag1.clearFocus();
        text_tag2.setFocusableInTouchMode(false);
        text_tag2.requestFocus();
        text_tag2.clearFocus();
        text_tag3.setFocusableInTouchMode(false);
        text_tag3.requestFocus();
        text_tag3.clearFocus();
        text_realname.setFocusableInTouchMode(false);
        text_realname.requestFocus();
        text_realname.clearFocus();
        text_realname.setBackgroundResource(R.color.transparent);
        text_introduction.setFocusableInTouchMode(false);
        text_introduction.requestFocus();
        text_introduction.clearFocus();
        radioGroup.setVisibility(View.GONE);
        text_gender.setVisibility(View.VISIBLE);
        text_gender.setText(gender);
/*        text_gender.setFocusableInTouchMode(false);
        text_gender.requestFocus();
        text_gender.clearFocus();
        text_gender.setBackgroundResource(R.color.transparent);*/
        text_address.setFocusableInTouchMode(false);
        text_address.requestFocus();
        text_address.clearFocus();
        text_address.setBackgroundResource(R.color.transparent);
        text_school.setFocusableInTouchMode(false);
        text_school.requestFocus();
        text_school.clearFocus();
        text_school.setBackgroundResource(R.color.transparent);
        text_phonenumber.setFocusableInTouchMode(false);
        text_phonenumber.requestFocus();
        text_phonenumber.clearFocus();
        text_phonenumber.setBackgroundResource(R.color.transparent);
        text_edit.setBackgroundColor(Color.WHITE);
        text_edit.setText("编辑资料");
        text_edit.setTextColor(Color.rgb(80, 154, 255));
    }

    public byte[] getBytes(Bitmap bitmap) {
        //实例化字节数组输出流
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);//压缩位图
        return baos.toByteArray();//创建分配字节数组
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case BuildTeamFragment.PHOTO_REQUEST:// 相册返回
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        changeHeader.setHeaderImgAlbum();
                        relativeLayout.setBackground(new BitmapDrawable(head.getBmp()));
                        break;
                }
                break;

            case BuildTeamFragment.CAMERA_REQUEST:// 照相返回

                switch (resultCode) {
                    case Activity.RESULT_OK:// 照相完成点击确定
                        changeHeader.getHeaderImgCamera();
                        break;

                    case Activity.RESULT_CANCELED:// 取消
                        break;
                }
                break;

            case BuildTeamFragment.CAMERA_CUT_REQUEST:

                switch (resultCode) {
                    case Activity.RESULT_OK:
                        changeHeader.setHeaderImgCamera(data);
                        relativeLayout.setBackground(new BitmapDrawable(head.getBmp()));
                        break;
                    case Activity.RESULT_CANCELED:
                        break;
                }
                break;
        }
    }
}