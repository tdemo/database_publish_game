package cn.sdu.online.findteam.aliwukong.imkit.chat.controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.alibaba.doraemon.Doraemon;
import com.alibaba.doraemon.image.ImageMagician;
import com.alibaba.wukong.Callback;
import com.alibaba.wukong.im.Conversation;
import com.alibaba.wukong.im.Message;
import com.alibaba.wukong.im.MessageContent;

import java.util.ArrayList;
import java.util.List;

import cn.sdu.online.findteam.R;
import cn.sdu.online.findteam.util.AndTools;

public class ImageShowerActivity extends Activity {
    private Gallery mGallery;
    private ImageAdapter mAdapter;
    private ProgressBar mProgressBar;
    private ImageMagician mImageMagician;
    public static final String MID_INTENT_KKEY = "messageId";
    public static final String CONV_INTENT_KKEY = "conversation";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_shower);
        mImageMagician = (ImageMagician) Doraemon.getArtifact(ImageMagician.IMAGE_ARTIFACT);

        initView();
        loadImageMessages();
    }

    private void initView(){
        mProgressBar = (ProgressBar)findViewById(R.id.shower_progressBar);
        mGallery = (Gallery)findViewById(R.id.shower_gallery);
        mAdapter = new ImageAdapter(this);
        mGallery.setAdapter(mAdapter);
    }

    //todo:分页拉取图片消息
    private void loadImageMessages(){
        Intent intent = getIntent();
        Conversation conversation = (Conversation) intent.getSerializableExtra(
                CONV_INTENT_KKEY);
        final long currentMid = (long) intent.getLongExtra(MID_INTENT_KKEY,0);
        conversation.sync();
        conversation.listPreviousLocalMessages(null,100, MessageContent.MessageContentType.IMAGE,new Callback<List<Message>>() {
            @Override
            public void onSuccess(List<Message> messages) {
                int showIndex = -1;
                List<String> list = new ArrayList<String>();
                for(Message message:messages){
                    MessageContent.ImageContent content = (MessageContent.ImageContent)message.messageContent();
                    list.add(content.url());
                    if(currentMid == message.messageId()){
                        showIndex = list.size()-1;
                    }
                }
                mAdapter.setList(list);
                mProgressBar.setVisibility(View.GONE);
                mGallery.setVisibility(View.VISIBLE);
                mGallery.setSelection(showIndex);
            }

            @Override
            public void onException(String s, String s2) {
                AndTools.showToast(ImageShowerActivity.this, "无法查看图片");
            }

            @Override
            public void onProgress(List<Message> messages, int i) {
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private class ImageAdapter extends BaseAdapter {
        private Context mContext;
        private List<String> mList;

        public ImageAdapter(Context c){
            mContext = c;
            mList = new ArrayList<String>();
        }

        public void setList(List<String> list) {
            if (this.mList != null) {
                this.mList.clear();
            } else {
                this.mList = new ArrayList<String>();
            }
            if (list != null) {
                this.mList.addAll(list);
            }
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView = null;
            if(convertView == null){
                imageView = new ImageView (mContext);
                Gallery.LayoutParams params = new Gallery.LayoutParams(
                        Gallery.LayoutParams.FILL_PARENT, Gallery.LayoutParams.FILL_PARENT);
                imageView.setLayoutParams(params);
            }else{
                imageView = (ImageView) convertView;
            }

            mImageMagician.setImageDrawable(imageView,mList.get(position),null);
            return imageView;
        }
    };
}
