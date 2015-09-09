package cn.sdu.online.findteam.aliwukong.imkit.base;

/**
 * Created by wn on 2015/8/14.
 */

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * 支持	wrap_content 的 gridview
 * @author zijunlzj
 *
 */
public class WrapperGridView extends GridView {

    public WrapperGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public WrapperGridView(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    public WrapperGridView(Context context){
        super(context);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightSpec;
        if (getLayoutParams().height == LayoutParams.WRAP_CONTENT){
            heightSpec = MeasureSpec.makeMeasureSpec(
                    Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        }else{
            heightSpec=heightMeasureSpec;
        }

        super.onMeasure(widthMeasureSpec, heightSpec);
    }

}
