package cn.sdu.online.findteam.aliwukong.imkit.base;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by wn on 2015/8/14.
 */
public abstract class ViewHolder {

    public int position = -1;
    public String tag;
    public ViewGroup parentView;

    public View inflate(Context context, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(getLayoutId(), parent);
        initView(view);
        return view;
    }

    /**
     * 初始化试图组件
     *
     * @param view
     */
    protected abstract void initView(View view);

    /**
     * 设置当前的layout资源
     *
     * @return
     */
    protected abstract int getLayoutId();

    public static boolean isNeedDraw(String text, TextView textView) {
        boolean b1 = !TextUtils.isEmpty(text);
        boolean b2 = !TextUtils.isEmpty(textView.getText());
        if (b1 && b2) {
            return !text.equals(textView.getText());
        }
        return b1 || b2;
    }
}
