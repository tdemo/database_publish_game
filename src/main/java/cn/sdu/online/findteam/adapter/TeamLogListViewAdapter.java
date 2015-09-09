package cn.sdu.online.findteam.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.sdu.online.findteam.R;
import cn.sdu.online.findteam.mob.TeamLogListViewItem;

/**
 * Created by wn on 2015/7/23.
 */
public class TeamLogListViewAdapter extends BaseAdapter {

    LayoutInflater layoutInflater;
    List<TeamLogListViewItem> listViewItems;

    public TeamLogListViewAdapter(Context context, List<TeamLogListViewItem> listViewItems){
        layoutInflater = LayoutInflater.from(context);
        this.listViewItems = listViewItems;
    }

    @Override
    public int getCount() {
        return listViewItems.size();
    }

    @Override
    public Object getItem(int position) {
        return listViewItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null){
            viewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.teamlog_list_item,null);

            viewHolder.headbmp = (ImageView) convertView.findViewById(R.id.teamlog_headbmp);
            viewHolder.name = (TextView) convertView.findViewById(R.id.teamlog_name);
            viewHolder.time = (TextView) convertView.findViewById(R.id.teamlog_time);
            viewHolder.content = (TextView) convertView.findViewById(R.id.teamlog_content);
            viewHolder.view = convertView.findViewById(R.id.teamlog_spacing_view);

            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.headbmp.setBackgroundResource(listViewItems.get(position).headbmp);
        viewHolder.name.setText(listViewItems.get(position).name);
        viewHolder.time.setText(listViewItems.get(position).time);
        viewHolder.content.setText(listViewItems.get(position).content);

        return convertView;
    }

    public class ViewHolder{
        public ImageView headbmp;
        public TextView name;
        public TextView time;
        public TextView content;
        public View view;
    }
}
