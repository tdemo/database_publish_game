package cn.sdu.online.findteam.aliwukong.imkit.chat.viewholder;

import android.view.View;

import cn.sdu.online.findteam.R;
import cn.sdu.online.findteam.aliwukong.imkit.widget.MakeupImageView;

public class ImageSendViewHolder extends SendViewHolder {
    //图片消息内容显示控件
    public MakeupImageView chatting_content_iv;

    @Override
    protected void initChatView(View view) {
        chatting_content_iv = (MakeupImageView) view.findViewById(R.id.chatting_content_v);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.chat_item_image_send;
    }
}
