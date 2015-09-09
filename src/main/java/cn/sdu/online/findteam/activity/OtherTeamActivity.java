package cn.sdu.online.findteam.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.sdu.online.findteam.R;
import cn.sdu.online.findteam.adapter.OtherTeamFragmentAdapter;
import cn.sdu.online.findteam.fragment.TeamInformationFragment;
import cn.sdu.online.findteam.fragment.TeamLogFragment;
import cn.sdu.online.findteam.fragment.TeamMemberFragment;
import cn.sdu.online.findteam.resource.DepthPageTransformer;
import cn.sdu.online.findteam.share.MyApplication;

public class OtherTeamActivity extends FragmentActivity {
    public static Context mContext;

    private List<Fragment> mFragmentList = new ArrayList<Fragment>();
    private OtherTeamFragmentAdapter mFragmentAdapter;

    private ViewPager mPageVp;
    /**
     * Tab显示内容TextView
     */
    private TextView mTeamInfoTv, mTeamLogTv, mTeamMemTv;
    /**
     * Fragment
     */
    private TeamInformationFragment mTeamInfoFg;
    private TeamMemberFragment mTeamMemFg;
    private TeamLogFragment mTeamLogFg;
    /**
     * ViewPager的当前选中页
     */
    private int currentIndex;
    /**
     * 屏幕的宽度
     */
    private int screenWidth;
    /**
     * Tab的那个引导线
     */
    private ImageView mTabLineIv;

    /**
     * 三个引导fragment的 Layout
     */
    private LinearLayout teaminfo_ll;
    private LinearLayout teammem_ll;
    private LinearLayout teamlog_ll;

