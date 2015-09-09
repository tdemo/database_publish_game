package cn.sdu.online.findteam.aliwukong.imkit.business;

import android.text.TextUtils;
import android.util.Log;

import com.alibaba.wukong.Callback;
import com.alibaba.wukong.im.Conversation;
import com.alibaba.wukong.im.ConversationListener;
import com.alibaba.wukong.im.ConversationService;
import com.alibaba.wukong.im.IMEngine;
import com.alibaba.wukong.im.Message;
import com.alibaba.wukong.im.UserService;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import cn.sdu.online.findteam.aliwukong.imkit.base.Functional;
import cn.sdu.online.findteam.aliwukong.imkit.chat.model.ChatMessage;
import cn.sdu.online.findteam.aliwukong.imkit.listener.ConversationUpdateListener;
import cn.sdu.online.findteam.aliwukong.imkit.session.model.GroupSession;
import cn.sdu.online.findteam.aliwukong.imkit.session.model.Session;
import cn.sdu.online.findteam.aliwukong.imkit.session.model.SingleSession;
import cn.sdu.online.findteam.share.DemoUtil;

/**
 * Created by wn on 2015/8/14.
 */
@Singleton
public class SessionServiceFacade {

    @Inject
    ConversationService mConversationService;

    @Inject
    ChatMessageFactory mChatMessageFactory;

    @Inject
    UserService mUserService;

    public SessionServiceFacade(){
        this.mConversationService = IMEngine.getIMService(ConversationService.class);
        this.mChatMessageFactory = new ChatMessageFactory();
        this.mUserService = IMEngine.getIMService(UserService.class);
    }

    public volatile static String mCurrentConversationId;//TODO 需要修改设计

    public static boolean isInConversation(String id) {
        if (TextUtils.isEmpty(id)) {
            return false;
        }
        return id.equals(mCurrentConversationId);

    }

    public Session buildSession(Conversation source) {
        Session result = null;
        switch (source.type()) {
            case Conversation.ConversationType.CHAT:
                result = new SingleSession(source);
                break;
            case Conversation.ConversationType.GROUP:
                result = new GroupSession(source);
                break;
        }
        if (result != null) {
            result.setServiceFacade(this);
            result.setCurrentUserId(DemoUtil.currentOpenId());
        }
        return result;
    }

    private List<Session> buildSessions(List<Conversation> conversations) {
        return Functional.each(conversations, new Functional.Func<Conversation, Session>() {

            @Override
            public Session func(Conversation source) {
                return buildSession(source);
            }
        });
    }

    public SessionServiceFacade listSessions(int size, final Callback<List<Session>> callback) {
        mConversationService.listConversations(new Callback<List<Conversation>>() {
            @Override
            public void onSuccess(List<Conversation> conversations) {
                List<Session> sessions = buildSessions(conversations);
                callback.onSuccess(sessions);
            }

            @Override
            public void onException(String code, String reason) {
                callback.onException(code, reason);
            }

            @Override
            public void onProgress(List<Conversation> conversations, int i) {
                callback.onProgress(null, i);
            }


        }, size, Conversation.ConversationType.CHAT | Conversation.ConversationType.GROUP);
        return this;
    }

    public void getUserByOpenId(Callback callback, long openId) {
        mUserService.getUser(callback, openId);
    }

    public String getSessionContent(Conversation conversation) {
        Message lastMessage = conversation.latestMessage();
        if (lastMessage == null) {
            return "";
        }
        ChatMessage chatMessage = mChatMessageFactory.create(lastMessage);
        if (chatMessage == null) {
            return "";
        }
        return chatMessage.getMessageContent();
    }

    public SessionServiceFacade remove(String id) {
        mConversationService.removeConversations(new Callback<Void>() {

            @Override
            public void onSuccess(Void aVoid) {

            }

            @Override
            public void onException(String s, String s2) {

            }

            @Override
            public void onProgress(Void aVoid, int i) {

            }
        }, id);
        return this;
    }

    public SessionServiceFacade onRemoved(final Functional.Action<String> action) {
        mConversationService.addConversationListener(new ConversationListener() {
            @Override
            public void onAdded(List<Conversation> list) {
            }

            @Override
            public void onRemoved(List<Conversation> list) {
                Conversation conversation = list.get(0);
                if (action != null && conversation != null) {
                    action.action(conversation.conversationId());
                }
            }
        });
        Log.v("ididididididd", "onRemoved "+SessionServiceFacade.mCurrentConversationId);
        return this;
    }

    public SessionServiceFacade onCreated(final Functional.Action<List<Session>> action) {
        mConversationService.addConversationListener(new ConversationListener() {
            @Override
            public void onAdded(List<Conversation> list) {
                doActionForSessionList(list, action);
            }

            /**
             * 会话删除
             */
            @Override
            public void onRemoved(List<Conversation> list) {

            }
        });
        Log.v("ididididididd", "oncreate " + SessionServiceFacade.mCurrentConversationId);
        return this;
    }

//    public SessionServiceFacade onReceiveMessage(final Action<List<Session>> action) {
//        mMessageService.addMessageListener(new MessageListener() {
//            @Override
//            public void onAdded(List<Message> messages) {
//                List<Conversation> conversations = new ArrayList<Conversation>();
//                for (Message msg : messages) {
//                    if(msg.senderId()==getCurrentUserId()){
//                        continue;
//                    }
//                    Conversation conversation = msg.conversation();
//                    if(conversation==null){
//                        return;
//                    }
//                    if(!isInConversation(conversation.conversationId())) {
//                        conversation.addUnreadCount(1);//新消息会话未读需要加一
//                    }
//                    conversations.add(conversation);
//                }
//                doActionForSessionList(conversations,action);
//            }
//
//            @Override
//            public void onRemoved(List<Message> messages) {
//
//            }
//
//            @Override
//            public void onChanged(List<Message> messages) {
//
//            }
//        });
//        return this;
//    }


    public SessionServiceFacade onUnreadCountChange(final Functional.Action<List<Session>> action) {
        mConversationService.addConversationChangeListener(new ConversationUpdateListener() {
            @Override
            public void onUnreadCountChanged(List<Conversation> list) {
                doActionForSessionList(list, action);
            }
        });
        Log.v("ididididididd", "unreadcountchange " + SessionServiceFacade.mCurrentConversationId);
        return this;
    }

    public SessionServiceFacade onContentChange(final Functional.Action<List<Session>> action) {
        mConversationService.addConversationChangeListener(new ConversationUpdateListener() {
            @Override
            public void onLatestMessageChanged(List<Conversation> list) {
                doActionForSessionList(list, action);
            }
        });
        Log.v("ididididididd", "contentchange " + SessionServiceFacade.mCurrentConversationId);
        return this;
    }

    //todo:会调用两次：回调一次，push一次
    public SessionServiceFacade onTopChange(final Functional.Action<List<Session>> action) {
        mConversationService.addConversationChangeListener(new ConversationUpdateListener() {
            @Override
            public void onTopChanged(List<Conversation> list) {
                doActionForSessionList(list, action);
            }
        });
        Log.v("ididididididd", "topchange " + SessionServiceFacade.mCurrentConversationId);
        return this;
    }

    private void doActionForSessionList(List<Conversation> list, Functional.Action<List<Session>> action) {
        List<Session> sessions = buildSessions(list);
        if (sessions.size() > 0 && action != null) {
            action.action(sessions);
        }
    }
}

