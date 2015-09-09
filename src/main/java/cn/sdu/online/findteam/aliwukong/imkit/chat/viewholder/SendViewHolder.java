package cn.sdu.online.findteam.aliwukong.imkit.chat.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import cn.sdu.online.findteam.R;

public abstract class SendViewHolder extends ChatViewHolder {
    public TextView chatting_unreadcount_tv;        //消息未读数
    public ImageView chatting_unread_icon_iv;       //未读标志
    public ImageView chatting_notsuccess_iv;        //发送出去的消息的状态
    public ProgressBar chatting_status_progress;    //消息发送中圆形进度条
    public ImageView sendChatting_avatar; // 消息发送者头像

    /**
     * 初始化试图组件
     */
    @Override
    protected void initView(View view) {
        super.initView(view);
        sendChatting_avatar = (ImageView) view.findViewById(R.id.Sendchatting_avatar);
        chatting_unread_icon_iv = (ImageView) view.findViewById(R.id.chatting_unread_icon_iv);
        chatting_unreadcount_tv = (TextView) view.findViewById(R.id.chatting_unreadcount_tv);
        chatting_notsuccess_iv = (ImageView) view.findViewById(R.id.chatting_notsuccess_iv);
        chatting_status_progress = (ProgressBar) view.findViewById(R.id.chatting_status_progress);

        initChatView(view);
    }

    protected abstract void initChatView(View view);
}
