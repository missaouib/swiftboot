package org.swiftboot.collections;

import java.util.*;

/**
 * @author swiftech
 */
public class CollectionUtils {

    /**
     * 数组转换为 LinkedList 对象
     *
     * @param array
     * @return
     */
    public static LinkedList<Object> toLinkedList(Object[] array) {
        LinkedList<Object> ret = new LinkedList<>();
        Collections.addAll(ret, array);
        return ret;
    }

    /**
     * 集合中是否包含指定类型
     *
     * @param collection
     * @param clazz
     * @return
     */
    public static boolean contains(Collection collection, Class clazz) {
        if (collection == null || clazz == null) {
            return false;
        }
        for (Object o : collection) {
            if (o.getClass().equals(clazz)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取数组中第一个类型匹配的元素
     *
     * @param collection
     * @param clazz
     * @return
     */
    public static Object getFirstMatch(Object[] collection, Class clazz) {
        if (collection == null || clazz == null) {
            return null;
        }
        for (Object o : collection) {
            if (clazz.isAssignableFrom(o.getClass())) {
                return o;
            }
        }
        return null;
    }

    /**
     * 获取数组中第一个类型匹配的元素
     *
     * @param collection
     * @param clazz
     * @return
     */
    public static Object getFirstMatch(List collection, Class clazz) {
        if (collection == null || clazz == null) {
            return null;
        }
        for (Object o : collection) {
            if (clazz.isAssignableFrom(o.getClass())) {
                return o;
            }
        }
        return null;
    }

    /**
     * 按照 ClassifyFilter 接口返回的 key 值分类存放元素集合（和原集合相同类型的集合）
     *
     * @param srcCollection
     * @param classifyFilter
     * @return
     */
    public static <T> Map<Object, Collection> classify(Collection<T> srcCollection, ClassifyFilter classifyFilter) {
        Map<Object, Collection> ret = new HashMap<>();
        for (Object o : srcCollection) {
            Object key = classifyFilter.filter(o);
            Collection coll = ret.get(key);
            if (coll == null) {
                if (srcCollection instanceof List) {
                    coll = new ArrayList();
                }
                else if (srcCollection instanceof Set) {
                    coll = new HashSet();
                }
                else {
                    throw new RuntimeException("集合类型不支持: " + srcCollection.getClass());
                }
                ret.put(key, coll);
            }
            coll.add(o);
        }
        return ret;
    }

    /**
     * 根据不同的集合类型构造不同的集合实例
     *
     * @param collectionType
     * @param <T>
     * @return
     */
    public static <T extends Collection> T constructCollectionByType(Class collectionType) {
        if (Set.class.isAssignableFrom(collectionType)) {
            return (T) new HashSet();
        }
        else if (List.class.isAssignableFrom(collectionType)) {
            return (T) new ArrayList();
        }
        else {
            return null;
        }
    }

}
