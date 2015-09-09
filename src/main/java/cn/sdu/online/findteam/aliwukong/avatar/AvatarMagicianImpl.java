package cn.sdu.online.findteam.aliwukong.avatar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.ImageView;

import com.alibaba.doraemon.Doraemon;
import com.alibaba.doraemon.image.ImageDecoder;
import com.alibaba.doraemon.image.ImageMagician;
import com.alibaba.wukong.Callback;
import com.alibaba.wukong.im.IMEngine;
import com.alibaba.wukong.im.User;
import com.alibaba.wukong.im.UserService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import cn.sdu.online.findteam.R;
import cn.sdu.online.findteam.aliwukong.imkit.widget.CustomGridView;
import cn.sdu.online.findteam.aliwukong.imkit.widget.MultiAvatarAdapter;

public class AvatarMagicianImpl implements AvatarMagician {
    private Context mContext;
    private static AvatarShaper mShaper;
    private static UserService mUserService;
    private static ImageDecoder mImageDecoder;
    private static ImageMagician mImageMagician;
    private static Bitmap mDefaultAvatar;

    private static final int KEY_URL = 2015011319;
    public static final int KEY_TAG = 2014124145;
    private static final String VALUE_NIL = "NIL";
    private static final int GRID_MAX_COUNT = 9;
    private static final String TAG = "AvatarMagician";
    private static final String IMAGE_DEFAULT_KEY = TAG+"_default.jpg";

    private volatile static AvatarMagician sSingleton;

    private static class InstanceHolder {
        public static AvatarMagician sInstance = new AvatarMagicianImpl();
    }

    public static AvatarMagician getInstance() {
        return InstanceHolder.sInstance;
    }

    private AvatarMagicianImpl() {

    }

    @Override
    public void init(Context context){
        Log.d("avatar", "gouzao");
        this.mContext = context.getApplicationContext();

        if(mUserService == null) {
            mUserService = IMEngine.getIMService(UserService.class);
        }

        if(mImageMagician == null) {
            mImageMagician = (ImageMagician) Doraemon.getArtifact(ImageMagician.IMAGE_ARTIFACT);
        }
    }

    /**
     * 设置用户头像
     *
     * @param imageView   显示头像的view
     * @param openid 用户id
     */
    @Override
    public void setUserAvatar(final ImageView imageView,final long openid,final AbsListView listView) {
        if(imageView == null){
            return;
        }

        //设置默认图片
        final String urlTag = (String)imageView.getTag(KEY_URL);
        if(TextUtils.isEmpty(urlTag)) {
            imageView.setImageBitmap(mDefaultAvatar);
            imageView.setTag(KEY_URL, VALUE_NIL);
        }

        if(mImageDecoder != null) {
            imageView.setTag(KEY_TAG,TAG);   //用于在url2key中区别其他地方的view
            // TODO WKNEW
            imageView.setTag(AvatarImageDecoder.SELFDECODERTAG, mImageDecoder);

        }
//        mImageMagician.setDecoder(mImageDecoder);

        //获取UserProfile
        mUserService.getUser(new Callback<User>() {
            @Override
            public void onSuccess(User user) {
                if(user != null && !TextUtils.isEmpty(user.avatar()) && !user.avatar().equals(urlTag)) {
                    mImageMagician.setImageDrawable(imageView, user.avatar(), listView);
                    imageView.setTag(KEY_URL, user.avatar());
                }
            }

            @Override
            public void onException(String code, String reason) {
                Log.e(TAG,"Get user from server fail");
            }

            @Override
            public void onProgress(User user, int progress) {
            }
        },openid);
    }

    /**
     * 设置会话icon
     *
     * @param viewMap    显示会话icon的view
     * @param listView 用于显示会话icon的用户id
     */
//    @Override
    public void setConversationAvatar(final Map<Long,ImageView> viewMap,AbsListView listView) {
        if(viewMap == null || viewMap.isEmpty()){
            return;
        }

        Long[] openids = new Long[viewMap.size()];
        viewMap.keySet().toArray(openids);
        List<Long> openidList = Arrays.asList(openids);

        if(viewMap.size() == 1){
            setUserAvatar(viewMap.get(openids[0]),openids[0],listView);
            return;
        }


        //设置Decoder和默认图片
        Collection<ImageView> collection = viewMap.values();
        for(ImageView imageView:collection){
            if(mImageDecoder != null) {
                imageView.setTag(KEY_TAG,TAG);   //用于在url2key中区别其他地方的view
                // TODO WKNEW
                imageView.setTag(AvatarImageDecoder.SELFDECODERTAG, mImageDecoder);
            }

            imageView.setImageBitmap(mDefaultAvatar);
        }

        mUserService.listUsers(new Callback<List<User>>() {
            @Override
            public void onSuccess(List<User> users) {
                for(User user : users) {
                    if (!TextUtils.isEmpty(user.avatar())) {
                        mImageMagician.setImageDrawable(viewMap.get(user.openId()), user.avatar(), null);
                    }
                }
            }

            @Override
            public void onException(String code, String reason) {
                Log.e(TAG,"Get users from server fail");
            }

            @Override
            public void onProgress(List<User> users, int progress) {
            }
        },openidList);
    }



