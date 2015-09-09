package cn.sdu.online.findteam.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.sdu.online.findteam.R;
import cn.sdu.online.findteam.activity.SingleCompetitionActivity;
import cn.sdu.online.findteam.adapter.ListViewAdapter;
import cn.sdu.online.findteam.mob.MainListViewItem;
import cn.sdu.online.findteam.net.NetCore;
import cn.sdu.online.findteam.resource.DialogDefine;
import cn.sdu.online.findteam.share.MyApplication;
import cn.sdu.online.findteam.util.AndTools;
import cn.sdu.online.findteam.util.Time;
import cn.sdu.online.findteam.view.XListView;

public class MainFragment extends Fragment implements
        XListView.IXListViewListener {

    private View view;
    XListView listView;
    private List<MainListViewItem> list;
    private ListViewAdapter adapter;
    /*    private OnFragmentInteractionListener mListener;*/
    private JSONArray refreshData;
    Dialog dialog;
    private int loadPageNum;

    // 网络不可用时显示的界面
    LinearLayout emptyContainer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        dialog = DialogDefine.createLoadingDialog(MainFragment.this.getActivity(),
                "加载中...");
        dialog.show();
        view = inflater.inflate(R.layout.fragment_main, container, false);
        emptyContainer = (LinearLayout) view.findViewById(R.id.empty_container);

        handleRefresh();

        // Inflate the layout for this fragment
        return view;
    }

    private void handleRefresh() {
        if (!AndTools.isNetworkAvailable(MyApplication.getInstance())) {
            if (dialog != null) {
                dialog.dismiss();
            }
            emptyContainer.setVisibility(View.VISIBLE);
            emptyContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleRefresh();
                }
            });
        } else {
            emptyContainer.setVisibility(View.GONE);
            listView = (XListView) view.findViewById(R.id.listview);
            listView.setXListViewListener(this);
            listView.setPullLoadEnable(true);
            listView.setRefreshTime(MyApplication.getInstance().getSharedPreferences("mainfragment_refreshtime", Context.MODE_PRIVATE).
                    getString("refreshtime", ""));
            list = new ArrayList<MainListViewItem>();
            myThread(0);
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
/*    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }*/

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

/*    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }*/

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
/*    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }*/

    @SuppressLint("HandlerLeak")
    public Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                // 刷新成功
                case 0:
                    try {
                        refreshFinish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;

                // 加载成功
                case 1:
                    try {
                        loadFinish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;

                // 下拉刷新失败
                case 2:
                    AndTools.showToast(MainFragment.this.getActivity(), "刷新失败");
                    listView.stopRefresh("mainfragment_refreshtime");
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    break;

                // 加载没有更多数据
                case 3:
                    AndTools.showToast(MainFragment.this.getActivity(), "没有更多的数据了");
                    listView.stopLoadMore();
                    break;

                // 加载获取数据错误
                case 4:
                    AndTools.showToast(MainFragment.this.getActivity(), "加载失败");
                    listView.stopLoadMore();
                    break;

                case 5: // 筛选数据

                    break;
            }
        }

        ;
    };

/*    */

    /**
     * 下拉刷新实现
     *
     * @return
     *//*
    private List<HashMap<String, String>> getListDate() {
        list = new ArrayList<HashMap<String, String>>();
        for (int i = 0; i < 15; i++) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("name", "第" + i + "项");
            list.add(map);
        }
        return list;
    }*/
    @Override
    public void onRefresh() {
        myThread(0);
    }

    @Override
    public void onLoadMore() {
        myThread(1);
    }

    /**
     * @param msg 0为下拉刷新或刚打开界面刷新 1为加载更多
     */
    private void myThread(final int msg) {
        new Thread() {
            @Override
            public void run() {
                switch (msg) {
                    case 0:
                        pullRefresh();
                        break;

                    case 1:
                        loadMore();
                        break;
                }
            }
        }.start();
    }

    // 下拉刷新时的刷新方法
    private void pullRefresh() {
        try {
            String jsonData = new NetCore().pullRefreshGamesData(NetCore.getGamesAddr,
                    1, 10);
            loadPageNum = 1;
            if (jsonData != null && !jsonData.equals("")) {
                try {
                    refreshData = new JSONArray(jsonData);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                handler.sendEmptyMessage(0);
            } else {
                handler.sendEmptyMessage(2);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 刷新完成调用
    private void refreshFinish() throws JSONException {
        list.clear();
        if (refreshData == null){
            if (dialog != null){
                dialog.dismiss();
            }
            AndTools.showToast(MainFragment.this.getActivity(), "加载失败！");
            return;
        }
        for (int i = 0; i < refreshData.length(); i++) {
            JSONObject temp = (JSONObject) refreshData.get(i);
            String name = temp.getString("name");
            String description = temp.getString("description");
            String id = temp.getString("id");
            list.add(i, new MainListViewItem(name, description, id));
        }
        adapter = new ListViewAdapter(getActivity(), list);
        listView.setAdapter(adapter);
        MyApplication.getInstance().getSharedPreferences("mainfragment_refreshtime", Context.MODE_PRIVATE).edit()
                .putString("refreshtime", Time.getDate()).apply();
        listView.stopRefresh("mainfragment_refreshtime");
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    // 加载更多调用
    private void loadMore() {
        try {
            String jsonData = new NetCore().pullRefreshGamesData(NetCore.getGamesAddr,
                    loadPageNum, 10);
            if (jsonData != null && !jsonData.equals("")) {
                try {
                    refreshData = new JSONArray(jsonData);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                handler.sendEmptyMessage(1);
            } else {
                handler.sendEmptyMessage(4);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 加载完成调用
    private void loadFinish() throws JSONException {
        if (refreshData == null){
            if (dialog != null){
                dialog.dismiss();
            }
            AndTools.showToast(MainFragment.this.getActivity(), "加载失败！");
            return;
        }

        if (refreshData.length() == 0){
            handler.sendEmptyMessage(3);
            return;
        }
        for (int i = 0; i < refreshData.length(); i++) {
            JSONObject temp = (JSONObject) refreshData.get(i);
            String name = temp.getString("name");
            String description = temp.getString("description");
            String id = temp.getString("id");
            list.add(list.size(), new MainListViewItem(name, description, id));
        }
        adapter.notifyDataSetChanged();
        loadPageNum = loadPageNum + 1;
        listView.stopLoadMore();
    }

    public void setFilter(CharSequence charSequence){
        adapter.getFilter().filter(charSequence);
    }
}
