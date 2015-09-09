package cn.sdu.online.findteam.aliwukong.imkit.chat.viewholder;

import android.view.View;
import android.widget.TextView;

import cn.sdu.online.findteam.R;

public class SysmsgViewHolder extends ChatViewHolder {
    public TextView chatting_sysmsg_tv; //系统消息内容

    /**
     * 初始化视图组件
     */
    @Override
    protected void initView(View view) {
        super.initView(view);
        chatting_sysmsg_tv = (TextView) view.findViewById(R.id.chatting_sysmsg_tv);
    }

    /**
     * 设置当前的layout资源
     */
    @Override
    protected int getLayoutId() {
        return R.layout.chat_item_text_sysmsg;
    }
}
