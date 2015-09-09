package cn.sdu.online.findteam.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import cn.sdu.online.findteam.R;
import cn.sdu.online.findteam.activity.MainActivity;
import cn.sdu.online.findteam.activity.SingleCompetitionActivity;
import cn.sdu.online.findteam.fragment.MainFragment;
import cn.sdu.online.findteam.mob.MainListViewItem;

public class ListViewAdapter extends BaseAdapter {
    Context mContext;
    List<MainListViewItem> mlist;
    List<MainListViewItem> mFilterlist;
    NameFilter mNameFilter;

    public ListViewAdapter(Context context, List<MainListViewItem> list) {
        mContext = context;
        mlist = list;
    }

    @Override
    public int getCount() {
        // TODO 自动生成的方法存根
        return mlist != null ? mlist.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        // TODO 自动生成的方法存根
        return mlist != null ? mlist.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        // TODO 自动生成的方法存根
        return mlist != null ? position : 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (mlist == null){
            return null;
        }
        Holder holder;
        if (convertView == null) {
            holder = new Holder();
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.list_item, null);
            // holder.type = (TextView) convertView.findViewById(R.id.item_type_tv);
            holder.title = (TextView) convertView.findViewById(R.id.item_title_tv);
            holder.content = (TextView) convertView.findViewById(R.id.item_content_tv);
            holder.view = convertView.findViewById(R.id.spacing_view);

            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        holder.title.setText(mlist.get(position).name);
        holder.content.setText(mlist.get(position).introduce);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.mainActivity, SingleCompetitionActivity.class);
                intent.putExtra("gameID", mlist.get(position).id);
                intent.putExtra("gameIntroduce", mlist.get(position).introduce);
                intent.putExtra("gameName", mlist.get(position).name);
                MainActivity.mainActivity.startActivity(intent);
            }
        });
        return convertView;
    }

    public class Holder {
        // TextView type;
        TextView title;
        TextView content;
        View view;
    }

    public Filter getFilter() {
        if (mNameFilter == null) {
            mNameFilter = new NameFilter();
        }
        return mNameFilter;
    }

    //过滤数据
    class NameFilter extends Filter {
        //执行筛选
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            FilterResults filterResults = new FilterResults();
            mFilterlist = new ArrayList<>();
            for (int i = 0;i<mlist.size();i++) {
                String name = mlist.get(i).name;
                if (name.contains(charSequence) || charSequence.equals("")) {
                    mFilterlist.add(mlist.get(i));
                }
            }
            filterResults.values = mFilterlist;
            return filterResults;
        }

        //筛选结果
        @Override
        protected void publishResults(CharSequence arg0, FilterResults results) {
            mlist = (List<MainListViewItem>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }}
}
