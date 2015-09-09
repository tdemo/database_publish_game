package cn.sdu.online.findteam.activity;


import java.util.ArrayList;
import java.util.List;


import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ActionMenuView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.Toast;

import com.alibaba.wukong.Callback;
import com.alibaba.wukong.im.Conversation;
import com.alibaba.wukong.im.ConversationChangeListener;
import com.alibaba.wukong.im.ConversationService;
import com.alibaba.wukong.im.IMEngine;
import com.alibaba.wukong.im.Message;
import com.alibaba.wukong.im.MessageListener;
import com.alibaba.wukong.im.MessageService;

import cn.sdu.online.findteam.aliwukong.imkit.session.model.Session;
import cn.sdu.online.findteam.fragment.FriendMainFragment;
import cn.sdu.online.findteam.share.DemoUtil;
import cn.sdu.online.findteam.share.MyApplication;
import cn.sdu.online.findteam.view.BadgeView;

import cn.sdu.online.findteam.R;
import cn.sdu.online.findteam.fragment.SessionFragment;


public class MyMessageActivity extends FragmentActivity implements View.OnClickListener {
    private ViewPager mViewPager;
    private FragmentPagerAdapter mAdapter;
    private List<Fragment> mDatas;
    private int mScreen1_2;

    private int mCurrentPageIndex;
    private ImageView mTabline;
    private TextView mChatTextView;
    private TextView mFriendTextView;

    private LinearLayout friend;
    private LinearLayout chat;
    private LinearLayout searchlayout;

    private Button back;
    private Button actionsearch;
    private Button newChat;

    // 搜索框的状态
    private boolean state;

    public static BadgeView badgeView;

/*    private static Bundle savedInstanceState;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
/*        this.savedInstanceState = savedInstanceState;*/
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        setActionBarLayout(R.layout.mymessage_actionbar);
        setContentView(R.layout.mymessageactivity_layout);
        state = false;
        initView();
        initTabLine();
        if (MyApplication.myMessage_CurrentPage == 0) {
            mViewPager.setCurrentItem(0);
        } else {
            mViewPager.setCurrentItem(1);
        }
    }

/*    public static Bundle getSavedInstanceState() {
        return savedInstanceState;
    }*/

    private void initTabLine() {
        mTabline = (ImageView) findViewById(R.id.id_invite_tabline);
        DisplayMetrics dpMetrics = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay()
                .getMetrics(dpMetrics);
        mScreen1_2 = dpMetrics.widthPixels / 2;
        LinearLayout.LayoutParams lp1 = (LinearLayout.LayoutParams) mTabline
                .getLayoutParams();
        lp1.width = mScreen1_2;
        if (MyApplication.myMessage_CurrentPage == 1) {
            lp1.setMargins(mScreen1_2, 0, 0, 0);
        }
        mTabline.setLayoutParams(lp1);
    }

    private void initView() {
        mViewPager = (ViewPager) findViewById(R.id.id_viewpager);
        mChatTextView = (TextView) findViewById(R.id.id_tv_chat);
        mFriendTextView = (TextView) findViewById(R.id.id_tv_friend);
        friend = (LinearLayout) findViewById(R.id.mymessage_friend_tab);
        chat = (LinearLayout) findViewById(R.id.mymessage_chat_tab);
        back = (Button) findViewById(R.id.mymessage_back_btn);
        actionsearch = (Button) findViewById(R.id.mymessage_actionsearch);
        searchlayout = (LinearLayout) findViewById(R.id.mymessage_search_layout);
        newChat = (Button) findViewById(R.id.newaction_chat);

        newChat.setOnClickListener(this);
        friend.setOnClickListener(this);
        chat.setOnClickListener(this);
        back.setOnClickListener(this);
        actionsearch.setOnClickListener(this);

        mDatas = new ArrayList<Fragment>();
        SessionFragment tab02 = new SessionFragment();
        FriendMainFragment tab01 = new FriendMainFragment();
        mDatas.add(tab01);
        mDatas.add(tab02);

        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return mDatas.size();
            }

            @Override
            public Fragment getItem(int arg0) {
                return mDatas.get(arg0);
            }
        };
        mViewPager.setAdapter(mAdapter);

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // TODO Auto-generated method stub
                resetTextView();
                switch (position) {
                    case 1:
                        mChatTextView.setTextColor(Color.parseColor("#509aff"));
                        MyApplication.myMessage_CurrentPage = 1;
                        break;
                    case 0:
                        mFriendTextView.setTextColor(Color.parseColor("#509aff"));
                        MyApplication.myMessage_CurrentPage = 0;
                        break;

                }
                mCurrentPageIndex = position;
            }

            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPx) {
                // TODO Auto-generated method stub
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mTabline
                        .getLayoutParams();

                if (mCurrentPageIndex == 0 && position == 0)// 0->1
                {
                    lp.leftMargin = (int) (positionOffset * mScreen1_2 + mCurrentPageIndex
                            * mScreen1_2);
                } else if (mCurrentPageIndex == 1 && position == 0)// 1->0
                {
                    lp.leftMargin = (int) (mCurrentPageIndex * mScreen1_2 + (positionOffset - 1)
                            * mScreen1_2);
                }
                mTabline.setLayoutParams(lp);

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO Auto-generated method stub

            }
        });
        badgeView = new BadgeView(this);
        addBadgeView();
        setBadgeCountListener();
