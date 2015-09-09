package cn.sdu.online.findteam.aliwukong.imkit.chat.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import cn.sdu.online.findteam.R;
import cn.sdu.online.findteam.aliwukong.base.BaseFragmentActivity;
import cn.sdu.online.findteam.aliwukong.imkit.session.model.Session;
import cn.sdu.online.findteam.aliwukong.util.Consts;

/**
 * Created by wn on 2015/8/14.
 */
public class GroupChatActivity extends BaseFragmentActivity implements View.OnClickListener {

    private Session mCurrentSession;
    Button back, setting;
    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.chat_layout);
        mCurrentSession = (Session) getIntent().getSerializableExtra(Session.SESSION_INTENT_KEY);
        mCurrentSession.sync();

        setActionBarLayout(R.layout.chatactivity_actionbar);
        title = (TextView) findViewById(R.id.chat_person);
        back = (Button) findViewById(R.id.chat_back_btn);
        setting = (Button) findViewById(R.id.item_chat_setting);
        back.setOnClickListener(this);
        setting.setOnClickListener(this);
        title.setText(mCurrentSession.title());

        initSystemStatusBar();
        ChatFragment fragment = (ChatFragment) getSupportFragmentManager().findFragmentById(R.id.chat_fragment);
        fragment.setCurrentConversation(mCurrentSession);
        ChatWindowManager.getInstance().setCurrentChatCid(mCurrentSession.conversationId());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.item_chat_setting:
                Intent intent = new Intent();
                intent.putExtra(Consts.KEY_CONVERSATION, mCurrentSession.mConversation);
                intent.setClass(GroupChatActivity.this, ChatSettingActivity.class);
                startActivity(intent);
                break;

            case R.id.chat_back_btn:
                finish();
                break;
        }
    }
}
