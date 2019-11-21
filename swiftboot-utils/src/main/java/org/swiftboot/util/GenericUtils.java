package org.swiftboot.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 *
 */
public class GenericUtils {

    /**
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> Class ancestorGenericClass(Class<T> clazz) {
        ParameterizedType genericSuperclass = firstParameterizedType(clazz);
        Class<T> genericClass = (Class<T>) genericSuperclass.getActualTypeArguments()[0];
        return genericClass;
    }

    /**
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> Class parentGenericClass(Class<T> clazz) {
        Type genericSuperclass = clazz.getGenericSuperclass();
        if (genericSuperclass == null) {
            throw new RuntimeException(Info.get(GenericUtils.class, "no_generic_super_class"));
        }
        if (genericSuperclass instanceof ParameterizedType) {
            Class<T> genericClass = (Class<T>) ((ParameterizedType) genericSuperclass).getActualTypeArguments()[0];
            return genericClass;
        }
        else {
            throw new RuntimeException(
                    Info.get(GenericUtils.class, "no_generic_super_class", clazz.getGenericSuperclass().getTypeName()));
        }
    }


    /**
     * 通过继承关系向上查找第一个 ParameterizedType
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> ParameterizedType firstParameterizedType(Class<T> clazz) {
        Type genericSuperclass = clazz.getGenericSuperclass();
        if (genericSuperclass == null) {
            throw new RuntimeException(Info.get(GenericUtils.class, "no_generic_class_to_ancestor1", clazz.getName()));
        }
        if (genericSuperclass instanceof ParameterizedType) {
            return (ParameterizedType) genericSuperclass;
        }
        Class clazz2 = (Class) genericSuperclass;
        return firstParameterizedType(clazz2);
    }


    /**
     * 获取一个类的父类（及其父类）的泛型类型
     * @param clazz
     * @param <T>
     * @return
     * @deprecated ancestorGenericClass()
     */
    public static <T> Class genericClass(Class<T> clazz) {
        Type genericSuperclass = clazz.getGenericSuperclass();
        if (genericSuperclass == null) {
            throw new RuntimeException(Info.get(GenericUtils.class, "no_generic_super_class"));
        }
        if (!(genericSuperclass instanceof ParameterizedType)) {
            // 如果存在继承，则向上取一级
            genericSuperclass = ((Class) genericSuperclass).getGenericSuperclass();
            if (genericSuperclass == null) {
                throw new RuntimeException(
                        Info.get(GenericUtils.class, "no_generic_class_to_parent1", clazz.getGenericSuperclass().getTypeName()));
            }
            if (!(genericSuperclass instanceof ParameterizedType)) {
                throw new RuntimeException(
                        Info.get(GenericUtils.class, "no_generic_class_to_parent1", clazz.getGenericSuperclass().getTypeName()));
            }
        }
        Class<T> genericClass = (Class<T>) ((ParameterizedType) genericSuperclass).getActualTypeArguments()[0];
        return genericClass;
    }
}
