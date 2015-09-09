package cn.sdu.online.findteam.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import cn.sdu.online.findteam.R;
import cn.sdu.online.findteam.mob.InviteMemListItem;

public class InviteMemListAdapter extends BaseAdapter {

    private List<InviteMemListItem> list;
    private LayoutInflater layoutInflater;
    private int selectTotal = 0;
    private TextCallback textcallback;

    public InviteMemListAdapter(List<InviteMemListItem> list, Context context) {
        this.list = list;
        this.layoutInflater = LayoutInflater.from(context);
    }

    public interface TextCallback {
        public void onListener(int count);
    }

    public void setTextCallback(TextCallback listener) {
        textcallback = listener;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.invite_listview_item, null);
            viewHolder.img = (ImageView) convertView.findViewById(R.id.invite_listview_img);
            viewHolder.info = (TextView) convertView.findViewById(R.id.invite_listview_infotv);
            viewHolder.title = (TextView) convertView.findViewById(R.id.invite_listview_nametv);
            viewHolder.linearLayout = (LinearLayout) convertView.findViewById(R.id.invite_list_arrowlayout);
            viewHolder.view = convertView.findViewById(R.id.invite_list_spacing);
            viewHolder.parent = (LinearLayout) convertView.findViewById(R.id.invite_layout_parent);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.img.setBackgroundResource(list.get(position).img);
        viewHolder.title.setText(list.get(position).title);
        viewHolder.info.setText(list.get(position).info);

        final InviteMemListItem listItem = list.get(position);
        if (listItem.isSelected) {
            viewHolder.linearLayout.setVisibility(View.VISIBLE);
        } else {
            viewHolder.linearLayout.setVisibility(View.GONE);
        }

        viewHolder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listItem.isSelected = !listItem.isSelected;
                if (listItem.isSelected) {
                    viewHolder.linearLayout.setVisibility(View.VISIBLE);
                    selectTotal++;
                    if (textcallback != null)
                        textcallback.onListener(selectTotal);
                } else if (!listItem.isSelected) {
                    viewHolder.linearLayout.setVisibility(View.GONE);
                    selectTotal--;
                    if (textcallback != null)
                        textcallback.onListener(selectTotal);
                }
            }
        });
        return convertView;
    }

    public class ViewHolder {
        public ImageView img;
        public TextView title;
        public TextView info;
        public View view;
        public LinearLayout linearLayout;
        private LinearLayout parent;
    }
}
