package cn.sdu.online.findteam.aliwukong.user;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.sdu.online.findteam.R;
import cn.sdu.online.findteam.aliwukong.avatar.AvatarMagicianImpl;
import cn.sdu.online.findteam.util.AndTools;

public class UserProfileAdapter extends BaseAdapter {

    private Context mContext;
    private List<UserProfileItemObject> mData;
    private Long mOpenId;

    public UserProfileAdapter(Context context, List<UserProfileItemObject> data, Long openId){
        mContext = context;
        mData = data;
        mOpenId = openId;
    }

    @Override
    public boolean isEnabled(int position) {
        return mData.get(position).onclick != null;
    }

    @Override
    public int getCount() {
        return mData != null?mData.size():0;
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return mData.get(position).type.ordinal();
    }

    @Override
    public int getViewTypeCount() {
        return UserProfileItemObject.Type.values().length;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        if(UserProfileItemObject.Type.Avatar.equals(mData.get(position).type)){
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();

                if (mData.get(position).isActive) {
                    convertView = LayoutInflater.from(mContext).inflate(
                            R.layout.header_user_profile, null);
                } else {
                    convertView = LayoutInflater.from(mContext).inflate(
                            R.layout.header_user_profile_unregistered, null);
                    holder.mUserName =  (TextView) convertView
                            .findViewById(R.id.user_name);
                    holder.mInMyContact =  (TextView) convertView
                            .findViewById(R.id.in_my_contact);
                }

                holder.mImage = (ImageView) convertView
                        .findViewById(R.id.user_profile_avatar_tv);
//                holder.mBackgroundImage = (ImageView)convertView.findViewById(R.id.user_profile_background_iv);
                holder.mContent = (TextView) convertView
                        .findViewById(R.id.user_profile_header_full_name);
                holder.mLabels = (TextView) convertView.findViewById(R.id.user_labels);// 目前先支持一个label
                holder.mGenderImageView = (ImageView) convertView
                        .findViewById(R.id.user_profile_header_gender);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            AvatarMagicianImpl.getInstance().setUserAvatar(holder.mImage,mOpenId,null);

            if(mData.get(position).onclick!=null){
                holder.mImage.setOnClickListener(mData.get(position).onclick);
            }
            if(!mData.get(position).isActive) {
                holder.mUserName.setText(mData.get(position).title);
                if(mData.get(position).imageResId==null) {
//                    holder.mInMyContact.setVisibility(View.VISIBLE);
                    holder.mInMyContact.setVisibility(View.GONE);//暂时关掉
                }else{
                    holder.mInMyContact.setVisibility(View.GONE);
                }
            }
            holder.mContent.setText(mData.get(position).content);
            // labels来自服务器，可能size为0，需要保护
            if ( (mData.get(position).labels != null)
                    && (mData.get(position).labels.size() != 0)) {
                String label = mData.get(position).labels.get(0);
                if(!TextUtils.isEmpty(label)) {
                    holder.mLabels.setText(label);// 目前先支持第一个label
                    holder.mLabels.setVisibility(View.VISIBLE);
                }else {
                    holder.mLabels.setVisibility(View.GONE);
                }
            } else if (holder.mLabels != null){
                holder.mLabels.setVisibility(View.GONE);
            }
            if ((mData.get(position) != null)
                    && (!TextUtils.isEmpty(mData.get(position).gender))) {
                if (mData.get(position).gender.equals("M")) {
                    holder.mGenderImageView.setBackgroundResource(R.drawable.male_userprofile);
                } else if(mData.get(position).gender.equals("F"))  {
                    holder.mGenderImageView.setBackgroundResource(R.drawable.female_userprofile);
                } else{
                    holder.mGenderImageView.setVisibility(View.GONE);
                }
            }

        }else if (UserProfileItemObject.Type.MyAvatar.equals(mData.get(position).type)) {

            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();

                if (mData.get(position).isActive) {
                    convertView = LayoutInflater.from(mContext).inflate(
                            R.layout.header_my_profile, null);
                } else {
                    convertView = LayoutInflater.from(mContext).inflate(
                            R.layout.header_my_profile_unregister, null);
                }
                convertView.setPadding(25, 0, 0, 0);
                convertView.setTag(holder);
                holder.mTitle = (TextView) convertView
                        .findViewById(R.id.item_user_profile_cell_title_tv);
                holder.mContent = (TextView) convertView
                        .findViewById(R.id.item_user_profile_cell_value_tv);
                holder.mImage = (ImageView) convertView
                        .findViewById(R.id.my_profile_avatar_tv);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            AvatarMagicianImpl.getInstance().setUserAvatar(holder.mImage,mOpenId,null);
            holder.mTitle.setText(mData.get(position).title);
            holder.mContent.setText("");

        }else if(UserProfileItemObject.Type.Header_TEMP.equals(mData.get(position).type)){
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(
                        R.layout.item_user_profile_cell_header_temp, null);
                convertView.setTag(holder);
                holder.mTitle = (TextView) convertView
                        .findViewById(R.id.item_user_profile_cell_header_title_tv);
                holder.divider = convertView.findViewById(R.id.top_divider);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            if(!TextUtils.isEmpty(mData.get(position).title)) {
                holder.mTitle.setVisibility(View.VISIBLE);
                holder.mTitle.setText(mData.get(position).title);
            }else{
                holder.mTitle.setVisibility(View.GONE);
            }
            if(mData.get(position).first) {
                holder.divider.setVisibility(View.VISIBLE);
            }else{
                holder.divider.setVisibility(View.GONE);
            }

        }else if(UserProfileItemObject.Type.TextContent.equals(mData.get(position).type)){
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(
                        R.layout.item_user_profile_cell, null);
                convertView.setTag(holder);
                convertView.setPadding(25, 0, 25, 0);
                holder.mTitle = (TextView) convertView
                        .findViewById(R.id.item_user_profile_cell_title_tv);
                holder.mContent = (TextView) convertView
                        .findViewById(R.id.item_user_profile_cell_value_tv);
                holder.mImage = (ImageView) convertView
                        .findViewById(R.id.item_user_profile_cell_img);
                holder.divider = convertView
                        .findViewById(R.id.divider_user_profile_cell);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.mTitle.setText(mData.get(position).title);
            holder.mContent.setText(mData.get(position).content);
            holder.mContent.setSingleLine(false);
            if(mData.get(position).title.equals(mContext.getString(R.string.user_profile_gender))) {
                String gender = mData.get(position).content;
                if(gender.equals("F")) {
                    holder.mContent.setText("女");
                }else if(gender.equals("M")) {
                    holder.mContent.setText("男");
                }else {
                    holder.mContent.setText("");
                }
            }
            if (mData.get(position).imageResId != null) {
                holder.mImage.setVisibility(View.VISIBLE);
                holder.mImage.setImageResource(mData.get(position).imageResId);
            } else {
                holder.mImage.setVisibility(View.GONE);
            }
            if(mData.get(position).first){
                holder.divider.setVisibility(View.GONE);
            }else{
                holder.divider.setVisibility(View.VISIBLE);
            }
        }else if(UserProfileItemObject.Type.TextContentRight.equals(mData.get(position).type)) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(
                        R.layout.item_user_left_profile_cell, null);
                convertView.setTag(holder);
                //convertView.setBackgroundResource(R.drawable.listview_line);

                convertView.setPadding(25, 0, 0, 0);
                holder.mTitle = (TextView) convertView
                        .findViewById(R.id.item_user_profile_cell_title_tv);
                holder.mContent = (TextView) convertView
                        .findViewById(R.id.item_user_profile_cell_value_tv);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.mTitle.setText(mData.get(position).title);
            if(mData.get(position).title.equals(mContext.getString(R.string.user_profile_nick))) {
                holder.mContent.setHint(R.string.nick_default);
            }
            holder.mContent.setText(mData.get(position).content);
            if(mData.get(position).title.equals(mContext.getString(R.string.user_profile_gender))) {
                String gender = mData.get(position).content;
                if(gender.equals("F")) {
                    holder.mContent.setText("女");
                }else if(gender.equals("M")) {
                    holder.mContent.setText("男");
                }else {
                    holder.mContent.setText("");
                }
            }

        }else if(UserProfileItemObject.Type.TextContentRight_NO_DIVIVDER.equals(mData.get(position).type)) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(
                        R.layout.item_user_left_profile_cell_no_divider, null);
                convertView.setTag(holder);
                //convertView.setBackgroundResource(R.drawable.listview_line);

