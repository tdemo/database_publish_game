package cn.sdu.online.findteam.aliwukong.imkit.chat.model;

import android.content.Context;

import com.alibaba.wukong.im.MessageContent;

import cn.sdu.online.findteam.aliwukong.imkit.base.ViewHolder;
import cn.sdu.online.findteam.aliwukong.imkit.chat.model.ChatMessage;
import cn.sdu.online.findteam.aliwukong.imkit.chat.viewholder.SysmsgViewHolder;
import cn.sdu.online.findteam.aliwukong.imkit.route.Router;

/**
 * Created by wn on 2015/8/14.
 */
@Router({SysmsgViewHolder.class})
public class SysmsgMessage extends ChatMessage {

    @Override
    public void showChatMessage(Context context, ViewHolder holder) {
        SysmsgViewHolder viewHolder = (SysmsgViewHolder)holder;
        viewHolder.chatting_sysmsg_tv.setText(getMessageContent());
    }

    public String getMessageContent() {
        MessageContent.TextContent msgContent = (MessageContent.TextContent) mMessage.messageContent();
        return msgContent.text();
    }
}
