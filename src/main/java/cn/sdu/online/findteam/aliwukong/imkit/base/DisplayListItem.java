package cn.sdu.online.findteam.aliwukong.imkit.base;

/**
 * Created by wn on 2015/8/14.
 */
import android.content.Context;


/**
 * AdapterView  显示的某一项
 * Created by zijunlzj on 14/11/25.
 */
public interface DisplayListItem<T extends ViewHolder> {

    /**
     * 显示视图
     * @param context
     * @param holder
     */
    public void onShow(Context context, T holder, String tag);

    public String getId();

    /**
     * 当需要指定多个viewholder时，根据对象实例返回对应的Viewholder 索引
     * 单个ViewHolder时返回0
     * 当一个 DisplayListItem 对象 指向多个 ViewHolder,需要要根据Model 的值指定
     * 返回的ViewHolder 对象的 索引值， ViewHolder 的索引值对应 @{link Router target}里的索引
     * 如
     * <code>
     * @Router({
     *     TextViewHolder.class
     *     AudioViewholder.class
     * })
     * public class Model implements DisplayListItem<MastViewHolder>{
     *
     * }
     * </code>
     * <br/>
     * 上面例子中，
     * TextViewHolder 索引为 0
     * AudioViewHolder 索引为 1
     *
     * @return
     */
    public int getViewType();
}