                convertView.setPadding(25, 0, 0, 0);
                holder.mTitle = (TextView) convertView
                        .findViewById(R.id.item_user_profile_cell_title_tv);
                holder.mContent = (TextView) convertView
                        .findViewById(R.id.item_user_profile_cell_value_tv);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.mTitle.setText(mData.get(position).title);
            holder.mContent.setText(mData.get(position).content);
        }else if (UserProfileItemObject.Type.Header.equals(mData.get(position).type)) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(
                        R.layout.item_user_profile_cell_header, null);
                convertView.setTag(holder);
                holder.mTitle = (TextView) convertView
                        .findViewById(R.id.item_user_profile_cell_header_title_tv);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            String title = mData.get(position).title;
            if(TextUtils.isEmpty(title)) {
                holder.mTitle.setHeight(AndTools.dp2px(mContext, 150));
                convertView.findViewById(R.id.bottom_divider).setVisibility(View.GONE);
            }
            holder.mTitle.setText(title);
        }

        convertView.setOnClickListener(mData.get(position).onclick);

        return convertView;
    }
    public class ViewHolder {
        TextView mTitle;
        TextView mContent;
        TextView mUserName;
        TextView mInMyContact;
        ImageView mImage;
        TextView mLabels;
        ImageView mGenderImageView;
        View divider;
        ImageView mBackgroundImage;
    }
}
