package cn.sdu.online.findteam.aliwukong.imkit.chat.viewholder;

import android.view.View;
import android.widget.TextView;

import cn.sdu.online.findteam.R;

public class TextReceiveViewHolder extends ReceiveViewHolder {
    //文本消息内容
    public TextView chatting_content_tv;

    /**
     * 初始化试图组件
     *
     * @param view
     */
    @Override
    protected void initChatView(View view) {
        this.chatting_content_tv = (TextView) view.findViewById(R.id.chatting_content_tv);
    }

    /**
     * 设置当前的layout资源
     *
     * @return
     */
    @Override
    protected int getLayoutId() {
        return R.layout.chat_item_text_receive;
    }
}

