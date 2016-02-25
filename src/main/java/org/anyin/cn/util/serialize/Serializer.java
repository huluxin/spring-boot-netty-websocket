package org.anyin.cn.util.serialize;

/**
 * Created by Anyin on 2016/1/24.
 */
public interface Serializer {

    /**
     * 二进制转对象
     * @param bytes
     * @param clazz
     * @param <T>
     * @return
     */
   <T> T Binary2Object (byte[] bytes,Class<T> clazz);

    /**
     * 对象转二进制
     * @param object
     * @return
     */
    byte[] Object2Binary(Object object);

    String Object2String(Object object);

    <T> T String2Object(String json,Class<T> clazz);
}
