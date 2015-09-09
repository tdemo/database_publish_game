package cn.sdu.online.findteam.share;

import android.app.Application;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import com.alibaba.wukong.AuthConstants;
import com.alibaba.wukong.Callback;
import com.alibaba.wukong.WKConstants;
import com.alibaba.wukong.auth.AuthInfo;
import com.alibaba.wukong.auth.AuthService;
import com.alibaba.wukong.im.Conversation;
import com.alibaba.wukong.im.ConversationService;
import com.alibaba.wukong.im.IMEngine;
import com.alibaba.wukong.im.Message;
import com.alibaba.wukong.im.MessageListener;
import com.alibaba.wukong.im.MessageService;

import java.util.List;

import cn.sdu.online.findteam.activity.MainActivity;
import cn.sdu.online.findteam.aliwukong.avatar.AvatarMagician;
import cn.sdu.online.findteam.aliwukong.avatar.AvatarMagicianImpl;
import cn.sdu.online.findteam.aliwukong.imkit.base.MessageSenderImpl;
import cn.sdu.online.findteam.aliwukong.imkit.chat.controller.ChatWindowManager;
import cn.sdu.online.findteam.aliwukong.imkit.route.RouteRegister;

public class MyApplication extends Application {
    // 个人身份，队长还是队员
    public static String IDENTITY = "";
    // 个人身份，用户还是游客(1为用户，0为游客)
    public static int USER_OR_NOT = 0;

    public static int myMessage_CurrentPage = 0;
    public static int myTeam_CurrentPage = 0;
    public static int ohterTeam_CurrentPage = 0;

    private AuthReceiver mAuthReceiver;

    private static MyApplication instance;

    // 聊天的未读数
    public static int unreadnum;

    // MainActivity需要加载的fragment
    public static int currentFragment = MainActivity.MAIN_FRAGMENT;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        initWukongIM();

        initIMUtil();

        registerMessageListener();
        registerAuthReceiver();
        RouteRegister.bootwrapped();
    }

    /**
     * 初始化 Wukong IM
     */
    private void initWukongIM() {
        // 只有用户profile（如：nickname、gender、mobile等）信息放在悟空时，才需要设置成true，默认false
        IMEngine.setUserAvailable(true);
        // 设置为上海沙箱环境
        IMEngine.setEnvironment(WKConstants.Environment.ONLINE);
        //初始化IMEngine
        IMEngine.launch(this);
        //自动登录上一次的账户
        AuthInfo authInfo = AuthService.getInstance().latestAuthInfo();
        if (authInfo != null) {
            AuthService.getInstance().autoLogin(authInfo.getOpenId());
        }
    }

    public static MyApplication getInstance() {
        return instance;
    }

    /**
     * 初始化IM工具包
     */
    private void initIMUtil() {
        MessageSenderImpl.getInstance().init(this);
        AvatarMagicianImpl.getInstance().init(this);
        AvatarMagicianImpl.getInstance().setAvatarShape(AvatarMagician.CIRCLE_AVATAR_SHAPE);
    }

    /**
     * 注册账号异常的广播监听
     */
    public void registerAuthReceiver() {
        if (mAuthReceiver == null) {
            mAuthReceiver = new AuthReceiver();
        }
        IntentFilter accountFilter = new IntentFilter();
        accountFilter.addAction(AuthConstants.Event.EVENT_AUTH_LOGOUT);
        accountFilter.addAction(AuthConstants.Event.EVENT_AUTH_KICKOUT);
        accountFilter.addAction(AuthConstants.Event.EVENT_AUTH_LOGIN);
        LocalBroadcastManager.getInstance(this).registerReceiver(mAuthReceiver, accountFilter);
    }

    /**
     * 注册接收消息监听器，用于更改消息未读数
     * 放在这里的原因:杀掉进程重启的时候未进入MainActivity就接收到消息了，
     * 所以如果放在主页处理会出现未读消息数异常
     */
    private void registerMessageListener() {
        IMEngine.getIMService(MessageService.class).addMessageListener(new MessageListener() {
            @Override
            public void onAdded(List<Message> list, DataType dataType) {
                String currentChatCid = ChatWindowManager.getInstance().getCurrentChatCid();
                for (Message msg : list) {
                    if (msg.senderId() == DemoUtil.currentOpenId()) {
                        continue;   //发送人是自己的时候，未读数不增加
                    }

                    Conversation conversation = msg.conversation();
                    if (conversation == null) {
                        continue;
                    }

                    //如果消息不属于当前会话则将累加未读数
                    if (currentChatCid == null || !currentChatCid.equals(conversation.conversationId())) {
                        msg.conversation().addUnreadCount(1);
                        newMessageNotify();
                        if (msg.isAt()) {
                            conversation.updateAtMeStatus(true);
                        }
                    }
                }
            }

            @Override
            public void onRemoved(List<Message> list) {
            }

            @Override
            public void onChanged(List<Message> list) {
            }
        });
    }

    private void newMessageNotify() {
        if (DemoUtil.isAppForeground(MyApplication.getInstance())) {
            return;
        }

        IMEngine.getIMService(ConversationService.class).getTotalUnreadCount(
                new Callback<Integer>() {
                    @Override
                    public void onSuccess(Integer unReadCount) {
                        DemoUtil.sendNotification(unReadCount);
                    }

                    @Override
                    public void onException(String code, String reason) {

                    }

                    @Override
                    public void onProgress(Integer data, int progress) {

                    }
                }, false);
    }

    @Override
    public void onTerminate() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mAuthReceiver);
        super.onTerminate();
    }
}
