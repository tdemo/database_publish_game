package cn.sdu.online.findteam.aliwukong.imkit.chat.controller;

import android.content.Context;

import java.util.List;

import cn.sdu.online.findteam.aliwukong.imkit.base.ListAdapter;
import cn.sdu.online.findteam.aliwukong.imkit.base.ViewHolder;
import cn.sdu.online.findteam.aliwukong.imkit.chat.model.ChatMessage;

/**
 * Created by wn on 2015/8/14.
 */
public class ChatAdapter extends ListAdapter<ChatMessage> {

    public ChatAdapter(Context context) {
        super(context);
    }

    public void removeChatMessage(long messageId) {
        for (int i = 0; i < mList.size(); i++) {
            ChatMessage message = mList.get(i);
            if (message.getMessageId() == messageId) {
                mList.remove(message);
                break;
            }
        }
        notifyDataSetChanged();
    }


    public void addChatMessage(ChatMessage chatMessage){
        mList.add(chatMessage);
        notifyDataSetChanged();
    }

    public void addChatMessage(List<ChatMessage> list){
        mList.addAll(list);
        notifyDataSetChanged();
    }

    public void addChatMessageFront(List<ChatMessage> list){
        mList.addAll(0,list);
        notifyDataSetChanged();
    }

    public void updateChatMessage(List<ChatMessage> list){
        notifyDataSetChanged(list,"");
    }

    @Override
    public long getItemId(int position) {
        return mList.get(position).getMessageId();
    }

    @Override
    protected String getDomainCategory() {
        return ChatMessage.DOMAIN_CATEGORY;
    }

    @Override
    protected void onBindView(ViewHolder viewHolder, ChatMessage item,int position) {
        int targetPosition = viewHolder.position-1;
        if (targetPosition - 1 >= 0 && targetPosition - 1 < getCount()) {
            ChatMessage preChat = (ChatMessage) getItem(targetPosition);
            item.setPreMessage(preChat);
        }
        super.onBindView(viewHolder, item,position);
    }
}
