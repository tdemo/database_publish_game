package cn.sdu.online.findteam.aliwukong.imkit.chat.controller;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.wukong.Callback;
import com.alibaba.wukong.auth.AuthService;
import com.alibaba.wukong.im.Conversation;
import com.alibaba.wukong.im.ConversationService;
import com.alibaba.wukong.im.IMEngine;
import com.alibaba.wukong.im.Member;
import com.alibaba.wukong.im.Message;
import com.alibaba.wukong.im.MessageBuilder;
import com.alibaba.wukong.im.MessageContent;
import com.alibaba.wukong.im.User;
import com.alibaba.wukong.im.UserService;
import com.alibaba.wukong.im.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import cn.sdu.online.findteam.R;
import cn.sdu.online.findteam.activity.MyMessageActivity;
import cn.sdu.online.findteam.aliwukong.avatar.AvatarMagicianImpl;
import cn.sdu.online.findteam.aliwukong.base.BaseFragmentActivity;
import cn.sdu.online.findteam.aliwukong.imkit.base.WrapperGridView;
import cn.sdu.online.findteam.aliwukong.util.Consts;
import cn.sdu.online.findteam.share.DemoUtil;
import cn.sdu.online.findteam.util.AndTools;

/**
 * Created by wn on 2015/8/14.
 */
public class ChatSettingActivity extends BaseFragmentActivity implements View.OnClickListener{
    private static final String TAG = ChatSettingActivity.class.getSimpleName();
    private List<User> mUserList;
    private WrapperGridView mAvatarGridView;
    private AvatarAdapter mAvatarAdapter;

    private ConversationService mConversationService;
    private Conversation mConversation;
    private MessageBuilder mMessageBuilder;

