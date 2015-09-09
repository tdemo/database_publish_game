package cn.sdu.online.findteam.aliwukong.imkit.chat.viewholder;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import cn.sdu.online.findteam.R;

/**
 * Created by wn on 2015/8/14.
 */
public class AudioSendViewHolder extends SendViewHolder {
    public TextView chatting_audio_length;      //语音长度
    public ImageButton chatting_play_pause_btn; //语音播放/暂停按钮

    @Override
    protected void initChatView(View view) {
        chatting_play_pause_btn = (ImageButton) view.findViewById(R.id.btn_play_pause);
        chatting_audio_length = (TextView) view.findViewById(R.id.tv_audio_length);
    }

    /**
     * 设置当前的layout资源
     */
    @Override
    protected int getLayoutId() {
        return R.layout.chat_item_audio_send;
    }
}
