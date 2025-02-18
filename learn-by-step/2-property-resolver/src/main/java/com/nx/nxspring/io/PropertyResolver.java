package com.nx.nxspring.io;

import java.time.*;
import java.util.*;
import java.util.function.Function;

/**
 * property resolver
 *
 * @author nx-xn2002
 */
public class PropertyResolver {
    Map<String, String> properties = new HashMap<>();
    Map<Class<?>, Function<String, Object>> converters = new HashMap<>();

    public PropertyResolver(Properties props) {
        // 存入环境变量
        this.properties.putAll(System.getenv());
        // 存入Properties
        Set<String> names = props.stringPropertyNames();
        for (String name : names) {
            this.properties.put(name, props.getProperty(name));
        }
        // register converters:
        converters.put(String.class, s -> s);
        converters.put(boolean.class, Boolean::parseBoolean);
        converters.put(Boolean.class, Boolean::valueOf);
        converters.put(byte.class, Byte::parseByte);
        converters.put(Byte.class, Byte::valueOf);
        converters.put(short.class, Short::parseShort);
        converters.put(Short.class, Short::valueOf);
        converters.put(int.class, Integer::parseInt);
        converters.put(Integer.class, Integer::valueOf);
        converters.put(long.class, Long::parseLong);
        converters.put(Long.class, Long::valueOf);
        converters.put(float.class, Float::parseFloat);
        converters.put(Float.class, Float::valueOf);
        converters.put(double.class, Double::parseDouble);
        converters.put(Double.class, Double::valueOf);
        converters.put(LocalDate.class, LocalDate::parse);
        converters.put(LocalTime.class, LocalTime::parse);
        converters.put(LocalDateTime.class, LocalDateTime::parse);
        converters.put(ZonedDateTime.class, ZonedDateTime::parse);
        converters.put(Duration.class, Duration::parse);
        converters.put(ZoneId.class, ZoneId::of);
    }

    /**
     * 转换到指定Class类型
     *
     * @param clazz clazz
     * @param value value
     * @return {@link T }
     */
    <T> T convert(Class<?> clazz, String value) {
        Function<String, Object> fn = this.converters.get(clazz);
        if (fn == null) {
            throw new IllegalArgumentException("Unsupported value type: " + clazz.getName());
        }
        return (T) fn.apply(value);
    }

    public <T> T getProperty(String key, Class<T> targetType) {
        String value = getProperty(key);
        if (value == null) {
            return null;
        }
        // 转换为指定类型:
        return convert(targetType, value);
    }

    /**
     * 按配置的 key 进行查询
     *
     * @param key key
     * @return {@link String }
     */
    public String getProperty(String key) {
        PropertyExpr keyExpr = PropertyExpr.parsePropertyExpr(key);
        if (keyExpr != null) {
            if (keyExpr.defaultValue() != null) {
                // 带默认值查询
                return getProperty(keyExpr.key(), keyExpr.defaultValue());
            } else {
                // 不带默认值查询
                return getRequiredProperty(keyExpr.key());
            }
        }
        // 普通key查询
        String value = this.properties.get(key);
        if (value != null) {
            return parseValue(value);
        }
        return null;
    }

    public String getProperty(String key, String defaultValue) {
        String value = getProperty(key);
        return value == null ? parseValue(defaultValue) : value;
    }

    public String getRequiredProperty(String key) {
        String value = getProperty(key);
        return Objects.requireNonNull(value, "Property '" + key + "' not found.");
    }

    String parseValue(String value) {
        PropertyExpr expr = PropertyExpr.parsePropertyExpr(value);
        if (expr == null) {
            return value;
        }
        if (expr.defaultValue() != null) {
            return getProperty(expr.key(), expr.defaultValue());
        } else {
            return getRequiredProperty(expr.key());
        }
    }

    /**
     * 占位符解析类
     *
     * @author nx-xn2002
     */
    record PropertyExpr(String key, String defaultValue) {
        /**
         * 解析占位符字符串
         *
         * @param key key
         * @return {@link PropertyExpr }
         */
        static PropertyExpr parsePropertyExpr(String key) {
            if (key.startsWith("${") && key.endsWith("}")) {
                // 是否存在defaultValue?
                int n = key.indexOf(':');
                if (n == (-1)) {
                    // 没有defaultValue: ${key}
                    String k = key.substring(2, key.length() - 1);
                    return new PropertyExpr(k, null);
                } else {
                    // 有defaultValue: ${key:default}
                    String k = key.substring(2, n);
                    return new PropertyExpr(k, key.substring(n + 1, key.length() - 1));
                }
            }
            return null;
        }
    }
}
