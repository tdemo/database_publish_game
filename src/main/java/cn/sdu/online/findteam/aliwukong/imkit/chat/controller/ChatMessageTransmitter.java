package cn.sdu.online.findteam.aliwukong.imkit.chat.controller;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.wukong.Callback;
import com.alibaba.wukong.im.Conversation;
import com.alibaba.wukong.im.IMEngine;
import com.alibaba.wukong.im.Message;
import com.alibaba.wukong.im.MessageBuilder;

import javax.inject.Inject;

import cn.sdu.online.findteam.R;
import cn.sdu.online.findteam.aliwukong.imkit.base.ImKitModule;
import cn.sdu.online.findteam.aliwukong.imkit.base.MessageSender;
import cn.sdu.online.findteam.aliwukong.imkit.base.MessageSenderImpl;
import cn.sdu.online.findteam.aliwukong.imkit.business.ChatMessageFactory;
import cn.sdu.online.findteam.aliwukong.imkit.chat.model.ChatMessage;
import dagger.ObjectGraph;

/**
 * Created by wn on 2015/8/14.
 */
public class ChatMessageTransmitter extends Fragment {
    private static final String TAG = ChatMessageTransmitter.class.getSimpleName();
    public static final int SEND_MODE = 1;
    public static final int VOICE_MODE = 2;
    public static final int FACES_MODE = 3;
    private static int SELECT_ALBUM = 0; // 从相册选择图片
    private static int SELECT_CAMER = 1; // 用相机拍摄照片
    private static String[] SELECT_ITEM = {"相册", "相机"};
    private View mFragmentView;
    private MessageBuilder mMessageBuilder;
    private Conversation mCurrentConeverstaion;
    private MessageSender mMessageSender;
    private Callback<ChatMessage> onTransmitted;
    private boolean mIsRecording = false;
    private LinearLayout mInputAreaView;
    private ImageView load_camera;
    private TextView edtSendMsg;
    private ImageView btn_voice;
    private ImageView btn_face;
    private TextView btn_send;
    private int mCurrentMode = VOICE_MODE;

    @Inject
    ChatMessageFactory mChatMessageFactory;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mFragmentView = inflater.inflate(R.layout.chat_transmitter, container, false);
        return mFragmentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ObjectGraph objectGraph = ObjectGraph.create(new ImKitModule());
        objectGraph.inject(this);
        initViews();
        mMessageSender = MessageSenderImpl.getInstance();
        mMessageBuilder = IMEngine.getIMService(MessageBuilder.class);
    }


    private void initViews() {
        this.mInputAreaView = (LinearLayout) mFragmentView.findViewById(R.id.rl_input);
        this.load_camera = (ImageView) mFragmentView.findViewById(R.id.load_camera);
        this.edtSendMsg = (EditText) mFragmentView.findViewById(R.id.et_sendmessage);
        this.btn_voice = (ImageView) mFragmentView.findViewById(R.id.img_voice_input);
        this.btn_face = (ImageView) mFragmentView.findViewById(R.id.btn_face);
        this.btn_send = (TextView) mFragmentView.findViewById(R.id.btn_send);

        this.initSendMessageAttrs();
        this.initClickListener();
    }

    private void initSendMessageAttrs() {

        edtSendMsg.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                toggleSendButtonStatus();
                if (mCurrentMode != SEND_MODE && hasMessageContent()) {
                    switchToTextSendMode();
                } else if (!hasMessageContent()) {
                    switchToVoiceSendMode();
                }
            }

            @Override
            public void afterTextChanged(Editable editText) {
            }
        });

