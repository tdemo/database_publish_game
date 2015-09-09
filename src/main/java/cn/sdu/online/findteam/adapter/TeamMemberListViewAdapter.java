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
import cn.sdu.online.findteam.mob.TeamMemberListItem;

public class TeamMemberListViewAdapter extends BaseAdapter{

    LayoutInflater inflater;
    List<TeamMemberListItem> listItems;

    public TeamMemberListViewAdapter(Context mContext, List<TeamMemberListItem> listItems){
        inflater = LayoutInflater.from(mContext);
        this.listItems = listItems;
    }

    @Override
    public int getCount() {
        return listItems.size();
    }

    @Override
    public Object getItem(int position) {
        return listItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position,
                        View convertView, ViewGroup parent) {
        Viewholder viewholder;
        if (convertView == null){
            viewholder = new Viewholder();
            convertView = inflater.inflate(R.layout.teammember_listitem_layout,null);
            viewholder.headerbmp = (ImageView) convertView.findViewById(R.id.teammem_listview_headbmp);
            viewholder.nametv = (TextView) convertView.findViewById(R.id.teammember_name);
            viewholder.introductiontv = (TextView) convertView.findViewById(R.id.teammember_introduction);

            convertView.setTag(viewholder);
        }
        else {
            viewholder = (Viewholder) convertView.getTag();
        }
        viewholder.nametv.setText(listItems.get(position).name);
        viewholder.introductiontv.setText(listItems.get(position).introduction);

        return convertView;
    }

    public class Viewholder{
        TextView nametv;
        TextView introductiontv;
        ImageView headerbmp;
    }
}
