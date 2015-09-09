package cn.sdu.online.findteam.aliwukong.imkit.chat.model;

import android.content.Context;

import com.alibaba.wukong.im.MessageContent;

import cn.sdu.online.findteam.aliwukong.imkit.base.ViewHolder;
import cn.sdu.online.findteam.aliwukong.imkit.chat.viewholder.TextSendViewHolder;
import cn.sdu.online.findteam.aliwukong.imkit.route.Router;

/**
 * Created by wn on 2015/8/14.
 */
@Router({TextSendViewHolder.class})
public class TextSendMessage extends SendMessage {

    @Override
    public void showChatMessage(Context context, ViewHolder holder) {
        super.showChatMessage(context,holder);
        TextSendViewHolder viewHolder = (TextSendViewHolder) holder;
        viewHolder.chatting_content_tv.setText(getMessageContent());
    }

    public String getMessageContent() {
        MessageContent.TextContent msgContent = (MessageContent.TextContent) mMessage.messageContent();
        return msgContent.text();
    }
}
