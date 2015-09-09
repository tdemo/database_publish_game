package cn.sdu.online.findteam.aliwukong.imkit.base;

import android.content.Context;
import android.media.MediaRecorder;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.doraemon.Doraemon;
import com.alibaba.doraemon.audio.AudioMagician;
import com.alibaba.doraemon.audio.OnRecordListener;
import com.alibaba.doraemon.threadpool.Thread;
import com.alibaba.wukong.Callback;
import com.alibaba.wukong.im.AudioStreamController;
import com.alibaba.wukong.im.Conversation;
import com.alibaba.wukong.im.IMConstants;
import com.alibaba.wukong.im.IMEngine;
import com.alibaba.wukong.im.Message;
import com.alibaba.wukong.im.MessageBuilder;
import com.alibaba.wukong.im.MessageContent;
import com.alibaba.wukong.im.MessageListener;
import com.alibaba.wukong.im.MessageService;

import java.io.File;
import java.util.List;

import cn.sdu.online.findteam.util.AndTools;
import cn.sdu.online.findteam.util.FileUtil;
import cn.sdu.online.findteam.util.PrefsUtil;
import cn.sdu.online.findteam.util.ThumbnailUtil;

/**
 * Created by wn on 2015/8/13.
 */
public class MessageSenderImpl implements MessageSender {
    private static final String TAG = "MessageSender";
    private static final String mPrefix = "imkit_";
    private static final String FILE_SCHEME = "http://";
    private static AudioRecordListener recordListener;
    private volatile static MessageSender sSingleton;

    private Context mContext;
    private Cache mImageCache;
    private long mLastSendTime = 0;
    private MessageBuilder mMessageBuilder;
    private AudioMagician mAudioMagician;
    private AudioStreamController mStreamController;
    private LocalBroadcastManager mLocalBroadcastManager;


//    public static MessageSender getInstance(Context context) {
//        if (sSingleton == null) {
//            synchronized (MessageSenderImpl.class) {
//                if (sSingleton == null)
//                    sSingleton = new MessageSenderImpl(context);
//            }
//        }
//
//        return sSingleton;
//    }
//
//    private MessageSenderImpl(Context context) {
//        mContext = context.getApplicationContext();
//        mMessageBuilder = IMEngine.getIMService(MessageBuilder.class);
//
//        mAudioMagician = (AudioMagician)Doraemon.getArtifact(AudioMagician.AUDIO_ARTIFACT);
//        mAudioMagician.setRecordParams(8500,MediaRecorder.OutputFormat.AMR_NB);
//
//        registerReceiver();
//        PrefsUtil.getInstance().init(mContext);
//    }

    private static class InstanceHolder {
        public static MessageSender sInstance = new MessageSenderImpl();
    }

    public static MessageSender getInstance() {
        return InstanceHolder.sInstance;
    }

    private MessageSenderImpl(){

    }

    @Override
    public void init(Context context){
        mContext = context.getApplicationContext();
        mMessageBuilder = IMEngine.getIMService(MessageBuilder.class);

        mAudioMagician = (AudioMagician)Doraemon.getArtifact(AudioMagician.AUDIO_ARTIFACT);
        mAudioMagician.setRecordParams(8500,MediaRecorder.OutputFormat.AMR_NB);

        registerReceiver();
        PrefsUtil.getInstance().init(mContext);
    }

    /** 监听消息发送状态，若发送成功，删除应用目录下图片消息对应的压缩图 */
    private MessageListener mMessageListener = new MessageListener() {
        @Override
        public void onAdded(List<Message> list, DataType dataType) {

        }

        @Override
        public void onRemoved(List<Message> list) {

        }

        @Override
        public void onChanged(List<Message> list) {
            for(Message message : list){
                int type = message.messageContent().type();
                if((MessageContent.MessageContentType.IMAGE == type || MessageContent.MessageContentType.AUDIO == type)
                        && Message.MessageStatus.SENT == message.status()){

                    String imgPath = PrefsUtil.getInstance().getString(message.localId());
                    if(!TextUtils.isEmpty(imgPath)){
                        Log.e("DemoLog", "recever delete file");
                        FileUtil.deleteFile(imgPath);
                        PrefsUtil.getInstance().remove(message.localId());
                    }
                }
            }
        }
    };


