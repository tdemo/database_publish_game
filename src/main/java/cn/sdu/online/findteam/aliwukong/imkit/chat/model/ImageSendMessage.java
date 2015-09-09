package cn.sdu.online.findteam.aliwukong.imkit.chat.model;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ListView;

import com.alibaba.doraemon.Doraemon;
import com.alibaba.doraemon.image.ImageMagician;
import com.alibaba.wukong.im.MessageContent;

import cn.sdu.online.findteam.aliwukong.imkit.base.ViewHolder;
import cn.sdu.online.findteam.aliwukong.imkit.chat.controller.ImageShowerActivity;
import cn.sdu.online.findteam.aliwukong.imkit.chat.viewholder.ImageSendViewHolder;
import cn.sdu.online.findteam.aliwukong.imkit.route.Router;

/**
 * Created by wn on 2015/8/14.
 */
@Router({ImageSendViewHolder.class} )
public class ImageSendMessage extends SendMessage {
    private static ImageMagician imageMagician;
    private static final String LAST_MESSAGE_SHOW_CONTENT = "[图片]";

    public ImageSendMessage() {
        if(imageMagician == null) {
            imageMagician = (ImageMagician) Doraemon.getArtifact(ImageMagician.IMAGE_ARTIFACT);
        }
    }

    @Override
    public void showChatMessage(Context context, ViewHolder holder) {
        super.showChatMessage(context,holder);
        displayImageContent(context,(ImageSendViewHolder) holder);
    }

    /**
     * 显示图片消息内容
     * @param context
     */
    private void displayImageContent(final Context context,ImageSendViewHolder viewHolder) {
        MessageContent.ImageContent messageContent = (MessageContent.ImageContent)mMessage.messageContent();
        imageMagician.setImageBackground(viewHolder.chatting_content_iv, messageContent.url(), (ListView)(viewHolder.parentView));
        viewHolder.chatting_content_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ImageShowerActivity.class);
                intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(ImageShowerActivity.CONV_INTENT_KKEY,mMessage.conversation());
                intent.putExtra(ImageShowerActivity.MID_INTENT_KKEY,mMessage.messageId());
                context.startActivity(intent);
            }
        });
    }

    /**
     * 用于会话列表lastmessage内容的显示
     * @return
     */
    public String getMessageContent() {
        return LAST_MESSAGE_SHOW_CONTENT;
    }
}