//        edtSendMsg.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                switchToTextSendMode();
//            }
//        });
    }

    private void initClickListener() {
        btn_face.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        edtSendMsg.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        btn_face.setImageResource(R.drawable.ib_face);
                    }
                }, 10);
                return false;
            }
        });

        btn_send.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String contString = edtSendMsg.getText().toString();
                if (contString.trim().length() > 0 && contString.trim().length() <= 5000) {
                    Message message = mMessageBuilder.buildTextMessage(contString);
                    edtSendMsg.setText("");
                    message.sendTo(mCurrentConeverstaion, sendCallback);
                }
            }
        });

        //发送相册图片
        load_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo:此处应该用UIkit的组件
                // showPickPhotoDialog();
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, SELECT_ALBUM);
            }
        });

        initVoiceBtnListener();
    }

    private void initVoiceBtnListener() {
        //长按语音按键弹出录音控件
        btn_voice.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mIsRecording = true;
                Toast.makeText(getActivity(), R.string.start_recording, Toast.LENGTH_SHORT).show();
                mMessageSender.benginAudioRecordAndSend(mCurrentConeverstaion);
                return true;
            }
        });

        btn_voice.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_OUTSIDE:
                        // 手抬起，即录音结束
                        if (mIsRecording) {
                            mIsRecording = false;
                            mMessageSender.endAudioSend();
                        }
                        break;
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:
                        break;
                }
                return false;
            }
        });
    }

    private void showPickPhotoDialog() {
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.select_image_dlg_tips))
                .setItems(SELECT_ITEM, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (SELECT_ALBUM == which) {
                            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                            intent.addCategory(Intent.CATEGORY_OPENABLE);
                            intent.setType("image/*");
                            startActivityForResult(intent, SELECT_ALBUM);
                        } else {
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent, SELECT_CAMER);
                        }
                    }
                }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).create();
        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Activity activity = getActivity();
        if (resultCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = activity.getContentResolver().query(uri, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String imgUrl = cursor.getString(columnIndex);
            cursor.close();

            if (mMessageSender != null) {
                mMessageSender.sendAlbumImage(imgUrl, mCurrentConeverstaion, true);
            }
        }
    }

    private Callback<Message> sendCallback = new Callback<Message>() {
        @Override
        public void onSuccess(Message message) {
//            edtSendMsg.setText("");
            if (onTransmitted != null) {
                onTransmitted.onSuccess(mChatMessageFactory.create(message));
            }
        }

        @Override
        public void onException(String s, String s2) {
            if (onTransmitted != null) {
                onTransmitted.onException(s, s2);
            }
        }

        @Override
        public void onProgress(Message message, int i) {
            if (onTransmitted != null) {
                onTransmitted.onProgress(mChatMessageFactory.create(message), i);
            }
        }
    };

    private void toggleSendButtonStatus() {
        if (hasMessageContent()) {
            btn_send.setEnabled(true);
        } else {
            btn_send.setEnabled(false);
        }
    }

    /**
     * 切换到文本发送模式，会自动隐藏
     */
    public void switchToTextSendMode() {
        switchSendBtnShow(SEND_MODE);
        this.setMessageEditCursorVisible(true);
    }

    /**
     * 切换到语音发送模式
     */
    public void switchToVoiceSendMode() {
        switchSendBtnShow(VOICE_MODE);
        this.setMessageEditCursorVisible(false);
    }

    private boolean hasMessageContent() {
        if (edtSendMsg.getEditableText() == null) {
            return false;
        }
        return !TextUtils.isEmpty(edtSendMsg.getEditableText().toString().trim());
    }


    protected void switchSendBtnShow(int mode) {
        this.mCurrentMode = mode;
        switch (mode) {
            case SEND_MODE:
                btn_voice.setVisibility(View.GONE);
                btn_send.setVisibility(View.VISIBLE);
                toggleSendButtonStatus();
                break;
            case VOICE_MODE:
                btn_send.setVisibility(View.GONE);
                btn_voice.setVisibility(View.VISIBLE);
                break;
            case FACES_MODE:
                btn_send.setVisibility(View.VISIBLE);
                btn_voice.setVisibility(View.GONE);
                break;
            default:
                break;

        }
    }

    public void setMessageEditCursorVisible(boolean isVislible) {
        edtSendMsg.setCursorVisible(isVislible);
    }

    public void setCurrentConeverstaion(Conversation mCurrentConeverstaion) {
        this.mCurrentConeverstaion = mCurrentConeverstaion;
    }

    public void setOnTransmitted(Callback<ChatMessage> onTransmitted) {
        this.onTransmitted = onTransmitted;
    }
}
