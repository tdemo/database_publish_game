package cn.sdu.online.findteam.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import cn.sdu.online.findteam.R;
import cn.sdu.online.findteam.activity.EditTeamSettingActivity;

public class TeamPopWindow extends PopupWindow implements View.OnClickListener {
    private View contentView;
    private LinearLayout search, edit;
    private Activity context;

    public TeamPopWindow(final Activity context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        contentView = inflater.inflate(R.layout.teampopup_dialog, null);
        search = (LinearLayout) contentView.findViewById(R.id.exit_team);
        search.setOnClickListener(this);
        edit = (LinearLayout) contentView.findViewById(R.id.edit_team_setting);
        edit.setOnClickListener(this);

        int w = context.getWindowManager().getDefaultDisplay().getWidth();
        // 设置SelectPicPopupWindow的View
        this.setContentView(contentView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(w / 3 );
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        // 刷新状态
        this.update();
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(996699);
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        this.setBackgroundDrawable(new BitmapDrawable());
        // mPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
        // 设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.AnimationPreview);

    }

    public void showPopupWindow(View parent) {
        if (!this.isShowing()) {
            this.showAsDropDown(parent, parent.getLayoutParams().width / 2, 10);
        } else {
            this.dismiss();
        }
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.exit_team:

                this.dismiss();
                break;

            case R.id.edit_team_setting:
                this.dismiss();
                Intent intent = new Intent(context, EditTeamSettingActivity.class);
                context.startActivity(intent);
                break;

            default:
                break;
        }
    }
}
