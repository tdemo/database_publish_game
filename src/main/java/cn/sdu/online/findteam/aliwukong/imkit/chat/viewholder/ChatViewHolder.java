package cn.sdu.online.findteam.aliwukong.imkit.chat.viewholder;

import android.view.View;
import android.widget.TextView;

import cn.sdu.online.findteam.R;
import cn.sdu.online.findteam.aliwukong.imkit.base.ViewHolder;

/**
 * Created by wn on 2015/8/14.
 */
public abstract class ChatViewHolder extends ViewHolder {
    public View tv_overlay;
    public TextView chatting_time_tv;

    @Override
    protected void initView(View view) {
        tv_overlay = view.findViewById(R.id.tv_overlay);
        chatting_time_tv = (TextView) view.findViewById(R.id.chatting_time_tv);
//        initChatView(view);
    }

//    protected abstract void initChatView(View view);
}
