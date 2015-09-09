package cn.sdu.online.findteam.aliwukong.user;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.wukong.Callback;
import com.alibaba.wukong.im.IMEngine;
import com.alibaba.wukong.im.User;
import com.alibaba.wukong.im.UserService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.sdu.online.findteam.R;
import cn.sdu.online.findteam.share.DemoUtil;
import cn.sdu.online.findteam.util.AndTools;

/**
 * Created by wn on 2015/8/15.
 */
@TargetApi(11)
public class EditProfileFragment extends Fragment {

    private static final String TAG = "EditProfileFragment";

    protected static final int MAX_PROFILE_NICK_LENGTH = 10;
    protected static final int MAX_PROFILE_REMARK_LENGTH = 30;
    protected static final int MAX_PROFILE_CITY_LENGTH = 10;
    protected static final int MAX_PROFILE_COMPANY_LENGTH = 50;

    protected static final int PICTURE_PICK_CODE = 100;

    //    private Button mOkBtn;
    private ListView mListView;

    private UserProfileItemObject userProfileItemObject;
    private List<UserProfileItemObject> mAdapterData;
    private UserProfileAdapter mListAdapter;

    private Long mOpenId;
    private User mMyOwnProfile;
    private UserService mUserService;

    private String genderString;
    final CharSequence[] sexitems = {
            "男", "女"
    };
    String select_item_gender = "M";
    private DatePickerDialog.OnDateSetListener Datelistener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//            changedDob = getBobDate(year, monthOfYear, dayOfMonth);
//            updateMyDate();
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, monthOfYear, dayOfMonth);

            updateBirthday(calendar.getTime());
        }

    };

    public static EditProfileFragment newInstance(){
        return new EditProfileFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        mMyOwnProfile = (User)bundle.getSerializable("user_profile");

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_edit_profile, container, false);
//        mOkBtn = (Button)view.findViewById(R.id.btn_next);
//        mOkBtn.setOnClickListener(clickListener);

        mListView = (ListView)view.findViewById(R.id.user_profile_lv);
        mAdapterData = new ArrayList<UserProfileItemObject>();
        if(mMyOwnProfile != null){
            updateUserData();
        }

        if(mMyOwnProfile != null) {
            mOpenId = mMyOwnProfile.openId();
        }else {
            mOpenId = DemoUtil.currentOpenId();
        }
        mListAdapter = new UserProfileAdapter(getActivity(), mAdapterData, mOpenId);
        mListView.setAdapter(mListAdapter);

        mUserService = IMEngine.getIMService(UserService.class);
        if(mMyOwnProfile == null){
            loadData();
        }
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICTURE_PICK_CODE && resultCode == Activity.RESULT_OK && data != null){
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getActivity().getContentResolver().query(selectedImage,filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            if(mUserService != null) {
                mUserService.updateAvatar(new Callback<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        loadData();
                    }

                    @Override
                    public void onException(String s, String s2) {

                    }

                    @Override
                    public void onProgress(Void aVoid, int i) {

                    }
                }, picturePath);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void updateNick(){
        final String nickName = mMyOwnProfile.nickname();
        View dialogView = LayoutInflater.from(getActivity()).inflate(
                R.layout.dialog_edit, null);
        final EditText editText = (EditText) dialogView.findViewById(R.id.edt_conversation_title);
        editText.setText(nickName);
        editText.setHint("");
        editText.setSingleLine(true);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                int length = s.length();
                if (length > MAX_PROFILE_NICK_LENGTH) {
                    Toast.makeText(getActivity(),
                            getString(R.string.profile_nick_hint, MAX_PROFILE_NICK_LENGTH),
                            Toast.LENGTH_SHORT)
                            .show();
                    s.delete(MAX_PROFILE_NICK_LENGTH, length);
                }
            }
        });
        if(nickName != null) {
            Selection.setSelection(editText.getText(), nickName.length());
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT);
        builder.setTitle(getString(R.string.my_profile_nick));
        builder.setView(dialogView);
        builder.setNegativeButton(getString(R.string.cancel), null);
        builder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                final String newNick = editText.getText().toString();
                int length = newNick.length();
                if (!newNick.equals(nickName)) {
                    if (length > 0 && newNick.trim().length() <= 0) {
                        Toast.makeText(getActivity(),
                                getString(R.string.profile_nick_not_null),
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (length < 1 || length > MAX_PROFILE_NICK_LENGTH) {
                        Toast.makeText(getActivity(),
                                getString(R.string.profile_nick_hint, MAX_PROFILE_NICK_LENGTH),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        //Update the nick name
                        DemoUtil.showProgressDialog(getActivity(), null);
                        if(mUserService != null) {
                            mUserService.updateNickname(new Callback<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    DemoUtil.dismissProgressDialog();
                                    loadData();
                                }

                                @Override
                                public void onException(String s, String s2) {

                                }

                                @Override
                                public void onProgress(Void aVoid, int i) {

                                }
                            }, newNick);
                        }
                    }
                }
            }
        });
        builder.setCancelable(false);
        builder.create().show();

    }

    private void updateRemark() {
        final String remark = mMyOwnProfile.remark();
        View dialogView = LayoutInflater.from(getActivity()).inflate(
                R.layout.dialog_edit, null);
        final EditText editText = (EditText) dialogView.findViewById(R.id.edt_conversation_title);
        editText.setText(remark);
        editText.setHint("");
        editText.setSingleLine(true);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                int length = s.length();
                if (length > MAX_PROFILE_REMARK_LENGTH) {
                    Toast.makeText(getActivity(),
                            getString(R.string.profile_nick_hint, MAX_PROFILE_REMARK_LENGTH),
                            Toast.LENGTH_SHORT)
                            .show();
                    s.delete(MAX_PROFILE_REMARK_LENGTH, length);
                }
            }
        });
        if(remark != null) {
            Selection.setSelection(editText.getText(), remark.length());
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT);
        builder.setTitle(getString(R.string.my_profile_label));
        builder.setView(dialogView);
        builder.setNegativeButton(getString(R.string.cancel), null);
        builder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                final String newRemark = editText.getText().toString();
                //Update the nick name
                DemoUtil.showProgressDialog(getActivity(), null);
                if(mUserService != null) {
                    mUserService.updateRemark(new Callback<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            DemoUtil.dismissProgressDialog();
                            loadData();
                        }

                        @Override
                        public void onException(String s, String s2) {

                        }

                        @Override
                        public void onProgress(Void aVoid, int i) {

                        }
                    }, newRemark);
                }
            }
        });
        builder.setCancelable(false);
        builder.create().show();
    }

    private void updateGender(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT);
        builder.setTitle(R.string.user_profile_gender);
        int checkedItem = -1;
        if (genderString != null) {
            if (genderString.equals("男") || genderString.equals("M")) {
                checkedItem = 0;
            } else if (genderString.equals("女") || genderString.equals("F")) {
                checkedItem = 1;
            }
        }
        builder.setSingleChoiceItems(sexitems, checkedItem,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        select_item_gender = sexitems[which].toString();
                    }
                });
        builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                User.Gender gender;
                if (select_item_gender.equals("男")) {
                    gender = User.Gender.MALE;
                } else if (select_item_gender.equals("女")) {
                    gender = User.Gender.FEMALE;
                } else if ("男".equals(genderString)) {
                    gender = User.Gender.MALE;
                } else {
                    gender = User.Gender.FEMALE;
                }

                DemoUtil.showProgressDialog(getActivity(), null);
                //update the gender
                if(mUserService != null) {
                    mUserService.updateProfile(new Callback<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            DemoUtil.dismissProgressDialog();
                            loadData();
                        }

                        @Override
                        public void onException(String s, String s2) {

                        }

                        @Override
                        public void onProgress(Void aVoid, int i) {

                        }
                    }, gender, null, null, null, null);
                }

            }
        });
        builder.show();

    }

    private void updateBirthday(Date date){
        if(date == null || mUserService == null) {
            return;
        }
        Long birthday = Long.valueOf(date.getTime());
        DemoUtil.showProgressDialog(getActivity(), null);
        mUserService.updateProfile(new Callback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                DemoUtil.dismissProgressDialog();
                loadData();
            }

            @Override
            public void onException(String s, String s2) {

            }

            @Override
            public void onProgress(Void aVoid, int i) {

            }
        }, null, birthday, null, null, null);
    }

    private void updateCity(){
        final String city = mMyOwnProfile.city();
        View dialogView = LayoutInflater.from(getActivity()).inflate(
                R.layout.dialog_edit, null);
        final EditText editText = (EditText) dialogView.findViewById(R.id.edt_conversation_title);
        editText.setText(city);
        editText.setHint("");
        editText.setSingleLine(true);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                int length = s.length();
                if (length > MAX_PROFILE_REMARK_LENGTH) {
                    Toast.makeText(getActivity(),
                            getString(R.string.profile_nick_hint, MAX_PROFILE_CITY_LENGTH),
                            Toast.LENGTH_SHORT)
                            .show();
                    s.delete(MAX_PROFILE_CITY_LENGTH, length);
                }
            }
        });
        if(city != null) {
            Selection.setSelection(editText.getText(), city.length());
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT);
        builder.setTitle(getString(R.string.my_profile_city));
        builder.setView(dialogView);
        builder.setNegativeButton(getString(R.string.cancel), null);
        builder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                final String newCity = editText.getText().toString();
                //Update the city
                DemoUtil.showProgressDialog(getActivity(), null);
                if(mUserService != null) {
                    mUserService.updateProfile(new Callback<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            DemoUtil.dismissProgressDialog();
                            loadData();
                        }

                        @Override
                        public void onException(String s, String s2) {

                        }

                        @Override
                        public void onProgress(Void aVoid, int i) {

                        }
                    }, null, null, null, null, newCity);
                }
            }
        });
        builder.setCancelable(false);
        builder.create().show();
    }

    private void updateExtesion(final String extesionKey) {
        if (extesionKey == null)
            return;
        //工作单位
        final String company = mMyOwnProfile.extension(extesionKey);
        View dialogView = LayoutInflater.from(getActivity()).inflate(
                R.layout.dialog_edit, null);
        final EditText editText = (EditText) dialogView.findViewById(R.id.edt_conversation_title);
        editText.setText(company);
        editText.setHint("");
        editText.setSingleLine(true);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                int length = s.length();
                if (length > MAX_PROFILE_COMPANY_LENGTH) {
                    Toast.makeText(getActivity(),
                            getString(R.string.profile_nick_hint, MAX_PROFILE_COMPANY_LENGTH),
                            Toast.LENGTH_SHORT)
                            .show();
                    s.delete(MAX_PROFILE_COMPANY_LENGTH, length);
                }
            }
        });
        if(company != null) {
            Selection.setSelection(editText.getText(), company.length());
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT);
        builder.setTitle(getString(R.string.my_profile_company));
        builder.setView(dialogView);
        builder.setNegativeButton(getString(R.string.cancel), null);
        builder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                final String newCompany = editText.getText().toString();
                //Update the city
                DemoUtil.showProgressDialog(getActivity(), null);
                if(mUserService != null) {
                    mUserService.updateExtension(new Callback<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            DemoUtil.dismissProgressDialog();
                            loadData();
                        }

                        @Override
                        public void onException(String s, String s2) {

                        }

                        @Override
                        public void onProgress(Void aVoid, int i) {

                        }
                    }, extesionKey, newCompany);
                }
            }
        });
        builder.setCancelable(false);
        builder.create().show();
    }

    private void updateUserData(){
        if(mMyOwnProfile == null)
            return;
        if(mAdapterData == null){
            mAdapterData = new ArrayList<UserProfileItemObject>();
        }else {
            mAdapterData.clear();
        }

        //个人信息
        userProfileItemObject = new UserProfileItemObject(
                UserProfileItemObject.Type.Header);
        userProfileItemObject.title = getString(R.string.header_edit_profile) + "(openID:" + mMyOwnProfile.openId() + ")";
        mAdapterData.add(userProfileItemObject);
        //头像
        userProfileItemObject = new UserProfileItemObject(
                UserProfileItemObject.Type.MyAvatar);
        userProfileItemObject.title = getString(R.string.user_profile_avator);
        userProfileItemObject.mediaId = mMyOwnProfile.avatar();
        userProfileItemObject.content = "";
        userProfileItemObject.onclick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICTURE_PICK_CODE);
            }
        };
        mAdapterData.add(userProfileItemObject);

        //昵称
        userProfileItemObject = new UserProfileItemObject(
                UserProfileItemObject.Type.TextContentRight);
        userProfileItemObject.first = true;
        userProfileItemObject.title = getString(R.string.user_profile_nick);
        String name = mMyOwnProfile.nickname();
        userProfileItemObject.content = name;
        userProfileItemObject.imageResId = null;
        userProfileItemObject.onclick = new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                updateNick();
            }
        };
        mAdapterData.add(userProfileItemObject);

        //性别
        userProfileItemObject = new UserProfileItemObject(
                UserProfileItemObject.Type.TextContentRight);
        userProfileItemObject.title = getString(R.string.user_profile_gender);
        String gender;
        if(mMyOwnProfile.gender() == 1) {
            gender = "M";
        }else if(mMyOwnProfile.gender() == 2) {
            gender = "F";
        }else {
            gender = "N";
        }
        userProfileItemObject.content = gender;
        genderString = gender;
        userProfileItemObject.onclick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateGender();
            }

        };
        mAdapterData.add(userProfileItemObject);

        //生日
        userProfileItemObject = new UserProfileItemObject(
                UserProfileItemObject.Type.TextContentRight);
        userProfileItemObject.title = getString(R.string.user_profile_birthday);
        final Date birthdayDate = new Date(mMyOwnProfile.birthday());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String birthday = dateFormat.format(birthdayDate);
        userProfileItemObject.content = birthday;
        userProfileItemObject.onclick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(birthdayDate);

                DatePickerDialog mDatePickerDialog = new DatePickerDialog(getActivity(),
                        Datelistener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH));
                mDatePickerDialog.show(); // 显示日期设置对话框
            }

        };
        mAdapterData.add(userProfileItemObject);

        //手机号
        userProfileItemObject = new UserProfileItemObject(
                UserProfileItemObject.Type.TextContentRight);
        userProfileItemObject.first = true;
        userProfileItemObject.title = getString(R.string.user_profile_mobile);
        String mobileCode;
        String mobile = mMyOwnProfile.mobile();
        if(mobile != null) {
            mobileCode = mMyOwnProfile.countryCode()+"+"+mobile;
        }else {
            mobileCode = "";
        }
        userProfileItemObject.content = mobileCode;
        mAdapterData.add(userProfileItemObject);

        //城市
        userProfileItemObject = new UserProfileItemObject(
                UserProfileItemObject.Type.TextContentRight);
        userProfileItemObject.first = true;
        userProfileItemObject.title = getString(R.string.user_profile_city);
        userProfileItemObject.content = mMyOwnProfile.city();
        userProfileItemObject.imageResId = null;
        userProfileItemObject.onclick = new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                updateCity();
            }
        };
        mAdapterData.add(userProfileItemObject);

        //工作单位
        userProfileItemObject = new UserProfileItemObject(
                UserProfileItemObject.Type.TextContentRight);
        userProfileItemObject.first = true;
        userProfileItemObject.title = getString(R.string.user_profile_company);
        userProfileItemObject.content = mMyOwnProfile.extension(userProfileItemObject.title);
        userProfileItemObject.imageResId = null;
        userProfileItemObject.onclick = new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                updateExtesion(getString(R.string.user_profile_company));
            }
        };
        mAdapterData.add(userProfileItemObject);

        //个性签名
        userProfileItemObject = new UserProfileItemObject(
                UserProfileItemObject.Type.TextContentRight);
        userProfileItemObject.first = true;
        userProfileItemObject.title = getString(R.string.user_profile_label);
        userProfileItemObject.content = mMyOwnProfile.remark();
        userProfileItemObject.imageResId = null;
        userProfileItemObject.onclick = new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                updateRemark();
            }
        };
        mAdapterData.add(userProfileItemObject);

        userProfileItemObject = new UserProfileItemObject(
                UserProfileItemObject.Type.Header);
        userProfileItemObject.first=true;
        mAdapterData.add(userProfileItemObject);
    }

    private void loadData(){
        if(mUserService == null || getActivity() == null)
            return;
        DemoUtil.showProgressDialog(getActivity(), getActivity().getString(R.string.profile_data_loading));
        mUserService.getUser(new Callback<User>() {
            @Override
            public void onSuccess(User user) {
                DemoUtil.dismissProgressDialog();
                mMyOwnProfile = user;
                updateUserData();
                mListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onException(String code, String reason) {
                DemoUtil.dismissProgressDialog();
                AndTools.showToast(getActivity(), reason);
                getActivity().finish();
            }

            @Override
            public void onProgress(User user, int i) {

            }
        }, DemoUtil.currentOpenId());

    }
}
