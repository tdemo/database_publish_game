package cn.sdu.online.findteam.aliwukong.imkit.base;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;

/**
 * Created by wn on 2015/8/14.
 */
public class ItemClick {

    public interface OnItemClickListener {
        void onClick(Context sender, View view, int position);
    }


    public static void bindItemClick(Context sender, AdapterView<?> adapterView) {
        if (adapterView == null) {
            return;
        }
        adapterView.setOnItemClickListener(buildListItemClick(sender));
    }

    private static AdapterView.OnItemClickListener buildListItemClick(final Context sender) {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listItemClickListener(sender, parent, view, position);
            }
        };
    }


    private static void listItemClickListener(Context sender, AdapterView<?> adapterView, View view, int position) {
        final Object item = adapterView.getItemAtPosition(position);
        if (item == null || !(item instanceof OnItemClickListener)) {
            return;
        }
        OnItemClickListener Listener = (OnItemClickListener) item;
        Listener.onClick(sender, view, position);
    }

}
