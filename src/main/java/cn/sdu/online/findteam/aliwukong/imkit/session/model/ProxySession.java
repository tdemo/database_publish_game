package cn.sdu.online.findteam.aliwukong.imkit.session.model;

import android.util.Log;

import com.alibaba.wukong.Callback;
import com.alibaba.wukong.im.Conversation;
import com.alibaba.wukong.im.Message;

import java.util.List;
import java.util.Map;

/**
 * Created by wn on 2015/8/14.
 */
public class ProxySession implements Conversation {

    public Conversation mConversation;

    public ProxySession(Conversation conversation) {
        if(conversation==null){
            throw new NullPointerException(" conversation is null");
        }
        mConversation = conversation;
    }

    @Override
    public long createdAt() {
        return mConversation.createdAt();
    }

    @Override
    public boolean isValid() {
        return mConversation.isValid();
    }

    @Override
    public String conversationId() {
        return mConversation.conversationId();
    }

    @Override
    public void sync() {
        mConversation.sync();
    }

    @Override
    public int type() {
        return mConversation.type();
    }

    @Override
    public ConversationStatus status() {
        return mConversation.status();
    }

    @Override
    public String title() {
        return mConversation.title();
    }

    @Override
    public void updateTitle(String s, Message message, Callback<Void> voidCallback) {
        mConversation.updateTitle(s,message,voidCallback);
    }

    @Override
    public String icon() {
        return mConversation.icon();
    }

    @Override
    public void updateIcon(String s, Message message, Callback<Void> voidCallback) {
        mConversation.updateIcon(s, message, voidCallback);
    }

    @Override
    public Message latestMessage() {
        return mConversation.latestMessage();
    }

    @Override
    public int unreadMessageCount() {
        return mConversation.unreadMessageCount();
    }

    @Override
    public void resetUnreadCount() {
        mConversation.resetUnreadCount();
    }

    @Override
    public void addUnreadCount(int i) {
        mConversation.addUnreadCount(i);
    }

    @Override
    public String draftMessage() {
        return mConversation.draftMessage();
    }

    @Override
    public void updateDraftMessage(String s) {
        mConversation.updateDraftMessage(s);
    }

    @Override
    public long tag() {
        return mConversation.tag();
    }

    @Override
    public void updateTag(long l) {
        mConversation.updateTag(l);
    }

    @Override
    public String extension(String s) {
        return mConversation.extension(s);
    }

    @Override
    public Map<String, String> extension() {
        return mConversation.extension();
    }

    @Override
    public void updateExtension(Map<String, String> map) {
        mConversation.updateExtension(map);
    }

    @Override
    public void updateExtension(String s, String s2) {
        mConversation.updateExtension(s,s2);
    }

    @Override
    public String privateExtension(String s) {
        return mConversation.privateExtension(s);
    }

    @Override
    public int totalMembers() {
        return mConversation.totalMembers();
    }

    @Override
    public boolean hasUnreadAtMeMessage() {
        return mConversation.hasUnreadAtMeMessage();
    }

    @Override
    public void updateAtMeStatus(boolean b) {
        mConversation.updateAtMeStatus(b);
    }

    @Override
    public Map<String, String> localExtras() {
        return mConversation.localExtras();
    }

    @Override
    public void updateLocalExtras(Map<String, String> map) {
        mConversation.updateLocalExtras(map);
    }

    @Override
    public void remove() {
        mConversation.remove();
    }

    @Override
    public void removeAndClearMessage() {
        mConversation.removeAndClearMessage();
    }

    @Override
    public void updateToVisible() {
        mConversation.updateToVisible();
    }

    @Override
    public void quit(Message message, Callback<Void> voidCallback) {
        mConversation.quit(message,voidCallback);
    }

    @Override
    public void listNextMessages(Message message, int i, Callback<List<Message>> listCallback) {
        mConversation.listNextMessages(message, i, listCallback);
    }

    @Override
    public void listPreviousMessages(Message message, int i, Callback<List<Message>> listCallback) {
        mConversation.listPreviousMessages(message, i, listCallback);
    }

    @Override
    public void listNextLocalMessages(Message message, int i,
                                      int messageContentType,
                                      Callback<List<Message>> listCallback) {
        mConversation.listNextLocalMessages(message, i, messageContentType, listCallback);
    }

    @Override
    public void listPreviousLocalMessages(Message message, int i,
                                          int messageContentType,
                                          Callback<List<Message>> listCallback) {
        mConversation.listPreviousLocalMessages(message, i, messageContentType, listCallback);
    }

    @Override
    public void listNextLocalMessages(Message message, int count, int contentType, boolean include,
                                      Callback<List<Message>> listCallback) {
        mConversation.listNextLocalMessages(message,count,contentType,include,listCallback);
    }

    @Override
    public void listPreviousLocalMessages(Message message, int count, int contentType, boolean include,
                                          Callback<List<Message>> listCallback) {
        mConversation.listPreviousLocalMessages(message,count,contentType,include,listCallback);
    }

    @Override
    public void listNextLocalMessages(Message message, int i, int i1, long l, Callback<List<Message>> callback) {

    }

    @Override
    public void listPreviousLocalMessages(Message message, int i, int i1, long l, Callback<List<Message>> callback) {

    }

    @Override
    public void getMessage(long l, Callback<Message> messageCallback) {
        mConversation.getMessage(l, messageCallback);
    }

    @Override
    public boolean isNotificationEnabled() {
        return mConversation.isNotificationEnabled();
    }

    @Override
    public void updateNotification(boolean b, Callback<Void> voidCallback) {
        mConversation.updateNotification(b, voidCallback);
    }

    @Override
    public long getTop() {
        return mConversation.getTop();
    }

    @Override
    public void stayOnTop(boolean b, Callback<Long> longCallback) {
        mConversation.stayOnTop(b, longCallback);
    }

    @Override
    public long getLastModify() {
        return mConversation.getLastModify();
    }

    @Override
    public long getOtherOpenId() {
        return mConversation.getOtherOpenId();
    }

    /**
     * Compares this object to the specified object to determine their relative order.
     *
     * @param another the object to compare to this instance.
     * @return a negative integer if this instance is less than {@code another}; a positive integer
     * if this instance is greater than {@code another}; 0 if this instance has the same order as
     * {@code another}.
     * @throws ClassCastException if {@code another} cannot be converted into something comparable
     *                            to {@code this} instance.
     */
    @Override
    public int compareTo(Conversation another) {
        return mConversation.compareTo(another);
    }
}

