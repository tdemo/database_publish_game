package cn.sdu.online.findteam.aliwukong.imkit.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;

import com.alibaba.doraemon.image.ImageDecoder;
import com.alibaba.doraemon.image.ImageMagician;

import java.util.ArrayList;
import java.util.List;

import cn.sdu.online.findteam.aliwukong.avatar.AvatarImageDecoder;
import cn.sdu.online.findteam.aliwukong.avatar.AvatarMagicianImpl;
import cn.sdu.online.findteam.util.AndTools;

/**
 * Created by wn on 2015/8/14.
 */
public class MultiAvatarAdapter extends CustomGridAdapter<String>{
    private AbsListView parent;
    private AbsListView mListView;
    private ImageDecoder mImageDecoder;
    private ImageMagician mImageMagician;
    private Bitmap mDefaultAvatar;
    //    private static final int KEY_ID = 2014124145;
    private static final int KEY_URL = 2015011319;
    private static final String VALUE_NIL = "NIL";
    private static final String TAG = "AvatarMagician";

    public MultiAvatarAdapter(Context context, AbsListView parent){
        super(context);
        this.parent = parent;

//		setBlockSize(AndTools.dp2px(context, 21), AndTools.dp2px(context, 21));
        setSpace(AndTools.dp2px(mContext, 1), AndTools.dp2px(mContext, 1));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setImageDecoder(ImageDecoder imageDecoder) {
        this.mImageDecoder = imageDecoder;
    }

    public void setDefaultDrawable(Bitmap defaultAvatar) {
        this.mDefaultAvatar = defaultAvatar;
    }

    public void setImageMagician(ImageMagician imageMagician) {
        this.mImageMagician = imageMagician;
    }

    public void setListView(AbsListView listView){
        mListView = listView;
    }

    @Override
    public void setColumnNum(int num) {
        super.setColumnNum(num);
        if(1 == num) {
            setBlockSize(AndTools.dp2px(mContext, 50), AndTools.dp2px(mContext, 50));
        }else if (2 == num) {
            setBlockSize(AndTools.dp2px(mContext, 24), AndTools.dp2px(mContext, 24));
        }else{
            setBlockSize(AndTools.dp2px(mContext, 16), AndTools.dp2px(mContext, 16));
        }
    }

    @Override
    public void setList(List<String> itemlist) {
        if(itemlist == null){
            return;
        }
        if(mList != null){
            mList.clear();
        }else{
            mList = new ArrayList<String>();
        }

        mList.addAll(itemlist);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView item = (ImageView) convertView;
        if(item == null){
            item = new ImageView(mContext);

            if(mImageDecoder != null) {
                item.setTag(AvatarMagicianImpl.KEY_TAG, TAG);//用于在url2key中区别其他地方的view
                // TODO WKNEW
                item.setTag(AvatarImageDecoder.SELFDECODERTAG, mImageDecoder);
            }
        }

        String url = getItem(position);
        String urlTag = (String)item.getTag(KEY_URL);

        item.setImageBitmap(mDefaultAvatar);
        item.setTag(KEY_URL, VALUE_NIL);

//        if(TextUtils.isEmpty(url) && (TextUtils.isEmpty(urlTag))){
//            item.setImageBitmap(mDefaultAvatar);
//            item.setTag(KEY_URL, VALUE_NIL);
//        }else if(!TextUtils.isEmpty(url) && !url.equals(urlTag)){
//            mImageMagician.setImageDrawable(item, url, mListView);
//            item.setTag(KEY_URL, url);
//        }

        return item;
    }
}
