package cn.sdu.online.findteam.aliwukong.imkit.route;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import cn.sdu.online.findteam.aliwukong.imkit.base.DefaultViewHolderCreator;
import cn.sdu.online.findteam.aliwukong.imkit.base.ViewHolder;
import cn.sdu.online.findteam.aliwukong.imkit.base.ViewHolderCreator;

/**
 * Created by wn on 2015/8/14.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Router {

    /**
     *目标ViewHolder,用于关联当前修饰的Domain对象,按照
     * @return
     */
    Class<? extends ViewHolder>[] value();

    /**
     * ViewHolder 关联对象的生成器
     */
    public Class<? extends ViewHolderCreator> generate() default DefaultViewHolderCreator.class;


}
