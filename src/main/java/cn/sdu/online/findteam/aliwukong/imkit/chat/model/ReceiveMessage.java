package cn.sdu.online.findteam.aliwukong.imkit.chat.model;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ListView;

import cn.sdu.online.findteam.aliwukong.avatar.AvatarMagicianImpl;
import cn.sdu.online.findteam.aliwukong.imkit.base.ViewHolder;
import cn.sdu.online.findteam.aliwukong.imkit.chat.viewholder.ReceiveViewHolder;
import cn.sdu.online.findteam.aliwukong.user.UserProfileActivity;

/**
 * Created by wn on 2015/8/14.
 */
public class ReceiveMessage extends ChatMessage{

    @Override
    public void showChatMessage(Context context, ViewHolder holder) {
        //显示头像
        showAvatar(context,(ReceiveViewHolder)holder);

        //置未读消息为读状态
        if(!mMessage.iHaveRead()) {
            readMessage();
        }
    }

    /**
     * 显示消息发送者头像
     * @param context
     * @param holder
     */
    public void showAvatar(final Context context,ReceiveViewHolder holder){
        AvatarMagicianImpl.getInstance().setUserAvatar(holder.chatting_avatar,mMessage.senderId(),(ListView)(holder.parentView));
        holder.chatting_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UserProfileActivity.class);
                intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("user_open_id",mMessage.senderId());
                context.startActivity(intent);
            }
        });
    }
}
