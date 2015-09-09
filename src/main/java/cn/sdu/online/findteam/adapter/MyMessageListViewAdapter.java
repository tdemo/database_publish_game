package cn.sdu.online.findteam.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import cn.sdu.online.findteam.R;
import cn.sdu.online.findteam.mob.ChatListItem;
import cn.sdu.online.findteam.view.BadgeView;

public class MyMessageListViewAdapter extends BaseAdapter {
    private LayoutInflater mInflater = null;
    private List<ChatListItem> data;

    public MyMessageListViewAdapter(Context context, List<ChatListItem> data) {
        //根据context上下文加载布局，这里的是Demo17Activity本身，即this
        this.data = data;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        //How many items are in the data set represented by this Adapter.
        //在此适配器中所代表的数据集中的条目数
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        //获取数据集中与指定索引对应的数据项
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        //Get the row id associated with the specified position in the list.
        //获取在列表中与指定索引对应的行id
        return position;
    }

    //Get a View that displays the data at the specified position in the data set.
    //获取一个在数据集中指定索引的视图来显示数据
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        //如果缓存convertView为空，则需要创建View
        /*if (convertView == null) {*/
        holder = new ViewHolder();
        //根据自定义的Item布局加载布局
        convertView = mInflater.inflate(R.layout.item, null);
        holder.img = (ImageView) convertView.findViewById(R.id.img);
        holder.title = (TextView) convertView.findViewById(R.id.tv);
        holder.info = (TextView) convertView.findViewById(R.id.info);
        holder.view = convertView.findViewById(R.id.invite_list_spacing);
        holder.badgeView = (BadgeView) convertView.findViewById(R.id.chat_num);

        //将设置好的布局保存到缓存中，并将其设置在Tag里，以便后面方便取出Tag
        convertView.setTag(holder);
        /*} else {
            holder = (ViewHolder) convertView.getTag();
        }*/

        holder.img.setBackgroundResource(data.get(position).img);
        holder.title.setText(data.get(position).title);
        holder.info.setText(data.get(position).info);

        if (data.get(position).getSeeornot()) {
            holder.badgeView.setVisibility(View.VISIBLE);
            holder.badgeView.setBackgroundResource(R.drawable.badgeview_bg);
            holder.badgeView.setBadgeCount(data.get(position).getNum());
        }

        return convertView;
    }

    public class ViewHolder {
        public ImageView img;
        public TextView title;
        public TextView info;
        public View view;
        public BadgeView badgeView;
    }
}
