package cn.sdu.online.findteam.aliwukong.imkit.chat.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.ImageButton;

import com.alibaba.doraemon.Doraemon;
import com.alibaba.doraemon.audio.AudioMagician;
import com.alibaba.doraemon.audio.OnPlayListener;
import com.alibaba.wukong.im.MessageContent;

import java.util.HashMap;
import java.util.Map;

import cn.sdu.online.findteam.R;
import cn.sdu.online.findteam.aliwukong.imkit.base.ItemClick;
import cn.sdu.online.findteam.aliwukong.imkit.base.ViewHolder;
import cn.sdu.online.findteam.aliwukong.imkit.chat.viewholder.AudioSendViewHolder;
import cn.sdu.online.findteam.aliwukong.imkit.route.Router;
import cn.sdu.online.findteam.share.MyApplication;
import cn.sdu.online.findteam.util.AndTools;

/**
 * Created by wn on 2015/8/14.
 */
@Router({AudioSendViewHolder.class})
public class AudioSendMessage extends SendMessage implements ItemClick.OnItemClickListener{
    private static AudioMagician mAudioMagician;
    private static BitmapDrawable mPalyDrawable;
    private static BitmapDrawable mStopDrawable;
    private static final String LAST_MESSAGE_SHOW_CONTENT = "[语音]";
    private static final Map<String,ImageButton> audioPlayingMap = new HashMap<String, ImageButton>();

    public AudioSendMessage() {
        if(mAudioMagician == null) {
            mAudioMagician = (AudioMagician) Doraemon.getArtifact(AudioMagician.AUDIO_ARTIFACT);
        }
    }

    @Override
    public void showChatMessage(Context context, ViewHolder holder) {
        super.showChatMessage(context,holder);
        displayAudioContent(context, (AudioSendViewHolder) holder);
    }

    private void initDrawable(Context context){
        if(mPalyDrawable == null) {
            Bitmap bmpPlay = BitmapFactory.decodeResource(context.getResources(), R.drawable.btn_play_left);
            mPalyDrawable = new BitmapDrawable(bmpPlay);
            Bitmap bmpStop = BitmapFactory.decodeResource(context.getResources(), R.drawable.btn_stop_left);
            mStopDrawable = new BitmapDrawable(bmpStop);
        }
    }

    /**
     * 显示语音消息内容以及给播放控件添加监听事件
     * @param context
     * @param viewHolder
     */
    private void displayAudioContent(final Context context,final AudioSendViewHolder viewHolder) {
        initDrawable(context);
        final MessageContent.AudioContent messageContent = (MessageContent.AudioContent)mMessage.messageContent();

        //语音长度显示
        viewHolder.chatting_audio_length.setText(
                context.getString(R.string.audio_duration, (messageContent.duration() / 1000))
        );

        //显示播放/停止按钮
        ImageButton button = audioPlayingMap.get(messageContent.url());
        if(button != null){
            viewHolder.chatting_play_pause_btn.setBackground(mStopDrawable);
            if(button != viewHolder.chatting_play_pause_btn){
                audioPlayingMap.put(messageContent.url(),viewHolder.chatting_play_pause_btn);
            }
        }else{
            viewHolder.chatting_play_pause_btn.setBackground(mPalyDrawable);
        }

        //给播放/停止按钮添加点击事件
        viewHolder.chatting_play_pause_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playAudio(context,viewHolder.chatting_play_pause_btn,messageContent.url());
            }
        });
    }

    /**
     * 点击播放语音
     * @param sender
     * @param view
     * @param position
     */
    @Override
    public void onClick(Context sender, View view, int position) {
        MessageContent.AudioContent messageContent = (MessageContent.AudioContent)mMessage.messageContent();
        ImageButton playBtn = (ImageButton) view.findViewById(R.id.btn_play_pause);
        playAudio(sender,playBtn,messageContent.url());
    }

    /**
     * 播放语音
     * @param context
     * @param imageButton
     * @param url
     */
    private void playAudio(final Context context,final ImageButton imageButton,String url){
        ImageButton button = audioPlayingMap.get(url);
        if(button == null){
            audioPlayingMap.put(url, imageButton);
            mAudioMagician.play(url, listener);
        }else{
            mAudioMagician.stop(url);
        }
    }

    /**
     * 语音播放监听器，切换播放按钮图标
     */
    private static final OnPlayListener listener = new OnPlayListener() {
        @Override
        public void onPlayStateListener(String url, int state) {
            ImageButton imageBtn = audioPlayingMap.get(url);
            switch (state) {
                case OnPlayListener.PLAY_START:
                case OnPlayListener.PLAY_RESUMED:
                    imageBtn.setBackground(mStopDrawable);
                    break;
                case OnPlayListener.PLAY_PAUSED:
                case OnPlayListener.PLAY_STOPED:
                case OnPlayListener.PLAY_COMPLEMENTED:
                    imageBtn.setBackground(mPalyDrawable);
                    audioPlayingMap.remove(url);
                    break;
            }
        }

        @Override
        public void onPlayErrorListener(String url, int error) {
            AndTools.showToast(MyApplication.getInstance(),
                    MyApplication.getInstance().getString(R.string.audio_play_err));
        }

        @Override
        public void onProgressListener(String url, int pos, int duration) {
        }
    };

    public String getMessageContent() {
        return LAST_MESSAGE_SHOW_CONTENT;
    }
}

