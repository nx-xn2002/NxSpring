package com.nx.nxspring.context;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * bean definition
 *
 * @author nx-xn2002
 */
public class BeanDefinition {
    /**
     * 全局唯一的Bean Name
     */
    String name;

    /**
     * Bean 的声明类型:
     */
    Class<?> beanClass;

    /**
     * Bean 的实例
     */
    Object instance = null;

    /**
     * 构造方法
     */
    Constructor<?> constructor;

    /**
     * 工厂方法名称
     */
    String factoryName;

    /**
     * 工厂方法
     */
    Method factoryMethod;

    /**
     * Bean的顺序
     */
    int order;

    /**
     * 是否标识 @Primary
     */
    boolean primary;

    /**
     * init method name
     */
    String initMethodName;
    /**
     * destroy method name
     */
    String destroyMethodName;

    /**
     * init method
     */
    Method initMethod;
    /**
     * destroy method
     */
    Method destroyMethod;
}
