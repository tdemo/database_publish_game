package cn.sdu.online.findteam.aliwukong.imkit.session.model;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.wukong.im.Conversation;
import com.alibaba.wukong.im.Message;

import cn.sdu.online.findteam.aliwukong.imkit.base.DisplayListItem;
import cn.sdu.online.findteam.aliwukong.imkit.base.ItemMenuAdapter;
import cn.sdu.online.findteam.aliwukong.imkit.base.ListItemDelete;
import cn.sdu.online.findteam.aliwukong.imkit.business.SessionServiceFacade;
import cn.sdu.online.findteam.aliwukong.imkit.session.SessionViewHolder;

/**
 * Created by wn on 2015/8/14.
 */
public abstract class Session extends ProxySession implements DisplayListItem<SessionViewHolder>
       /* ItemMenuAdapter.onMenuListener, ListItemDelete*/ {

    public static final String DOMAIN_CATEGORY = "session_list";

    public static final int CONTEXT_DELETE_ID = Menu.FIRST + 1;

    public static final int CONTEXT_TOP_OPERATION = Menu.FIRST + 2;

    public static final String SESSION_INTENT_KEY = "session_conversation";

    protected transient SessionServiceFacade mServiceFacade;

    protected long mCurrentUserId;

    public Session(Conversation conversation) {
        super(conversation);
    }

    public void setServiceFacade(SessionServiceFacade value) {
        mServiceFacade = value;
    }

    public void setCurrentUserId(long userId) {
        mCurrentUserId = userId;
    }

    /**
     * 获取Item的唯一标识
     */
    public String getId() {
        return conversationId();
    }

    /**
     * 获取会话的透出内容
     */
//    public String getContent() {
//        return mServiceFacade.getSessionContent(this);
//    }

    /**
     * 设置会话的透出内容
     *
     * @param contentView
     */
    public abstract void setSessionContent(TextView contentView);

    public boolean hasDraft() {
        return !TextUtils.isEmpty(draftMessage());
    }


    public long getLastMessageCreateTime() {
        if (latestMessage() == null) {
            return createdAt();
        }
        return latestMessage().createdAt();
    }

    @Override
    public void onShow(Context context, SessionViewHolder viewHolder, String tag) {
        if (TextUtils.isEmpty((tag))) {

            refreshAll(context, viewHolder);

        } else if ("unread".equals(tag)) {
            refreshUnreadCount(viewHolder);
        } else if ("content".equals(tag)) {
            refreshContent(viewHolder);
        }
    }

    private void refreshAll(Context context, SessionViewHolder viewHolder) {
        refreshTitle(viewHolder);
        refreshTime(viewHolder);
        refreshUnreadCount(viewHolder);
        refreshSilence(viewHolder);
        refreshContent(viewHolder);
        refreshAvatar(context, viewHolder);
    }

    protected void refreshTitle(SessionViewHolder viewHolder) {
        viewHolder.sessionTitleTxt.setText(title());
    }

    protected void refreshTime(SessionViewHolder viewHolder) {
        viewHolder.showTime(getLastMessageCreateTime());
    }

    protected void refreshSilence(SessionViewHolder viewHolder) {
        int silenceImgVisibility = isNotificationEnabled() ? View.GONE : View.VISIBLE;
        viewHolder.sessionSilenceImgView.setVisibility(silenceImgVisibility);
    }

    protected void refreshUnreadCount(SessionViewHolder viewHolder) {
        viewHolder.showSessionUnread(unreadMessageCount());
    }

    /**
     * 加载消息内容
     */
    protected void refreshContent(SessionViewHolder viewHolder) {
        if (hasDraft()) { // 有草稿时优先展示草稿信息
            viewHolder.setDraft(draftMessage());
        } else {  // 无草稿，展示最后一条信息
            if (latestMessage() == null || latestMessage().messageContent() == null) {
                viewHolder.sessionContentTxt.setText("");
                return;
            }
            //显示状态之前应该处理一下是否是发送中的消息
            Message msg = latestMessage();
            // 发送状态
            if (msg.status() == Message.MessageStatus.OFFLINE) {
                viewHolder.showSessionStatusFail();
            } else if (msg.status() == Message.MessageStatus.SENDING) {
                viewHolder.showSessionStatusSending();
            } else {
                viewHolder.mMessageStatus.setVisibility(View.GONE);
            }
//            viewHolder.sessionContentTxt.setText(getContent());
            setSessionContent(viewHolder.sessionContentTxt);
        }
    }

    protected void refreshAvatar(Context context, SessionViewHolder viewHolder) {
        int iconVisibility = TextUtils.isEmpty(icon()) ? View.GONE : View.VISIBLE;
        viewHolder.sessionIconView.setVisibility(iconVisibility);
        showAvatar(context, icon(), viewHolder.sessionIconView, (ListView) (viewHolder.parentView));
    }

    public abstract void showAvatar(Context context, String mediaIds, View view, ListView itemParent);

/*    @Override
    public void onCreateMenu(Context context, ContextMenu menu) {
        menu.setHeaderTitle(title());
        menu.add(0, CONTEXT_DELETE_ID, 0, "删除会话");
        menu.add(0, CONTEXT_TOP_OPERATION, 0, "会话置顶");
    }*/

/*    @Override
    public boolean onMenuItemSelected(Context context, int itemId) {
        switch (itemId) {
            case CONTEXT_DELETE_ID:     //删除会话
*//*                this.onDelete(context); // 直接删除，不再弹确认框*//*
                break;
            case CONTEXT_TOP_OPERATION: //会话置顶
*//*                stayOnTop(true, null);*//*
                break;
        }
        return false;
    }*/

/*    @Override
    public void onDelete(Context context) {
        mServiceFacade.remove(getId());
    }*/

    @Override
    public int compareTo(Conversation another) {
        if (another == null) {
            return -1;
        }
        long old = another.latestMessage() == null ? another.createdAt()
                : another.latestMessage().createdAt();

        long ret = getLastMessageCreateTime() - old;
        return ret < 0 ? 1 : (ret > 0 ? -1 : 0);
    }

    /**
     * 如果是置顶会话，数值越大，越靠前，不是置顶的话比较lastMessage创建时间
     *
     * @param another
     * @return this比another靠前的话返回-1，靠后则返回1，相等则返回0
     */
    public int compareTo(Session another) {
        if (another == null) {
            return -1;
        }

        long ret = 0;
        long indexL = getTop();
        long indexR = another.getTop();

        if (indexL > 0 || indexR > 0) {
            ret = indexL - indexR;
            return ret < 0 ? 1 : (ret > 0 ? -1 : 0);
        } else {
            ret = getLastMessageCreateTime() - another.getLastMessageCreateTime();
            return ret < 0 ? 1 : (ret > 0 ? -1 : 0);
        }
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

