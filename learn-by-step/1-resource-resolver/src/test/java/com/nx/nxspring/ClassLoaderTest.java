package com.nx.nxspring;

import org.junit.jupiter.api.Test;

/**
 * class loader test
 *
 * @author nx-xn2002
 */
public class ClassLoaderTest {
    @Test
    public void testClassLoader() {
        try {
            // 设置要加载的类的类名
            String className = "com.nx.nxspring.loadexample.MyClass1";
            // 创建ClassLoader实例
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            // 加载指定类
            Class<?> clazz = classLoader.loadClass(className);
            // 创建实例并调用方法
            Object instance = clazz.getDeclaredConstructor().newInstance();
            clazz.getMethod("print").invoke(instance);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
