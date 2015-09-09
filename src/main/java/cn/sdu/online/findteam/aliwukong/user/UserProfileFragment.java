package cn.sdu.online.findteam.aliwukong.user;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.alibaba.wukong.Callback;
import com.alibaba.wukong.im.Conversation;
import com.alibaba.wukong.im.ConversationService;
import com.alibaba.wukong.im.IMEngine;
import com.alibaba.wukong.im.User;
import com.alibaba.wukong.im.UserService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.sdu.online.findteam.R;
import cn.sdu.online.findteam.aliwukong.imkit.chat.controller.SingleChatActivity;
import cn.sdu.online.findteam.aliwukong.imkit.session.model.Session;
import cn.sdu.online.findteam.share.DemoUtil;
import cn.sdu.online.findteam.util.AndTools;

@TargetApi(11)
public class UserProfileFragment extends Fragment {

    private static final String TAG = "UserProfileFragment";

    private View mBottomView;
    /**创建聊天*/
    private View mSendMessageView;
    /**用户信息*/
    private ListView mListView;
    /**用户OpenID*/
    private Long mOpenId;
    /**用户Mobile id*/
    private String mMobile;

    /**用户自己的账号*/
    private boolean mBeMyProfile;

    private UserService mUserService;
    private User mUserProfile;

    private UserProfileItemObject userProfileItemObject;
    private List<UserProfileItemObject> mAdapterData;
    private UserProfileAdapter mListAdapter;

