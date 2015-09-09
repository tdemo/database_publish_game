package cn.sdu.online.findteam.aliwukong.imkit.base;

/**
 * Created by wn on 2015/8/14.
 */

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.sdu.online.findteam.aliwukong.imkit.route.RouteProcessor;
import cn.sdu.online.findteam.aliwukong.imkit.session.SessionViewHolder;
import cn.sdu.online.findteam.aliwukong.imkit.session.model.Session;

/**
 * 线程不安全
 *
 * @param <T>
 */
public abstract class ListAdapter<T extends DisplayListItem> extends BaseAdapter {

    protected List<T> mList;
    private Context mContext;
    private HashMap<String, ViewHolder> viewHolderMap;

    public ListAdapter(Context context) {
        mContext = context;
        viewHolderMap = new HashMap<String, ViewHolder>();
        mList = new ArrayList<T>();
    }

    public void setList(List<T> list) {
        if (this.mList != null) {
            this.mList.clear();
        } else {
            this.mList = new ArrayList<T>();
        }
        if (list != null) {
            this.mList.addAll(list);
        }
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public Object getItem(int position) {
        if (mList == null || mList.size() == 0) {
            return null;
        } else {
            return mList.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        T item = (T) getItem(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = RouteProcessor.route(item, getDomainCategory());
            convertView = viewHolder.inflate(mContext, null);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        removeOldViewHolder(viewHolder);
        viewHolder.parentView = parent;
        addViewHolder(viewHolder, item.getId());
        onBindView(viewHolder, item, position);
        return convertView;
    }

    private void removeOldViewHolder(ViewHolder viewHolder) {
        if (viewHolderMap.containsKey(viewHolder.tag)
                && viewHolderMap.get(viewHolder.tag) == viewHolder) {
            viewHolderMap.remove(viewHolder.tag);
        }
    }

    private void addViewHolder(ViewHolder viewHolder, String key) {
        viewHolder.tag = key;
        viewHolderMap.put(viewHolder.tag, viewHolder);
    }

    protected void onBindView(ViewHolder viewHolder, T item, int position) {
        viewHolder.position = position;
        item.onShow(mContext, viewHolder, null);
    }

    /**
     * 更新某项
     */
    public void notifyDataSetChanged(T item, String tag) {
        if (item == null) {
            return;
        }
        ViewHolder viewHolder = viewHolderMap.get(item.getId());
        if (viewHolder != null && viewHolder.tag.equals(item.getId())) {
            item.onShow(mContext, viewHolder, tag);
        }
    }

    public void notifyDataSetChanged(List<T> list, String tag) {
        for (T item : list) {
            notifyDataSetChanged(item, tag);
        }
    }

    @Override
    public int getItemViewType(int position) {
        DisplayListItem msg = (DisplayListItem) getItem(position);
        return RouteProcessor.getViewType(msg, getDomainCategory());
    }

    @Override
    public int getViewTypeCount() {
        int size = RouteProcessor.getViewCount(getDomainCategory());
        return size == 0 ? 1 : size;
    }

    protected abstract String getDomainCategory();

}