    @Override
    public void setConversationAvatar(final CustomGridView gridView,List<Long> openids,AbsListView listView){
        if(gridView == null || openids == null){
            return;
        }

        final MultiAvatarAdapter adapter;
        if(gridView.getAdapter() == null) {
            adapter = new MultiAvatarAdapter(mContext, null);
            adapter.setListView(listView);
            adapter.setImageDecoder(mImageDecoder);
//            mImageMagician.setDecoder(mImageDecoder);
            adapter.setImageMagician(mImageMagician);
            adapter.setDefaultDrawable(mDefaultAvatar);
            gridView.setAdapter(adapter);
        }else {
            adapter = (MultiAvatarAdapter) gridView.getAdapter();
        }

        int count = openids.size() > 9 ? 9 : openids.size();
        if (count == 0) {
            String[] nilUrl = new String[1];
            setGridColumn(gridView, 1);
            adapter.setList(Arrays.asList(nilUrl));
            adapter.displayBlocks();
            return;
        } else {
            new ArrayList<String>(2);
            String[] nilUrl = new String[count];
            setGridColumn(gridView, count);
            adapter.setList(Arrays.asList(nilUrl));
            adapter.displayBlocks();
        }

        mUserService.listUsers(new Callback<List<User>>() {
            @Override
            public void onSuccess(List<User> users) {
                List<String> urls = new ArrayList<String>();
                for(User user : users) {
                    urls.add(user.avatar());
                }

                //todo: show icon first
                if(!urls.isEmpty() && gridView.isAttachedToWindow()) {
                    adapter.setList(urls);
                    adapter.displayBlocks();
                }
            }

            @Override
            public void onException(String code, String reason) {
                Log.e(TAG,"Get users from server fail");
            }

            @Override
            public void onProgress(List<User> users, int progress) {
            }
        },openids);
    }

    /**
     * 设置Avatar的形状，设置完成后，应用内所有头像将使用该形状。
     *
     * @param shape 形状类型，{@link #CIRCLE_AVATAR_SHAPE}圆形，
     *              {@link #ROUNDRECT_AVATAR_SHAPE}圆角矩形
     */
    @Override
    public void setAvatarShape(int shape) {
        Log.d("avatar","setAvatarShape");
        if (ROUNDRECT_AVATAR_SHAPE == shape) {
            mShaper = new RoundRectAvatarShaper();
        } else if(CIRCLE_AVATAR_SHAPE == shape){
            mShaper = new CircleAvatarShaper();
        }

        initialize();
    }

    /**
     * 是指Avatar的形状修改器，设置完成后，应用内所有头像将使用该形状。
     *
     * @param shaper 形状修改器。
     */
    @Override
    public void setAvatarShape(AvatarShaper shaper) {
        this.mShaper = shaper;
        initialize();
    }

    /**
     * 获取Avatar头像遮盖，当view onDraw方式时，在super.onDraw方法后调用AvatarMask的drawMask方法。
     *
     * @param shape 形状类型，{@link #CIRCLE_AVATAR_SHAPE}圆形， {@link #ROUNDRECT_AVATAR_SHAPE}圆角矩形
     * @return AvatarMask实例，如果未曾调用setAvatarShape则返回null。
     */
    @Override
    public AvatarMask getAvatarMask(int shape) throws Throwable {
        AvatarMask avatarMask = null;

        switch (shape){
            case CIRCLE_AVATAR_SHAPE:
                avatarMask = new CircleAvatarMask();
                break;
            case ROUNDRECT_AVATAR_SHAPE:
                avatarMask = new RoundRectAvatarMask();
                break;
//            default:
//                throw new Throwable("Unsupport shape");
        }

        return avatarMask;
    }

    /**
     * 如果未设置mShaper则不做任何处理
     */
    private void initialize(){
        if(mShaper != null) {
            //初始化DefaultAvatar
            mDefaultAvatar = mShaper.defaultAvatar(mContext);

            //初始化ImageDecoder
            mImageDecoder = new AvatarImageDecoder(mContext, mShaper);

            //设置Url2Key Parser  TODO WKNEW
//            mImageMagician.setUrlParser(mContext, new UrlParser() {
//                @Override
//                public String url2Key(View view, String url) {
//                    String tag = (String) view.getTag(KEY_TAG);
//                    if (TAG.equals(tag)) {
//                        String name = url.substring(url.lastIndexOf(File.separator) + 1);
////                        Log.d(TAG,"url2key:"+name);
//                        return TAG + "_" + name;
//                    }
//                    return url;
//                }
//            });
        }else{
            //mDefaultAvatar = ThumbnailUtil.defaultAvatar();
        }
        mDefaultAvatar = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.avatar_in_active);

    }

    private void setGridColumn(CustomGridView gridView,int count){
        if(count == 0)
            return;

        switch (count){
            case 1:     //单个
                gridView.setNumColumns(1);
                break;
            case 3: case 4: //4宫格
                gridView.setNumColumns(2);
                break;
            default:        //9宫格
                gridView.setNumColumns(3);
                break;
        }
    }
}

