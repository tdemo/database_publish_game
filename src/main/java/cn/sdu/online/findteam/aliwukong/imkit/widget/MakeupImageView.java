package cn.sdu.online.findteam.aliwukong.imkit.widget;

/**
 * Created by wn on 2015/8/14.
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import cn.sdu.online.findteam.R;
import cn.sdu.online.findteam.util.AndTools;

/**
 * 通用的带倒角效果的图片处理内容
 *
 */
public class MakeupImageView extends ImageView {

    private final static int DEFAULT_MAKEUP_HEIGHT = 16;

    private static Drawable mForgroundDrawableRight = null;
    private static Drawable mForgroundDrawableLeft = null;
    private static Drawable mFrontgroudDrawable = null;
    private static Drawable mFrontgroundYellowDrawable = null;
    private static Drawable mLoading = null;
    private Paint mPaint;

    private Drawable mCurrentDrawable = null;

    public static enum MakeupDirection{

        Normal(0),

        Left(1),

        Right(2);

        private MakeupDirection(int direction) {
            this.direction = direction;
        }

        private int direction;

        public int direction() {
            return this.direction;
        }
    }

    private static final MakeupDirection[] sDirectionArray = {
            MakeupDirection.Normal,
            MakeupDirection.Left,
            MakeupDirection.Right
    };

    private MakeupDirection mMakeupDirection = MakeupDirection.Normal;
    private int makeupHeight = DEFAULT_MAKEUP_HEIGHT;  //倒角距离底部的高度

    public MakeupImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.initViews(context, attrs);
    }

    public MakeupImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initViews(context, attrs);
    }

    public MakeupImageView(Context context) {
        super(context);
        this.initViews(context, null);
    }


    private void initViews(Context context, AttributeSet attrs) {

        /** share the loading, forground drawable with each other */
        if(null == mLoading){
            mLoading = context.getApplicationContext().getResources().getDrawable(R.drawable.chatting_default_image);
        }

        if(null == mForgroundDrawableLeft){
            mForgroundDrawableLeft = context.getApplicationContext().getResources().getDrawable(R.drawable.chat_arrow_leftnum9);
        }

        if(null == mForgroundDrawableRight){
            mForgroundDrawableRight = context.getApplicationContext().getResources().getDrawable(R.drawable.chat_arrow_rightnum9);
        }

        if(null == mFrontgroudDrawable){
            mFrontgroudDrawable = context.getApplicationContext().getResources().getDrawable(R.drawable.image_normal_background);
        }

        if(null == mFrontgroundYellowDrawable){
            mFrontgroundYellowDrawable = context.getApplicationContext().getResources().getDrawable(R.drawable.image_coor_yellow_bg);
        }


        int colorPicker = 0;

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(context.getResources().getColor(R.color.bg_gray));
        mPaint.setStyle(Paint.Style.FILL);

        if(attrs != null){
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MakeupImageView);
            //根据用户设置的位置信息值设置选项的背景信息
            try{
                int index = a.getInt(R.styleable.MakeupImageView_makeupDirection, 0);
                if (index >= 0 && index < sDirectionArray.length) {
                    setMakeupDirection(sDirectionArray[index]);
                }

                colorPicker = a.getInt(R.styleable.MakeupImageView_colorSelection, 0);


            }catch(Throwable tr){}
            //设置左方的图片信息
            try{
//				int height = (int) a.getDimension(R.styleable.MakeupImageView_makeupHeight, DEFAULT_MAKEUP_HEIGHT);
                //获取到的
                int height = a.getDimensionPixelSize(R.styleable.MakeupImageView_makeupHeight, -1);
                //将px转为dp，主要是BitmapMakeup中用会将dp转会成px
                if(height > 0) {
                    height = AndTools.px2dip(context, height);
                } else {
                    height = DEFAULT_MAKEUP_HEIGHT;
                }
                this.setMakeupHeightDP(height);
            }catch(Throwable tr){
                tr.printStackTrace();
            }

            a.recycle();
        }

        /** in order to improve the performance a little bit, just check what kind of drawable this image is supposed to be. */
        if(mMakeupDirection.ordinal() == MakeupDirection.Left.ordinal()){
            mCurrentDrawable = mForgroundDrawableLeft;
        }else if(mMakeupDirection.ordinal() == MakeupDirection.Right.ordinal()){
            mCurrentDrawable = mForgroundDrawableRight;
        }else{

            if(0 == colorPicker){
                mCurrentDrawable = mFrontgroudDrawable;
            }else{
                mCurrentDrawable = mFrontgroundYellowDrawable;
            }
        }
    }

    public void setColorSelection(int selection){
        if(0 == selection){
            mCurrentDrawable = mFrontgroudDrawable;
        }else{
            mCurrentDrawable = mFrontgroundYellowDrawable;
        }
    }

    /**
     * 设置起泡的方向
     * @param direction
     */
    public void setMakeupDirection(MakeupDirection direction) {

        this.mMakeupDirection = direction;


    }

    /**
     * 获取起泡的方向
     * @return
     */
    public MakeupDirection getMakeupDirection() {
        return this.mMakeupDirection;
    }
    /**
     * 获取倒角距离底部的高度，返回的单位是dp而非px
     * @return
     */
    public int getMakeupHeightDP() {
        return makeupHeight;
    }

    /**
     * 设置倒角距离底部的高度
     * @param makeupHeight
     */
    public void setMakeupHeightDP(int makeupHeight) {
        this.makeupHeight = makeupHeight;
    }

    @Override
    public void draw (Canvas canvas){

        Drawable drawable = super.getBackground();

        /** if the background image is here, do not draw background any more */
        if(null == drawable){
            canvas.save();

            // draw background
            canvas.drawRect(0, 0, this.getWidth(), this.getHeight(), mPaint);

            int width = mLoading.getIntrinsicWidth();
            int height = mLoading.getIntrinsicHeight();

            int tWidth = width;
            int tHeight = height;

            if(width > this.getWidth() || height > this.getHeight()){

                float xRatio = (float)this.getWidth() / (float)width;
                float yRatio = (float)this.getHeight() / (float)height;

                float ratio = Math.min(xRatio, yRatio);
                tWidth = (int)(width * ratio);
                tHeight = (int)(height * ratio);

            }

            int x = Math.max(0, (this.getWidth() - tWidth) / 2);
            int y =  Math.max(0, (this.getHeight() - tHeight) / 2);

            mLoading.setBounds(x, y, x + width, y + height);
            mLoading.draw(canvas);

            canvas.restore();
        }

        super.draw(canvas);

        if(null != mCurrentDrawable){
            canvas.save();
            mCurrentDrawable.setBounds(0, 0, this.getWidth(), this.getHeight());
            mCurrentDrawable.draw(canvas);
            canvas.restore();
        }
    }
}
