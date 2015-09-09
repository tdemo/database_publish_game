package cn.sdu.online.findteam.aliwukong.imkit.chat.model;

import android.content.Context;
import android.view.View;
import android.widget.AbsListView;

import com.alibaba.wukong.im.Message;

import cn.sdu.online.findteam.aliwukong.imkit.base.DisplayListItem;
import cn.sdu.online.findteam.aliwukong.imkit.base.ViewHolder;
import cn.sdu.online.findteam.aliwukong.imkit.chat.viewholder.ChatViewHolder;
import cn.sdu.online.findteam.aliwukong.imkit.widget.DateUtil;
import cn.sdu.online.findteam.share.DemoUtil;

/**
 * Created by wn on 2015/8/14.
 */
public abstract class ChatMessage implements DisplayListItem<ChatViewHolder> {

    public static final String DOMAIN_CATEGORY = "chat_message";

    public static final long GROUP_DURATION = 15 * 60 * 1000;

    protected Message mMessage;//核心message 类

    protected Message mPreMessage;//上一条message .如果没有则为null

    protected AbsListView parentListView = null; //父类ListView

    public void setMessage(Message message) {
        mMessage = message;
    }

    public Message getMessage() {
        return mMessage;
    }

    public String getId() {
//        return mMessage.messageId()+"";
        return mMessage.localId() + "";
    }

    public long getMessageId() {
        return mMessage.messageId();
    }

    public int getUnreadCount() {
        return mMessage.unReadCount();
    }

    public boolean isReceive() {
        return mMessage.senderId() == DemoUtil.currentOpenId();
    }

    public void setPreMessage(ChatMessage message) {
        mPreMessage = message.mMessage;
    }

    public long getSenderId() {
        return mMessage.senderId();
    }

    public String getConversationId() {
        return mMessage.conversation().conversationId();
    }

    protected void readMessage() {
        mMessage.read();
    }

    protected int getConversationType() {
        return mMessage.conversation().type();
    }

    @Override
    public void onShow(Context context, ChatViewHolder viewHolder, String tag) {
        if (isShowCreateTime(this)) {
            viewHolder.tv_overlay.setVisibility(View.VISIBLE);
            viewHolder.chatting_time_tv.setText(DateUtil.formatRimetShowTime(context, mMessage.createdAt(), true));
        } else {
            viewHolder.tv_overlay.setVisibility(View.GONE);
        }
        showChatMessage(context, viewHolder);
    }


    public abstract void showChatMessage(Context context, ViewHolder holder);

    public String getMessageContent() {
        return "";
    }

    public static boolean isShowCreateTime(ChatMessage chatMessage) {
        boolean isShow = false;
        Message tmp = chatMessage.mPreMessage;
        if (null != tmp) {
            Long preT = chatMessage.mMessage.createdAt() - tmp.createdAt();
            isShow = Math.abs(preT) >= GROUP_DURATION;
        }
        return isShow;
    }

    public AbsListView getParentListView() {
        return this.parentListView;
    }

    public void setParentListView(AbsListView lv) {
        this.parentListView = lv;
    }


    /**
     * 当需要指定多个viewholder时，根据对象实例返回对应的Viewholder 索引 单个ViewHolder时返回0 当一个 DisplayListItem 对象 指向多个
     * ViewHolder,需要要根据Model 的值指定 返回的ViewHolder 对象的 索引值， ViewHolder 的索引值对应 @{link Router target}里的索引
     * 如 <code>
     *
     * @Router({ TextViewHolder.class AudioViewholder.class }) public class Model implements
     * DisplayListItem<MastViewHolder>{
     * <p/>
     * } </code> <br/> 上面例子中， TextViewHolder 索引为 0 AudioViewHolder 索引为 1
     */
    @Override
    public int getViewType() {
        return 0;
    }
}