    private void registerReceiver() {
        IMEngine.getIMService(MessageService.class).addMessageListener(mMessageListener);
    }

    //TODO 需要注销
    private void unregisterReceiver() {
        IMEngine.getIMService(MessageService.class).removeMessageListener(mMessageListener);
    }

    /**
     * 发送相册图片，提供压缩和非压缩两种发送方式，当应用采用压缩发送时，图片未上传云端前，压缩图片保存在应用数据目录。 当该消息的图片上传成功后，将调用{@link
     * #setImageCache(Cache) setImageCache} 设置的ImageCache接口缓存图片，并删除压缩后的应用目录图片文件。
     *
     * @param url            图片本地url
     * @param conversation   在该会话内发送图片
     * @param isNeedCompress 是否需要压缩
     */
    @Override
    public void sendAlbumImage(final String url, final Conversation conversation, final boolean isNeedCompress) {
        if(TextUtils.isEmpty(url) || !FileUtil.isLocalUrl(url) || conversation == null) {
            return;
        }

        Thread thread = (Thread)Doraemon.getArtifact(Thread.THREAD_ARTIFACT);
        thread.start(new Runnable() {
            @Override
            public void run() {
                String imgPath = url;
                //压缩并保存图片到应用数据目录
                if(isNeedCompress) {
                    imgPath = ThumbnailUtil.compressAndRotateToThumbFile(mContext, url);
                }

                //创建图片消息并发送
                final File file = new File(imgPath);
                int picType = isNeedCompress ? 0 : 1;
                int picSize = (int)file.length();
                final Message message = mMessageBuilder.buildImageMessage(imgPath,picSize,picType);

                message.sendTo(conversation,new Callback<Message>() {
                    @Override
                    public void onProgress(Message message, int progress) {
                    }

                    @Override
                    public void onSuccess(Message message) {
                        if(isNeedCompress) {
                            //消息的图片上传成功后,存缓存
                            if (mImageCache != null) {
                                String remoteUrl = ((MessageContent.MediaContent) message.messageContent()).url();
                                mImageCache.onWriteData(remoteUrl,ThumbnailUtil.getBytesFromFile(file));
                            }

                            //删压缩图片
                            if (file.exists()) {
                                file.delete();
                            }
                        }
                    }

                    @Override
                    public void onException(String code, String reason) {
                        //图片上传成功但是发送消息失败，删除压缩图，否则关联文件名与Localid
                        if(isNeedCompress) {
                            String url = ((MessageContent.MediaContent) message.messageContent()).url();
                            if (url.startsWith(FILE_SCHEME) && file.exists()) {
                                file.delete();
                            } else {
                                PrefsUtil.getInstance().putString(message.localId(),file.getAbsolutePath());
                            }
                        }
                    }
                });
            }
        });
    }

    /**
     * 设置图片缓存，用于缓存发送成功后的图片。当发送原图时，将不存在图片缓存。
     *
     * @param cache 图片缓存
     */
    @Override
    public void setImageCache(Cache cache) {
        this.mImageCache = cache;
    }

