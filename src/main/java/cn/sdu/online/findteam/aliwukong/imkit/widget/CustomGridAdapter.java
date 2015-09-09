package cn.sdu.online.findteam.aliwukong.imkit.widget;

/**
 * Created by wn on 2015/8/14.
 */

import android.content.Context;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * 自定义X宫格适配器
 *
 */
public abstract class CustomGridAdapter<T>  extends BaseAdapter {

    protected List<T> mList;
    protected CustomGridView mView;
    protected Context mContext;

    // 设置每一个Item的高宽
    protected int mBlockWidth = 180;
    protected int mBlockHeight = 180;

    //设置上下的间距
    protected int mWidthSpace = 1;
    protected int mHeightSpace = 1;

    //设置列的个数
    protected int mCloumnNum = 2;

    protected String         imageLoaderKey;
    protected boolean          isFromFlow;

    public CustomGridAdapter() {
    }

    public CustomGridAdapter(Context context) {
        mContext = context;
    }

    public T getItem(int position) {
        return mList.get(position);
    }

    public void registerView(CustomGridView observer) {
        mView = observer;
    }

    public void setList(List<T> itemlist){
        this.mList = itemlist;
    }
    public List<T> getList() {
        return this.mList;
    }

    public void displayBlocks() {
        if (null == mView) {
            return;
//            throw new IllegalArgumentException("Adapter has not been attach to any BlockListView");
        }

        mView.onDataListChange();
    }

    public int getCount() {
        return mList.size();
    }

    public void setSpace(int w, int h) {
        mWidthSpace = w;
        mHeightSpace = h;
    }

    public int getHorizontalSpacing() {
        return mWidthSpace;
    }

    public int getVerticalSpacing() {
        return mHeightSpace;
    }

    public void setBlockSize(int w, int h) {
        mBlockWidth = w;
        mBlockHeight = h;
    }

    public int getBlockWidth() {
        return mBlockWidth;
    }

    public int getBlockHeight() {
        return mBlockHeight;
    }

    public void setColumnNum(int num) {
        mCloumnNum = num;
    }

    public int getCloumnNum() {
        return mCloumnNum;
    }

    public void setImageLoaderKey(String imageLoaderKey) {
        this.imageLoaderKey = imageLoaderKey;
    }

    public void setNeedPut(boolean isFromFlow) {
        this.isFromFlow = isFromFlow;
    }
}
