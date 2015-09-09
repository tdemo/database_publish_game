package cn.sdu.online.findteam.aliwukong.imkit.chat.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import cn.sdu.online.findteam.R;

/**
 * 接收的消息类型需继承该SendViewHolder
 */

public abstract class ReceiveViewHolder extends ChatViewHolder {
    public ImageView chatting_avatar;  //发送者头像
    public TextView chatting_title;    //消息标题(预留备用)

    /**
     * 初始化试图组件
     */
    @Override
    protected void initView(View view) {
        super.initView(view);
        chatting_title = (TextView) view.findViewById(R.id.chatting_title);
        chatting_avatar = (ImageView) view.findViewById(R.id.chatting_avatar);

        initChatView(view);
    }

    protected abstract void initChatView(View view);
}
