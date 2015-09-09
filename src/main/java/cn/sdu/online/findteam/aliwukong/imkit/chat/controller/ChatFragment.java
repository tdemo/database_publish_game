package cn.sdu.online.findteam.aliwukong.imkit.chat.controller;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.alibaba.wukong.Callback;
import com.alibaba.wukong.im.Conversation;
import com.alibaba.wukong.im.Message;
import com.alibaba.wukong.im.MessageListener;
import com.alibaba.wukong.im.MessageService;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import cn.sdu.online.findteam.R;
import cn.sdu.online.findteam.aliwukong.imkit.base.ListAdapter;
import cn.sdu.online.findteam.aliwukong.imkit.base.ListFragment;
import cn.sdu.online.findteam.aliwukong.imkit.business.ChatMessageFactory;
import cn.sdu.online.findteam.aliwukong.imkit.business.SessionServiceFacade;
import cn.sdu.online.findteam.aliwukong.imkit.chat.model.ChatMessage;
import cn.sdu.online.findteam.share.DemoUtil;
import cn.sdu.online.findteam.util.AndTools;

/**
 * Created by wn on 2015/8/14.
 */
public class ChatFragment extends ListFragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "ChatFragment";
    private static final int PAGE_NUM = 10;  //没次刷新的消息数

    @Inject
    ChatMessageFactory mChatMessageFactory;
    @Inject
    MessageService mMessageService;

    private Handler handler;
    private boolean isRefresh = false;      //是否刷新中
    private boolean isAllLoaded = false;    //是否加载完所有历史消息
    private ChatAdapter mChatAdapter;
    private Conversation mCurrentConversation;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        initSwipeLayout(mFragmentView);
        return mFragmentView;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.chat_list;
    }

    @Override
    public ListView findListView(View fragmentView) {
        ListView lv = (ListView) fragmentView.findViewById(R.id.chat_list);
        return lv;
    }

    @Override
    public ListAdapter buildAdapter(Activity activity) {
        mChatAdapter = new ChatAdapter(activity);
        return mChatAdapter;
    }

    public void setCurrentConversation(Conversation conversation) {//TODO 修改Fragment赋值方式
        mCurrentConversation = conversation;
        if (conversation != null) {
            SessionServiceFacade.mCurrentConversationId = conversation.conversationId();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //加载首屏消息
        loadData();
        //注册消息监听器
        registerMessageListener();
    }

    /**
     * 初始化 SwipeRefreshLayout 的view和颜色，颜色可以自己根据Activity当前的主题自由设置
     */
    protected void initSwipeLayout(View parent) {
        handler = new Handler();
        swipeRefreshLayout = (SwipeRefreshLayout) parent.findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.white, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);
    }

    private void bindMessageListView(List<ChatMessage> messages) {
        for (ChatMessage item : messages) {
            item.setParentListView(mListView);
        }
    }

    private void registerMessageListener() {
        mMessageService.addMessageListener(new MessageListener() {
            @Override
            public void onAdded(List<Message> list, DataType dataType) {
                List<ChatMessage> chatMessages = mChatMessageFactory.createList(list);
                List<ChatMessage> needAdd = new ArrayList<ChatMessage>();
                for (ChatMessage item : chatMessages) {
                    if (item.getConversationId().equals(mCurrentConversation.conversationId())) {
                        needAdd.add(item);
                    }
                }
                if (needAdd.size() > 0) {
                    mChatAdapter.addChatMessage(needAdd);
                    bindMessageListView(needAdd);
                }
                scrollToBottom();
            }

            @Override
            public void onChanged(List<Message> list) {
                //已读状态处理
                List<ChatMessage> chatMessages = mChatMessageFactory.createList(list);
                List<ChatMessage> needUpdate = new ArrayList<ChatMessage>();
                for (ChatMessage item : chatMessages) {
                    if (item.getConversationId().equals(mCurrentConversation.conversationId())
                            && item.getSenderId() == DemoUtil.currentOpenId()) {
                        needUpdate.add(item);
                    }
                }
                if (needUpdate.size() > 0) {
                    mChatAdapter.updateChatMessage(needUpdate);
                }
            }


            @Override
            public void onRemoved(List<Message> list) {
            }
        });
    }

    private void loadData() {
        mCurrentConversation.listPreviousMessages(null, PAGE_NUM, new Callback<List<Message>>() {
            @Override
            public void onSuccess(List<Message> messages) {
                if (messages != null && messages.size() > 0) {
//                    Collections.sort(messages, new Comparator<Message>() {
//                        @Override
//                        public int compare(Message lhs, Message rhs) {
//                            return lhs.createdAt() > rhs.createdAt() ? 1 : -1;
//                        }
//                    });
                    List<ChatMessage> list = mChatMessageFactory.createList(messages);
                    bindMessageListView(list);
                    mChatAdapter.setList(list);
                    scrollToBottom();
                }
            }

            @Override
            public void onException(String code, String reason) {
                DemoUtil.dismissProgressDialog();
            }

            @Override
            public void onProgress(List<Message> messages, int i) {
            }
        });
    }

    private void scrollToBottom() {
        mListView.setSelection(mChatAdapter.getCount() - 1);
    }


    public void onMessageReceive(ChatMessage chatMessage) {
        mChatAdapter.addChatMessage(chatMessage);
    }

    public void onMessageSended(ChatMessage chatMessage) {
        mChatAdapter.addChatMessage(chatMessage);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SessionServiceFacade.mCurrentConversationId = null;
        ChatWindowManager.getInstance().exitChatWindow(mCurrentConversation.conversationId());
    }

    private void refreshData(Message cursorMessage) {
        //设置新加item的时候不要自动滚动到底部显示新项
        mListView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_DISABLED);
        mCurrentConversation.listPreviousMessages(cursorMessage, PAGE_NUM, new Callback<List<Message>>() {
            @Override
            public void onSuccess(List<Message> messages) {
                if (messages != null && messages.size() > 0) {
                    List<ChatMessage> list = mChatMessageFactory.createList(messages);
                    bindMessageListView(list);
                    mChatAdapter.addChatMessageFront(list);
                    isAllLoaded = false;
                } else {
                    isAllLoaded = true;
                }
                isRefresh = false;
                swipeRefreshLayout.setRefreshing(false);
                mListView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_NORMAL);
/*                mListView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);*/
            }

            @Override
            public void onException(String code, String reason) {
                AndTools.showToast(getActivity(), "刷新失败");
                mListView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_NORMAL);
/*                mListView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);*/
            }

            @Override
            public void onProgress(List<Message> messages, int i) {
            }
        });
    }

    @Override
    public void onRefresh() {
        if (!isRefresh) {
            isRefresh = true;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (isAllLoaded) {    //如果已经加载完所有消息就不需要在刷新数据了
                        isRefresh = false;
                        swipeRefreshLayout.setRefreshing(false);
                    } else {
                        refreshData(((ChatMessage) mChatAdapter.getItem(0)).getMessage());
                    }
                }
            }, 500);
        }
    }
}
