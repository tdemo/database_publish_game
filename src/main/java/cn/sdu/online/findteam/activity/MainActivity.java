package cn.sdu.online.findteam.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.widget.ActionMenuView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.sdu.online.findteam.R;
import cn.sdu.online.findteam.fragment.AllGamesFragment;
import cn.sdu.online.findteam.fragment.BuildTeamFragment;
import cn.sdu.online.findteam.fragment.FragmentSetting;
import cn.sdu.online.findteam.fragment.MainFragment;
import cn.sdu.online.findteam.share.MyApplication;
import cn.sdu.online.findteam.view.ActionBarDrawerToggle;
import cn.sdu.online.findteam.view.DrawerArrowDrawable;

public class MainActivity extends FragmentActivity implements View.OnClickListener {

    public static MainActivity mainActivity;

    private DrawerLayout mDrawerLayout;
    private RelativeLayout mDrawerRelative;
    private ActionBarDrawerToggle mDrawerToggle;
    private FragmentManager fragmentManager;
    /**
     * 记录actionsearch按钮的点击状态，true为不搜索状态，false为搜索栏弹出状态
     */
    private boolean acState;
    /**
     * 主界面搜索按钮
     */
    private Button actionsearch;
    private Button searchButton;
    // 主界面的搜索框布局
    private LinearLayout searchLayout;
    // 导航栏
    private View view_actionbar;

    private LinearLayout mVisitorDrawerLayout;
    // 获取intent传入的字符串和长整型ID
/*    private String intentString;
    private Long intentID;*/
    // 侧边栏用户名
    private TextView tv_text, tv_id;
    EditText search_edit;

    MainFragment mainFragment;
    BuildTeamFragment buildTeamFragment;
    FragmentSetting fragmentSetting;
    AllGamesFragment allGamesFragment;
    private List<Fragment> fragmentList;

    public final static int MAIN_FRAGMENT = 0;
    public final static int ALLGAMES_FRAGMENT = 1;
    public final static int BUILDTEAM_FRAGMENT = 2;
    public final static int FRAGMENT_SETTING = 3;
    private int currentFragment = MAIN_FRAGMENT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mainActivity = MainActivity.this;
        super.onCreate(savedInstanceState);

        fragmentManager = getSupportFragmentManager();
        if (null != savedInstanceState) {
            currentFragment = savedInstanceState.getInt("currentFragment");
            if (savedInstanceState.getInt("user_or_not") == 0) {
                MyApplication.USER_OR_NOT = 0;
            } else if (savedInstanceState.getInt("user_or_not") == 1) {
                MyApplication.USER_OR_NOT = 1;
            }
        }

        // 设置ActionBar的自定义布局。
        view_actionbar = View.inflate(this, R.layout.actionbar_layout, null);
        setActionBarLayout(view_actionbar);
        setContentView(R.layout.activity_main);

/*        intentString = getIntent().getExtras().getString("loginIdentity");
        intentID = getIntent().getExtras().getLong("loginID");*/