/*        if (getUnreadNum() == 0) {
            badgeView.setBadgeCount(0);
            badgeView.setVisibility(View.GONE);
        }
        else {
            badgeView.setBadgeCount(getUnreadNum());
            badgeView.setVisibility(View.VISIBLE);
        }*/
    }


    public static int getCount() {
        return badgeView.getBadgeCount();
    }


    protected void resetTextView() {
        mChatTextView.setTextColor(Color.BLACK);
        mFriendTextView.setTextColor(Color.BLACK);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mymessage_friend_tab:
                mViewPager.setCurrentItem(0);
                MyApplication.myMessage_CurrentPage = 0;
                break;
            case R.id.mymessage_chat_tab:
                mViewPager.setCurrentItem(1);
                MyApplication.myMessage_CurrentPage = 1;
                break;
            case R.id.mymessage_back_btn:
                MyMessageActivity.this.finish();
                break;
            case R.id.mymessage_actionsearch:
                if (!state) {
                    searchlayout.setVisibility(View.VISIBLE);
                    state = true;
                } else {
                    searchlayout.setVisibility(View.GONE);
                    state = false;
                }
                break;

            case R.id.newaction_chat:
                Intent intent = new Intent(this, NewChatActivity.class);
                startActivity(intent);
                break;

        }
    }

    /**
     * @param layoutId 布局Id
     **/

    public void setActionBarLayout(int layoutId) {
        ActionBar actionBar = getActionBar();
        if (null != actionBar) {
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setDisplayShowCustomEnabled(true);
            LayoutInflater inflator = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = inflator.inflate(layoutId, null);
            ActionBar.LayoutParams layout = new ActionBar.LayoutParams(ActionMenuView.LayoutParams.FILL_PARENT, ActionMenuView.LayoutParams.FILL_PARENT);
            actionBar.setCustomView(v, layout);
        }
    }

    public void addBadgeView() {
        IMEngine.getIMService(ConversationService.class).getTotalUnreadCount(
                new Callback<Integer>() {
                    @Override
                    public void onSuccess(Integer unReadCount) {
                        chat.addView(badgeView);
                        badgeView.setBackgroundResource(R.drawable.badgeview_bg);
                        badgeView.setBadgeCount(unReadCount);
                        MyApplication.unreadnum = unReadCount;
                    }

                    @Override
                    public void onException(String code, String reason) {
                        chat.addView(badgeView);
                        badgeView.setBackgroundResource(R.drawable.badgeview_bg);
                        badgeView.setBadgeCount(0);
                        badgeView.setVisibility(View.GONE);
                    }

                    @Override
                    public void onProgress(Integer data, int progress) {

                    }
                }, false);
    }

    public void setBadgeCountListener() {
        IMEngine.getIMService(ConversationService.class).addConversationChangeListener(new ConversationChangeListener() {
            @Override
            public void onTitleChanged(List<Conversation> list) {

            }

            @Override
            public void onIconChanged(List<Conversation> list) {

            }

            @Override
            public void onStatusChanged(List<Conversation> list) {

            }

            @Override
            public void onLatestMessageChanged(List<Conversation> list) {

            }

            @Override
            public void onUnreadCountChanged(List<Conversation> list) {
                IMEngine.getIMService(ConversationService.class).getTotalUnreadCount(new Callback<Integer>() {
                    @Override
                    public void onSuccess(Integer integer) {
                        badgeView.setBadgeCount(integer);
                    }

                    @Override
                    public void onException(String s, String s1) {
                        Toast.makeText(MyMessageActivity.this, s1, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onProgress(Integer integer, int i) {

                    }
                }, false);
            }

            @Override
            public void onDraftChanged(List<Conversation> list) {

            }

            @Override
            public void onTagChanged(List<Conversation> list) {

            }

            @Override
            public void onExtensionChanged(List<Conversation> list) {

            }

            @Override
            public void onAtMeStatusChanged(List<Conversation> list) {

            }

            @Override
            public void onLocalExtrasChanged(List<Conversation> list) {

            }

            @Override
            public void onNotificationChanged(List<Conversation> list) {

            }

            @Override
            public void onTopChanged(List<Conversation> list) {

            }

            @Override
            public void onMemberCountChanged(List<Conversation> list) {

            }
        });
    }
}
