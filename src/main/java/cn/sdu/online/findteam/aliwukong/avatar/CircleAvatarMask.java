package cn.sdu.online.findteam.aliwukong.avatar;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;

public class CircleAvatarMask implements AvatarMask {
    private static Paint mDrawPaint;
    private static Paint mMaskPaint;

    public CircleAvatarMask() {
        initPaints();
    }

    private void initPaints(){
        mDrawPaint = new Paint();
        //设置边缘光滑，去掉锯齿
        mDrawPaint.setAntiAlias(true);
        mDrawPaint.setFilterBitmap(false);
        //设置两个图形相交模式为取下层非交集部分与上层交集部分
        mDrawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));

        mMaskPaint = new Paint();
        mMaskPaint.setAntiAlias(true);
        mMaskPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    public void drawMask(Canvas canvas, int bk_color) {
        if(canvas == null) {
            return;
        }

        int width = canvas.getWidth();
        int height = canvas.getHeight();

        //创建遮盖层
        Bitmap mask = makeMask(width,height,bk_color);

        //将遮盖层画到canvas上面
        if(mask != null) {
            canvas.drawBitmap(mask, 0.0F, 0.0F, mDrawPaint);
            mask.recycle();
        }
    }

    /**
     * 创建镂空遮盖层
     * @param width 画布宽度
     * @param height 画布高度
     * @param bk_color 画布背景颜色
     * @return 圆形mask
     */
    private Bitmap makeMask(int width,int height,int bk_color){
        if(width == 0 || height == 0)
            return null;

        float cx,cy;
        float radius = 0;

        if(width > height){
            radius = height >> 1;
            cx = width >> 1;
            cy = radius;
        }else{
            radius = width >> 1;
            cx = radius;
            cy = height >> 1;
        }

        Bitmap output = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
//        canvas.drawColor(Color.TRANSPARENT);

        //设置边缘光滑，去掉锯齿,相交模式为取交集
        if(Color.TRANSPARENT == bk_color)
            bk_color = Color.WHITE;
        mMaskPaint.setColor(bk_color);
        canvas.drawColor(Color.TRANSPARENT);

        //通过制定的rect画一个圆角矩形，当圆角X轴方向的半径等于Y轴方向的半径时，
        //且都等于r/2时，画出来的圆角矩形就是圆形
//        canvas.drawRoundRect(rect, r/2, r/2, paint);

        mMaskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.XOR));
        canvas.drawCircle(cx,cy,radius,mMaskPaint);
//        canvas.drawRect(new RectF(0,0,width,height),mMaskPaint);
//        mMaskPaint.setXfermode(null);

        return output;
    }

    public Bitmap test(Bitmap myCoolBitmap){
        int w = myCoolBitmap.getWidth(), h = myCoolBitmap.getHeight();

        Bitmap rounder = Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(rounder);

        Paint xferPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        xferPaint.setColor(Color.RED);

        canvas.drawRoundRect(new RectF(0,0,w,h), 20.0f, 20.0f, xferPaint);

        xferPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        canvas.drawBitmap(myCoolBitmap, 0,0, null);
        canvas.drawBitmap(rounder, 0, 0, xferPaint);
        return rounder;
    }

    /**
     * 获取一个bitmap，目的是用来承载drawable;
     * <p>
     * 将这个bitmap放在canvas上面承载，并在其上面画一个椭圆(其实也是一个圆，因为width=height)来固定显示区域
     *
     * @param width
     * @param height
     * @return
     */
    private Bitmap makeOvalMask(final int width, final int height) {
        Bitmap.Config localConfig = Bitmap.Config.ARGB_8888;
        Bitmap localBitmap = Bitmap.createBitmap(width, height, localConfig);
        Canvas localCanvas = new Canvas(localBitmap);
        Paint localPaint = new Paint();
//        final int padding = (mBorderWidth - 3) > 0 ? mBorderWidth - 3 : 1;
        /**
         * 设置椭圆的大小(因为椭圆的最外边会和border的最外边重合的，如果图片最外边的颜色很深，有看出有棱边的效果，所以为了让体验更加好，
         * 让其缩进padding px)
         */
        RectF localRectF = new RectF(0, 0, width, height);
        localCanvas.drawOval(localRectF, localPaint);
        return localBitmap;
    }
}
