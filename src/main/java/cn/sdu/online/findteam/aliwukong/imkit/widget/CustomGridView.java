package cn.sdu.online.findteam.aliwukong.imkit.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义X宫格实现，在无翻页需求下，GridView的替代容器 使用方法同GridView，触发创建和刷新通过
 * adapter.displayblocks()
 *
 * 群聊头像使用
 *
 */
public class CustomGridView extends RelativeLayout {

    private static final int INDEX_TAG = 0x04 << 24;

    private boolean isAttach = false;

    private CustomGridAdapter<?> mGridListAdapter;

    private LayoutInflater mLayoutInflater;

    private AdapterView.OnItemClickListener mOnItemClickListener;

    //是否需要针对三个人的群聊头像做适配处理。
    private boolean isTriangleTreatForThree = true;

    public CustomGridView(Context context) {
        this(context, null, 0);
    }

    public CustomGridView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setmLayoutInflater(LayoutInflater.from(context));
    }

    public void setAdapter(CustomGridAdapter<?> adapter) {
        if (adapter == null) {
            throw new IllegalArgumentException("adapter should not be null");
        }
        mGridListAdapter = adapter;
        adapter.registerView(this);
    }

    /**
     * 设置三个头像的时候是否启用三角阵型
     * @param isSpecial true为启用，false为关闭。默认启动，
     */
    public void setTriangleTreat(boolean isTriangle){
        isTriangleTreatForThree = isTriangle;
    }

    @Override
    public boolean isAttachedToWindow(){
//        if(Build.VERSION.SDK_INT >= 19)
//            return super.isAttachedToWindow();
        return isAttach;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (null != mGridListAdapter) {
            mGridListAdapter.registerView(null);
        }
        isAttach = false;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (null != mGridListAdapter) {
            mGridListAdapter.registerView(this);
        }
        isAttach = true;
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    OnClickListener mOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            int index = (Integer) v.getTag(INDEX_TAG);
            if (null != mOnItemClickListener) {
                mOnItemClickListener.onItemClick(null, v, index, index);
            }
        }
    };

    private List<View> viewList = new ArrayList<View>();

    public void onDataListChange() {

        removeAllViews();

        int len = mGridListAdapter.getCount();
        int w = mGridListAdapter.getBlockWidth();
        int h = mGridListAdapter.getBlockHeight();
        int cloumnNum = mGridListAdapter.getCloumnNum();

        int horizontalSpacing = mGridListAdapter.getHorizontalSpacing();
        int verticalSpacing = mGridListAdapter.getVerticalSpacing();
        boolean blockDescendant = getDescendantFocusability() == ViewGroup.FOCUS_BLOCK_DESCENDANTS;


        int left = 0;
        int top = 0;
        int row = 0;
        int clo = 0;

        for(int i = 0; i < len; i++){
            if(3 == len && isTriangleTreatForThree){
                left = w >> 1;
                top  = 0;

                if(i == 1){
                    left = 0;
                    top  = verticalSpacing + h;
                }else if(i == 2){
                    left = horizontalSpacing + w;
                    top  = verticalSpacing + h;
                }
            }else{
                row = i / cloumnNum;
                clo = i % cloumnNum;
                left = 0;
                top = 0;

                if (clo > 0) {
                    left = (horizontalSpacing + w) * clo;
                }
                if (row > 0) {
                    top = (verticalSpacing + h) * row;
                }
            }

            LayoutParams lyp = new LayoutParams(w, h);
            lyp.setMargins(left, top, 0, 0);

            View convertView = null;
            if (viewList.size() > i) {
                convertView = viewList.get(i);
            }
            View view = mGridListAdapter.getView(i, convertView, null);
            if (i >= viewList.size()) {
                viewList.add(view);
            }
            if (!blockDescendant) {
//				view.setOnClickListener(mOnClickListener);
            }
            view.setTag(INDEX_TAG, i);
            addView(view, lyp);
        }
    }

    public BaseAdapter getAdapter() {
        return mGridListAdapter;
    }

    public void setNumColumns(int column) {
        mGridListAdapter.setColumnNum(column);
    }

    public LayoutInflater getmLayoutInflater() {
        return mLayoutInflater;
    }

    public void setmLayoutInflater(LayoutInflater mLayoutInflater) {
        this.mLayoutInflater = mLayoutInflater;
    }
}
