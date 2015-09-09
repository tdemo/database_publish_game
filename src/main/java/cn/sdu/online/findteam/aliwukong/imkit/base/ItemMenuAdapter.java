package cn.sdu.online.findteam.aliwukong.imkit.base;

import android.content.Context;
import android.view.ContextMenu;
import android.view.View;
import android.widget.AdapterView;

/**
 * Created by wn on 2015/8/14.
 */
public class ItemMenuAdapter implements AdapterView.OnItemLongClickListener, View.OnCreateContextMenuListener {

    public interface onMenuListener {

        void onCreateMenu(Context context, ContextMenu menu);

        boolean onMenuItemSelected(Context context, int itemId);
    }

    public static ItemMenuAdapter setItemMenu(Context context, AdapterView<?> adapterView) {
        return new ItemMenuAdapter(context, adapterView);
    }

    private Context mContext;
    private Object mCurrentSelectedItem;
    private AdapterView<?> mAdapterView;

    public ItemMenuAdapter(Context context, AdapterView<?> adapterView) {
        mContext = context;
        mAdapterView = adapterView;
        adapterView.setOnItemLongClickListener(this);
        adapterView.setOnCreateContextMenuListener(this);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView,
                                   View view, int position, long id) {
        mCurrentSelectedItem = adapterView.getItemAtPosition(position);
        mAdapterView.showContextMenu();
        return true;
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (mCurrentSelectedItem == null) {
            return;
        }

        if (mCurrentSelectedItem instanceof onMenuListener) {
            ((onMenuListener) mCurrentSelectedItem).onCreateMenu(mContext, menu);
        }
    }

    public boolean onContextItemSelected(android.view.MenuItem item) {
        if (mCurrentSelectedItem == null) {
            return false;
        }
        if (mCurrentSelectedItem instanceof onMenuListener) {
            return ((onMenuListener) mCurrentSelectedItem)
                    .onMenuItemSelected(mContext, item.getItemId());
        }
        return false;
    }

}
