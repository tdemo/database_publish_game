package cn.sdu.online.findteam.aliwukong.imkit.chat.model;

import android.content.Context;

import com.alibaba.wukong.im.MessageContent;

import cn.sdu.online.findteam.aliwukong.imkit.base.ViewHolder;
import cn.sdu.online.findteam.aliwukong.imkit.chat.viewholder.TextReceiveViewHolder;
import cn.sdu.online.findteam.aliwukong.imkit.route.Router;

@Router({TextReceiveViewHolder.class})
public class TextReceiveMessage extends ReceiveMessage {

    @Override
    public void showChatMessage(Context context, ViewHolder holder) {
        super.showChatMessage(context,holder);
        TextReceiveViewHolder viewHolder = (TextReceiveViewHolder) holder;
        viewHolder.chatting_content_tv.setText(getMessageContent());
    }

    public String getMessageContent() {
        MessageContent.TextContent msgContent = (MessageContent.TextContent) mMessage.messageContent();
        return msgContent.text();
    }
}
