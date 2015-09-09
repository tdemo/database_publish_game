package cn.sdu.online.findteam.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.alibaba.wukong.Callback;
import com.alibaba.wukong.im.Conversation;
import com.alibaba.wukong.im.ConversationService;
import com.alibaba.wukong.im.IMEngine;
import com.alibaba.wukong.im.Message;
import com.alibaba.wukong.im.MessageBuilder;
import com.alibaba.wukong.im.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import cn.sdu.online.findteam.R;
import cn.sdu.online.findteam.aliwukong.imkit.chat.controller.SingleChatActivity;
import cn.sdu.online.findteam.aliwukong.imkit.session.model.GroupSession;
import cn.sdu.online.findteam.aliwukong.imkit.session.model.Session;
import cn.sdu.online.findteam.share.DemoUtil;
import cn.sdu.online.findteam.util.AndTools;

/**
 * Created by wn on 2015/8/19.
 */
public class NewChatActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newchat_layout);

        /*initActionBar(null);*/

        final EditText mIdsText = (EditText) findViewById(R.id.et_create_chat);
        Button mOkButton = (Button) findViewById(R.id.btn_create_chat);
        mOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String openIdString = mIdsText.getText().toString();
                if (TextUtils.isEmpty(openIdString)) {
                    AndTools.showToast(NewChatActivity.this, R.string.chat_create_invalid_openId);
                    return;
                }
                String[] openIds = openIdString.split(" ");
                List<Long> list = new ArrayList<Long>();
                for (String id : openIds) {
                    long openId = Utils.toLong(id.trim());
                    if (openId > 0) {
                        list.add(openId);
                    }
                }
                createConversation(list);
            }
        });
    }

    private static final int AVATAR_MAX_NUM = 4;

    private void createConversation(List<Long> list) {
        int size = list.size();
        if (size == 0) {
            AndTools.showToast(NewChatActivity.this, R.string.chat_create_invalid_openId);
            return;
        }

        StringBuilder title = new StringBuilder();
        StringBuilder icon = new StringBuilder();
        int count = 1;
        int limit = Math.min(AVATAR_MAX_NUM, size);
        for (Long openId : list) {
            icon.append(openId);
            title.append(openId);
            if (count >= limit)
                break;
            count++;
            icon.append(":");
            title.append(",");
        }

        DemoUtil.showProgressDialog(NewChatActivity.this, getString(R.string.chat_create_doing));
        Long[] uids = list.toArray(new Long[size]);

        String sysMsg = getString(R.string.chat_create_sysmsg, DemoUtil.currentNickname());
        Message message = IMEngine.getIMService(MessageBuilder.class).buildTextMessage(sysMsg); //系统消息
        IMEngine.getIMService(ConversationService.class).createConversation(new Callback<Conversation>() {
            @Override
            public void onSuccess(Conversation conversation) {
                DemoUtil.dismissProgressDialog();
                Intent intent = new Intent(NewChatActivity.this, SingleChatActivity.class);
                intent.putExtra(Session.SESSION_INTENT_KEY, new GroupSession(conversation));
                startActivity(intent);
                finish();
            }

            @Override
            public void onException(String s, String s1) {
                DemoUtil.dismissProgressDialog();
                AndTools.showToast(NewChatActivity.this, R.string.chat_create_fail);
            }

            @Override
            public void onProgress(Conversation conversation, int i) {

            }
        }, title.toString(), icon.toString(), message, size == 1 ? Conversation.ConversationType.CHAT : Conversation.ConversationType.GROUP, uids);
    }

}