    /**
     * 返回按钮
     */
    private ImageView backimg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.otherteam_layout);
        mContext = OtherTeamActivity.this;
        findById();
        init();
        initTabLineWidth();
        if (MyApplication.ohterTeam_CurrentPage == 0) {
            mPageVp.setCurrentItem(0);
        } else if (MyApplication.ohterTeam_CurrentPage == 1) {
            mPageVp.setCurrentItem(1);
        } else {
            mPageVp.setCurrentItem(2);
        }
    }

    private void findById() {
        mTeamLogTv = (TextView) this.findViewById(R.id.id_teamlog_tv);
        mTeamInfoTv = (TextView) this.findViewById(R.id.id_teaminfo_tv);
        mTeamMemTv = (TextView) this.findViewById(R.id.id_teammem_tv);
        mTabLineIv = (ImageView) this.findViewById(R.id.id_tab_line_iv);
        mPageVp = (ViewPager) this.findViewById(R.id.id_page_vp);
        backimg = (ImageView) this.findViewById(R.id.otherteam_back_img);
        backimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OtherTeamActivity.this.finish();
            }
        });
    }

    private void init() {
        mTeamMemFg = new TeamMemberFragment();
        mTeamLogFg = new TeamLogFragment();
        mTeamInfoFg = new TeamInformationFragment();
        mFragmentList.add(mTeamInfoFg);
        mFragmentList.add(mTeamMemFg);
        mFragmentList.add(mTeamLogFg);

        mFragmentAdapter = new OtherTeamFragmentAdapter(
                this.getSupportFragmentManager(), mFragmentList);
        mPageVp.setAdapter(mFragmentAdapter);
        mPageVp.setCurrentItem(0);
        mPageVp.setPageTransformer(true, new DepthPageTransformer());

        teaminfo_ll = (LinearLayout) findViewById(R.id.id_teaminfo_ll);
        teammem_ll = (LinearLayout) findViewById(R.id.id_teammem_ll);
        teamlog_ll = (LinearLayout) findViewById(R.id.id_tab_teamlog_ll);

        mPageVp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

           /* *
             * state滑动中的状态 有三种状态（0，1，2） 1：正在滑动 2：滑动完毕 0：什么都没做。*/

            @Override
            public void onPageScrollStateChanged(int state) {

            }

            /**
             * position :当前页面，及你点击滑动的页面 offset:当前页面偏移的百分比
             * offsetPixels:当前页面偏移的像素位置
             * **/

            @Override
            public void onPageScrolled(int position, float offset,
                                       int offsetPixels) {
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mTabLineIv
                        .getLayoutParams();
                /**
                 * 利用currentIndex(当前所在页面)和position(下一个页面)以及offset来
                 * 设置mTabLineIv的左边距 滑动场景：
                 * 记3个页面,
                 * 从左到右分别为0,1,2
                 * 0->1; 1->2; 2->1; 1->0*/


                if (currentIndex == 0 && position == 0)// 0->1
                {
                    lp.leftMargin = (int) (offset * (screenWidth * 1.0 / 3) + currentIndex
                            * (screenWidth / 3));

                } else if (currentIndex == 1 && position == 0) // 1->0
                {
                    lp.leftMargin = (int) (-(1 - offset)
                            * (screenWidth * 1.0 / 3) + currentIndex
                            * (screenWidth / 3));

                } else if (currentIndex == 1 && position == 1) // 1->2
                {
                    lp.leftMargin = (int) (offset * (screenWidth * 1.0 / 3) + currentIndex
                            * (screenWidth / 3));
                } else if (currentIndex == 2 && position == 1) // 2->1
                {
                    lp.leftMargin = (int) (-(1 - offset)
                            * (screenWidth * 1.0 / 3) + currentIndex
                            * (screenWidth / 3));
                }
                mTabLineIv.setLayoutParams(lp);
            }

            @Override
            public void onPageSelected(int position) {
                resetTextView();
                switch (position) {
                    case 0:
                        mTeamInfoTv.setTextColor(Color.rgb(80, 154, 255));
                        MyApplication.ohterTeam_CurrentPage = 0;
                        break;
                    case 1:
                        mTeamMemTv.setTextColor(Color.rgb(80, 154, 255));
                        MyApplication.ohterTeam_CurrentPage = 1;
                        break;
                    case 2:
                        mTeamLogTv.setTextColor(Color.rgb(80, 154, 255));
                        MyApplication.ohterTeam_CurrentPage = 2;
                        break;
                }
                currentIndex = position;
            }
        });
        mPageVp.setOffscreenPageLimit(2);
        setTabListener();
    }

    /**
     * 设置滑动条的宽度为屏幕的1/3(根据Tab的个数而定)
     */
    private void initTabLineWidth() {
        DisplayMetrics dpMetrics = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay()
                .getMetrics(dpMetrics);
        screenWidth = dpMetrics.widthPixels;
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mTabLineIv
                .getLayoutParams();
        lp.width = screenWidth / 3;

        if (MyApplication.ohterTeam_CurrentPage == 1) {
            lp.setMargins(screenWidth / 3, 0, 0, 0);
        } else if (MyApplication.ohterTeam_CurrentPage == 2) {
            mPageVp.setCurrentItem(2);
            lp.setMargins((screenWidth / 3) * 2, 0, 0, 0);
        }
        mTabLineIv.setLayoutParams(lp);
    }

    /**
     * 重置颜色
     */
    private void resetTextView() {
        mTeamInfoTv.setTextColor(Color.BLACK);
        mTeamMemTv.setTextColor(Color.BLACK);
        mTeamLogTv.setTextColor(Color.BLACK);
    }

    private void setTabListener() {
        teaminfo_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPageVp.setCurrentItem(0);
                MyApplication.ohterTeam_CurrentPage = 0;
            }
        });

        teammem_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPageVp.setCurrentItem(1);
                MyApplication.ohterTeam_CurrentPage = 1;
            }
        });

        teamlog_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPageVp.setCurrentItem(2);
                MyApplication.ohterTeam_CurrentPage = 3;
            }
        });
    }

    public static Context getContext() {
        return mContext;
    }
}