    private View.OnClickListener clickListener = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.ll_send_message:
                    createSingleConversation();
                    break;
                default:
                    break;
            }
        }
    };

    public static UserProfileFragment newInstance(){
        return new UserProfileFragment();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        mBeMyProfile = bundle.getBoolean("user_is_self");
        mOpenId = Long.valueOf(bundle.getLong("user_open_id"));
        if(mOpenId == null) {
            mMobile = bundle.getString("user_mobile_id");
        }

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_user_profile, container, false);
        mBottomView = view.findViewById(R.id.layout_user_profile_bottom);
        mSendMessageView = view.findViewById(R.id.ll_send_message);
        mSendMessageView.setOnClickListener(clickListener);

        mListView = (ListView)view.findViewById(R.id.user_profile_lv);
        mAdapterData = new ArrayList<UserProfileItemObject>();
        mListAdapter = new UserProfileAdapter(getActivity(), mAdapterData, mOpenId);
        mListView.setAdapter(mListAdapter);

        if(mBeMyProfile) {
            mBottomView.setVisibility(View.GONE);
            mBeMyProfile = true;
        }
        mUserService = IMEngine.getIMService(UserService.class);
        //加载个人信息
        loadUserData();
        return view;
    }

    public void updateProfileData() {
        loadUserData();
    }

    private void updateUserData(){
        if(mUserProfile == null)
            return;
        if(mAdapterData == null){
            mAdapterData = new ArrayList<UserProfileItemObject>();
        }else {
            mAdapterData.clear();
        }
        userProfileItemObject  = new UserProfileItemObject(UserProfileItemObject.Type.Avatar);
        String nickName = mUserProfile.nickname();
        if(nickName != null) {
            userProfileItemObject.title = nickName.trim();
        }else {
            userProfileItemObject.title = "";
        }

        List<String> remarks = new ArrayList<String>();
        remarks.add(mUserProfile.remark());
        userProfileItemObject.labels = remarks;
        String gender;
        if(mUserProfile.gender() == 1) {
            gender = "M";
        }else if(mUserProfile.gender() == 2) {
            gender = "F";
        }else {
            gender = "N";
        }
        userProfileItemObject.gender = gender;

        if(mUserProfile.isActive()) {
            if(nickName != null) {
                userProfileItemObject.content = nickName.trim();
            }else {
                userProfileItemObject.content = "";
            }

            userProfileItemObject.mediaId = mUserProfile.avatar();
            if(!TextUtils.isEmpty(mUserProfile.avatar())){
                userProfileItemObject.onclick = new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), UserAvatarActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent.putExtra("user_avatar_url", mUserProfile.avatar());
                        startActivity(intent);
                    }
                };
            }
        }else {
            if(nickName != null) {
                userProfileItemObject.content = nickName.trim();
            }else {
                userProfileItemObject.content = "";
            }
            userProfileItemObject.imageResId = R.drawable.avatar_in_active;
        }
        userProfileItemObject.isActive = mUserProfile.isActive();

        mAdapterData.add(userProfileItemObject);
        //个人信息
        userProfileItemObject = new UserProfileItemObject(
                UserProfileItemObject.Type.Header_TEMP);
        userProfileItemObject.first = true;
        userProfileItemObject.title = getString(R.string.user_profile_personal_title) + "(" + mUserProfile.openId() + ")";
        mAdapterData.add(userProfileItemObject);
        //昵称
        userProfileItemObject = new UserProfileItemObject(
                UserProfileItemObject.Type.TextContent);
        userProfileItemObject.first = true;
        userProfileItemObject.title = getString(R.string.user_profile_nick);
        if(nickName != null) {
            userProfileItemObject.content = nickName;
        }else {
            userProfileItemObject.content = "";
        }

        userProfileItemObject.imageResId = null;
        mAdapterData.add(userProfileItemObject);

        //生日 yyyy-MM-dd
        userProfileItemObject = new UserProfileItemObject(
                UserProfileItemObject.Type.TextContent);
        userProfileItemObject.title = getString(R.string.user_profile_birthday);
        Date birthdayDate = new Date(mUserProfile.birthday());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String birthday = dateFormat.format(birthdayDate);
        userProfileItemObject.content = birthday;
        userProfileItemObject.imageResId = null;
        mAdapterData.add(userProfileItemObject);

        //手机号
        userProfileItemObject = new UserProfileItemObject(
                UserProfileItemObject.Type.TextContent);
        userProfileItemObject.title = getString(R.string.user_profile_mobile);
        String mobileCode;
        String mobile = mUserProfile.mobile();
        if(mobile != null) {
            mobileCode = mUserProfile.countryCode()+"+"+mobile;
        }else {
            mobileCode = "";
        }
        userProfileItemObject.content = mobileCode;
        userProfileItemObject.imageResId = null;
        mAdapterData.add(userProfileItemObject);

        //城市
        userProfileItemObject = new UserProfileItemObject(
                UserProfileItemObject.Type.TextContent);
        userProfileItemObject.title = getString(R.string.user_profile_city);
        userProfileItemObject.content = mUserProfile.city();
        userProfileItemObject.imageResId = null;
        mAdapterData.add(userProfileItemObject);

        //工作单位
        userProfileItemObject = new UserProfileItemObject(
                UserProfileItemObject.Type.TextContent);
        userProfileItemObject.title = getString(R.string.user_profile_company);
        userProfileItemObject.content = mUserProfile.extension(userProfileItemObject.title);
        userProfileItemObject.imageResId = null;
        mAdapterData.add(userProfileItemObject);
    }

    private void loadUserData() {
        if(mUserService == null || (mOpenId == null && mMobile == null)
                || getActivity() == null)
            return;
        if(mOpenId != null) {
            DemoUtil.showProgressDialog(getActivity(), getActivity().getString(R.string.profile_data_loading));
            mUserService.getUser(new Callback<User>() {
                @Override
                public void onSuccess(User user) {
                    DemoUtil.dismissProgressDialog();
                    mUserProfile = user;
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
            }, mOpenId);

        }else if(mMobile != null) {
            DemoUtil.showProgressDialog(getActivity(), getActivity().getString(R.string.profile_data_loading));
            mUserService.getUser(new Callback<User>() {
                @Override
                public void onSuccess(User user) {
                    DemoUtil.dismissProgressDialog();
                    mUserProfile = user;
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
            }, mMobile);
        }
    }

    /**
     * 创建单聊会话
     */
    public void createSingleConversation(){
        DemoUtil.showProgressDialog(getActivity(), "正在创建会话...");
        IMEngine.getIMService(ConversationService.class).createConversation(new Callback<Conversation>() {
            @Override
            public void onSuccess(Conversation conversation) {
                DemoUtil.dismissProgressDialog();
                conversation.resetUnreadCount();
                Intent intent = new Intent( getActivity(),SingleChatActivity.class);
                intent.putExtra(Session.SESSION_INTENT_KEY,conversation);
                startActivity(intent);
            }

            @Override
            public void onException(String code, String reason) {
                DemoUtil.dismissProgressDialog();
                AndTools.showToast(getActivity(), R.string.chat_create_fail);
                Log.e(TAG,
                        R.string.chat_create_fail + ".code=" + code + " reason=" + reason);
            }

            @Override
            public void onProgress(Conversation data, int progress) {
            }
        },null, null, null, Conversation.ConversationType.CHAT,mOpenId);
    }


    public User getUserProfile(){
        return mUserProfile;
    }
}
