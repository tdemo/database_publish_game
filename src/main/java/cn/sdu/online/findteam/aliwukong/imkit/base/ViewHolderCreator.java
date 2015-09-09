package cn.sdu.online.findteam.aliwukong.imkit.base;

/**
 *
 * ViewHolder 创建接口，实现该接口的类必须包含有一个无参的构造函数
 * Created by zijunlzj on 14/12/23.
 */
public interface ViewHolderCreator<T extends ViewHolder,S> {
    ViewHolder onCreate(Class<T>[] target,S source);
}
