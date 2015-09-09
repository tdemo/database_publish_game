package cn.sdu.online.findteam.aliwukong.imkit.chat.controller;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.wukong.Callback;
import com.alibaba.wukong.im.Conversation;
import com.alibaba.wukong.im.IMEngine;
import com.alibaba.wukong.im.User;
import com.alibaba.wukong.im.UserService;
import com.alibaba.wukong.im.utils.Utils;

import cn.sdu.online.findteam.R;
import cn.sdu.online.findteam.aliwukong.base.BaseFragmentActivity;
import cn.sdu.online.findteam.aliwukong.imkit.chat.model.ChatMessage;
import cn.sdu.online.findteam.aliwukong.imkit.session.model.Session;
import cn.sdu.online.findteam.aliwukong.util.Consts;
import cn.sdu.online.findteam.util.AndTools;

public class SingleChatActivity extends BaseFragmentActivity implements View.OnClickListener {
    private int mSessionType;
    private ChatFragment chat;
    private ChatMessageTransmitter transmitter;
    private Conversation mConversation;

    Button back, setting;
    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.chat_layout);
        mConversation = (Conversation) getIntent().getSerializableExtra(Session.SESSION_INTENT_KEY);
        mConversation.sync();
        mSessionType = mConversation.type();
        Log.e("DemoLog", "mSessionType1=" + mSessionType);
        initSystemStatusBar(); //高版本上statusbar一体化
//        initActionBar(conversation.title());
        setUpActionBar(mConversation);
        chat = (ChatFragment) getSupportFragmentManager().findFragmentById(R.id.chat_fragment);
        chat.setCurrentConversation(mConversation);
        ChatWindowManager.getInstance().setCurrentChatCid(mConversation.conversationId());
        transmitter = (ChatMessageTransmitter) getSupportFragmentManager().findFragmentById(R.id.chat_transmitter);
        transmitter.setCurrentConeverstaion(mConversation);
        transmitter.setOnTransmitted(new Callback<ChatMessage>() {
            @Override
            public void onSuccess(ChatMessage chatMessage) {
//                chat.onMessageSended(chatMessage);
            }

            @Override
            public void onException(String code, String reason) {

            }

            @Override
            public void onProgress(ChatMessage chatMessage, int i) {

            }
        });

        if (!AndTools.isNetworkAvailable(this)) {
            AndTools.showToast(this, getString(R.string.network_error));
        }
    }

    //todo:群聊跳转到GroupChatActivity的话改方法需要修改
    public void setUpActionBar(final Conversation conversation) {
        setActionBarLayout(R.layout.chatactivity_actionbar);
        title = (TextView) findViewById(R.id.chat_person);
        back = (Button) findViewById(R.id.chat_back_btn);
        setting = (Button) findViewById(R.id.item_chat_setting);
        back.setOnClickListener(this);
        setting.setOnClickListener(this);

        if (Conversation.ConversationType.GROUP == conversation.type()) {
            title.setText(conversation.title());
        } else {
            IMEngine.getIMService(UserService.class).getUser(new Callback<User>() {
                @Override
                public void onSuccess(User user) {
                    if (TextUtils.isEmpty(user.nickname())) {
                        title.setText(conversation.title());
                    } else {
                        title.setText(user.nickname());
                    }
                }

                @Override
                public void onException(String code, String reason) {
                    Log.e("DemoLog", "Get user error.code=" + code + " reason=" + reason);
                }

                @Override
                public void onProgress(User user, int progress) {
                }
            }, Utils.toLong(conversation.title()));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.item_chat_setting:
                Intent intent = new Intent();
                intent.putExtra(Consts.KEY_CONVERSATION, mConversation);
                intent.setClass(SingleChatActivity.this, ChatSettingActivity.class);
                startActivity(intent);
                break;

            case R.id.chat_back_btn:
                finish();
                break;
        }
    }
}