    /**
     * 拍照并发送照片，缓存及压缩策略同{@link #sendAlbumImage(String, com.alibaba.wukong.im.Conversation, boolean)
     * sendAlbumImage}接口
     *
     * @param conversation   在该会话内发送照片
     * @param context        Context实例
     * @param isNeedCompress 是否需要压缩
     */
//    @Override
    public void takePhotoAndSend(Conversation conversation, Context context,
                                 boolean isNeedCompress) {
        if(conversation == null || context == null){
            return;
        }

//        Intent intent = new Intent();
//        // 指定开启系统相机的Action
//        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
//        intent.addCategory(Intent.CATEGORY_DEFAULT);
//        // 根据文件地址创建文件
//        File file = new File(FILE_PATH);
//        if (file.exists()) {
//            file.delete();
//        }
//        // 把文件地址转换成Uri格式
//        Uri uri = Uri.fromFile(file);
//        // 设置系统相机拍摄照片完成后图片文件的存放地址
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
//        ((Activity)context).startActivityForResult(intent,1);
    }

    /**
     * 启动语音录音并发送语音。当语音消息发送失败时，语音文件将保存在应用数据目录。当该语音消息发送成功后， 调用{@link #setAudioCache(Cache)
     * setAudioCache} 设置的AudioCache接口缓存语音，并删除应用数据目录缓存的语音文件。
     *
     * @param conversation 在该会话内发送语音
     */
    @Override
    public void benginAudioRecordAndSend(final Conversation conversation) throws RuntimeException{
        if(conversation == null) {
            return;
        }

        if(recordListener == null){
            recordListener = new AudioRecordListener();
        }

        recordListener.setConversation(conversation);
        mAudioMagician.record(recordListener);
    }

    /**
     * 结束语音录制发送
     */
    @Override
    public void endAudioSend() {
        mAudioMagician.stopRecord();
    }

    /**
     * 取消语音发送
     */
    @Override
    public void cancelAudioSend() {
        if(recordListener != null){
            recordListener.resetController();
        }
        mAudioMagician.stopRecord();
    }

    /**
     * 录音监听器。
     * 使用前先调用{@link #setConversation(com.alibaba.wukong.im.Conversation)}设置发送消息的会话
     */
    protected class AudioRecordListener implements OnRecordListener{
        Conversation mConversation;
        AudioStreamController mStreamController;

        public void setConversation(Conversation conversation){
            mConversation = conversation;
        }

        private void resetController(){
            if(mStreamController != null) {
                mStreamController.cancel();
                mStreamController = null;
            }
        }

        @Override
        public void onRecordStart(final String audioPath) {
            final Message message = mMessageBuilder.buildAudioMessage(audioPath,true);
            mStreamController = message.getAudioStreamController();

            message.sendTo(mConversation,new Callback<Message>() {

                @Override
                public void onSuccess(Message message) {
                    FileUtil.deleteFile(audioPath);
                }

                @Override
                public void onException(String code, String reason) {
                    String url = ((MessageContent.MediaContent) message.messageContent()).url();
                    //语音上传成功但是发送消息失败，或者取消发送语音，都删除相应语音文件
                    if (url.startsWith(FILE_SCHEME) || IMConstants.ErrorCode.ERR_CODE_CANCELED.equals(code)) {
                        FileUtil.deleteFile(audioPath);
                    } else {
                        PrefsUtil.getInstance().putString(message.localId(),audioPath);
                    }
                }

                @Override
                public void onProgress(Message message, int progress) {
                }
            });
        }

        @Override
        public void onRecordCompleted(String audioPath, List<Integer> volumns, long duration) {
            if(duration < 1000){
                resetController();
                AndTools.showToast(mContext, "录音太短");
                return;
            }

            if (!FileUtil.isCanUseSDCard()) {
                FileUtil.deleteFile(audioPath);
                resetController();
                AndTools.showToast(mContext, "SD卡不可用");
                return;
            }

            long now = System.currentTimeMillis();
            if (now - mLastSendTime > 1000) { // 防止出现重复发送的问题
                if (mStreamController != null) {
                    mStreamController.finish(duration, volumns);
                    mStreamController = null;
                }
                mLastSendTime = now;
            }
        }

        @Override
        public void onRecordErrorListener(int i) {
            resetController();
            AndTools.showToast(mContext, "录音失败");
        }

        @Override
        public void notifySampleResult(long duration, List<Integer> volumns) {
        }
    }
}