        searchLayout = (LinearLayout) findViewById(R.id.mainactivity_search_layout);
        acState = true;
        if (/*intentString.startsWith("<##用户##>")*/MyApplication.USER_OR_NOT == 1) {
            ViewStub viewStub = (ViewStub) findViewById(R.id.drawer_viewstub);
            viewStub.setLayoutResource(R.layout.drawer_layout);
            viewStub.inflate();
            mDrawerRelative = (RelativeLayout) findViewById(R.id.navdrawer);
            initMDrawer();
            init_button();
            tv_text = (TextView) findViewById(R.id.tv_name);
            tv_id = (TextView) findViewById(R.id.tv_ID);
            tv_text.setText(MyApplication.getInstance().getSharedPreferences("loginmessage", Context.MODE_PRIVATE).getString("loginName", ""));
            tv_id.setText("(聊天帐号:" + MyApplication.getInstance().getSharedPreferences("loginmessage", Context.MODE_PRIVATE).getLong("loginID", 0) + ")");
        } else if (MyApplication.USER_OR_NOT == 0) {
            ViewStub viewStub = (ViewStub) findViewById(R.id.drawer_viewstub);
            viewStub.setLayoutResource(R.layout.visitor_drawer_layout);
            viewStub.inflate();
            mVisitorDrawerLayout = (LinearLayout) findViewById(R.id.visitor_drawer_layout);
            initMDrawer();
            init_Visitor_Btn();
        }
        addFragment(savedInstanceState);
        MyApplication.IDENTITY = "队长";
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("currentFragment", currentFragment);
        if (MyApplication.USER_OR_NOT == 1) {
            outState.putInt("user_or_not", 1);
        } else if (MyApplication.USER_OR_NOT == 0) {
            outState.putInt("user_or_not", 0);
        }
    }

    private void addFragment(Bundle savedInstanceState) {
        fragmentList = new ArrayList<Fragment>();
        mainFragment = new MainFragment();
        allGamesFragment = new AllGamesFragment();
        buildTeamFragment = new BuildTeamFragment();
        fragmentSetting = new FragmentSetting();
        fragmentList.add(mainFragment);
        fragmentList.add(allGamesFragment);
        fragmentList.add(buildTeamFragment);
        fragmentList.add(fragmentSetting);

        if (null != savedInstanceState) {
            fragmentManager.beginTransaction().replace(R.id.container, fragmentList.get(MAIN_FRAGMENT))
                    .add(R.id.container, fragmentList.get(ALLGAMES_FRAGMENT))
                    .add(R.id.container, fragmentList.get(BUILDTEAM_FRAGMENT))
                    .add(R.id.container, fragmentList.get(FRAGMENT_SETTING)).commitAllowingStateLoss();
        } else {
            fragmentManager.beginTransaction().replace(R.id.container, fragmentList.get(MAIN_FRAGMENT))
                    .add(R.id.container, fragmentList.get(ALLGAMES_FRAGMENT))
                    .add(R.id.container, fragmentList.get(BUILDTEAM_FRAGMENT))
                    .add(R.id.container, fragmentList.get(FRAGMENT_SETTING)).commit();
        }
        if (currentFragment == MAIN_FRAGMENT) {
            currentFragment = MAIN_FRAGMENT;
            fragmentManager.beginTransaction().hide(fragmentList.get(ALLGAMES_FRAGMENT))
                    .hide(fragmentList.get(BUILDTEAM_FRAGMENT))
                    .hide(fragmentList.get(FRAGMENT_SETTING)).commit();
        } else if (currentFragment == ALLGAMES_FRAGMENT) {
            currentFragment = ALLGAMES_FRAGMENT;
            fragmentManager.beginTransaction().hide(fragmentList.get(MAIN_FRAGMENT))
                    .hide(fragmentList.get(BUILDTEAM_FRAGMENT))
                    .hide(fragmentList.get(FRAGMENT_SETTING)).commit();
        } else if (currentFragment == BUILDTEAM_FRAGMENT) {
            currentFragment = BUILDTEAM_FRAGMENT;
            fragmentManager.beginTransaction().hide(fragmentList.get(ALLGAMES_FRAGMENT))
                    .hide(fragmentList.get(MAIN_FRAGMENT))
                    .hide(fragmentList.get(FRAGMENT_SETTING)).commit();
        } else if (currentFragment == FRAGMENT_SETTING) {
            currentFragment = FRAGMENT_SETTING;
            fragmentManager.beginTransaction().hide(fragmentList.get(ALLGAMES_FRAGMENT))
                    .hide(fragmentList.get(BUILDTEAM_FRAGMENT))
                    .hide(fragmentList.get(MAIN_FRAGMENT)).commit();
        }
    }

    void initMDrawer() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mVisitorDrawerLayout = (LinearLayout) findViewById(R.id.visitor_drawer_layout);

        final DrawerArrowDrawable drawerArrow = new DrawerArrowDrawable(this) {
            @Override
            public boolean isLayoutRtl() {
                return false;
            }
        };
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                drawerArrow, R.string.drawer_open,
                R.string.drawer_close) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                //             listView.setEnabled(true);
                actionsearch.setVisibility(View.VISIBLE);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                actionsearch.setVisibility(View.INVISIBLE);//搜索按钮消失
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
    }

    /**
     * 布局Id
     */
    public void setActionBarLayout(View v) {
        ActionBar actionBar = getActionBar();
        if (null != actionBar) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayShowCustomEnabled(true);

            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);

            ActionBar.LayoutParams layout = new ActionBar.LayoutParams(ActionMenuView.LayoutParams.FILL_PARENT, ActionMenuView.LayoutParams.FILL_PARENT);
            actionBar.setCustomView(v, layout);
        }
    }

    /**
     * 设置actionbar的标题
     */
    void setActionBarTest(String test) {
        TextView title = (TextView) view_actionbar.findViewById(R.id.main_actionbar_title);
        title.setText(test);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (mDrawerRelative != null) {
                if (mDrawerLayout.isDrawerOpen(mDrawerRelative)) {
                    mDrawerLayout.closeDrawer(mDrawerRelative);

                } else {
                    mDrawerLayout.openDrawer(mDrawerRelative);

                }
            } else {
                if (mDrawerLayout.isDrawerOpen(mVisitorDrawerLayout)) {
                    mDrawerLayout.closeDrawer(mVisitorDrawerLayout);
                } else {
                    mDrawerLayout.openDrawer(mVisitorDrawerLayout);
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    //初始化用户的侧滑的按钮
    void init_button() {
        Button bt_game = (Button) this.findViewById(R.id.bt_games);
        Button bt_set = (Button) this.findViewById(R.id.bt_set);
        Button bt_news = (Button) this.findViewById(R.id.bt_news);
        Button bt_my = (Button) this.findViewById(R.id.bt_my);
        Button bt_make = (Button) this.findViewById(R.id.bt_make);
        Button bt_hot = (Button) this.findViewById(R.id.bt_hot);
        Button bt_head = (Button) this.findViewById(R.id.bt_head);
        bt_head.setBackgroundResource(R.drawable.head_moren);

        search_edit = (EditText) findViewById(R.id.main_searchtext);
        searchButton = (Button) findViewById(R.id.search_button);
        actionsearch = (Button) view_actionbar.findViewById(R.id.mian_action_search);
        actionsearch.setOnClickListener(this);
        searchButton.setOnClickListener(this);

        bt_game.setOnClickListener(this);
        bt_set.setOnClickListener(this);
        bt_news.setOnClickListener(this);
        bt_my.setOnClickListener(this);
        bt_make.setOnClickListener(this);
        bt_hot.setOnClickListener(this);
        bt_head.setOnClickListener(this);
    }

    private void init_Visitor_Btn() {
        Button visitorLogin = (Button) findViewById(R.id.visitor_login_btn);
        LinearLayout visitorAllGame = (LinearLayout) findViewById(R.id.visitor_allgame_btn);
        LinearLayout visitorHotGame = (LinearLayout) findViewById(R.id.visitor_hotgame_btn);
        LinearLayout visitorSetting = (LinearLayout) findViewById(R.id.visitor_setting_btn);

        searchButton = (Button) findViewById(R.id.search_button);
        actionsearch = (Button) view_actionbar.findViewById(R.id.mian_action_search);
        actionsearch.setOnClickListener(this);
        searchButton.setOnClickListener(this);

        visitorAllGame.setOnClickListener(this);
        visitorHotGame.setOnClickListener(this);
        visitorLogin.setOnClickListener(this);
        visitorSetting.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int tag = v.getId();
        switch (tag) {
            case R.id.bt_head:
                mDrawerLayout.closeDrawer(mDrawerRelative);
                Timer timer1 = new Timer(true);
                TimerTask timerTask1 = new TimerTask() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(MainActivity.this, InfoPersonActivity.class);
                        startActivity(intent);
                    }
                };
                timer1.schedule(timerTask1, 200);
                break;
            case R.id.bt_set:
                mDrawerLayout.closeDrawer(mDrawerRelative);
                if (fragmentSetting == null) {
                    fragmentSetting = new FragmentSetting();
                }
                Timer timer2 = new Timer(true);
                TimerTask timerTask2 = new TimerTask() {
                    @Override
                    public void run() {
                        fragmentManager.beginTransaction().hide(fragmentList.get(currentFragment))
                                .show(fragmentList.get(FRAGMENT_SETTING)).commit();
/*                                .replace(R.id.container, fragmentSetting, "fragmentsetting").commit();*/
                        currentFragment = FRAGMENT_SETTING;
                        MyApplication.currentFragment = FRAGMENT_SETTING;
                    }
                };
                timer2.schedule(timerTask2, 200);
                setActionBarTest("设置");
                break;
            case R.id.bt_make:
                mDrawerLayout.closeDrawer(mDrawerRelative);
                if (buildTeamFragment == null) {
                    buildTeamFragment = new BuildTeamFragment();
                }
                Timer timer3 = new Timer(true);
                TimerTask timerTask3 = new TimerTask() {
                    @Override
                    public void run() {
                        fragmentManager.beginTransaction().hide(fragmentList.get(currentFragment))
                                .show(fragmentList.get(BUILDTEAM_FRAGMENT)).commit();
                        currentFragment = BUILDTEAM_FRAGMENT;
                        MyApplication.currentFragment = BUILDTEAM_FRAGMENT;
                    }
                };
                timer3.schedule(timerTask3, 200);
                setActionBarTest("创建队伍");
                break;
            case R.id.bt_my:
                mDrawerLayout.closeDrawer(mDrawerRelative);
                Timer timer4 = new Timer(true);
                TimerTask timerTask4 = new TimerTask() {
                    @Override
                    public void run() {
                        Intent intent1 = new Intent();
                        intent1.setClass(MainActivity.this, MyTeamActivity.class);
                        startActivity(intent1);
                    }
                };
                timer4.schedule(timerTask4, 200);
                break;

            case R.id.bt_news:
                mDrawerLayout.closeDrawer(mDrawerRelative);
                //延迟200毫秒，让侧边栏完全收回时再开新的Activity
                Timer timer5 = new Timer(true);
                TimerTask timerTask5 = new TimerTask() {
                    @Override
                    public void run() {
                        Intent intent2 = new Intent();
                        intent2.setClass(MainActivity.this, MyMessageActivity.class);
                        startActivity(intent2);
                    }
                };
                timer5.schedule(timerTask5, 200);
                break;
            case R.id.bt_games:
                mDrawerLayout.closeDrawer(mDrawerRelative);
                if (allGamesFragment == null) {
                    allGamesFragment = new AllGamesFragment();
                }
                Timer timer6 = new Timer(true);
                TimerTask timerTask6 = new TimerTask() {
                    @Override
                    public void run() {
                        fragmentManager.beginTransaction().hide(fragmentList.get(currentFragment))
                                .show(fragmentList.get(ALLGAMES_FRAGMENT)).commit();
                        currentFragment = ALLGAMES_FRAGMENT;
                        MyApplication.currentFragment = ALLGAMES_FRAGMENT;
                    }
                };
                timer6.schedule(timerTask6, 200);
                setActionBarTest("所有比赛");
                break;
            case R.id.bt_hot:
                mDrawerLayout.closeDrawer(mDrawerRelative);
                if (mainFragment == null) {
                    mainFragment = new MainFragment();
                }
                Timer timer9 = new Timer(true);
                TimerTask timerTask9 = new TimerTask() {
                    @Override
                    public void run() {
                        fragmentManager.beginTransaction().hide(fragmentList.get(currentFragment))
                                .show(fragmentList.get(MAIN_FRAGMENT)).commit();
                        currentFragment = MAIN_FRAGMENT;
                        MyApplication.currentFragment = MAIN_FRAGMENT;
                    }
                };
                timer9.schedule(timerTask9, 200);
                setActionBarTest("热门赛事");
                break;
            case R.id.mian_action_search:
                if (acState) {
                    searchLayout.setVisibility(View.VISIBLE);
                    acState = false;
                } else {
                    searchLayout.setVisibility(View.GONE);
                    acState = true;
                }
                break;

            case R.id.search_button:
                if (search_edit.getText().toString().trim().length() != 0) {
                    if (!mainFragment.isHidden()) {
                        String str = search_edit.getText().toString();
                        CharSequence constraint = str.subSequence(0, str.length());
                        mainFragment.setFilter(constraint);
                    } else if (!allGamesFragment.isHidden()) {
                        String str = search_edit.getText().toString();
                        CharSequence constraint = str.subSequence(0, str.length());
                        allGamesFragment.setFilter(constraint);
                    }
                }
                break;

            case R.id.visitor_allgame_btn:
                mDrawerLayout.closeDrawer(mVisitorDrawerLayout);
                if (allGamesFragment == null) {
                    allGamesFragment = new AllGamesFragment();
                }
                Timer timer8 = new Timer(true);
                TimerTask timerTask8 = new TimerTask() {
                    @Override
                    public void run() {
                        fragmentManager.beginTransaction().hide(fragmentList.get(currentFragment))
                                .show(fragmentList.get(ALLGAMES_FRAGMENT)).commit();
                        currentFragment = ALLGAMES_FRAGMENT;
                        MyApplication.currentFragment = ALLGAMES_FRAGMENT;
                    }
                };
                timer8.schedule(timerTask8, 200);
                setActionBarTest("所有比赛");
                break;

            case R.id.visitor_hotgame_btn:
                mDrawerLayout.closeDrawer(mVisitorDrawerLayout);
                if (mainFragment == null) {
                    mainFragment = new MainFragment();
                }
                Timer timer10 = new Timer(true);
                TimerTask timerTask10 = new TimerTask() {
                    @Override
                    public void run() {
                        fragmentManager.beginTransaction().hide(fragmentList.get(currentFragment))
                                .show(fragmentList.get(MAIN_FRAGMENT)).commit();
                        currentFragment = MAIN_FRAGMENT;
                        MyApplication.currentFragment = MAIN_FRAGMENT;
                    }
                };
                timer10.schedule(timerTask10, 200);
                setActionBarTest("热门赛事");
                break;

            case R.id.visitor_setting_btn:
                mDrawerLayout.closeDrawer(mVisitorDrawerLayout);
                if (fragmentSetting == null) {
                    fragmentSetting = new FragmentSetting();
                }
                Timer timer7 = new Timer(true);
                TimerTask timerTask7 = new TimerTask() {
                    @Override
                    public void run() {
                        fragmentManager.beginTransaction().hide(fragmentList.get(currentFragment))
                                .show(fragmentList.get(FRAGMENT_SETTING)).commit();
                        currentFragment = FRAGMENT_SETTING;
                        MyApplication.currentFragment = FRAGMENT_SETTING;
                    }
                };
                timer7.schedule(timerTask7, 200);
                setActionBarTest("设置");
                break;

            case R.id.visitor_login_btn:
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, LoginActivity.class);
                startActivity(intent);

            default:
                break;
        }

    }

    /**
     * 双击退出函数
     */
    private long exitTime = 0;

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - exitTime > 2000) {
            Toast.makeText(this, R.string.to_exit, Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            this.finish();
        }
        // super.onBackPressed();
    }

    /**
     * 菜单、返回键响应
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if (mDrawerRelative != null) {
                if (mDrawerLayout.isDrawerOpen(mDrawerRelative)) {
                    mDrawerLayout.closeDrawer(mDrawerRelative);
                } else {
                    if (currentFragment != MAIN_FRAGMENT) {
                        if (mainFragment == null) {
                            mainFragment = new MainFragment();
                        }
                        fragmentManager.beginTransaction().hide(fragmentList.get(currentFragment))
                                .show(fragmentList.get(MAIN_FRAGMENT)).commit();
                        currentFragment = MAIN_FRAGMENT;
                        MyApplication.currentFragment = MAIN_FRAGMENT;
                    } else {
                        onBackPressed(); // 调用双击退出函数
                    }
                }
            } else {
                if (mDrawerLayout.isDrawerOpen(mVisitorDrawerLayout)) {
                    mDrawerLayout.closeDrawer(mVisitorDrawerLayout);
                } else {
                    if (currentFragment != MAIN_FRAGMENT) {
                        if (mainFragment == null) {
                            mainFragment = new MainFragment();
                        }
                        fragmentManager.beginTransaction().hide(fragmentList.get(currentFragment))
                                .show(fragmentList.get(MAIN_FRAGMENT)).commit();
                        currentFragment = MAIN_FRAGMENT;
                        MyApplication.currentFragment = MAIN_FRAGMENT;
                    } else {
                        onBackPressed(); // 调用双击退出函数
                    }
                }
            }

        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case BuildTeamFragment.PHOTO_REQUEST:// 相册返回
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        if (buildTeamFragment == null) {
                            buildTeamFragment = new BuildTeamFragment();
                        }
                        buildTeamFragment.setHeaderImgAlbum();
                        break;
                }
                break;

            case BuildTeamFragment.CAMERA_REQUEST:// 照相返回

                switch (resultCode) {
                    case Activity.RESULT_OK:// 照相完成点击确定
                        if (buildTeamFragment == null) {
                            buildTeamFragment = new BuildTeamFragment();
                        }
                        buildTeamFragment.getHeaderImgCamera();
                        break;

                    case Activity.RESULT_CANCELED:// 取消
                        break;
                }
                break;

            case BuildTeamFragment.CAMERA_CUT_REQUEST:

                switch (resultCode) {
                    case Activity.RESULT_OK:
                        if (buildTeamFragment == null) {
                            buildTeamFragment = new BuildTeamFragment();
                        }
                        buildTeamFragment.setHeaderImgCamera(data);
                        break;
                    case Activity.RESULT_CANCELED:
                        break;
                }
                break;
        }
    }
}


