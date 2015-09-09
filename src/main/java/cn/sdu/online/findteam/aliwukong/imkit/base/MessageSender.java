package cn.sdu.online.findteam.aliwukong.imkit.base;

import android.content.Context;

import com.alibaba.wukong.im.Conversation;

/**
 * 消息发送机，用于应用的消息发送，简化发送逻辑。<br>
 * 图片发送：<br>
 *      1、发送相册图片，提供压缩功能，应用可根据需求发送压缩后图片或原图。<br>
 *      2、拍照并发送照片，调用系统拍照接口拍照，并发送拍照后的照片<br>
 * 图片发送提供压缩和非压缩两种发送方式，当应用采用压缩发送时，图片未上传云端前，压缩图片保存在应用目录。
 * 当该消息的图片上传成功后，将调用ImageCache接口缓存图片，并删除压缩后的应用目录图片文件。
 * 普通图片压缩采用等比压缩方式，将图片宽度压缩为640像素，采用75quality jpg压缩格式。
 * 长图（宽高或高宽比大于等于3，并小边大于640）时，将不压缩图片尺寸，而采用35的quality进行压缩。<br>
 * <br>
 * 语音发送：<br>
 * 语音发送采用边录边传的方式进行，压缩算法采用amr，码率采用8500。应用调用benginAudoiRecordAndSend
 *
 * Created by bokui on 14/12/1.
 */
public interface MessageSender {
    public void init(Context context);

    /**
     * 发送相册图片，提供压缩和非压缩两种发送方式，当应用采用压缩发送时，图片未上传云端前，压缩图片保存在应用数据目录。
     * 当该消息的图片上传成功后，将调用{@link #setImageCache(Cache) setImageCache}
     * 设置的ImageCache接口缓存图片，并删除压缩后的应用目录图片文件。
     * @param url               图片本地url
     * @param conversation      在该会话内发送图片
     * @param isNeedCompress    是否需要压缩
     *
     */
    public void sendAlbumImage(String url,Conversation conversation,boolean isNeedCompress);

    /**
     * 拍照并发送照片，缓存及压缩策略同{@link #sendAlbumImage(String, com.alibaba.wukong.im.Conversation, boolean) sendAlbumImage}接口
     * @param conversation      在该会话内发送照片
     * @param context           Context实例
     * @param isNeedCompress    是否需要压缩
     */
//    public void takePhotoAndSend(Conversation conversation,Context context,boolean isNeedCompress);

    /**
     * 设置图片缓存，用于缓存发送成功后的图片。当发送原图时，将不存在图片缓存。
     * @param cache 图片缓存
     */
    public void setImageCache(Cache cache);

    /**
     * 启动语音录音并发送语音。当语音消息发送失败时，语音文件将保存在应用数据目录。当该语音消息发送成功后，
     * 调用{@link #setAudioCache(Cache) setAudioCache} 设置的AudioCache接口缓存语音，并删除应用数据目录缓存的语音文件。
     * @param conversation  在该会话内发送语音
     */
    public void benginAudioRecordAndSend(Conversation conversation) throws RuntimeException;

    /**
     * 结束语音录制发送
     */
    public void endAudioSend();

    /**
     * 取消语音发送
     */
    public void cancelAudioSend();

    /**
     * 设置音频缓存，用于缓存语音发送成功后的缓存。
     * @param cache 语言缓存
     */
//    public void setAudioCache(Cache cache);
}
