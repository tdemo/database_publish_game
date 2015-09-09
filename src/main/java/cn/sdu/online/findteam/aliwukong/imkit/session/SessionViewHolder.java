package cn.sdu.online.findteam.aliwukong.imkit.session;

import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import cn.sdu.online.findteam.R;
import cn.sdu.online.findteam.aliwukong.imkit.base.ViewHolder;
import cn.sdu.online.findteam.aliwukong.imkit.widget.CustomGridView;
import cn.sdu.online.findteam.aliwukong.imkit.widget.DateUtil;

/**
 * Created by wn on 2015/8/14.
 */
public class SessionViewHolder extends ViewHolder {

    public CustomGridView sessionIconView;
    public TextView sessionUnreadTxt, sessionTitleTxt, sessionGmtTxt, sessionContentTxt;
    public ImageView sessionSilenceImgView,mMessageStatus;
    private  int DRAFT_COLOR;
    public static final String DRAFT_TIP = "[草稿] ";

    @Override
    protected void initView(View view) {
        sessionContentTxt = (TextView) view.findViewById(R.id.session_content);
        sessionGmtTxt = (TextView) view.findViewById(R.id.session_gmt);
        sessionIconView = (CustomGridView) view.findViewById(R.id.session_icon);
        sessionSilenceImgView = (ImageView) view.findViewById(R.id.session_silence);
        sessionTitleTxt = (TextView) view.findViewById(R.id.session_title);
        sessionUnreadTxt = (TextView) view.findViewById(R.id.session_unread);
        DRAFT_COLOR = view.getContext().getResources().getColor(R.color.session_draft);
        mMessageStatus = (ImageView)view.findViewById(R.id.chatting_notsuccess_iv);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.session_item;
    }

    public void setDraft(String draft){
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(DRAFT_TIP);
        builder.append(draft);
        ForegroundColorSpan span = new ForegroundColorSpan(DRAFT_COLOR);
        builder.setSpan(span, 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        sessionContentTxt.setText(builder);
    }

    public void showSessionStatusFail(){
        mMessageStatus.setBackgroundResource(R.drawable.session_status_failed);
        mMessageStatus.setVisibility(View.VISIBLE);
    }

    public void showSessionStatusSending(){
        mMessageStatus.setBackgroundResource(R.drawable.session_status_sending);
        mMessageStatus.setVisibility(View.VISIBLE);
    }

    public void showSessionUnread(int count){
        if (count > 0) {
            sessionUnreadTxt.setVisibility(View.VISIBLE);
            sessionUnreadTxt.setText(count + "");
        } else {
            sessionUnreadTxt.setVisibility(View.GONE);
        }
    }

    /**
     * 设置session的时间
     *
     * @param time
     */
    public void showTime(long time) {
        if (time==0) {
            sessionGmtTxt.setVisibility(View.GONE);
        } else {
            sessionGmtTxt.setVisibility(View.VISIBLE);
            sessionGmtTxt.setText(DateUtil.formatRimetShowTime(sessionContentTxt.getContext(), time, false));
        }
    }
}

