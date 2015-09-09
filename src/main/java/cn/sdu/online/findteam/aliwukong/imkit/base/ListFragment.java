package cn.sdu.online.findteam.aliwukong.imkit.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import dagger.ObjectGraph;

/**
 * Created by wn on 2015/8/14.
 */
public abstract class ListFragment extends Fragment {
    protected View mFragmentView;
    protected ListView mListView;
    private ItemMenuAdapter menuAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(mFragmentView==null) {
            mFragmentView = inflater.inflate(getLayoutResId(), container, false);
        }

        //缓存的mFragmentView需要判断是否已经被加过parent， 如果有parent需要从parent删除，
        // 要不然会发生这个mFragmentView已经有parent的错误。
        ViewGroup parent = (ViewGroup) mFragmentView.getParent();
        if (parent != null) {
            parent.removeView(mFragmentView);
        }

        return mFragmentView;
    }

    public abstract int getLayoutResId();

    public abstract ListView findListView(View fragmentView);

    public abstract ListAdapter buildAdapter(Activity activity);

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ObjectGraph objectGraph = ObjectGraph.create(new ImKitModule());
        objectGraph.inject(this);
        initViews();
    }

    protected void initViews() {
        mListView = findListView(mFragmentView);
        mListView.setAdapter(buildAdapter(getActivity()));
        menuAdapter = ItemMenuAdapter.setItemMenu(getActivity(), mListView);
        ItemClick.bindItemClick(getActivity(), mListView);
        onInitViews(mFragmentView);
    }

    protected void onInitViews(View parent) {
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        return menuAdapter.onContextItemSelected(item);
    }

}