    Button back;
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_setting);

        setActionBarLayout(R.layout.chatsettingactivity_actionbar);
        back = (Button) findViewById(R.id.chatsetting_back_btn);
        tv = (TextView) findViewById(R.id.chat_setting_tv);
        back.setOnClickListener(this);

        mConversation = (Conversation) getIntent().getSerializableExtra(Consts.KEY_CONVERSATION);

        mConversationService = IMEngine.getIMService(ConversationService.class);
        mAvatarGridView = (WrapperGridView) findViewById(R.id.avatar_grid);
        mAvatarAdapter = new AvatarAdapter(this);
        mAvatarGridView.setAdapter(mAvatarAdapter);
        mMessageBuilder = IMEngine.getIMService(MessageBuilder.class);
        loadData();

        findViewById(R.id.quit_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ChatSettingActivity.this);
                builder.setTitle(getString(R.string.quit_confirm))
                        .setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Message message = buildSystemMessage(MessageContent.MessageTemplate.QUIT_CONVERSATION);
                                DemoUtil.showProgressDialog(ChatSettingActivity.this, getString(R.string.quiting));
                                mConversation.quit(message,
                                        new Callback<Void>() {
                                            @Override
                                            public void onSuccess(Void data) {
                                                Log.e(TAG, "[onSuccess] quit");
                                                DemoUtil.dismissProgressDialog();
                                                startActivity(new Intent(ChatSettingActivity.this, MyMessageActivity.class));
                                                finish();
                                            }

                                            @Override
                                            public void onException(String code, String reason) {
                                                DemoUtil.dismissProgressDialog();
                                                Log.e(TAG,
                                                        "[onException] quit; code: " + code + "reason: "
                                                                + reason);
                                            }

                                            @Override
                                            public void onProgress(Void data, int progress) {

                                            }
                                        });

                            }
                        })
                        .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                builder.show();

            }
        });
    }

    private void loadData() {
        if (mConversation.type() == Conversation.ConversationType.GROUP) {
            mConversationService.listMembers(new Callback<List<Member>>() {
                @Override
                public void onSuccess(List<Member> data) {
                    mUserList = resolveMemberImageUrl(data);
                    Log.e(TAG, "refreshData onSuccess, mUserList: " + mUserList);
                    mAvatarAdapter.notifyDataSetChanged();

                }

                @Override
                public void onException(String code, String reason) {
                    Log.e(TAG, "refreshData onException, code: " + code + "reason: " + reason);
                }

                @Override
                public void onProgress(List<Member> data, int progress) {
                }

            }, mConversation.conversationId(), 0, 20);
        }

        if (mConversation.type() == Conversation.ConversationType.CHAT) {
            List<Long> uids = new ArrayList<Long>();
            uids.add(mConversation.getOtherOpenId());
            uids.add(AuthService.getInstance().latestAuthInfo().getOpenId());
            IMEngine.getIMService(UserService.class).listUsers(new Callback<List<User>>() {
                @Override
                public void onSuccess(List<User> users) {
                    mUserList = users;
                    mAvatarAdapter.notifyDataSetChanged();
                }

                @Override
                public void onException(String s, String s1) {

                }

                @Override
                public void onProgress(List<User> users, int i) {

                }
            }, uids);

            findViewById(R.id.quit_btn).setVisibility(View.GONE);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.chatsetting_back_btn:
                finish();
                break;
        }
    }


    public class AvatarAdapter extends BaseAdapter {
        private Context mContext;

        public AvatarAdapter(Context ctx) {
            mContext = ctx;
        }

        @Override
        public int getCount() {
            if (mUserList == null) {
                return 0;
            }
            return mUserList.size() + 1;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView avatarImg;
            convertView = LayoutInflater.from(mContext).inflate(R.layout.chat_avatar_item, null);
            avatarImg = (ImageView) convertView.findViewById(R.id.avatar_iv);
            TextView nameTv = (TextView) convertView.findViewById(R.id.name_tv);

            if (position == mUserList.size()) {
                avatarImg.setImageDrawable(mContext.getResources().getDrawable(R.drawable.chat_setting_plus));
                nameTv.setVisibility(View.GONE);
                avatarImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(ChatSettingActivity.this);
                        alert.setTitle(getString(R.string.add_new_member_tips));
                        final EditText input = new EditText(ChatSettingActivity.this);
                        alert.setView(input);
                        alert.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String openId = input.getText().toString();
                                if (TextUtils.isEmpty(openId)) {
                                    AndTools.showToast(ChatSettingActivity.this, getString(R.string.chat_create_invalid_openId));
                                    return;
                                }
                                if (mConversation.type() == Conversation.ConversationType.GROUP) {
                                    addMember(Utils.toLong(openId));
                                } else {
                                    createConversation(Utils.toLong(openId));
                                }
                            }
                        }).setNegativeButton(getString(R.string.cancel), null);
                        alert.show();
                    }
                });
            } else {
                User u = mUserList.get(position);
                if (u != null) {
                    if (!TextUtils.isEmpty(u.avatar())) {
                        AvatarMagicianImpl.getInstance().setUserAvatar(avatarImg, u.openId(), mAvatarGridView);
                    }
                    if (!TextUtils.isEmpty(u.nickname())) {
                        nameTv.setText(u.nickname());
                    }
                }
                nameTv.setVisibility(View.VISIBLE);
            }

            return convertView;
        }
    }

    private void addMember(long uid) {
        final Long[] uids = new Long[1];
        uids[0] = uid;
        Message message = buildSystemMessage(MessageContent.MessageTemplate.ADD_MEMBER);
        mConversationService.addMembers(new Callback<List<Long>>() {
            @Override
            public void onSuccess(List<Long> longs) {
                loadData();
            }

            @Override
            public void onException(String s, String s1) {
                AndTools.showToast(ChatSettingActivity.this, getString(R.string.add_new_member_error));
            }

            @Override
            public void onProgress(List<Long> longs, int i) {

            }
        }, mConversation.conversationId(), message, uids);
    }

    private void createConversation(long uid) {
        Long[] uids = new Long[3];
        uids[0] = uid;

        StringBuilder title = new StringBuilder();
        StringBuilder icon = new StringBuilder();
        icon.append(uid);
        title.append(uid);

        int count = 1;
        for (User u : mUserList) {
            icon.append(":").append(u.openId());
            title.append(",").append(u.openId());
            uids[count] = u.openId();
            if (count >= 2)
                break;
            count++;
        }
        String sysMsg = getString(R.string.chat_create_sysmsg, DemoUtil.currentNickname());
        Message message = IMEngine.getIMService(MessageBuilder.class).buildTextMessage(sysMsg); //系统消息
        mConversationService.createConversation(new Callback<Conversation>() {
            @Override
            public void onSuccess(Conversation co) {
                Intent intent = new Intent(ChatSettingActivity.this, MyMessageActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onException(String s, String s1) {
                AndTools.showToast(ChatSettingActivity.this, getString(R.string.add_new_member_error));
            }

            @Override
            public void onProgress(Conversation co, int i) {

            }
        }, title.toString(), icon.toString(), message, Conversation.ConversationType.GROUP, uids);
    }

    private Message buildSystemMessage(String text) {
        return mMessageBuilder.buildTextMessage(text);
    }

    public ArrayList<User> resolveMemberImageUrl(List<Member> data) {
        ArrayList<User> stringArrayList = new ArrayList<User>();

        for (Member member : data) {
            stringArrayList.add((member.user()));
        }
        return stringArrayList;
    }

}
